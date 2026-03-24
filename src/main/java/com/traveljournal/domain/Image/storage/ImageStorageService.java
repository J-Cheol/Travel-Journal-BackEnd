package com.traveljournal.domain.Image.storage;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {

	String uploadProfileImage(MultipartFile imageFile, Long memberId) throws IOException;

	String uploadToStorage(byte[] imageBytes, String uploadId, String extension, boolean isResized);

	String getDefaultProfileImageUrl();

	String getImageUrl(String filename);

	void deleteImage(String uploadId);
}