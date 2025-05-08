package com.traveljournal.domain.journal.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record JournalIdListRequest(
	@Schema(description = "읽은 여행일지 ID 리스트", example = "[1,2,3]")
	List<Long> journalIds
) {}