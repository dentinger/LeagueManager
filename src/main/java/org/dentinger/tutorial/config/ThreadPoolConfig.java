package org.dentinger.tutorial.config;

import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class ThreadPoolConfig {

  @Bean(name = "teamProcessorThreadPool")
  public TaskExecutor getTeamProcessorThreadPool(Environment applicationProperties) {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

    taskExecutor.setAllowCoreThreadTimeOut(false);

    taskExecutor
        .setCorePoolSize(
            Integer.valueOf(applicationProperties.getProperty("teams.threads.size.core")));
    taskExecutor.setMaxPoolSize(
        Integer.valueOf(applicationProperties.getProperty("teams.threads.size.maxpool")));
    taskExecutor.setQueueCapacity(
        Integer.valueOf(applicationProperties.getProperty("teams.threads.queue.capacity")));
    taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    taskExecutor.setKeepAliveSeconds(5);
    taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
    taskExecutor.setThreadNamePrefix("teamTP-");

    return taskExecutor;
  }

  @Bean(name = "leagueProcessorThreadPool")
  public TaskExecutor getLeagueProcessorThreadPool(Environment applicationProperties) {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

    taskExecutor.setAllowCoreThreadTimeOut(false);

    taskExecutor
        .setCorePoolSize(
            Integer.valueOf(applicationProperties.getProperty("leagues.threads.size.core")));
    taskExecutor.setMaxPoolSize(
        Integer.valueOf(applicationProperties.getProperty("leagues.threads.size.maxpool")));
    taskExecutor.setQueueCapacity(
        Integer.valueOf(applicationProperties.getProperty("leagues.threads.queue.capacity")));
    taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    taskExecutor.setKeepAliveSeconds(5);
    taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
    taskExecutor.setThreadNamePrefix("leagueTP-");

    return taskExecutor;
  }

  @Bean(name = "personProcessorThreadPool")
  public TaskExecutor getPersonProcessorThreadPool(Environment applicationProperties) {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

    taskExecutor.setAllowCoreThreadTimeOut(false);

    taskExecutor
        .setCorePoolSize(
            Integer.valueOf(applicationProperties.getProperty("persons.threads.size.core")));
    taskExecutor.setMaxPoolSize(
        Integer.valueOf(applicationProperties.getProperty("persons.threads.size.maxpool")));
    taskExecutor.setQueueCapacity(
        Integer.valueOf(applicationProperties.getProperty("persons.threads.queue.capacity")));
    taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    taskExecutor.setKeepAliveSeconds(5);
    taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
    taskExecutor.setThreadNamePrefix("personTP-");

    return taskExecutor;
  }

}
