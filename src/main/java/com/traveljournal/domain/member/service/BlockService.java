package com.traveljournal.domain.member.service;

import com.traveljournal.domain.member.dto.BlockResponse;
import com.traveljournal.domain.member.entity.Block;
import com.traveljournal.domain.member.entity.Member;
import com.traveljournal.domain.member.repository.BlockRepository;
import com.traveljournal.global.exception.BlockBadRequestException;
import com.traveljournal.global.exception.FollowBadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlockService {
    private final BlockRepository blockRepository;
    private final MemberService memberService;
    private Member findMemberById(Long id) {return memberService.findById(id);}
    @Transactional
    public void blockMember(Long blockerId, Long blockedId) {

        Member blocker = findMemberById(blockerId);
        Member blocked = findMemberById(blockedId);

        if (blockRepository.existsByBlockerAndBlocked(blocker, blocked)) {
            throw new BlockBadRequestException("이미 차단한 사용자입니다.");
        }

        Block block = Block.builder()
                .blocker(blocker)
                .blocked(blocked)
                .build();

        blockRepository.save(block);
    }

    public void unblockMember(Long blockerId, Long blockedId) {
        Member blocker = findMemberById(blockerId);
        Member blocked = findMemberById(blockedId);

        Block block = blockRepository.findByBlockerAndBlocked(blocker, blocked)
                .orElseThrow(()-> new BlockBadRequestException("차단 내역이 없습니다."));

        blockRepository.delete(block);
    }

    @Transactional(readOnly = true)
    public Page<BlockResponse> getBlockedMembers(Long blockerId, Pageable pageable) {
        Member blocker = findMemberById(blockerId);
        return blockRepository.findAllByBlocker(blocker, pageable)
                .map(block -> BlockResponse.of(block.getBlocked()));
    }

    @Transactional(readOnly = true)
    public boolean isBlocked(Member viewer, Member target) {
        return blockRepository.existsByBlockerAndBlocked(viewer, target) ||
                blockRepository.existsByBlockerAndBlocked(target, viewer);
    }
}