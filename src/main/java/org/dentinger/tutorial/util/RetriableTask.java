package org.dentinger.tutorial.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetriableTask {
  /**
   * The default 'multiplier' value - value 2 (100% increase per backoff).
   */
  public static final int DEFAULT_MULTIPLIER = 2;
  public static final long DEFAULT_INITIAL_INTERVAL = 10L;

  private Logger logger = LoggerFactory.getLogger(RetriableTask.class);
  private int retries;
  private long delay;
  private long step;
  private static final AtomicLong totalRetries = new AtomicLong(0);
  private static final AtomicLong totalFails = new AtomicLong(0);

  public RetriableTask retries(int retries) {
    this.retries = retries;
    return this;
  }

  public RetriableTask delay(long delay, TimeUnit unit) {
    this.delay = unit.toMillis(delay);
    return this;
  }

  public RetriableTask step(long step, TimeUnit unit) {
    this.step = unit.toMillis(step);
    return this;
  }

  public <T> T execute(Runnable runner) throws Exception {
    for (int i = 0; i < retries; i++) {
      long start=0l;
      try {
        runner.run();
        break;
      } catch (Exception e) {
        sleepOrThrow(i, e);
      }
    }
    return null;
  }

  public <T> T execute(Supplier<T> supplier) throws Exception {
    for (int i = 0; i < retries; i++) {
      try {
        return supplier.get();
      } catch (Exception e) {
        sleepOrThrow(i, e);
      }
    }
    return null;
  }

  private void sleepOrThrow(final int i, final Exception e) throws Exception {
    if (i < retries - 1) {
      try {
        sleep(i);
      } catch (InterruptedException e2) {
        logger.error("Thread interrupted while sleeping", e2);
        logger.error("totalFails={}",totalFails.incrementAndGet());
        throw e2;
      } catch (Exception e3) {
        logger.error("Error while sleeping", e3);
        throw e3;
      }
    } else {
      logger.error("totalFails={}",totalFails.incrementAndGet());
      throw e;
    }
  }

  private void sleep(int numRetry) throws Exception {
    long sleep = delay + ((step != 0)? ThreadLocalRandom.current().nextLong(step)*numRetry:0);
    logger.warn("Delaying {}ms before retrying, totalRetries={}",sleep, totalRetries.incrementAndGet());
    Thread.sleep(sleep);
  }

  /**
   * This method allows you to sleep exponentially based on numRetry
   * @param numRetry
   * @throws Exception
   */
  private void sleepWithExponentialBackOff(int numRetry) throws InterruptedException {
    long sleep = DEFAULT_INITIAL_INTERVAL + DEFAULT_MULTIPLIER^numRetry;
    logger.warn("Delaying {}ms before retrying, totalRetries={}",sleep, totalRetries.incrementAndGet());
    Thread.sleep(sleep);
  }
}
