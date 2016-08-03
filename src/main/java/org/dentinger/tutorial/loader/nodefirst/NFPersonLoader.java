package org.dentinger.tutorial.loader.nodefirst;

import com.google.common.collect.Lists;
import java.util.List;
import org.dentinger.tutorial.dal.SportsBallRepository;
import org.dentinger.tutorial.domain.Person;
import org.dentinger.tutorial.util.AggregateExceptionLogger;
import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class NFPersonLoader {

  private static Logger logger = LoggerFactory.getLogger(NFPersonLoader.class);

  private SessionFactory sessionFactory;
  private SportsBallRepository repo;
  private int numThreads;
  private TaskExecutor poolTaskExecutor;
  private PersonWorker personWorker;

  private String MERGE_PERSON_NODES =
      "unwind {json} as person "
          + "merge (p:Person {personId: person.personId}) "
          + " on create set p.uuid = person.uuid, p.name = person.name, p.dateOfBirth = person.dateOfBirth";

  private String MERGE_PERSON_RELATIONSHIPS =
      " UNWIND {json} AS person "
          + "unwind person.playson as team "
          + "match (t:Team {teamId: team.teamId}) "
          + "match (p:Person {personId: person.personId}) "
          + "merge (t)-[:PLAYS_ON ]-(p) ";

  private String MERGE_FAN_RELATIONSHIPS =
      " UNWIND {json} AS person "
          + "unwind person.fanOf as team "
          + "match (t:Team {teamId: team.teamId}) "
          + "match (p:Person {personId: person.personId}) "
          + "merge (t)-[:FAN_OF]-(p) ";


  private String CLEAN_UP =
      "match (p:Person) detach delete p";

  @Autowired
  public NFPersonLoader(TaskExecutor personProcessorThreadPool,
                        SessionFactory sessionFactory,
                        SportsBallRepository repo,
                        Environment env, PersonWorker personWorker) {
    this.sessionFactory = sessionFactory;
    this.repo = repo;
    this.personWorker = personWorker;
    this.numThreads = Integer.valueOf(env.getProperty("persons.loading.threads", "1"));
    this.poolTaskExecutor = personProcessorThreadPool;
  }

  public void cleanup(){
    logger.info("About to cleanup persons");
    new Neo4jTemplate(sessionFactory.openSession()).execute(CLEAN_UP);
    logger.info("Cleanup of persons complete");
  }

  public void loadPersonNodes() {
    AggregateExceptionLogger aeLogger = AggregateExceptionLogger.getLogger(this.getClass());

    List<Person> personList = repo.getPersons();
    List<Person> fanList = repo.getFans();
    personList.addAll(fanList);
    logger.info("About to load {} Persons using {} threads", personList.size(), numThreads);
    personWorker.getRecordsWritten().set(0);

    int subListSize = (int) Math.floor(personList.size() / numThreads);
    long start = System.currentTimeMillis();
    Lists.partition(personList, subListSize).stream().parallel()
        .forEach(subList -> {
          personWorker.doSubmitableWork(aeLogger, subList, MERGE_PERSON_NODES);
        });
    monitorThreadPool();
    logger.info("Processing of {} Persons using {} threads complete: {}ms",
        personWorker.getRecordsWritten().get(),
        numThreads,
        System.currentTimeMillis() - start);
  }

  public void loadPlayerRelationships() {
    AggregateExceptionLogger aeLogger = AggregateExceptionLogger.getLogger(this.getClass());
    List<Person> persons = repo.getPersons();
    logger.info("About to load Person relationships using {} threads", numThreads);
    personWorker.getRecordsWritten().set(0);

    int subListSize = (int) Math.floor(persons.size() / numThreads);
    long start = System.currentTimeMillis();

    Lists.partition(persons, subListSize).stream().parallel()
        .forEach(subList -> {
          personWorker.doSubmitableWork(aeLogger, subList, MERGE_PERSON_RELATIONSHIPS);
        });
    monitorThreadPool();
    logger
        .info("Processing of {} Person relationships using {} threads complete: {}ms",
            personWorker.getRecordsWritten().get(), numThreads,
            System.currentTimeMillis() - start);
  }

  public void loadFanRelationships() {
    AggregateExceptionLogger aeLogger = AggregateExceptionLogger.getLogger(this.getClass());
    List<Person> fans = repo.getFans();
    logger.info("About to load Fan relationships using {} threads", numThreads);
    personWorker.getRecordsWritten().set(0);

    int subListSize = (int) Math.floor(fans.size() / numThreads);
    long start = System.currentTimeMillis();

    Lists.partition(fans, subListSize).stream().parallel()
        .forEach(subList -> {
          personWorker.doSubmitableWork(aeLogger, subList, MERGE_FAN_RELATIONSHIPS);
        });
    monitorThreadPool();
    logger
        .info("Processing of {} Fan relationships using {} threads complete: {}ms",
            personWorker.getRecordsWritten().get(), numThreads,
            System.currentTimeMillis() - start);
  }


  private void monitorThreadPool() {
    while (((ThreadPoolTaskExecutor) poolTaskExecutor).getActiveCount() > 0) {
//      logger.info("{} threads: {}, jobs still in pool {}",
//          ((ThreadPoolTaskExecutor) poolTaskExecutor).getThreadNamePrefix(),
//          ((ThreadPoolTaskExecutor) poolTaskExecutor).getActiveCount(),
//          ((ThreadPoolTaskExecutor) poolTaskExecutor).getPoolSize());
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

}
