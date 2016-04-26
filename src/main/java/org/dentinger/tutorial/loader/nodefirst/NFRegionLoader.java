package org.dentinger.tutorial.loader.nodefirst;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.dentinger.tutorial.autoconfig.Neo4jProperties;
import org.dentinger.tutorial.dal.SportsBallRepository;
import org.dentinger.tutorial.domain.Region;
import org.dentinger.tutorial.util.AggregateExceptionLogger;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.stereotype.Component;

@Component
public class NFRegionLoader {
  private static Logger logger = LoggerFactory.getLogger(NFRegionLoader.class);
  private Neo4jProperties neo4jProperties;
  private SessionFactory sessionFactory;
  private SportsBallRepository repo;
  private int numThreads;
  private final AtomicLong recordsWritten = new AtomicLong(0);

  private String MERGE_REGIONS =
      "unwind {json} as q " +
          "merge (r:Region {id: q.id})" +
          "  on create set r.name = q.name " +
          "  on match set r.name = q.name";

  @Autowired
  public NFRegionLoader(Neo4jProperties neo4jProperties,
                        SessionFactory sessionFactory,
                        SportsBallRepository repo,
                        Environment env) {
    this.neo4jProperties = neo4jProperties;
    this.sessionFactory = sessionFactory;
    this.repo = repo;
    this.numThreads = Integer.valueOf(env.getProperty("regions.loading.threads", "1"));
  }

  public void loadRegions() {
    AggregateExceptionLogger aeLogger = AggregateExceptionLogger.getLogger(this.getClass());
    List<Region> regions = repo.getRegions();
    logger.info("About to load {} Regions using {} threads", regions.size(), numThreads);
    recordsWritten.set(0);
    ExecutorService executorService = getExecutorService(numThreads);
    int subListSize = (int) Math.floor(regions.size() / numThreads);
    long start = System.currentTimeMillis();
    Lists.partition(regions, subListSize).stream().parallel()
        .forEach((sublist) -> {
          executorService.submit(() -> {
            try {
              Map<String, Object> map = new HashMap<String, Object>();
              map.put("json", sublist);
              getNeo4jTemplate().execute(MERGE_REGIONS, map);
              logger.info("Processing of sublist (size={}) complete", sublist.size());
              recordsWritten.addAndGet(sublist.size());
            } catch (Exception e) {
              aeLogger.error("Unable to update graph, regionCount={}", regions.size(), e);
            }
          });
        });
    executorService.shutdown();
    try {
      executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    } catch (Exception e) {
      logger.error("executorService exception: ", e);
    }
    logger.info("Processing of {} Regions using {} threads complete: {}ms", recordsWritten.get(),
        numThreads, System.currentTimeMillis() - start);
  }

  private Neo4jTemplate getNeo4jTemplate() {
    Session session = sessionFactory.openSession(neo4jProperties.getUrl(),
        neo4jProperties.getUsername(), neo4jProperties.getPassword());

    return new Neo4jTemplate(session);
  }

  private ExecutorService getExecutorService(int numThreads) {
    final ThreadFactory threadFactory = new ThreadFactoryBuilder()
        .setNameFormat("regionLoader-nodefirst-%d")
        .setDaemon(true)
        .build();
    return Executors.newFixedThreadPool(numThreads, threadFactory);
  }
}
