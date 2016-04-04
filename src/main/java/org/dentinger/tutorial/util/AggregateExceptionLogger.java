package org.dentinger.tutorial.util;

import ch.qos.logback.classic.spi.EventArgUtil;
import java.util.concurrent.ConcurrentSkipListSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AggregateExceptionLogger {
  private Logger logger;
  private ConcurrentSkipListSet errors = new ConcurrentSkipListSet();

  private final static class ThrowableAndArgs {
    Throwable throwable;
    Object[] args;

    public ThrowableAndArgs(Object[] args) {
      this.args = args;
    }
  }

  private AggregateExceptionLogger(Logger logger){
    this.logger = logger;
  }

  public static AggregateExceptionLogger getLogger() {
    return new AggregateExceptionLogger(LoggerFactory.getLogger(AggregateExceptionLogger.class));
  }

  public static AggregateExceptionLogger getLogger(Logger delegateLogger) {
    return new AggregateExceptionLogger(delegateLogger);
  }

  public static AggregateExceptionLogger getLogger(Class c) {
    return new AggregateExceptionLogger(LoggerFactory.getLogger(c));
  }

  public void error(String message, Throwable t) {
    if (isNewError(t)) {
      logger.error(message, t);
    } else {
      logger.error(message);
    }
  }

  public void error(String message, Object arg1, Object arg2) {
    error(message, new Object[]{arg1, arg2});
  }

  public void error(String message, Object arg1, Object arg2, Object arg3) {
    error(message, new Object[]{arg1, arg2, arg3});
  }

  public void error(String message, Object[] args) {
    ThrowableAndArgs ta = extractThrowableAnRearrangeArguments(args);
    if (ta.throwable == null || isNewError(ta.throwable)) {
      logger.error(message, args);
    } else {
      logger.error(message, ta.args);
    }
  }

  private boolean isNewError(Throwable t) {
    return errors.add(t.getClass().getName());
  }

  private ThrowableAndArgs extractThrowableAnRearrangeArguments(Object[] args) {
    ThrowableAndArgs ta = new ThrowableAndArgs(args);
    ta.throwable = EventArgUtil.extractThrowable(args);
    if (EventArgUtil.successfulExtraction(ta.throwable)) {
      ta.args = EventArgUtil.trimmedCopy(args);
    }
    return ta;
  }
}
