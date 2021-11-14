#!/bin/sh -p
#@(#)
#@(#) Utility command for the ctaas-dis container.
#@(#)
#@(#) Usage:
#@(#)     ./container_control.sh <sub-command>
#@(#)
#@(#) Sub-commands:
#@(#)     configure <${config_dir}>
#@(#)         Re-configures the given container with the given ${config_dir}.  See "tokens" sub-command.
#@(#)     healthcheck
#@(#)         Connects to the healtcheck URL.
#@(#)     load-image <${image_file}>
#@(#)         Loads the given ${image_file} to the container.  The existing image will be removed.  The given ${image_file} argument
#@(#)         can be a URL.
#@(#)     logs
#@(#)         Prints log info for the container.  (this is different than the application logs)
#@(#)     provision <${artifact_location}> <${config_dir}>
#@(#)         Provisions a container for the host-fs from the ${artifact_location}, and configures it with the ${config_dir}.  The given
#@(#)         ${artifact_location} and ${config_dir} can be URLs.  See "tokens" sub-command for sample tokens entries.
#@(#)     restart
#@(#)         Restarts the container.
#@(#)     shell
#@(#)         Provides interactive shell to the container.
#@(#)     start
#@(#)         Starts the container.
#@(#)    status
#@(#)         Status info for the container.
#@(#)     stop
#@(#)         Stops the container.
#@(#)     version
#@(#)         Prints version info for the application within the container.
#@(#)     
#@(#) Options:
#@(#)     [--container]
#@(#)         Name of the container to run the commands for.  If container_control.sh is called from within /opt/cadent/containers/${container}/ ,
#@(#)         then the name of the container will default to the sub-directory name.  If the container name is not given, and it cannot
#@(#)         be ascertained from the sub-directory name, then it will default to "ctaas-dis".
#@(#)
#@(#) Examples:
#@(#)     ./container_control.sh start
#@(#)     ./container_control.sh status
#@(#)     ./container_control.sh stop
#@(#)

## FIGURE OUT WHAT THE PARENT DIRECTORY IS CALLED (used later)
_prevpwd=`/bin/pwd 2> /dev/null` ## Ignore failure
cd `dirname $0`
root=`/bin/pwd`
cd ${_prevpwd} > /dev/null 2>&1 ## Ignore failure

## REQUIRED umask
umask 002
UMASK=002 ## REQUIRED AFTER UPGRADING TO TOMCAT 8.5
export UMASK

#### SOME COMMON FUNCTIONS
run()
{
    if [ "$1" = "-i" ]; then
        shift
        ignore_exitcode=1
    fi

    prompt "$@"

    eval "$@" 2>&1
    exit_code=$?
    if [ ${exit_code} != 0 ]; then
        if [ "${ignore_exitcode}" = "1" ]; then
            echo "## IGNORING EXIT CODE: ${exit_code}"
            return ${exit_code}
        else
            echo
            echo "*** FAILED: Unexpected exit code [${exit_code}]."
            echo
            exit ${exit_code}
        fi
    fi
}

prompt()
{
    echo "(`date +'%T %Z'`) ${LOGNAME}@`hostname`:`pwd`/"
    echo "$ $@"
}

log()
{
    printf "# ${1}: `date +'%D %T %Z'`"
    echo " - ${2}"
}

log2()
{
    printf "# ${1} - $2"
    echo ""
}

abort()
{
    message=$1
    echo "* ERROR: ${message}"

    shift
    for extra in "$@"
    do
        echo "         ${extra}"
    done
    exit 1
}

#### MAIN CODE IS BELOW HERE

## CAN ONLY RUN THIS COMMAND AS 'root'
user=`id -nu`
if [ "${user}" != "root" ]; then
    echo ""
    run id -nu
    echo ""
    abort "This command may only be run by the 'root' user."
fi

## INIT ${original_arguments}, ${nowait}, ${action}, ${kill}
port=58080
debugging_port=58087
solr_port=59390
original_arguments="$@"
for arg in "$@"; do
    case ${arg} in
        --nowait)
            nowait=" ${arg}"
            more_arguments="${more_arguments} ${arg}"
            ;;
        --nolog)
            ## DON'T BOTHER LOGGING
            ##   (useful if the parent process is already logging)
            nolog=1
            ;;
        --kill)
            ## KILL IT?
            kill=1
            ;;
        --container=*)
            container=`echo ${arg} | sed -e 's/--container=//'`
            root="/opt/cadent/containers/${container}"
            ;;
	--profile=*)
            # get profile name from input
            machine_name=`echo ${arg} | sed -e 's/--profile=//'`
            ;;
         --machine=*)
	    # get machine name from input
	    machine_name=`echo ${arg} | sed -e 's/--machine=//'`
            ;;
          --token=*)
            # get vault token from input
            vault_token=`echo ${arg} | sed -e 's/--token=//'`
            ;;
        help|--help|-\?)
            ## IGNORE ALL OTHER ARGUMENTS, AND SHOW THE HELP MESSAGE
            help=1
            arguments=""
            break
            ;;
        *)
            arguments="${arguments} ${arg}"
            ;;
    esac
done
set -- ${arguments}
action=$1

echo "${action}" | grep -q '/'
if [ "$?" = "0" ]; then
    help=1
fi

## SHOW HELP MESSAGE
if [ "${help}" = "1" -o "$#" -lt "1" ]; then
    egrep "^#@\(#\)" $0 | sed -e "s/^#@(#)//"
    exit 1
fi

## INIT ${container}, ${image}
if [ "${container}" = "" ]; then
    echo ${root} | grep -q "cadent/containers/" > /dev/null 2>&1
    if [ "$?" = "0" ]; then
        container=`echo ${root} | sed -e 's|.*cadent/containers/||' -e 's|/.*||'`
    else
        root="/opt/cadent/containers"
    fi
    if [ "${container}" = "work" ]; then
        root="/opt/cadent/containers"
        container=""
    fi

    if [ "${container}" = "" ]; then
        container="ctaas-dis"

    fi
elif [ ! -d "${root}/" ]; then
    root="/opt/cadent/containers"
fi

# get image name from Tar file
pwd
tar -x -O -f /opt/cadent/containers/work/install-ctaas-dis/ctaas-dis_image.tar manifest.json >> image.txt
version=`awk -F':' '{print $4;}' image.txt | cut -d '"' -f1`
rm -rf image.txt
echo "RELEASE_VERSION: ${version}"
image="artifactory-eng.cadent-ops.tv/docker/cadent/ctaas-dis:${version}"

## LOG THIS COMMAND
if [ "${nolog}" = "" ]; then
    log_dir=${root}/control_logs
    if [ ! -d ${log_dir} ]; then
        run mkdir -p ${log_dir}
    fi
    log_file=${log_dir}/`date +"%Y%m%d-%H%M%S"`.container.${action}.log

    if [ ! -d ${log_dir} ]; then
        run mkdir -p ${log_dir}
    fi
    (
        echo "# Beginning log... [`date`]"
        if [ "${SUDO_USER}" != "" ]; then
            echo "# ... sudo'd by [${SUDO_USER}] running [${SUDO_COMMAND}]"
        fi
        prompt "$0 ${original_arguments}"
    ) >> ${log_file}
else
    log_file="/dev/null"
fi

## WE NEED THIS FUNNY FILE BECAUSE WE ARE tee'ing THE OUTPUT, AND
## IT IS DIFFICULT TO KNOW THE EXIT CODE.
exitcode_file=/tmp/docker_control_exitcode.$$

## RUN THE GIVEN ${action}, AND PUT THE RESULTS IN THE LOG FILE
(
## RUN THIS IN A DOUBLE-SUB-SHELL (To both log, and keep the exit_code)
(

systemctl is-active --quiet docker
if [ "$?" != "0" ]; then
    echo "ERROR: Exiting script as docker service is not running."
    run systemctl is-active docker
fi

_ps="docker ps --format '{{.ID}} - {{.Names}} - {{.Status}}' --filter 'name=^${container}$' -a"

get_image()
{
    _images="docker images --format '{{.ID}} - {{.Repository}} - {{.Tag}}' -q -a ${image}"
    eval ${_images} 2>&1
}

get_container()
{
    _ls="docker container ls --format '{{.ID}} - {{.Names}}' --filter name='^${container}$' -q -a"
    eval ${_ls} 2>&1
}

verify_container()
{
    _ls_out=`get_container`
    if [ "${_ls_out}" = "" ]; then
         echo "# WARN: Could not find any container named: [${container}].  Exiting."
         exit 0
    fi
}

_local_config="${root}/mnt/conf/container_config.sh"
if [ -f "${_local_config}" ]; then

    if [ ! -r "${_local_config}" ]; then
         echo "# WARN: Found config file, but it is not readable: [${_local_config}].  Exiting."
         exit 0
    fi

    unset config
    declare -A config
    run . "${_local_config}"

    if [ "${config["API_PORT"]}" != "" ]; then
         port="${config["API_PORT"]}"
    fi
fi


case ${action} in

    status)
        verify_container
        run "${_ps}"
        ;;

    start)
        verify_container
        _ps_out=`eval ${_ps} 2>&1`
        echo "${_ps_out}" | grep -q " - Up " > /dev/null 2>&1
        if [ "${?}" = "0" ]; then
            echo ""
        else
            run docker start ${container}

            echo ""
            echo "# INFO: Success: Container [${container}] started."
            echo ""
        fi
        ;;

    stop)
        verify_container
        _ps_out=`eval ${_ps} 2>&1`
        echo "${_ps_out}" | grep -q " - Up " > /dev/null 2>&1
        if [ "${?}" = "0" ]; then
            run docker stop ${container}

            echo ""
            echo "# INFO: Success: Container [${container}] stopped."
            echo ""
        else
            echo ""
            echo "# INFO: Success: Container [${container}] already stopped."
            echo ""
        fi
        ;;

    restart)
        verify_container
        run $0 --nolog stop ${nowait}
        run sleep 3  ## Let the ports die, etc
        run $0 --nolog start ${nowait}
        ;;

    healthcheck)
        verify_container
        run "wget -O- http://localhost:${port}/healthcheck"
        ;;

    shell)
        verify_container
        echo "# INFO: Loading interactive shell for [${container}]"
        ;;

    logs)
        verify_container
        run docker logs ${container}
        ;;

    version)
        verify_container
        run docker exec ${container} cat /opt/cadent/ctaas-dis/README.version
        ;;

    load-image)
	echo "# INFO: machine_name: ${machine_name}"
        echo "# INFO: vault_token: ${vault_token}"
        new_image=$2
 
        if [ "${new_image}" = "" ]; then
            echo "# ERROR: Usage: [$0 load-image <image-file-name>]"  
            exit 1
        fi

        echo "${new_image}" | grep -q '\(http\|https\):'
        if [ "$?" = "0" ]; then
            _download=/opt/cadent/containers/${container}/work/ctaas-dis_image.tar
            run wget -O${_download}
            new_image=${_download}
        fi

        if [ ! -r "${new_image}" ]; then
            echo "# ERROR: Image file not found or is not readable: [${new_image}]."
            exit 1
        fi

        _ls_out=`get_container`
        if [ "${_ls_out}" = "" ]; then
            echo "# INFO: Success: Container [${container}] already removed."
        else
            run docker stop ${container}
            run docker rm -f ${container}
            echo "# INFO: Success: Container [${container}] stopped and removed."
        fi

        _images_out=`get_image`
        if [ "${_images_out}" = "" ]; then
            echo "# INFO: Success: Image [${image}] already removed."
        else
            run docker rmi -f ${image}
            echo "# INFO: Success: Image [${image}] removed."
        fi
 
        run docker load -i ${new_image}
	## CREATE TOP-LEVEL DIRECTORY STRUCTURE FOR HOST-OS
        run mkdir -p /opt/cadent/containers/${container}/mnt/logs
        IP=$(ip addr show docker0 | grep -Po 'inet \K[\d.]+')
	set -x

        run docker run -d \
             --name ${container} \
             -p ${port}:8080 \
             -p ${debugging_port}:8787 \
             -p ${solr_port}:9390 \
             --mount type=bind,source=/opt/cadent/containers/${container}/mnt/logs,target=/opt/cadent/ctaas-dis/logs  \
             --add-host=host.docker.internal:$IP \
             --env machine_name="${machine_name}" \
	     --env vault_token="${vault_token}" \
             ${image}
        set +x
        echo "# INFO: Success: Image [${image}] loaded with [${new_image}]."
        ;;

    provision)
	echo "# INFO: machine_name: ${machine_name}"
        echo "# INFO: vault_token: ${vault_token}"
        
        if [ "$machine_name" == "" ];then
            machine_name=$( hostname )
        fi        
        if [[ "${machine_name}" != "default" && "${vault_token}" = "" ]];then
             echo "# FAIL: please pass --machine=<machine_name> --token=<vault_token>"
             exit 1
        fi
        artifact_loc=$2
	machine=$machine_name
	config_dir=$machine_name
	
	    

        if [ "${artifact_loc}" = "" -o "${machine}"  = "" ]; then
            echo "# ERROR: Usage: [$0 provision <artifact-location>]"
            exit 1
        fi

        ## CREATE TOP-LEVEL DIRECTORY STRUCTURE FOR HOST-OS
        run mkdir -p /opt/cadent/containers/${container}/mnt/logs
	run chmod -R 775 /opt/cadent/containers/${container}
	run chmod 775 /opt/cadent/ /opt/cadent/containers/ /opt/cadent/containers/control_logs/ /opt/cadent/containers/work/

        ## PREP work DIRECTORY
        run rm -fr /opt/cadent/containers/work/provision-${container}/
        run mkdir -p /opt/cadent/containers/work/provision-${container}/

        ## DOWNLOAD ${application_tar} IF THE ARGUMENT IS A URL
        echo "${artifact_loc}" | grep -q '\(http\|https\):'
        if [ "$?" = "0" ]; then
            run mkdir -p /opt/cadent/containers/work/download-${container}/
            _base=`basename ${artifact_loc}`
            _download=/opt/cadent/containers/work/download-${container}/${_base}
            run wget --progress=bar:force -O${_download} ${artifact_loc}
            artifact_loc=${_download}
        fi

        ## DOWNLOAD ${application_tar} IF THE ARGUMENT IS A URL
        echo "${artifact_loc}" | grep -q '\(http\|https\):'
        if [ "$?" = "0" ]; then
            run mkdir -p /opt/cadent/containers/work/download-${container}/
            _base=`basename ${artifact_loc}`
            _download=/opt/cadent/containers/work/download-${container}/${_base}
            run wget --progress=bar:force -O${_download} ${artifact_loc}
            artifact_loc=${_download}
        fi

        ## IF THE ARGUMENT IS AN INSTALLER FILE:
        ##   1. Run the installer
        ##   2. Exit
        if [ -f "${artifact_loc}" ]; then
            if [ ! -r "${artifact_loc}" ]; then
                echo "# ERROR: Container installer file is not readable: [${artifact_loc}]."
                exit 1
            fi
 
            ## UNTAR ${artifact_loc}
            run sh ${artifact_loc} ${config_dir} --container=${container}
            exit
        fi

        ## CHECK FOR ISSUES
        if [ ! -d "${artifact_loc}" ]; then
             echo "# ERROR: Artifact location is not a file or a directory: [${artifact_loc}]."
             exit 1
        elif [ ! -r "${artifact_loc}" -o ! -x "${artifact_loc}" ]; then
             echo "# ERROR: Artifact directory not readable or not executable: [${artifact_loc}]."
             exit 1
        elif [ ! -f "${artifact_loc}/ctaas-dis_image.tar" ]; then
             echo "# ERROR: Artifact directory does not contain required file: [ctaas-dis_image.tar]."
             exit 1
        fi

        ## INSTALL HOST-OS DIRECTORY STRUCTURE AND CONFIGURE
        run rsync -a ${artifact_loc}/ /opt/cadent/containers/${container}/
        run find /opt/cadent/containers/${container}/ -name mnt -prune -o -exec chown -R baadmin:bacdx \{\} \\\;
        #run find /opt/cadent/containers/${container}/mnt/ -type d -exec chown baadmin:bacdx \{\} \\\;
     ## run $0 configure ${config_dir} --container=${container}

        ## INSTALL CONTAINER IMAGE
        run $0 load-image ${artifact_loc}/ctaas-dis_image.tar --container=${container} --machine=${machine_name} --token=${vault_token}

        echo ""
        echo "# INFO: Success: Container application [${container}] provisioned."
	echo "# INFO: Please review below log for ${container} container status in 5 secs ..."
        sleep 5
        echo ""
        run docker logs ${container} | tail -5

        ;;
    *)
        echo "# WARN: The [${action}] action is not recognized."
        exit 1
        ;;

esac
)
    echo $? > ${exitcode_file}
) | tee -a ${log_file}

echo "# Ending log... [`date`]" >> ${log_file}
exit_code=`cat ${exitcode_file}`
rm -f ${exitcode_file}

## START A SHELL?
if [ "${action}" = "shell" ]; then
    _shell="docker exec -it ${container} bash"
    prompt "${_shell}"
    ${_shell}
fi

exit ${exit_code}
