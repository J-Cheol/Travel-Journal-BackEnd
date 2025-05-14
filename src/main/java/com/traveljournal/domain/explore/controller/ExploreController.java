package com.traveljournal.domain.explore.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.traveljournal.domain.explore.dto.ExploreJournalFeedResponse;
import com.traveljournal.domain.explore.service.ExploreFeedService;
import com.traveljournal.domain.journal.dto.JournalIdListRequest;
import com.traveljournal.global.data.ApiResponseHandler;
import com.traveljournal.global.security.util.SecurityUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/explore")
@Tag(name = "Explore API")
public class ExploreController {

	private final ExploreFeedService exploreFeedService;

	@Operation(
		summary = "Explore Journals",
		description = """
			탐험하기 여행일지 리스트입니다.
			<br> 팔로우한 회원이 존재하고, 팔로우한 회원이 올린 게시물을 읽지 않았다면 최근에 올린 게시글 위주로 보여줍니다.
			<br> 팔로우한 회원이 존재하지만 게시글을 모두 읽었거나 팔로우한 회원의 게시글이 없다면 비팔로우 회원의 게시글을 랜덤하게 보여줍니다.
			<br>  팔로우한 회원이 없으면 비팔로우 회원의 게시글을 랜덤하게 보여줍니다.""",
		security = @SecurityRequirement(name = "bearer-key")
	)
	@GetMapping("/journals/feed")
	public ResponseEntity<Page<ExploreJournalFeedResponse>> findFollowingJournalFeed(
		@ParameterObject @PageableDefault Pageable pageable
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		Page<ExploreJournalFeedResponse> result = exploreFeedService.getExploreFeed(memberId, pageable);

		return ApiResponseHandler.getObjectSuccess(result);
	}

	@Operation(
		summary = "mark Journal as seen",
		description = "탐험하기 여행일지를 보았을때 기능입니다.",
		security = @SecurityRequirement(name = "bearer-key")
	)
	@PostMapping("/journals/seen")
	public ResponseEntity<?> markJournalAsSeen(
		@RequestBody JournalIdListRequest request
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		exploreFeedService.markJournalsAsSeen(memberId, request.journalIds());
		return ApiResponseHandler.onSuccess("");
	}
}
