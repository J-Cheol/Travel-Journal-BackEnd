package com.traveljournal.domain.member.repository;

import com.traveljournal.domain.member.entity.Block;
import com.traveljournal.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BlockRepository extends JpaRepository<Block, Long> {
    boolean existsByBlockerAndBlocked(Member blocker, Member blocked);
    Optional<Block> findByBlockerAndBlocked(Member blocker, Member blocked);
    Page<Block> findAllByBlocker(Member blocker, Pageable pageable);
    void deleteByBlockerAndBlocked(Member blocker, Member blocked);

    @Query("SELECT b.blocked.id FROM Block b WHERE b.blocker = :blocker")
    List<Long> findBlockedMemberIdsByBlocker(@Param("blocker") Member blocker);
}
