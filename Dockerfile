FROM java:8
COPY target/demo-prod-0.jar /parser
WORKDIR /parser
EXPOSE 8080
CMD ["java", "-jar", "demo-prod-0.jar"]