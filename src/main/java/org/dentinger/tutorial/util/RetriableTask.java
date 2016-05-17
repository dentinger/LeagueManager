package org.dentinger.tutorial.util;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class RetriableTask {
  private int retries;
  private long delay;

  public RetriableTask retries(int retries) {
    this.retries = retries;
    return this;
  }

  public RetriableTask delay(long delay, TimeUnit unit) {
    this.delay = unit.toMillis(delay);
    return this;
  }

  public <T> T execute(Runnable runner) throws Exception {
    for (int i = 0; i < retries; i++) {
      try {
        runner.run();
        break;
      } catch (Exception e) {
        if (i < retries - 1) {
          try {
            Thread.sleep(delay);
          } catch (Exception e2) {
            throw e2;
          }
        } else {
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
            Thread.sleep(delay);
          } catch (Exception e2) {
            throw e2;
          }
        } else {
          throw e;
        }
      }
    }
    return null;
  }
}
