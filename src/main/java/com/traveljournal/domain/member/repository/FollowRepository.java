package com.traveljournal.domain.member.repository;

import com.traveljournal.domain.member.entity.Follow;
import com.traveljournal.domain.member.entity.FollowStatus;
import com.traveljournal.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    // 내가 팔로우 하는 사람들
    List<Follow> findByFromUser(Member fromUser);
    // 나를 팔로우 하는 사람들
    List<Follow> findByToUser(Member toUser);
    long countByFromUser(Member fromUser);
    long countByToUser(Member toUser);
    boolean existsByFromUserIdAndToUserId(Long fromUserId, Long toUserId);
    void deleteByFromUserIdAndToUserId(Long fromUser, Long toUser);

}
