package org.dentinger.tutorial.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolConfig {

  public ThreadFactory getTeamThreadFactory() {
    final ThreadFactory threadFactory = new ThreadFactoryBuilder()
        .setNameFormat("teamLoader-%d")
        .setDaemon(true)
        .build();

    return threadFactory;
  }

  public ThreadFactory getLeagueThreadFactory() {
    return new ThreadFactoryBuilder()
        .setNameFormat("leagueLoader-%d")
        .setDaemon(true)
        .build();

  }

  @Bean(name = "teamProcessorThreadPool")
  public ThreadPoolTaskExecutor getTeamProcessorThreadPool(Environment applicationProperties) {
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
    taskExecutor.setThreadFactory(getTeamThreadFactory());
    taskExecutor.setWaitForTasksToCompleteOnShutdown(true);

    return taskExecutor;
  }

  @Bean(name = "leagueProcessorThreadPool")
  public ThreadPoolTaskExecutor getLeagueProcessorThreadPool(Environment applicationProperties) {
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
    taskExecutor.setThreadFactory(getLeagueThreadFactory());
    taskExecutor.setWaitForTasksToCompleteOnShutdown(true);

    return taskExecutor;
  }

  @Bean(name = "personProcessorThreadPool")
  public ThreadPoolTaskExecutor getPersonProcessorThreadPool(Environment applicationProperties) {
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
    taskExecutor.setThreadFactory(getLeagueThreadFactory());
    taskExecutor.setWaitForTasksToCompleteOnShutdown(true);

    return taskExecutor;
  }

}
