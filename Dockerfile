FROM registry-vpc.cn-shanghai.aliyuncs.com/wms-test-repository/amazoncorretto:17.0.7
VOLUME /tmp
ADD target/planManagement-1.0-SNAPSHOT.jar plan.jar
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' >/etc/timezone
RUN bash -c 'touch /plan.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/plan.jar"]