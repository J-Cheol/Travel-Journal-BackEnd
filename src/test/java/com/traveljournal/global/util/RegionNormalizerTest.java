package com.traveljournal.global.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RegionNormalizerTest {

	@Test
	@DisplayName("지역명이 표준 지역명으로 정규화됩니다.")
	void normalizeRegion() {
		// given
		String rawRegion = "서울특별시";

		// when
		String result = RegionNormalizer.normalize(rawRegion);

		// then
		assertEquals("서울", result);
	}

	@Test
	@DisplayName("축약 지역명이 권역명으로 변환됩니다.")
	void normalizeShortRegionName() {
		// given
		String rawRegion = "경북";

		// when
		String result = RegionNormalizer.normalize(rawRegion);

		// then
		assertEquals("경상도", result);
	}

	@Test
	@DisplayName("양쪽 공백을 제거한 뒤 정규화됩니다.")
	void trimBeforeNormalize() {
		// given
		String rawRegion = "  제주특별자치도  ";

		// when
		String result = RegionNormalizer.normalize(rawRegion);

		//then
		assertEquals("제주도", result);
	}

	@Test
	@DisplayName("매핑되지 않은 지역명은 그대로 반환합니다.")
	void returnOriginalWhenNotMapped() {
		// given
		String rawRegion = "부산";

		// when
		String result = RegionNormalizer.normalize(rawRegion);

		// then
		assertEquals("부산", result);
	}

	@Test
	@DisplayName("null 입력은 null을 반환합니다.")
	void returnNullWhenInputIsNull() {
		// given
		String rawRegion = null;

		// when
		String result = RegionNormalizer.normalize(rawRegion);

		//then
		assertNull(result);
	}
}
