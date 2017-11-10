#!/bin/sh
#
# Make the version infomation corresponding to package version
oldVer=$(grep -rn 'private String currentVersion =' ${SOURCE}/${PACKAGE_NAME}/* | awk -F= '{print $2}' | awk -F\" '{print $2}')
curVer=$(echo ${PACKAGE_NAME} | awk -F- '{print $2}')
sed -i "s/${oldVer}/${curVer}/g" ${SOURCE}/${PACKAGE_NAME}/data/src/com/ami/kvm/jviewer/gui/JViewerApp.java

