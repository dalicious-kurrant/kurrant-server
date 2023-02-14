package com.kurrant.batch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JobConfiguration {
    private final JobRepository jobRepository;

    @Bean
    public Job job1(Step step) {
        return new JobBuilder("job1", jobRepository)
                .start(step)
                .build();
    }

    @Bean
    public Step step() {

    }
}
