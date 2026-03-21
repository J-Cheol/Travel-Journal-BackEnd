package com.traveljournal.global.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public class PaginationUtilsTest {

	@Test
	@DisplayName("요청한 페이지 크기만큼 데이터를 잘라 반환합니다.")
	void returnPagedData() {
		// given
		List<String> allData = List.of("A", "B", "C", "D", "E");
		PageRequest pageable = PageRequest.of(0, 2);

		// when
		Page<String> result = PaginationUtils.getPagedList(allData, pageable);

		// then
		assertEquals(2, result.getContent().size());
		assertEquals(List.of("A", "B"), result.getContent());
		assertEquals(5, result.getTotalElements());
	}

	@Test
	@DisplayName("마지막 페이지에서는 남은 데이터만 반환합니다.")
	void returnRemainingDataOnLastPage() {
		// given
		List<String> allData = List.of("A", "B", "C", "D", "E");
		PageRequest pageable = PageRequest.of(2, 2);

		// when
		Page<String> result = PaginationUtils.getPagedList(allData, pageable);

		// then
		assertEquals(1, result.getContent().size());
		assertEquals(List.of("E"), result.getContent());
		assertEquals(5, result.getTotalElements());
	}

	@Test
	@DisplayName("범위를 초과한 페이지 요청이면 빈 리스트를 반환합니다.")
	void returnEmptyPageWhenPageOutOfRange() {
		// given
		List<String> allData = List.of("A", "B", "C");
		PageRequest pageable = PageRequest.of(5, 2);

		// when
		Page<String> result = PaginationUtils.getPagedList(allData, pageable);

		// then
		assertTrue(result.getContent().isEmpty());
		assertEquals(3, result.getTotalElements());
	}

	@Test
	@DisplayName("빈 리스트가 들어오면 빈 페이지를 반환합니다.")
	void returnEmptyPageWhenListIsEmpty() {
		// given
		List<String> allData = List.of();
		PageRequest pageable = PageRequest.of(0, 2);

		// when
		Page<String> result = PaginationUtils.getPagedList(allData, pageable);

		// then
		assertTrue(result.getContent().isEmpty());
		assertEquals(0, result.getTotalElements());
	}
}
