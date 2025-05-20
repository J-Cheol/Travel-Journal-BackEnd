package com.traveljournal.domain.block.dto;

import com.traveljournal.domain.member.entity.Member;
import lombok.Builder;

@Builder
public record BlockResponse(
        Long memberId,
        String nickname,
        String profileImageUrl
) {
    public static BlockResponse of(Member blocked) {
        return BlockResponse.builder()
                .memberId(blocked.getId())
                .nickname(blocked.getNickname())
                .profileImageUrl(blocked.getProfileImageUrl())
                .build();
    }
}
