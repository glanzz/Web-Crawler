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

We use the following packages:
[JUnit](https://junit.org/junit5/)
[Log4j](https://logging.apache.org/log4j/2.x/manual/getting-started.html)
[Mockito](https://site.mockito.org/)

[Why Jericho ?](https://www.reddit.com/r/java/comments/tcw9wt/html_parsers_benchmark/)
[URL Validator](https://commons.apache.org/proper/commons-validator/apidocs/org/apache/commons/validator/routines/UrlValidator.html)
[Connection Pool and HTTP Client](https://www.baeldung.com/httpclient-connection-management)
[Crawler commons for robots check](https://github.com/crawler-commons/crawler-commons)


[Why Bytebuddy is included explicitly {dependency of mockito}](https://github.com/mockito/mockito/issues/2272)


Additionaly each PRs have reason why the decision was made Eg: https://github.com/glanzz/Web-Crawler/pull/2
