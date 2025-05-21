package com.traveljournal.domain.search.service;

import com.traveljournal.domain.member.dto.MemberProfileResponse;
import com.traveljournal.domain.member.entity.Member;
import com.traveljournal.domain.block.repository.BlockRepository;
import com.traveljournal.domain.search.dto.MemberSearchResponse;
import com.traveljournal.domain.search.repository.MemberSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberSearchService {

    private final MemberSearchRepository memberSearchRepository;
    private final BlockRepository blockRepository;

    @Transactional(readOnly = true)
    public Page<MemberSearchResponse> searchMembers(String keyword, Pageable pageable) {

        Page<Member> membersPage = memberSearchRepository.findByNicknameContaining(keyword, pageable);

        return membersPage.map(member ->
                MemberSearchResponse.of(
                        member.getId(),
                        MemberProfileResponse.of(member)
                )
        );
    }

    @Transactional(readOnly = true)
    public Page<MemberSearchResponse> searchByNickname(String keyword, Pageable pageable, Long currentMemberId) {

        // currentMemberId를 넘겨서 repository 쿼리에서 차단 회원 자동 제외
        Page<Member> members = memberSearchRepository.findByNicknameContainingAndIdNotIn(keyword, currentMemberId, pageable);

        return members.map(member -> {
            MemberProfileResponse profile = MemberProfileResponse.of(member);
            return MemberSearchResponse.of(member.getId(), profile);
        });
    }

}
