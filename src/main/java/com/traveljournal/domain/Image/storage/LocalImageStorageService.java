package com.traveljournal.domain.Image.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.traveljournal.domain.Image.entity.ImageInfo;
import com.traveljournal.domain.Image.repository.ImageInfoRepository;
import com.traveljournal.domain.Image.util.FilenameUtil;
import com.traveljournal.global.exception.ImageDeleteException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "custom.storage.type", havingValue = "local")
@Transactional
@Slf4j
public class LocalImageStorageService implements ImageStorageService {
	private final ImageInfoRepository imageInfoRepository;

	@Value("${custom.file.upload-dir}")
	private String uploadDir;

	@Value("${custom.file.upload-path}")
	private String uploadPath;

	@Value("${custom.file.resized-path}")
	private String resizedPath;

	@Value("${custom.file.default-profile-image}")
	private String defaultProfileImage;

	@Value("${custom.file.base-url}")
	private String baseUrl;

	@Value("${custom.app.base-url}")
	private String appBaseUrl;

	// 이미지 크기 정의
	private static final int PROFILE_ORIGINAL_SIZE = 800; // 원본 이미지 최대 크기

	/**
	 * 프로필 이미지 업로드 및 최적화
	 */
	@Override
	@Transactional
	public String uploadProfileImage(MultipartFile imageFile, Long memberId) throws IOException {
		validateImageFile(imageFile);

		// 파일명 생성
		String originalFilename = imageFile.getOriginalFilename();
		String extension = FilenameUtil.getExtension(originalFilename);
		String uploadId = FilenameUtil.generateUniqueFileName("profile", memberId, extension);

		// 원본 이미지 저장
		byte[] originalImageBytes = imageFile.getBytes();
		String originalImagePath = uploadToStorage(originalImageBytes, uploadId, extension, false);

		// // 리사이즈된 이미지 생성 및 저장
		// byte[] resizedImageBytes = resizeImage(imageFile);
		// uploadToStorage(resizedImageBytes, fileName, extension, true);

		// 이미지 정보 저장
		saveImageInfo(uploadId, originalFilename);

		// 원본 이미지 경로 반환
		return originalImagePath;
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
	 * 로컬(NAS)에 이미지 업로드
	 * @param imageBytes 업로드할 이미지 바이트 배열
	 * @param uploadId 파일 이름
	 * @param extension 파일 확장자
	 * @param isResized 리사이즈된 이미지인지 여부
	 * @return 업로드된 이미지의 URL
	 */
	@Override
	public String uploadToStorage(byte[] imageBytes, String uploadId, String extension, boolean isResized) {
		// 리사이즈된 이미지는 resizedPath에, 원본 이미지는 uploadPath에 저장
		String path = isResized ? resizedPath : uploadPath;

		try {
			Path directoryPath = Paths.get(uploadDir, path);
			Files.createDirectories(directoryPath);

			Path filePath = directoryPath.resolve(uploadId);
			Files.write(filePath, imageBytes);

			return buildLocalUrl(path + "/" + uploadId);
		} catch (IOException e) {
			throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다.", e);
		}
	}

	@Override
	public String getDefaultProfileImageUrl() {
		log.info(appBaseUrl + defaultProfileImage);
		return appBaseUrl + "/" + defaultProfileImage;
	}

	@Override
	public String getImageUrl(String filename) {
		String key = uploadPath + "/" + filename;
		return buildLocalUrl(key);
	}

	@Override
	public void deleteImage(String uploadId) {
		try {
			// 로컬에서 원본 파일 삭제
			deleteLocalFile(uploadPath + "/" + uploadId);

			// 썸네일이 있다면 썸네일도 삭제
			try {
				deleteLocalFile(resizedPath + "/" + uploadId);
			} catch (Exception e) {
				log.warn("썸네일 삭제 실패 (무시): {}", uploadId);
			}

			log.info("로컬 파일 삭제 완료: {}", uploadId);
		} catch (Exception e) {
			log.error("로컬 파일 삭제 실패: {}", uploadId, e);
			throw new ImageDeleteException("로컬 파일 삭제에 실패했습니다: " + uploadId);
		}
	}

	private void deleteLocalFile(String relativePath) {
		try {
			Path filePath = Paths.get(uploadDir, relativePath);
			Files.deleteIfExists(filePath);
		} catch (IOException e) {
			throw new RuntimeException("로컬 파일 삭제 실패", e);
		}
	}

	private String buildLocalUrl(String relativePath) {
		return baseUrl + "/" + relativePath.replace("\\", "/");
	}
}