FROM java:8
WORKDIR /
ADD target/hackernews-0.0.1-SNAPSHOT-jar-with-dependencies.jar hackernews.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","hackernews.jar"]
