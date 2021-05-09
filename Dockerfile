FROM spark:base_spark

COPY target/jvm_tuning-1.0-SNAPSHOT-jar-with-dependencies.jar /app/sparkApp.jar

CMD ["/bin/bash"]