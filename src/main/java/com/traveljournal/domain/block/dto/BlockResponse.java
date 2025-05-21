package com.traveljournal.domain.block.dto;

import com.traveljournal.domain.member.entity.Member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record BlockResponse(
	@Schema(example = "1")
	Long memberId,
	@Schema(example = "도요새")
	String nickname,
	@Schema(example = "https://travel-journal-s3.s3.amazonaws.com/default/profile/default1.png")
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
