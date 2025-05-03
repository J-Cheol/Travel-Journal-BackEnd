package com.traveljournal.domain.member.controller;

import com.traveljournal.domain.member.dto.FollowCountResponse;
import com.traveljournal.domain.member.dto.FollowProfileResponse;
import com.traveljournal.domain.member.dto.FollowRequestResponse;
import com.traveljournal.domain.member.dto.MemberProfileResponse;
import com.traveljournal.domain.member.service.FollowService;
import com.traveljournal.global.data.ApiResponseHandler;
import com.traveljournal.global.security.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<Page<FollowProfileResponse>> getFollowings(@PathVariable("memberId") Long memberId,
                                                                     Pageable pageable) {
        return ApiResponseHandler.getObjectSuccess(followService.findFollowings(memberId, pageable));
    }

    @Operation(
            summary = "특정 회원의 팔로워 목록",
            description = "해당 회원을 팔로우하고 있는 사람들의 리스트를 조회합니다.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @GetMapping("/{memberId}/followers")
    public ResponseEntity<Page<FollowProfileResponse>> getFollowers(@PathVariable Long memberId,
                                                                    Pageable pageable) {
        return ApiResponseHandler.getObjectSuccess(followService.findFollowers(memberId, pageable));
    }

    @Operation(
            summary = "팔로우 수 및 팔로워 수 조회",
            description = "특정 회원의 팔로우/팔로워 수를 반환합니다.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @GetMapping("/{memberId}/count")
    public ResponseEntity<FollowCountResponse> getFollowCounts(@PathVariable Long memberId) {
        long followers = followService.getFollowerCount(memberId);
        long followings = followService.getFollowingCount(memberId);
        return ApiResponseHandler.getObjectSuccess(
                new FollowCountResponse(followers, followings)
        );
    }

    @Operation(
            summary = "팔로우 여부 확인",
            description = "현재 로그인한 회원이 해당 회원을 팔로우하고 있는지 여부를 반환합니다.",
            security = @SecurityRequirement(name = "bearer-key"))
    @GetMapping("/{memberId}/is-following")
    public ResponseEntity<Boolean> isFollowing(@PathVariable Long memberId) {
        Long currentMemberId = SecurityUtil.getCurrentMemberId();
        return ApiResponseHandler.getObjectSuccess(followService.isFollowing(currentMemberId, memberId));
    }

    @Operation(
            summary = "팔로우 요청 수락",
            description = "현재 로그인한 회원이 받은 팔로우 요청을 수락합니다.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @PostMapping("/requests/{followId}/accept")
    public ResponseEntity<?> acceptFollowRequest(@PathVariable Long followId) {
        Long currentMemberId = SecurityUtil.getCurrentMemberId();
        followService.acceptFollowRequest(currentMemberId, followId);
        return ApiResponseHandler.onSuccess("팔로우 요청 수락 성공");
    }

    @Operation(
            summary = "팔로우 요청 거절",
            description = "현재 로그인한 회원이 받은 팔로우 요청을 거절합니다.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @PostMapping("/requests/{followId}/reject")
    public ResponseEntity<?> rejectFollowRequest(@PathVariable Long followId) {
        Long currentMemberId = SecurityUtil.getCurrentMemberId();
        followService.rejectFollowRequest(currentMemberId, followId);
        return ApiResponseHandler.onSuccess("팔로우 요청 거절 성공");
    }

    @Operation(
            summary = "회원에게 온 팔로우 요청 목록",
            description = "현재 로그인한 회원에게 요청된 팔로우 요청 목록을 조회합니다",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @GetMapping("/requests")
    public ResponseEntity<Page<FollowRequestResponse>> getFollowRequests(Pageable pageable) {
        Long currentMemberId = SecurityUtil.getCurrentMemberId();
        return ApiResponseHandler.getObjectSuccess(followService.findFollowRequests(currentMemberId,pageable));
    }
}

