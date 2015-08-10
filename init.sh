#!/bin/bash

scriptDir=""

os=`uname`

# OS scecific initialization
case $os in
    Darwin)
        scriptDir=$(cd "$(dirname "$0")"; pwd)
    ;;
    Linux)
        scriptDir="$(dirname "$(readlink -f "$0")")"
    ;;
    *)
       echo "Unknown OS."
       exit 1
    ;;
esac

mkdir -p ${scriptDir}/src/test/resources/big

cd ${scriptDir}/src/test/resources/big

curl ftp://ftp.ncbi.nlm.nih.gov/blast/db/16SMicrobial.tar.gz | tar -xzv 
