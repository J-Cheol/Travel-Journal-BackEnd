package com.traveljournal.domain.member.controller;

import com.traveljournal.domain.member.dto.MemberProfileResponse;
import com.traveljournal.domain.member.service.FollowService;
import com.traveljournal.global.data.ApiResponseHandler;
import com.traveljournal.global.security.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @Operation(
            summary = "Member Follow",
            description = "현재 로그인한 회원이 특정 회원을 팔로우합니다.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @PostMapping("/{memberId}")
    public ResponseEntity<?> followMember(@PathVariable("memberId") Long memberId) {
        Long currentMemberId = SecurityUtil.getCurrentMemberId();
        followService.follow(currentMemberId, memberId);
        return ApiResponseHandler.onSuccess("팔로우 성공");
    }

    @Operation(
            summary = "Member UnFollow",
            description = "현재 로그인한 회원이 특정 회원을 언팔로우합니다.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @DeleteMapping("/{memberId}")
    public ResponseEntity<?> unfollowMember(@PathVariable("memberId") Long memberId) {
        Long currentMemberId = SecurityUtil.getCurrentMemberId();
        followService.unfollow(currentMemberId, memberId);
        return ApiResponseHandler.deletedSuccess("언팔로우 성공");
    }

    @Operation(
            summary = "특정 회원의 팔로잉 목록",
            description = "해당 회원이 팔로우하고 있는 사람들의 리스트를 조회합니다.",
            security = @SecurityRequirement(name = "bearer-key")
    )

    @GetMapping("/{memberId}/followings")
    public ResponseEntity<?> getFollowings(@PathVariable("memberId") Long memberId) {
        return ApiResponseHandler.getObjectSuccess(followService.getFollowings(memberId));
    }

    @Operation(
            summary = "특정 회원의 팔로워 목록",
            description = "해당 회원을 팔로우하고 있는 사람들의 리스트를 조회합니다.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @GetMapping("/{memberId}/followers")
    public ResponseEntity<?> getFollowers(@PathVariable Long memberId) {
        return ApiResponseHandler.getObjectSuccess(followService.getFollowers(memberId));
    }

    @Operation(
            summary = "팔로우 수 및 팔로워 수 조회",
            description = "특정 회원의 팔로우/팔로워 수를 반환합니다.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @GetMapping("/{memberId}/count")
    public ResponseEntity<Map<String, Long>> getFollowCounts(@PathVariable Long memberId) {
        long followers = followService.getFollowerCount(memberId);
        long followings = followService.getFollowingCount(memberId);
        return ResponseEntity.ok(Map.of(
                "followers", followers,
                "followings", followings
        ));
    }

    @Operation(summary = "팔로우 여부 확인", description = "현재 로그인한 회원이 해당 회원을 팔로우하고 있는지 여부를 반환합니다.", security = @SecurityRequirement(name = "bearer-key"))
    @GetMapping("/{memberId}/is-following")
    public ResponseEntity<Boolean> isFollowing(@PathVariable Long memberId) {
        Long currentMemberId = SecurityUtil.getCurrentMemberId();
        return ApiResponseHandler.getObjectSuccess(followService.isFollowing(currentMemberId, memberId));
    }
}

