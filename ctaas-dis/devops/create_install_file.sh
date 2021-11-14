#!/bin/bash

application=ctaas-dis

#### INIT host/
mkdir -p $WORKSPACE/host
mv $WORKSPACE/devops/container_control.sh $WORKSPACE/host
docker save artifactory-eng.cadent-ops.tv/docker/cadent/${application}:${RELEASE_VERSION} > $WORKSPACE/host/${application}_image.tar

cat <<"end" > $WORKSPACE/install-${application}-${RELEASE_VERSION}.sh
#!/bin/sh
set -eo pipefail
application=ctaas-dis
umask 002
dir=/opt/cadent/containers/work/install-${application}
if [ "$1" = "--extractonly" ]; then
    extract_only=1
    shift
elif [ "$1" = "--help" ]; then
    echo "# INFO: Please pass --machine=default (to start container with default configurations present in Github)"
    echo "# INFO: Please pass --token=<$vault_token> (to start container with hostname configurations that stored in S3 bucket)"
    echo "# INFO: Please pass --machine=<hostname>/<profile> --token=<$vault_token> (to start container with either profile or host configs that stored in S3 bucket)"
    echo "# INFO: Option: [--extractonly] *Must be first argument, does not configure the container.* Extracts files into [${dir}] and exits."
    echo "# INFO: Option: $0 [--help] *Must be first argument.* Prints this help message and exits."
    exit 1
fi

rm -fr ${dir}/
mkdir -p ${dir}/

line_number=`grep -m1 -an '^#### TAR BELOW HERE ####$' $0 | cut -d':' -f1`
line_number=`expr ${line_number} + 1`

tail -n +${line_number} $0 | tar xz --dir=${dir}/
if [ "${extract_only}" = "1" ]; then
	echo "# INFO: Extracted files into [${dir}]"
	exit 0
else
        echo "# INFO: Files have been extracted. Starting the provisioning process with the following parameters: $@"
        ${dir}/container_control.sh provision ${dir} $@
fi

exit 0
#### TAR BELOW HERE ####
end

cd $WORKSPACE/host/

tar zc . >> $WORKSPACE/install-${application}-${RELEASE_VERSION}.sh
