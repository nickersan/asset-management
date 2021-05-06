#!/bin/sh

CURRENT_DIR=${PWD}
BUILD_LIST_FILE=${CURRENT_DIR}/build.txt

if [ ! -f ${BUILD_LIST_FILE} ]; then
  
  echo "File build.txt"
  exit 1

fi

cat ${BUILD_LIST_FILE} | while read projectDir 
do
  
  cd ${CURRENT_DIR}
  cd ${projectDir}
  mvn $@

  if [ $? != 0 ]; then

    exit 1
    
  fi
  
done