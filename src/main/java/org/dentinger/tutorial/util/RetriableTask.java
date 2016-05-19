package org.dentinger.tutorial.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetriableTask {
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
        if (i < retries - 1) {
          try {
            sleep(i);
          } catch (Exception e2) {
            logger.error("totalFails={}",totalFails.incrementAndGet());
            throw e2;
          }
        } else {
          logger.error("totalFails={}",totalFails.incrementAndGet());
          throw e;
        }
      }
    }
    return null;
  }

  public <T> T execute(Supplier<T> supplier) throws Exception {
    for (int i = 0; i < retries; i++) {
      try {
        return supplier.get();
      } catch (Exception e) {
        if (i < retries - 1) {
          try {
            sleep(i);
          } catch (Exception e2) {
            logger.error("totalFails={}",totalFails.incrementAndGet());
            throw e2;
          }
        } else {
          logger.error("totalFails={}",totalFails.incrementAndGet());
          throw e;
        }
      }
    }
    return null;
  }

  private void sleep(int numRetry) throws Exception {
    long sleep = delay + ((step != 0)? ThreadLocalRandom.current().nextLong(step)*numRetry:0);
    logger.warn("Delaying {}ms before retrying, totalRetries={}",sleep, totalRetries.incrementAndGet());
    Thread.sleep(sleep);
  }
}
