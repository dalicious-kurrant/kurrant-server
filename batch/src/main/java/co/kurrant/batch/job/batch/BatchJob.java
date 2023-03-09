package co.kurrant.batch.job.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchJob {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean(name = "batchJob1")
    public Job batchJob1() {
        return jobBuilderFactory.get("BatchJob1")
                .start()
                .build;
    }

    public Step batchJob1_step1() {
        return stepBuilderFactory.get("batchJob1_step1")
                .tasklet()
                .build();
    }
}
