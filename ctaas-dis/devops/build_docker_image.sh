#!/bin/bash
echo "RELEASE_VERSION: ${RELEASE_VERSION}"
application=ctaas-dis
app_home=opt/cadent/${application}
set -e

mkdir -p devops/image-fs/

create_readme_file()
{
## CREATE README.VERSION
cat <<end > $WORKSPACE/devops/image-fs/README.version
##
## THIS DIRECTORY CONTAINS THE FOLLOWING CODE:
##   - NOTE: This file must be formatted as a shell script
##
release_name="${BRANCH_NAME}"
release_component="${application}"
release_version="${RELEASE_VERSION}"
release_changelist="${LAST_COMMIT_HASH}"
release_bundle_url="${BUILD_URL}"
release_bundle_time="`date`"
end
}

download_solr(){
echo $WORKSPACE
cd $WORKSPACE/devops/image-fs/

wget https://archive.apache.org/dist/lucene/solr/8.7.0/solr-8.7.0.tgz
tar -xzf solr-8.7.0.tgz 
rm solr-8.7.0.tgz
rm -fr solr
mv solr-8.7.0 solr
}

create_dir_structure(){

# create dir structure
mkdir -p ${app_home}/{bin,config,logs,compilation,work,solr,rules} \
         ${app_home}/work/configs \
         ${app_home}/work/configs/default-configs \
         ${app_home}/solr/server/solr/${application}/conf
}

copy_solr_and_artifacts(){

# Copy Solr files
cp -r solr/* ${app_home}/solr/
cp $WORKSPACE/solr-config/dis/core.properties ${app_home}/solr/server/solr/ctaas-dis/
cp -r $WORKSPACE/solr-config/dis/conf/* ${app_home}/solr/server/solr/ctaas-dis/conf/

cp $WORKSPACE/target/ctaas-dis.jar ${app_home}/
cp -R $WORKSPACE/default-configs/cadent/${application}/* ${app_home}/work/configs/default-configs/
cp $WORKSPACE/devops/bootstrap.sh $WORKSPACE/devops/image-fs/
cp $WORKSPACE/devops/configure_container.sh $WORKSPACE/devops/image-fs/
cp $WORKSPACE/devops/pre_stop.sh ${app_home}/bin/
cp $WORKSPACE/devops/image-fs/README.version ${app_home}/

}

copy_default_configs () {
  pushd $WORKSPACE
    for config_file in `find default-configs`; do
      if [ -f $config_file ]; then
        file=`basename ${config_file}`
        dir=`dirname ${config_file} | cut -d'/' -f2-`
        echo "Conf file $file --> $WORKSPACE/devops/image-fs/opt/${dir} directory"
        mkdir -p $WORKSPACE/devops/image-fs/opt/${dir}
        cp -r ${config_file} $WORKSPACE/devops/image-fs/opt/${dir}/${file}
      fi
    done
    popd
}



create_docker_image(){	
# Build docker image
cd $WORKSPACE/devops
	docker build -t ${application}:${RELEASE_VERSION} .
	if [ $? -eq 0 ]; then
        echo "Docker image for ${application} created successfully."
	else
        echo $PWD 
        echo "Docker image creation for ${application} failed.."
        exit 1
	fi
}
create_readme_file
download_solr
create_dir_structure
copy_solr_and_artifacts
copy_default_configs
create_docker_image
