package com.traveljournal.domain.member.service;

import com.traveljournal.domain.member.dto.FollowProfileResponse;
import com.traveljournal.domain.member.entity.Follow;
import com.traveljournal.domain.member.entity.Member;
import com.traveljournal.domain.member.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final MemberService memberService;
    private Member findMemberById(Long id) {return memberService.findById(id);}
    @Transactional
    public void follow(Long fromMemberId, Long toMemberId) {
        if (fromMemberId.equals(toMemberId)) {
            throw new RuntimeException("자신을 팔로우 할 수 없습니다.");
        }

        if(followRepository.existsByFromMemberIdAndToMemberId(fromMemberId, toMemberId)) {
            throw new RuntimeException("이미 팔로우한 사용자입니다.");
        }
        Member fromMember = findMemberById(fromMemberId);
        Member toMember = findMemberById(toMemberId);

        Follow follow = Follow.builder()
                .fromMember(fromMember)
                .toMember(toMember)
                .build();

        followRepository.save(follow);
    }

    @Transactional
    public void unfollow(Long fromMemberId, Long toMemberId) {
        followRepository.deleteByFromMemberIdAndToMemberId(fromMemberId, toMemberId);
    }

    // 내가 팔로우한 사람들
    @Transactional(readOnly = true)
    public Page<FollowProfileResponse> findFollowings(Long memberId, Pageable pageable) {
        Member member = findMemberById(memberId);
        return followRepository.findByFromMember(member, pageable)
                .map(follow -> FollowProfileResponse.of(follow.getToMember()));
    }

    // 나를 팔로우하는 사람들
    @Transactional(readOnly = true)
    public Page<FollowProfileResponse> findFollowers(Long memberId, Pageable pageable) {
        Member member = findMemberById(memberId);
        return followRepository.findByToMember(member, pageable)
                .map(follow -> FollowProfileResponse.of(follow.getFromMember()));
    }

    @Transactional(readOnly = true)
    public long getFollowerCount(Long memberId) {
        Member member = findMemberById(memberId);
        return followRepository.countByToMember(member);
    }

    @Transactional(readOnly = true)
    public long getFollowingCount(Long memberId) {
        Member member = findMemberById(memberId);
        return followRepository.countByFromMember(member);
    }

    @Transactional(readOnly = true)
    public boolean isFollowing(Long followerId, Long followingId) {
        return followRepository.existsByFromMemberIdAndToMemberId(followerId, followingId);
    }

}
