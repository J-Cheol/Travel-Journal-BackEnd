package com.traveljournal.domain.journal.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class JournalOptimizationConfig {

	@Bean
	@Profile("production")
	public CommandLineRunner createRandomIndexColumn(JdbcTemplate jdbcTemplate) {
		return args -> {
			// 랜덤 인덱스 컬럼 추가
			jdbcTemplate.execute("ALTER TABLE journal ADD COLUMN IF NOT EXISTS random_index DOUBLE");

			// 기존 데이터에 랜덤값 설정
			jdbcTemplate.execute("UPDATE journal SET random_index = RAND() WHERE random_index IS NULL");

			// 랜덤 인덱스 컬럼에 인덱스 생성
			jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_journal_random ON journal (random_index)");
		};
	}
}