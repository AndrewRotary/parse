FROM java:8
ADD target/demo-prod-0.jar 
WORKDIR /apps/spring_app
EXPOSE 8080
CMD ["java", "-jar", "demo-prod-0.jar"]