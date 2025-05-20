package com.traveljournal.domain.block.controller;

import com.traveljournal.domain.block.dto.BlockResponse;
import com.traveljournal.domain.block.service.BlockService;
import com.traveljournal.domain.member.service.MemberService;
import com.traveljournal.global.data.ApiResponseHandler;
import com.traveljournal.global.security.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/block")
public class BlockController {
    private final BlockService blockService;
    private final MemberService memberService;

    @Operation(
            summary = "회원 차단",
            description = "현재 로그인한 사용자가 특정 사용자를 차단합니다.",
            security = @SecurityRequirement(name = "bearer-key")
    )
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
    public ResponseEntity<Page<BlockResponse>> getBlockedMembers(Pageable pageable) {
        Long currentMemberId = SecurityUtil.getCurrentMemberId();
        return ApiResponseHandler.getObjectSuccess(blockService.getBlockedMembers(currentMemberId, pageable));
    }
}
