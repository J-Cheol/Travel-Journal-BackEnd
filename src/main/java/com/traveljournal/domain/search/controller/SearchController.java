package com.traveljournal.domain.search.controller;

import com.traveljournal.domain.search.dto.MemberSearchResponse;
import com.traveljournal.domain.search.service.MemberSearchService;
import com.traveljournal.global.data.ApiResponseHandler;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/search")
@Tag(name = "Search API", description = "사용자 검색")
public class SearchController {

    private final MemberSearchService memberSearchService;

    @GetMapping
    public ResponseEntity<?> searchMembers(@RequestParam String keyword,
                                           @PageableDefault(size = 10, sort = "nickname") Pageable pageable)
    {
        Page<MemberSearchResponse> result = memberSearchService.searchMembers(keyword, pageable);
        if(result.isEmpty()) {
            return ApiResponseHandler.onFailure("검색 결과가 없습니다.");
        }

        return ApiResponseHandler.getObjectSuccess(result);
    }
}
