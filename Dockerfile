FROM openjdk:8-jre-slim
ENV PARAMS="-Duser.timezone=GMT+8"

#ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo Asia/Shanghai > /etc/timezone

ADD target/aws-demo-biz-0.0.1-SNAPSHOT.jar /app.jar

ENTRYPOINT ["sh","-c","java -jar /app.jar $PARAMS"]