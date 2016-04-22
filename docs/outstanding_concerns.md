# Outstanding Concerns


## Deadlock concern while running loader with multiple threads.

To replicate this issue:
1. start with a clean database
2. run LeagueManager with parameter loadAll
3. See Neo4j deadlock error in log

We plan on running multiple multi-threaded  loader instances where we are not able to guarantee the
order in which data is loaded.  The current implementation has a single app instance that when
loading multiple leagues in multiple threads a deadlock is encountered because multiple Leagues share regions.

### Questions to answer:

* Is there a way to get around this issue without slowing down the data loading processing?
> Current thoughts are around changing the way nodes and relationships are created in the application.
First add all nodes.  Then add relationships. To add relationships randomize the creation of relationships
in threads and assign a random node to each thread. This should minimize the potential of deadlocks or
limit the length a thread has to wait. The current approach (UNWIND with merges for nodes and relationships)
 currently can have situations where a thread at a higher level in the graph is locking some nodes and there are
  other threads waiting for that single thread to finsih and release the locks.

## General Questions:
* Detach Delete:  It appears that a similar deadlock is encountered when trying the query below.  Is
there an appropriate way to do multiple detached deletes at a time?

>match (r:Region) match (l:League) match (t:Team) detach delete r, l, t


# No longer concerns

## Answered Questions
* Is there a limit to how much data that can be passed to a single Unwind statement? (i.e  number
of json objects passed)
    * It is recommened to not pass more than 10k actions into a single UNWIND
* If there is a problem encountered during an unwind is everything rolled back or can data be in a
partially commited state.
    * It is recommended to wrap each UNWIND in a transaction.
