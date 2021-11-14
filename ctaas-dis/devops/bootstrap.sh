#!/bin/sh
#@(#)
#@(#) Startup script for dis containers.
#@(#)

###ENV VARIABLES
umask 002
UMASK=002
export UMASK

root=/opt/cadent/ctaas-dis
tmp_work=${root}/work
nohup_log=${root}/logs/dis.nohup.log
sleep_seconds=300
application=ctaas-dis

k8s=$1

if [ "--k8s" == "${k8s}" ]; then
 
 	audit_log_uuid=$(cat /proc/sys/kernel/random/uuid)
  	echo " UUID  :- ${audit_log_uuid}"
  	export LinearNodeServerID=${audit_log_uuid}

fi

echo "# INFO: PROFILE: ${profile}"
###echo "# INFO: vault_token: ${vault_token}"

###LOGGING LOGIC
log()
{
    _level=$1
    shift
    printf "# `date +"%D %T %Z"` - ${_level} - $@\n"
}
	
##CHECK IF CONTAINER HAS BEEN SETUP WITH ENVIRONMENT AND CONFIGURATION
log "INFO" "Checking if [${application}] container has been configured for an environment ${profile}"
if [ -e ${root}/work/configs/token_expired ]; then
	  log "INFO" "Token files are updated in the initial startup. please redeploy the container if you update token files in Github"
	  echo "Token files are updated in the initial startup. please redeploy the container if you update token files in Github"
else

   if [ "${k8s}" == "--k8s" ]; then
	    sleep 30	
	    echo "Start k8s default conf"
		#some logic to move files into the right directory via another script with the logic
        bash configure_container.sh ${profile} ${vault_token} --k8s
    else
	    #some logic to move files into the right directory via another script with the logic
     	bash -x configure_container.sh ${profile} ${vault_token}
	fi
    if [ $? -eq 1 ]; then
    	echo "# ERROR: Failed to download configuration files from Vault, Exiting the container "
    	exit 1
  	else
    	touch ${root}/work/configs/token_expired
  	fi
fi

log "INFO" "Starting [${application}] container."
 
mkdir -p ${tmp_work}/
if [ -f "${nohup_log}" ]; then
    mv ${nohup_log} ${nohup_log}.`date +"%Y%m%d-%H%M%S"`
fi

LANG="en_US.UTF-8"

# Unset http_proxy before starting the service
unset http_proxy https_proxy HTTPS_PROXY HTTP_PROXY


log "INFO" "Starting [${application}] Solr"
if [ -z ${SOLR_JAVA_MEMORY} ]; then
  SOLR_JAVA_MEMORY=256m
fi 

$root/solr/bin/solr start -f -h localhost -p 9390 -m $SOLR_JAVA_MEMORY -force &> /dev/null &
 
sleep 30

log "INFO" "Started [${application}] Solr at 9390"




JAVA_OPTS="-Djava.net.preferIPv4Stack=true -Dsun.rmi.dgc.client.gcInterval=3600000 \
-Dsun.rmi.dgc.server.gcInterval=3600000 -XX:+HeapDumpOnOutOfMemoryError \
-XX:CMSInitiatingOccupancyFraction=40 \
-Xrunjdwp:transport=dt_socket,address=0.0.0.0:8787,server=y,suspend=n"


#Default
java_default_mem="-Xms1024m -Xmx1024m"
if [ -z ${JAVA_MEMORY} ]; then
    JAVA_OPTS="$java_default_mem $JAVA_OPTS"
else
    JAVA_OPTS="${JAVA_MEMORY} $JAVA_OPTS"
fi
command="java $JAVA_OPTS -Dconfig_dir=$root/config/ -Dloader.path=$root/config/ -Drules.dir=$root/rules/ -Dsun.net.spi.nameservice.nameservers=default -Dapp_home=$root -Dlog4j2.isThreadContextMapInheritable=true -cp $root/ctaas-dis.jar org.springframework.boot.loader.PropertiesLauncher"
				 
if [ "--k8s" == "${k8s}" ]; then
    echo "inside catalina k8s"
	trap _term SIGTERM
	log "INFO" "${command} &"
	${command} &
	appPID=$!
	wait "$appPID"
else
   log "INFO" "${command} > ${nohup_log} 2>&1"
   ${command} > ${nohup_log} 2>&1
fi
_code=$?				 

log "INFO" "Application [${application}] has exited with code: [${_code}]."

if [ "--k8s" == "${k8s}" ]; then
log "INFO" "Keeping container alive for another [${sleep_seconds}] seconds, in case troubleshooting is needed."
sleep ${sleep_seconds}
fi
