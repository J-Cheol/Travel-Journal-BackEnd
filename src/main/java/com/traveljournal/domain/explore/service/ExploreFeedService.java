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
import com.traveljournal.domain.member.repository.FollowRepository;

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

		List<Long> seenJournalIds = exploreSeenJournalRepository.findSeenJournalIdsByMemberId(memberId);

		Page<Long> followedJournalIds = (seenJournalIds == null || seenJournalIds.isEmpty())
			? journalRepository.findIdsByMemberIdInOrderByCreatedAtDesc(followingIds, pageable)
			: journalRepository.findIdsByMemberIdInAndIdNotInOrderByCreatedAtDesc(followingIds, seenJournalIds, pageable);

		List<Journal> followedJournals = followedJournalIds.isEmpty()
			? List.of()
			: journalRepository.findAllByIdInFetchJoin(followedJournalIds.getContent());

		Map<Long, Journal> journalMap = followedJournals.stream()
			.collect(Collectors.toMap(Journal::getId, j -> j));
		List<Journal> sortedFollowedJournals = followedJournalIds.getContent().stream()
			.map(journalMap::get)
			.filter(Objects::nonNull)
			.toList();

		List<Long> notFollowMemberIds = Stream.concat(followingIds.stream(), Stream.of(memberId)).toList();
		int limit = pageable.getPageSize();

		List<Long> notFollowedJournalIds = (seenJournalIds == null || seenJournalIds.isEmpty())
			? journalRepository.findRandomIdsByMemberIdNotInAndIdNotIn(notFollowMemberIds, List.of(), limit)
			: journalRepository.findRandomIdsByMemberIdNotInAndIdNotIn(notFollowMemberIds, seenJournalIds, limit);

		List<Journal> notFollowedJournals = notFollowedJournalIds.isEmpty()
			? List.of()
			: journalRepository.findAllByIdInFetchJoin(notFollowedJournalIds);

		List<ExploreJournalFeedResponse> feed = Stream.concat(
			sortedFollowedJournals.stream().map(j -> ExploreJournalFeedResponse.of(j, j.getMember())),
			notFollowedJournals.stream().map(j -> ExploreJournalFeedResponse.of(j, j.getMember()))
		).toList();

		return new PageImpl<>(feed, pageable, followedJournalIds.getTotalElements() + notFollowedJournalIds.size());
	}

	@Transactional
	public void markJournalsAsSeen(Long memberId, List<Long> journalIds) {
		List<Long> alreadySeen = exploreSeenJournalRepository.findSeenJournalIdsByMemberIdAndJournalIds(memberId, journalIds);

		List<Long> toSave = journalIds.stream()
			.filter(id -> !alreadySeen.contains(id))
			.toList();

		List<ExploreSeenJournal> entities = toSave.stream()
			.map(journalId -> ExploreSeenJournal.builder()
				.member(Member.builder().id(memberId).build())
				.journal(Journal.builder().id(journalId).build())
				.seenAt(LocalDateTime.now())
				.build())
			.toList();

		if (!entities.isEmpty()) {
			exploreSeenJournalRepository.saveAll(entities);
		}
	}

	@Scheduled(cron = "0 0 3 * * *")
	@Transactional
	public void deleteOldSeenJournals() {
		LocalDateTime cutoff = LocalDateTime.now().minusDays(3);
		exploreSeenJournalRepository.deleteAllBySeenAtBefore(cutoff);
	}
}
