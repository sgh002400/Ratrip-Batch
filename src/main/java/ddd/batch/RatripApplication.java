package ddd.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.RequiredArgsConstructor;

@EnableBatchProcessing
@EnableScheduling
@SpringBootApplication
@RequiredArgsConstructor
public class RatripApplication {

	public static void main(String[] args) {
		SpringApplication.run(RatripApplication.class, args);
	}

	private final JobLauncher jobLauncher;

	private final Job updateTotalScoreJob;

	@Scheduled(cron = "*/10 * * * * *") // 매일 자정에 실행
	public void perform() throws Exception {
		JobParameters params = new JobParametersBuilder()
			.addString("JobID", String.valueOf(System.currentTimeMillis()))
			.toJobParameters();
		jobLauncher.run(updateTotalScoreJob, params);
	}

}
