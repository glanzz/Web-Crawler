# Web-Crawler
Simple Multithreaded Web Crawler in Java 
The repository has an accompanying user interface which provides live visualize of the knowledge graph and stats. (https://github.com/glanzz/crawler-ui)

#### Setup
- The root of the project is the senku folder ( Open maven project with senku folder as root, look for pom.xml)
- The application was built with open-JDK 22.0.1
- Add neo4j.properties file in the resources folder with
```bash
neo4j.uri=YOUR_BOLT_URI
neo4j.username=YOUR_NAME
neo4j.password=YOUR_PASS
```
- Run the following: `mvn clean install`
- Run the `WebCrawler.java` file in the repository


### Modules
- The webcrawler has test setup in the test repository with unit testcases. The package com.senku.crawler has the db, parser, structures and utils packages.
- The crawler has parser package has all the classes:
  - Parser: Core logic that parses and extracts links from the given URL.
  - SafetyWall: This uses robots validator and URL validator to validate if links are safe to be parsed
- The db has the Neo4j repository to update the knowledge, the structures has all the structures the Bloomfilter and Page(Link) class which are main elements used through the application to crawl the sites.

### Data Structures
We have used  bloomfilter over hashsets as it is less memory overhead as our timed runs show that we crawl about 40 sites per second(Until this implementation) and 100 URLs visited. This mainly avoids processing node twice.
With this rate we are flexible to loose some URLs and consider space overhead. Instead of synchroize we use the Reentrant Lock as we want the fairness, if link at depth n has processed and is waiting.Now, link B with depth n has finished leading to depth n+1 with link A again, acquires the lock which is unfair, then resources are wasted multiple times.

To increase the speed, we have minimized the number of query calls to the Neo4j database by including a single query and we have used Reentrant lock for controlling updates to the Bloomfilter.


We use the following packages:
- [JUnit](https://junit.org/junit5/)
- [Log4j](https://logging.apache.org/log4j/2.x/manual/getting-started.html)
- [Mockito](https://site.mockito.org/)

- [Why Jericho ?](https://www.reddit.com/r/java/comments/tcw9wt/html_parsers_benchmark/)
- [URL Validator](https://commons.apache.org/proper/commons-validator/apidocs/org/apache/commons/validator/routines/UrlValidator.html)
- [Connection Pool and HTTP Client](https://www.baeldung.com/httpclient-connection-management)
- [Crawler commons for robots check](https://github.com/crawler-commons/crawler-commons)


[Why Bytebuddy is included explicitly {dependency of mockito}](https://github.com/mockito/mockito/issues/2272)


Additionaly each PRs have reason why the decision was made Eg: https://github.com/glanzz/Web-Crawler/pull/2
