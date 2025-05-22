package com.traveljournal.domain.explore.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.traveljournal.domain.explore.dto.ExploreJournalFeedResponse;
import com.traveljournal.domain.explore.entity.ExploreSeenJournal;
import com.traveljournal.domain.explore.repository.ExploreSeenJournalRepository;
import com.traveljournal.domain.journal.entity.Journal;
import com.traveljournal.domain.journal.repository.JournalRepository;
import com.traveljournal.domain.member.entity.Member;
import com.traveljournal.domain.follow.repository.FollowRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExploreFeedService {

	private final FollowRepository followRepository;
	private final JournalRepository journalRepository;
	private final ExploreSeenJournalRepository exploreSeenJournalRepository;

	@Transactional(readOnly = true)
	public Page<ExploreJournalFeedResponse> getExploreFeed(Long memberId, Pageable pageable) {
		List<Long> followingIds = followRepository.findAcceptedToMemberIdsByFromMemberId(memberId);

		// 1. 팔로우한 회원이 없으면 바로 랜덤 피드
		if (followingIds == null || followingIds.isEmpty()) {
			return getOptimizedRandomFeed(memberId, List.of(), List.of(), pageable);
		}

		// 2. 이미 본 일지 목록 조회
		List<Long> seenJournalIds = exploreSeenJournalRepository.findSeenJournalIdsByMemberId(memberId);

		// 3. 페이징 최적화: 필요한 ID만 먼저 조회
		Page<Long> journalIdPage;
		if (seenJournalIds == null || seenJournalIds.isEmpty()) {
			journalIdPage = journalRepository.findIdsByMemberIdInOrderByCreatedAtDesc(followingIds, pageable);
		} else {
			journalIdPage = journalRepository.findIdsByMemberIdInAndIdNotInOrderByCreatedAtDesc(
				followingIds, seenJournalIds, pageable);
		}

		// 4. 팔로우한 회원의 읽지 않은 게시글이 없으면 랜덤 피드
		if (journalIdPage.isEmpty()) {
			return getOptimizedRandomFeed(memberId, followingIds, seenJournalIds, pageable);
		}

		// 5. fetch join으로 필요한 데이터만 한 번에 조회
		List<Journal> journals = journalRepository.findAllByIdInFetchJoin(journalIdPage.getContent());

		// 6. ID 기준으로 정렬하여 순서 보장
		Map<Long, Journal> journalMap = journals.stream()
			.collect(Collectors.toMap(Journal::getId, j -> j));

		List<ExploreJournalFeedResponse> content = journalIdPage.getContent().stream()
			.map(journalMap::get)
			.filter(Objects::nonNull)
			.map(j -> ExploreJournalFeedResponse.of(j, j.getMember()))
			.toList();

		return new PageImpl<>(content, pageable, journalIdPage.getTotalElements());
	}

	private Page<ExploreJournalFeedResponse> getOptimizedRandomFeed(
		Long memberId, List<Long> followingIds, List<Long> seenJournalIds, Pageable pageable) {

		int limit = pageable.getPageSize();
		List<Long> notFollowMemberIds = (followingIds == null || followingIds.isEmpty())
			? List.of(memberId)
			: Stream.concat(followingIds.stream(), Stream.of(memberId)).toList();

		// 7. 최적화된 랜덤 조회 사용
		List<Long> randomJournalIds;
		if (notFollowMemberIds.size() == 1 && notFollowMemberIds.contains(memberId)) {
			if (seenJournalIds == null || seenJournalIds.isEmpty()) {
				randomJournalIds = journalRepository.findOptimizedRandomAll(limit);
			} else {
				randomJournalIds = journalRepository.findOptimizedRandomIdsByMemberIdNotIn(
					List.of(memberId), seenJournalIds, limit
				);
			}
		} else {
			randomJournalIds = journalRepository.findOptimizedRandomIdsByMemberIdNotIn(
				notFollowMemberIds, seenJournalIds == null ? List.of() : seenJournalIds, limit
			);
		}

		// 8. 최적화 실패 시
		if (randomJournalIds.isEmpty()) {
			if (notFollowMemberIds.size() == 1 && notFollowMemberIds.contains(memberId)) {
				if (seenJournalIds == null || seenJournalIds.isEmpty()) {
					randomJournalIds = journalRepository.findRandomAll(limit);
				} else {
					randomJournalIds = journalRepository.findRandomIdsByMemberIdNotInAndIdNotIn(
						List.of(memberId), seenJournalIds, limit
					);
				}
			} else {
				randomJournalIds = journalRepository.findRandomIdsByMemberIdNotInAndIdNotIn(
					notFollowMemberIds, seenJournalIds == null ? List.of() : seenJournalIds, limit
				);
			}
		}

		// 9. 데이터 한 번에 조회
		List<Journal> journals = journalRepository.findAllByIdInFetchJoin(randomJournalIds);

		List<ExploreJournalFeedResponse> content = journals.stream()
			.map(j -> ExploreJournalFeedResponse.of(j, j.getMember()))
			.toList();

		// 10. 랜덤 데이터는 정확한 총 개수를 알기 어려움
		return new PageImpl<>(content, pageable,
			content.size() < limit ? content.size() : pageable.getOffset() + content.size() + 1);
	}

	@Transactional
	public void markJournalsAsSeen(Long memberId, List<Long> journalIds) {
		// 이미 본 일지 필터링하여 불필요한 쿼리 감소
		List<Long> alreadySeen = exploreSeenJournalRepository.findSeenJournalIdsByMemberIdAndJournalIds(memberId,
			journalIds);

		List<Long> toSave = journalIds.stream()
			.filter(id -> !alreadySeen.contains(id))
			.toList();

		if (!toSave.isEmpty()) {
			// 벌크 삽입으로 쿼리 최소화
			List<ExploreSeenJournal> entities = toSave.stream()
				.map(journalId -> ExploreSeenJournal.builder()
					.member(Member.builder().id(memberId).build())
					.journal(Journal.builder().id(journalId).build())
					.seenAt(LocalDateTime.now())
					.build())
				.toList();

			exploreSeenJournalRepository.saveAll(entities);
		}
	}

	@Scheduled(cron = "0 0 3 * * *")
	@Transactional
	public void deleteOldSeenJournals() {
		// 주기적으로 오래된 데이터 정리
		LocalDateTime cutoff = LocalDateTime.now().minusDays(3);
		exploreSeenJournalRepository.deleteAllBySeenAtBefore(cutoff);
	}
}
