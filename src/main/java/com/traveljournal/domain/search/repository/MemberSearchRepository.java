package com.traveljournal.domain.search.repository;

import com.traveljournal.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberSearchRepository extends JpaRepository<Member, Long> {
    Page<Member> findByNicknameContaining(String nickname, Pageable pageable);
    @Query("""
        SELECT m FROM Member m
        WHERE m.nickname LIKE %:nickname%
        AND m.id <> :currentMemberId
        AND m.id NOT IN (
            SELECT b.blocked.id FROM Block b WHERE b.blocker.id = :currentMemberId
        )
    """)
    Page<Member> findByNicknameContainingAndIdNotIn(@Param("nickname") String nickname, @Param("currentMemberId") Long currentMemberId, Pageable pageable);
}
