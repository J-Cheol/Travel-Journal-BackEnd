package com.traveljournal.domain.member.repository;

import com.traveljournal.domain.member.entity.Follow;
import com.traveljournal.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    // 내가 팔로우 하는 사람들
    Page<Follow> findByFromMember(Member fromMember, Pageable pageable);
    // 나를 팔로우 하는 사람들
    Page<Follow> findByToMember(Member toMember, Pageable pageable);
    long countByFromMember(Member fromMember);
    long countByToMember(Member toMember);
    boolean existsByFromMemberIdAndToMemberId(Long fromfromUserMemberId, Long toMemberId);
    void deleteByFromMemberIdAndToMemberId(Long fromMember, Long toMember);
}
