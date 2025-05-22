package com.traveljournal.domain.block.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.traveljournal.domain.block.dto.BlockResponse;
import com.traveljournal.domain.block.service.BlockService;
import com.traveljournal.global.data.ApiResponseHandler;
import com.traveljournal.global.security.util.SecurityUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/block")
@Tag(name = "Block API", description = "차단 기능 API")
public class BlockController {
	private final BlockService blockService;

	@Operation(
		summary = "회원 차단",
		description = "현재 로그인한 사용자가 특정 사용자를 차단합니다.",
		security = @SecurityRequirement(name = "bearer-key")
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "성공",
			content = @Content(mediaType = "text/plain", examples = @ExampleObject(name = "차단 성공", value = "차단 성공"))
		)
	})
	@PostMapping("/{blockedId}")
	public ResponseEntity<?> blockMember(@PathVariable Long blockedId) {
		Long currentMemberId = SecurityUtil.getCurrentMemberId();
		blockService.blockMember(currentMemberId, blockedId);
		return ApiResponseHandler.onSuccess("차단 성공");
	}

	@Operation(
		summary = "회원 차단 해제",
		description = "현재 로그인한 사용자가 차단한 회원을 차단 해제합니다. ",
		security = @SecurityRequirement(name = "bearer-key")
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "성공",
			content = @Content(mediaType = "text/plain", examples = @ExampleObject(name = "차단 해제 성공", value = "차단 해제 성공"))
		)
	})
	@DeleteMapping("/{blockedId}")
	public ResponseEntity<?> unblockMember(@PathVariable Long blockedId) {
		Long currentMemberId = SecurityUtil.getCurrentMemberId();
		blockService.unblockMember(currentMemberId, blockedId);
		return ApiResponseHandler.onSuccess("차단 해제 성공");
	}

	@Operation(
		summary = "차단한 회원 목록 조회",
		description = "현재 로그인한 회원이 차단한 회원 목록을 조회합니다.",
		security = @SecurityRequirement(name = "bearer-key")
	)
	@GetMapping("/list")
	public ResponseEntity<Page<BlockResponse>> getBlockedMembers(@ParameterObject Pageable pageable) {
		Long currentMemberId = SecurityUtil.getCurrentMemberId();
		return ApiResponseHandler.getObjectSuccess(blockService.getBlockedMembers(currentMemberId, pageable));
	}
}
