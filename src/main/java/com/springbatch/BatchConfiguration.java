package com.springbatch;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.PathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration // to configure the batch we need a configuration class create beans of
				// reader,processor,writer
public class BatchConfiguration {

	@Bean
	public FlatFileItemReader<Person> reader() {

		return new FlatFileItemReaderBuilder<Person>().name("personItemReader")
				.resource(new PathResource("C:\\Users\\Dimple Sai Naveena\\Workspaces\\learnSpring\\SpringBatch\\src\\main\\resources\\sample-data.csv")).delimited().names("firstName", "lastName")
				.targetType(Person.class).build();
	}

	@Bean
	public PersonItemProcessor processor() {

		return new PersonItemProcessor();
	}

	@Bean
	public JdbcBatchItemWriter<Person> writer(DataSource dataSource) {

		return new JdbcBatchItemWriterBuilder<Person>()
				.sql("INSERT INTO people(first_name,last_name) VALUES (:firstName,:lastName)").dataSource(dataSource)
				.beanMapped().build();

	}
	
	@Bean //DataSourceTransactionManager is implementation of PlatformTransactionManager
	public PlatformTransactionManager transactionManager(DataSource dataSource) {
	    return new DataSourceTransactionManager(dataSource);
	}

	// each job has steps to do we define step now -> in each step we define 
	//how many chunks of data to be written,we use reader,processor,writer
	//chunk is here generic method so mention i/p and o/p of each “chunk” of processing and lines up with ItemReader<Person> and ItemWriter<Person>.

	@Bean
	public Step step1(JobRepository jobRepository, DataSourceTransactionManager dataSourceTransactionManager,
			FlatFileItemReader<Person> reader, PersonItemProcessor processor, JdbcBatchItemWriter<Person> writer) {

		
		return new StepBuilder("step1",jobRepository)
				.<Person,Person> chunk(3,dataSourceTransactionManager)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.build();
	}
	
	@Bean 
	public Job importUserJob(JobRepository jobRepository,Step step1,JobCompletionNotificationListener listener) {
		
		return new JobBuilder("importUserJob",jobRepository)
		.listener(listener)
		.start(step1)
		.build();
		
	}

}
