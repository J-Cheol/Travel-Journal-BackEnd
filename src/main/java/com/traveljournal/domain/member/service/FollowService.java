package com.traveljournal.domain.member.service;

import com.traveljournal.domain.member.dto.MemberProfileResponse;
import com.traveljournal.domain.member.entity.Follow;
import com.traveljournal.domain.member.entity.Member;
import com.traveljournal.domain.member.repository.FollowRepository;
import com.traveljournal.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final MemberService memberService;

    @Transactional
    public void follow(Long fromUserId, Long toUserId) {
        if (fromUserId.equals(toUserId)) {
            throw new RuntimeException("자신을 팔로우 할 수 없습니다.");
        }

        Member fromUser = memberService.findById(fromUserId);
        Member toUser = memberService.findById(toUserId);

        if(followRepository.existsByFromUserAndToUser(fromUser, toUser)) {
            throw new RuntimeException("이미 팔로우한 사용자입니다.");
        }

        Follow follow = Follow.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .build();

        followRepository.save(follow);
    }

    @Transactional
    public void unfollow(Long fromUserId, Long toUserId) {
        Member fromUser = memberService.findById(fromUserId);
        Member toUser = memberService.findById(toUserId);

        followRepository.deleteByFromUserAndToUser(fromUser, toUser);
    }

    // 내가 팔로우한 사람들
    @Transactional(readOnly = true)
    public List<MemberProfileResponse> getFollowings(Long memberId) {
        Member member = memberService.findById(memberId);
        return followRepository.findByFromUser(member).stream()
                .map(follow -> MemberProfileResponse.of(follow.getToUser()))
                .collect(Collectors.toList());
    }

    // 나를 팔로우하는 사람들
    @Transactional(readOnly = true)
    public List<MemberProfileResponse> getFollowers(Long memberId) {
        Member member = memberService.findById(memberId);
        return followRepository.findByToUser(member).stream()
                .map(follow -> MemberProfileResponse.of(follow.getFromUser()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getFollowerCount(Long memberId) {
        Member member = memberService.findById(memberId);
        return followRepository.countByToUser(member);
    }

    @Transactional(readOnly = true)
    public long getFollowingCount(Long memberId) {
        Member member = memberService.findById(memberId);
        return followRepository.countByFromUser(member);
    }

    @Transactional(readOnly = true)
    public boolean isFollowing(Long followerId, Long followingId) {
        Member follower = memberService.findById(followerId);
        Member following = memberService.findById(followingId);
        return followRepository.existsByFromUserAndToUser(follower, following);
    }

}
