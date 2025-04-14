package com.traveljournal.domain.search.service;

import com.traveljournal.domain.member.dto.MemberProfileResponse;
import com.traveljournal.domain.member.entity.Member;
import com.traveljournal.domain.search.dto.MemberSearchResponse;
import com.traveljournal.domain.search.repository.MemberSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberSearchService {

    private final MemberSearchRepository memberSearchRepository;

    @Transactional(readOnly = true)
    public Page<MemberSearchResponse> searchMembers(String keyword, Pageable pageable) {

        if (keyword == null || keyword.trim().isEmpty())
            throw new IllegalArgumentException("검색어는 비어 있을 수 없습니다.");

        Page<Member> membersPage = memberSearchRepository.findByNicknameContaining(keyword, pageable);

        return membersPage.map(member ->
                MemberSearchResponse.of(
                        member.getId(),
                        MemberProfileResponse.of(member) // 여기서 바로 사용!
                )
        );
    }
}
