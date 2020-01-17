# HackerNewsScraper

A simple solution to the hacker news scraper.

Built with Java using the libraries:
  - HtmlUnit (used to manipulate the html website)
  - Jackson (used to serialize or map java objects to JSON)
  - JCommander (used as a small framework to parse command line parameters)
  
## Installation

Required for use:
  - Docker 

## Usage

### Running with docker

Build:
```docker
docker build -t hackernews .
```
Run:
```docker
docker run -it hackernews --posts X // where X is the number of posts 
```

----

###### For Local use:
Alternative way to run the program on local machine with Java JRE
```java
java -jar hackernews.jar --posts X // where X is the number of posts 
```
