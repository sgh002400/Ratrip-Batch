package ddd.batch.job;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ddd.batch.domain.Place;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfiguration {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final EntityManagerFactory entityManagerFactory;

	@Bean
	public ItemReader<Place> itemReader() {
		JpaPagingItemReader<Place> reader = new JpaPagingItemReader<>();
		reader.setEntityManagerFactory(entityManagerFactory);
		reader.setQueryString("SELECT p FROM Place p");
		reader.setPageSize(10);
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
	public Step updateTotalScoreStep() {
		return stepBuilderFactory.get("updateTotalScoreStep")
			.<Place, Place>chunk(10)
			.reader(itemReader())
			.processor(itemProcessor())
			.writer(itemWriter())
			.build();
	}

	@Bean
	public Job updateTotalScoreJob() {
		return jobBuilderFactory.get("updateTotalScoreJob")
			.incrementer(new RunIdIncrementer())
			.start(updateTotalScoreStep())
			.build();
	}
}

