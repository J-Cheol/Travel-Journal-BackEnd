package com.traveljournal.domain.member.dto;

import com.traveljournal.domain.member.entity.Follow;
import com.traveljournal.domain.member.entity.Member;
import com.traveljournal.domain.member.entity.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
public record FollowRequestResponse(
        Long followId,
        Long memberId,
        String nickname,
        String profileImageUrl,
        Long travelDiaryCount,
        Long placesCount,
        RequestStatus requestStatus
) {
    public static FollowRequestResponse of(Follow follow) {
        Member fromMember = follow.getFromMember();
        return FollowRequestResponse.builder()
                .followId(follow.getId()) // 여기에 followId 추가
                .memberId(fromMember.getId())
                .nickname(fromMember.getNickname())
                .profileImageUrl(fromMember.getProfileImageUrl())
                .travelDiaryCount(36L)
                .placesCount(88L)
                .requestStatus(follow.getRequestStatus())
                .build();
    }
}
