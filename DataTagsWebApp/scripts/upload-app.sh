#!/bin/bash

# This script requires a file called "username" in the same directory, with the username of the user in the test server.

echo Creating app and copying it to the server
date
echo


source $(dirname $0)/vars.sh
APP=dist-`date +%m%d`

echo username: $USER
echo server: $SERVER
echo app: $APP


activator -Dsbt.log.noformat=true clean dist | tee activator.log
DIST=`grep "package is ready" activator.log | cut -d" " -f7`

echo Distribution file is $DIST
mv $DIST $APP

echo Uploading...
scp $APP $USER@$SERVER:tagging-server/$APP.zip

echo Cleaning up
rm $APP
rm activator.log

echo done
