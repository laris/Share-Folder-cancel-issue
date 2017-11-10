#!/bin/sh

# Uncomment for Script debugging 

# Build tool for KVM JViewer application

# Clean the build directory
echo "Cleaning the build directory"
rm -rf ${BUILD}/${PACKAGE_NAME}/data/build
mkdir ${BUILD}/${PACKAGE_NAME}/data/build

# Compile java source files
echo "Compiling.."
${TOOLDIR}/JDK/bin/javac -version

${TOOLDIR}/JDK/bin/javac -d ${BUILD}/${PACKAGE_NAME}/data/build  ${BUILD}/${PACKAGE_NAME}/data/src/com/ami/kvm/jviewer/*.java ${BUILD}/${PACKAGE_NAME}/data/src/com/ami/kvm/jviewer/communication/*.java ${BUILD}/${PACKAGE_NAME}/data/src/com/ami/kvm/jviewer/gui/*.java  ${BUILD}/${PACKAGE_NAME}/data/src/com/ami/kvm/jviewer/hid/*.java ${BUILD}/${PACKAGE_NAME}/data/src/com/ami/kvm/jviewer/kvmpkts/*.java ${BUILD}/${PACKAGE_NAME}/data/src/com/ami/kvm/jviewer/lang/*.java ${BUILD}/${PACKAGE_NAME}/data/src/com/ami/kvm/jviewer/videorecord/*.java ${BUILD}/${PACKAGE_NAME}/data/src/com/ami/kvm/jviewer/common/*.java ${BUILD}/${PACKAGE_NAME}/data/src/com/ami/kvm/jviewer/jvvideo/*.java ${BUILD}/${PACKAGE_NAME}/data/src/com/ami/iusb/*.java ${BUILD}/${PACKAGE_NAME}/data/src/com/ami/iusb/protocol/*.java ${BUILD}/${PACKAGE_NAME}/data/src/com/ami/kvm/imageredir/*.java ${BUILD}/${PACKAGE_NAME}/data/src/com/ami/kvm/imageredir/cd/*.java ${BUILD}/${PACKAGE_NAME}/data/src/com/ami/kvm/isocaching/*.java ${BUILD}/${PACKAGE_NAME}/data/src/com/ami/kvm/jviewer/common/oem/*.java ${BUILD}/${PACKAGE_NAME}/data/src/com/ami/vmedia/*.java ${BUILD}/${PACKAGE_NAME}/data/src/com/ami/vmedia/gui/*.java
if [ $? != 0 ]
then
echo "JViewer Compilation failed"
exit 1
fi

# Copy resource files
#echo "Copying the resource files.."
mkdir ${BUILD}/${PACKAGE_NAME}/data/build/com/ami/kvm/jviewer/res/
cp ${BUILD}/${PACKAGE_NAME}/data/src/com/ami/kvm/jviewer/res/*.* ${BUILD}/${PACKAGE_NAME}/data/build/com/ami/kvm/jviewer/res
cp ${BUILD}/${PACKAGE_NAME}/data/manifest.tmp ${BUILD}/${PACKAGE_NAME}/data/build
cd ${BUILD}/${PACKAGE_NAME}/data/build

# Make a StandAloneApp directory in .workspace/temp, and copy class files to that directory, for building JViewer Stand #Alone Application jar.
if [ $CONFIG_SPX_FEATURE_SINGLE_STANDALONE_APP ]
then
	mkdir -p ${TEMPDIR}/StandAloneApp/data/
	cp -rf ${BUILD}/${PACKAGE_NAME}/data/build ${TEMPDIR}/StandAloneApp/data/
fi

# Create jar file
${TOOLDIR}/JDK/bin/jar -cfm ${BUILD}/${PACKAGE_NAME}/data/JViewer.jar manifest.tmp com
if [ $? != 0 ]
then
	echo "Failed"
exit 1
fi
cd ..
rm -rf ${BUILD}/${PACKAGE_NAME}/data/build

#invoke sign.sh to sign jar file
bash ${TEMPDIR}/../certs/sign.sh ${BUILD}/${PACKAGE_NAME}/data/JViewer.jar
if [ $? != 0 ]
then
        echo "Failed signing JViewer.jar"
exit 1
fi

echo "done"
