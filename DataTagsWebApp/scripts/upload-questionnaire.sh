#!/bin/bash

# This script requires a file called "username" in the same directory, with the username of the user in the test server.

echo uploading the current questionnaire

source $(dirname $0)/vars.sh
DIR=q-`date +%m%d`

echo username: $USER
echo server: $SERVER
echo dir: $DIR

ssh $USER@$SERVER "mkdir tagging-server/$DIR"
scp -r public/questionnaire/* $USER@$SERVER:tagging-server/$DIR

echo done.

