package com.traveljournal.domain.member.repository;

import com.traveljournal.domain.member.entity.Follow;
import com.traveljournal.domain.member.entity.Member;
import com.traveljournal.domain.member.entity.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    // 내가 팔로우 하는 사람들
    Page<Follow> findByFromMemberAndRequestStatus(Member fromMember, RequestStatus status, Pageable pageable);
    // 나를 팔로우 하는 사람들
    Page<Follow> findByToMemberAndRequestStatus(Member toMember, RequestStatus status, Pageable pageable);
    long countByFromMemberAndRequestStatus(Member fromMember, RequestStatus status);
    long countByToMemberAndRequestStatus(Member toMember, RequestStatus status);
    boolean existsByFromMemberIdAndToMemberId(Long fromMemberId, Long toMemberId);
    boolean existsByFromMemberIdAndToMemberIdAndRequestStatus(Long fromMemberId, Long toMemberId, RequestStatus requestStatus);
    void deleteByFromMemberIdAndToMemberId(Long fromMember, Long toMember);
    Page<Follow> findAllByToMemberIdAndRequestStatus(Long toMemberId, RequestStatus requestStatus, Pageable pageable);
    Optional<Follow> findByIdAndToMemberId(Long followId, Long toMemberId);
    Optional<Follow> findByFromMemberIdAndToMemberIdAndRequestStatus(Long fromMemberId, Long toMemberId, RequestStatus requestStatus);
}
