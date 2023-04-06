package ddd.batch.job;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;

import ddd.batch.domain.Place;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfiguration {
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final EntityManagerFactory entityManagerFactory;
	private final JobLauncher jobLauncher;
	private final Job updateTotalScoreJob;

	@Bean
	public ItemReader<Place> itemReader() {
		JpaPagingItemReader<Place> reader = new JpaPagingItemReader<>();
		reader.setEntityManagerFactory(entityManagerFactory);
		reader.setQueryString("SELECT p FROM Place p");
		reader.setPageSize(100);
		return reader;
	}

	@Bean
	public ItemProcessor<Place, Place> itemProcessor() {
		return place -> {
			place.decreaseTotalScore((long)(place.getTotalScore() * 0.9));
			return place;
		};
	}

	@Bean
	public ItemWriter<Place> itemWriter() {
		JpaItemWriter<Place> writer = new JpaItemWriter<>();
		writer.setEntityManagerFactory(entityManagerFactory);
		return writer;
	}

	@Bean
	public TaskExecutor taskExecutor() {
		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
		taskExecutor.setConcurrencyLimit(5); // 동시에 실행할 작업의 수
		return taskExecutor;
	}

	@Bean
	public Step updateTotalScoreStep() {
		return stepBuilderFactory.get("updateTotalScoreStep")
			.<Place, Place>chunk(100)
			.reader(itemReader())
			.processor(itemProcessor())
			.writer(itemWriter())
			.faultTolerant()
			.retryLimit(3) // 실패한 job 롤백 후 재시도 (최대 3번)
			.taskExecutor(taskExecutor()) // 병렬 처리
			.throttleLimit(5) // 동시에 실행할 작업의 수
			.build();
	}

	@Bean
	public Job updateTotalScoreJob() {
		return jobBuilderFactory.get("updateTotalScoreJob")
			.incrementer(new RunIdIncrementer())
			.start(updateTotalScoreStep())
			.build();
	}

	@Scheduled(cron = "0 0 5 * * *") // "*/10 * * * * *" -> 10초에 한 번씩 실행
	public void perform() throws Exception {
		JobParameters params = new JobParametersBuilder()
			.addString("JobID", String.valueOf(System.currentTimeMillis()))
			.toJobParameters();
		jobLauncher.run(updateTotalScoreJob, params);
	}
}

