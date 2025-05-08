package com.traveljournal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@EnableFeignClients
@EnableScheduling
@SpringBootApplication
public class TravelJournalApplication {

	public static void main(String[] args) {
		SpringApplication.run(TravelJournalApplication.class, args);
	}

}
