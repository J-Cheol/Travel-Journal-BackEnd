package com.traveljournal.domain.statistics.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.traveljournal.domain.statistics.entity.MemberRegionStatistics;
import com.traveljournal.domain.statistics.entity.MemberRegionStatisticsId;
import com.traveljournal.domain.statistics.repository.MemberRegionStatisticsRepository;

@ExtendWith(MockitoExtension.class)
public class MemberRegionStatisticsServiceTest {

	@Mock
	private MemberRegionStatisticsRepository memberRegionStatisticsRepository;

	@InjectMocks
	private MemberRegionStatisticsService memberRegionStatisticsService;

	@Test
	@DisplayName("기존 지역 통계가 있으면 여행일지 수를 1 증가시킵니다.")
	void increaseTravelDiaryCountWhenStatisticsExists() {
		// given
		Long memberId = 1L;
		String rawRegion = "경북";
		MemberRegionStatisticsId id = new MemberRegionStatisticsId(memberId, "경상도");
		MemberRegionStatistics statistics = new MemberRegionStatistics(id, 3L, 0L);

		when(memberRegionStatisticsRepository.findById(id)).thenReturn(Optional.of(statistics));

		// when
		memberRegionStatisticsService.increaseTravelDiaryCount(memberId, rawRegion);

		// then
		assertEquals(4L, statistics.getTravelDiaryCount());
	}

	@Test
	@DisplayName("기존 지역 통계가 없으면 새로 저장한 뒤 여행일지 수를 증가시킵니다.")
	void createAndIncreaseTravelDiaryCountWhenStatisticsDoesNotExist() {
		// given
		Long memberId = 1L;
		String rawRegion = "서울특별시";
		MemberRegionStatisticsId id = new MemberRegionStatisticsId(memberId, "서울");
		MemberRegionStatistics savedStatistics = new MemberRegionStatistics(id, 0L, 0L);

		when(memberRegionStatisticsRepository.findById(id)).thenReturn(Optional.empty());
		when(memberRegionStatisticsRepository.save(any(MemberRegionStatistics.class))).thenReturn(savedStatistics);

		// when
		memberRegionStatisticsService.increaseTravelDiaryCount(memberId, rawRegion);

		// then
		assertEquals(1L, savedStatistics.getTravelDiaryCount());
		verify(memberRegionStatisticsRepository).save(any(MemberRegionStatistics.class));
	}

	@Test
	@DisplayName("여행일지 수 감소 후 0 이하면 통계를 삭제합니다.")
	void deleteStatisticsWhenTravelDiaryCountBecomesZero() {
		// given
		Long memberId = 1L;
		String rawRegion = "제주";
		MemberRegionStatisticsId id = new MemberRegionStatisticsId(memberId, "제주도");
		MemberRegionStatistics statistics = new MemberRegionStatistics(id, 1L, 0L);

		when(memberRegionStatisticsRepository.findById(id)).thenReturn(Optional.of(statistics));

		// when
		memberRegionStatisticsService.decreaseTravelDiaryCount(memberId, rawRegion);

		// then
		assertEquals(0L, statistics.getTravelDiaryCount());
		verify(memberRegionStatisticsRepository).delete(statistics);
	}
}
