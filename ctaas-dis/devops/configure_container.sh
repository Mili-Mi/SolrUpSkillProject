#!/bin/bash

profile=$1
vault_token=$2
k8s=$3
application=ctaas-dis
app_home=/opt/cadent/${application}

#Download config files from S3 using vault token.
download_config_files() {
	echo "Inside download_config_files function"
	echo "$(id -u -n) is downloading configuration"
	#Download config file from vault
	config_tarball="${profile}-${application}.tgz"
	if [ "${vault_token}" == "" ]; then
	  echo "# INFO: please provide vault_token"
	exit 1
	else
	  curl -sS --header X-Vault-Token:${vault_token} -X GET https://prod-vault.cadenttech.tv/v1/ctaas/data/${profile} -o ${app_home}/work/configs/response.txt
	fi

	cd ${app_home}/work/configs
	if grep -q error "response.txt"; then
	  echo "Token Expired. Please redeploy using rundeck job"
	  exit 1
	  else
	  echo "Received encrypted config file location and password from Vault"
	  password=` awk -F':' '{print $10;}' response.txt | cut -d '"' -f2 `
	  s3_path=` awk -F':' '{print $9;}' response.txt | cut -d '"' -f1 `
	  config_file=http:$s3_path/${config_tarball}.enc
	fi

	#Download config file S3 bucket
	wget $config_file

	if [[ $? -ne 0 ]]; then
	    echo "wget failed. Please check file path"
	    exit 1;
	fi

	#Decrypt Config file downloaded from S3  bucket
	openssl enc -d -aes-256-ecb -pass pass:${password} -in ${config_tarball}.enc  -out ${config_tarball}
	
	echo "Current Directory $PWD"
	
	tar -xzvf ${config_tarball} > /dev/null
	if [ "$?" -eq 0 ]; then
	echo "Decrypted and Extracted Config files Succesfully"
	rm -rf $app_home/work/configs/response.txt
	else
	echo "Openssl version is not compatible with encrypted version.Please install OpenSSL 1.1.1j in container. Exiting!"
	exit 1
	fi
}

replace_tokens() {
	set -f
	configfile=$1
	IFS=$'\n'
	tokens=$( cat $2)
	echo "tokens: ${tokens}"
	for _src in ${configfile}; do
		for i in ${tokens}; do
		    i=$( echo $i | sed 's/\"//g' )
		    key=$(echo $i  |  sed  -r 's/token\[([a-zA-Z0-9\_\-]+)\].*/\1/' )
		    value=$(echo $i | sed  -r 's/.*\=(.*)/\1/' )
		    sed -Ei "s|$key|$value|g" ${_src}
		done
	done
	set +f
}

replace_appender(){
#Change logging appender for k8s to console.
	if [ "${k8s}" == "--k8s" ]; then
      	sed -i 's/AppenderRef ref="fileLogger"/AppenderRef ref="Console"/' $1
      	sed -i 's/<Root level="WARN">/<Root level="WARN"\ includeLocation="true">/' $1
	fi
}



copy_updated_config_file () {

#Only Copy config files into container if CS/Ops has put any config files specially in codecommit/ctaas-eng-config profiles. This is
#particularly useful to update log4js, or any file that they want to retain in deployment.
	if [ -d "${app_home}/work/configs/${application}/conf"  ]; then
		cp -rf ${app_home}/work/configs/${application}/conf/* /opt/cadent/${application}/config/
	fi	
}


if [ "${profile}" != "default" ]; then
	echo "profile: {profile}"
    download_config_files
    copy_updated_config_file
    token_file="${app_home}/work/configs/${application}/tokens.txt"
else
	echo "default profile"
    echo "INFO" "Starting the container with default configuration"
    token_file="${app_home}/tokens.txt"
fi

#Source Token file to replace token values in application.properties files
config_file=${app_home}/config/application.properties
log4j_file=${app_home}/config/log4j2.xml
replace_tokens $config_file $token_file
echo "Token file ${token_file} applied to ${config_file}"
replace_appender $log4j_file

#Only deleting the token file that may have come with docker installtion
rm -f ${app_home}/tokens.txt
