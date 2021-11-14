#Called by dev.sh or build-image.sh
#Can also be called directly by developers by passing the image tag. eg. sh run-container.sh dev, sh run-container.sh prod-develop-8.21, sh run-container.sh qa-8.21

SERVICE_NAME=$1
IMAGE_TAG=$2

FORMAT="run_container.sh <SERVICE_NAME> <IMAGE_TAG>"
EXAMPLE="run_container.sh ctaas-dis dev-develop-10"

if [ "$#" -ne 2 ]; then
    echo "Illegal number of parameters"
    echo "Format  : " $FORMAT
    echo "Example : " $EXAMPLE
    exit 1
fi

port=58080
debugging_port=58087
solr_port=59390

#IP=$(ip -4 route list match 0/0 | awk '{print $3}')
IP=$(ip addr show docker0 | grep -Po 'inet \K[\d.]+')

container_id=$(docker ps -aqf "name=$SERVICE_NAME")

if [ -n "$container_id" ];
then
  echo "Stopping and removing container_id:[$container_id]"
  docker stop $container_id
  docker rm $container_id
else
 echo "No $SERVICE_NAME container running"
fi

if [ $IMAGE_TAG = *"prod"* ] || [ $IMAGE_TAG = *"qa"* ]
then
  echo "Pulling image from repo.";
  docker pull 4info/$SERVICE_NAME:$IMAGE_TAG
else
  echo 'Running container in Dev mode'
fi

docker run -d --name $SERVICE_NAME -p $port:8080 -p $debugging_port:8787 -p $solr_port:9390 --add-host=host.docker.internal:$IP 4info/$SERVICE_NAME:$IMAGE_TAG
