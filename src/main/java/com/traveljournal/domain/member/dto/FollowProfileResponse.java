package com.traveljournal.domain.member.dto;

import com.traveljournal.domain.member.entity.Member;
import com.traveljournal.domain.member.entity.RequestStatus;
import lombok.Builder;

@Builder
public record FollowProfileResponse(
        Long memberId,
        String nickname,
        String profileImageUrl,
        Long travelDiaryCount,
        Long placesCount
) {
    public static FollowProfileResponse of(Member member) {
        return FollowProfileResponse.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileImageUrl())
                .travelDiaryCount(36L)
                .placesCount(88L)
                .build();
    }
}
