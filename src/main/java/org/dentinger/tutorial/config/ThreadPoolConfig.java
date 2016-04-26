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

  @Bean(name = "teamProcessorThreadPool")
  public ThreadPoolTaskExecutor getFileProcessorThreadPool(Environment applicationProperties) {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

    taskExecutor.setAllowCoreThreadTimeOut(false);

    taskExecutor
        .setCorePoolSize(
            Integer.valueOf(applicationProperties.getProperty("team.threads.size.core")));
    taskExecutor.setMaxPoolSize(
        Integer.valueOf(applicationProperties.getProperty("team.threads.size.maxpool")));
    taskExecutor.setQueueCapacity(
        Integer.valueOf(applicationProperties.getProperty("team.threads.queue.capacity")));
    taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    taskExecutor.setThreadFactory(getTeamThreadFactory());
    taskExecutor.setWaitForTasksToCompleteOnShutdown(true);

    return taskExecutor;
  }
}
