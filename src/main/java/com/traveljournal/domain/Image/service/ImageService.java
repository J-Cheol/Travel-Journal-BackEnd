package com.traveljournal.domain.Image.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.traveljournal.domain.Image.entity.ImageInfo;
import com.traveljournal.domain.Image.repository.ImageInfoRepository;
import com.traveljournal.domain.Image.storage.ImageStorageService;
import com.traveljournal.global.exception.ImageDeleteException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ImageService {

	private final ImageStorageService imageStorageService;
	private final ImageInfoRepository imageInfoRepository;

	/**
	 * 프로필 이미지 업로드 및 최적화
	 */
	public String uploadProfileImage(MultipartFile imageFile, Long memberId) throws IOException {
		return imageStorageService.uploadProfileImage(imageFile, memberId);
	}

	/**
	 * 이미지 정보를 데이터베이스에 저장
	 */
	public void saveImageInfo(String uploadId, String uploadFilename) {
		ImageInfo imageInfo = ImageInfo.builder()
			.uploadId(uploadId)
			.uploadFilename(uploadFilename)
			.build();

		imageInfoRepository.save(imageInfo);
	}

	/**
	 * 이미지 파일 유효성 검사
	 */
	public void validateImageFile(MultipartFile file) {
		if (file.isEmpty()) {
			throw new IllegalArgumentException("빈 파일은 업로드할 수 없습니다.");
		}

		String contentType = file.getContentType();
		if (contentType == null || !contentType.startsWith("image/")) {
			throw new IllegalArgumentException("이미지 파일만 업로드할 수 있습니다.");
		}
	}

	/**
	 * 기존 호출부 호환용 메서드
	 */
	public String uploadToS3(byte[] imageBytes, String uploadId, String extension, boolean isResized) {
		return imageStorageService.uploadToStorage(imageBytes, uploadId, extension, isResized);
	}

	public String getDefaultProfileImageUrl() {
		return imageStorageService.getDefaultProfileImageUrl();
	}

	public String getImageUrl(String filename) {
		return imageStorageService.getImageUrl(filename);
	}

	/**
	 * 기존 호출부 호환용 메서드
	 */
	public void deleteImageFromS3(String uploadId) {
		try {
			imageStorageService.deleteImage(uploadId);
		} catch (Exception e) {
			log.error("이미지 삭제 실패: {}", uploadId, e);
			throw new ImageDeleteException("이미지 삭제에 실패했습니다: " + uploadId);
		}
	}

	/**
	 * 나중에 이름 정리할 때 사용할 공통 메서드
	 */
	public String uploadToStorage(byte[] imageBytes, String uploadId, String extension, boolean isResized) {
		return imageStorageService.uploadToStorage(imageBytes, uploadId, extension, isResized);
	}

	public void deleteImage(String uploadId) {
		deleteImageFromS3(uploadId);
	}
}