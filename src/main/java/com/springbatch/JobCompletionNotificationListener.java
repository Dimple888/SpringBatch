package com.springbatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
//The JobCompletionNotificationListener listens for when a job is BatchStatus.COMPLETED and then uses JdbcTemplate to inspect the results.
public class JobCompletionNotificationListener implements JobExecutionListener {
	
	private static Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);
	
	private final JdbcTemplate jdbcTemplate;
	
	public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = new JdbcTemplate();
		
		
	}
	
	@Override
	public void afterJob(JobExecution jobExecution) {
		
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
			
			log.info("Batch Completed,Time to verify results");
			
			jdbcTemplate.query("SELECT first_name,last_name FROM people", new DataClassRowMapper<>(Person.class))
			.forEach(person -> log.info("found {} in db",person));
		}
		
	}

}
