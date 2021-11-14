SERVICE_NAME=$1

FORMAT="runAppLocal.sh <SERVICE_NAME>"
EXAMPLE="runAppLocal.sh ctaas-dis"

if [ "$#" -ne 1 ]; then
    echo "Illegal number of parameters"
    echo "Usage  : " $FORMAT
    echo "Example : " $EXAMPLE
    exit 1
fi

mvn clean install -DskipTests
PROFILE=local

PROPERTY_FILE=target/staging/config/app.conf
JAVA_OPTS=$(grep JAVA_OPTS "${PROPERTY_FILE}"| sed -e "s/JAVA_OPTS=//g")


echo "java $JAVA_OPTS -DAPP_HOME=$PWD/target/ -Dspring.profiles.active=$PROFILE -Dconfig_dir=$PWD/target/staging/config/ -Dlog4j2.isThreadContextMapInheritable=true -Dloader.path=$PWD/target/staging/config/$PROFILE/ org.springframework.boot.loader.PropertiesLauncher"

java $JAVA_OPTS -DAPP_HOME=$PWD/target/ -Dlog4j2.isThreadContextMapInheritable=true -Dspring.profiles.active=$PROFILE -Dconfig_dir=$PWD/target/staging/config/ -cp target/$SERVICE_NAME.jar -Dloader.path=$PWD/target/staging/config/ org.springframework.boot.loader.PropertiesLauncher
