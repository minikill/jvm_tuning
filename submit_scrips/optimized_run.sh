#!/bin/bash

spark-submit --master k8s://$KUBERNETES_CLUSTER \
             --deploy-mode cluster \
             --conf spark.executor.instances=3 \
             --conf spark.kubernetes.namespace=default \
             --conf spark.kubernetes.container.image=test_spark_app:latest \
             --conf spark.kubernetes.driver.podTemplateFile=../kubernetes/pod_templates/driver_template.yml \
             --conf spark.kubernetes.executor.podTemplateFile=../kubernetes/pod_templates/executor_template.yml \
             --conf "spark.driver.extraJavaOptions=-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=3000 -Dcom.sun.management.jmxremote.rmi.port=3001 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Djava.rmi.server.hostname=127.0.0.1 -Dcom.amazonaws.sdk.disableCertChecking=true" \
             --conf "spark.executor.extraJavaOptions=-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=3100 -Dcom.sun.management.jmxremote.rmi.port=3101 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Djava.rmi.server.hostname=127.0.0.1 -Dcom.amazonaws.sdk.disableCertChecking=true -XX:+UnlockExperimentalVMOptions -Xms=150m -XX:MaxInlineSize=150 -XX:ProfiledCodeHeapSize=150m -XX:ReservedCodeCacheSize=300m" \
             --class inc.sad.app.BatchProcessingApplication \
             local:///app/sparkApp.jar \
             --config-file /etc/application_conf/application.conf \
             --spark-config /etc/spark_conf/spark.conf
