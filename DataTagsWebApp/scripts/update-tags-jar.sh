#!/bin/bash
LIB_DIR='../../DataTagsLib/'
pushd $LIB_DIR 
ant jar
popd
mv $LIB_DIR/dist/*.jar ../datatags-app/lib

