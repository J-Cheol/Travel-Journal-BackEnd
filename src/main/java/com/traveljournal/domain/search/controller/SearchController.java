package com.traveljournal.domain.search.controller;

import com.traveljournal.domain.search.dto.MemberSearchResponse;
import com.traveljournal.domain.search.service.MemberSearchService;
import com.traveljournal.global.data.ApiResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/search")
@Tag(name = "Search API", description = "사용자 검색")
public class SearchController {

    private final MemberSearchService memberSearchService;

    @GetMapping
    @Operation(
            summary = "사용자 검색",
            description = "사용자가 입력한 키워드로 다른 사용자를 검색합니다.",
            security = @SecurityRequirement(name = "bearer-key"))
    public ResponseEntity<?> searchMembers(
            @RequestParam String keyword, @PageableDefault(size = 10, sort = "nickname") Pageable pageable)
    {
        Page<MemberSearchResponse> result = memberSearchService.searchMembers(keyword, pageable);
        if(result.isEmpty()) {
            return ApiResponseHandler.onFailure("검색 결과가 없습니다.");
        }

        return ApiResponseHandler.getObjectSuccess(result);
    }
}
