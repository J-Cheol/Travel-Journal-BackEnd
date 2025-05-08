package com.traveljournal.domain.explore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.traveljournal.domain.explore.entity.ExploreSeenJournal;

public interface ExploreSeenJournalRepository extends JpaRepository<ExploreSeenJournal, Long> {

	@Query("SELECT esj.journal.id FROM ExploreSeenJournal esj WHERE esj.member.id = :memberId")
	List<Long> findSeenJournalIdsByMemberId(@Param("memberId") Long memberId);

	@Query("SELECT esj.journal.id FROM ExploreSeenJournal esj WHERE esj.member.id = :memberId AND esj.journal.id IN :journalIds")
	List<Long> findSeenJournalIdsByMemberIdAndJournalIds(@Param("memberId") Long memberId, @Param("journalIds") List<Long> journalIds);
}