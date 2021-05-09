#!/bin/bash

mvn clean package

eval $(minikube docker-env)

docker-image-tool.sh -t base_spark build

docker build . -t test_spark_app

kubectl apply -f ./kubernetes/configMaps.yml
kubectl apply -f ./kubernetes/minio.yml


