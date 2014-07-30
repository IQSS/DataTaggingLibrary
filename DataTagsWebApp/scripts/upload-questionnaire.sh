#!/bin/bash

# This script requires a file called "username" in the same directory, with the username of the user in the test server.

USER=$(cat $(dirname $0)/username)

echo username: $USER

DIR=q-`date +%m%d`
ssh $USER@dvnweb-vm1.hmdc.harvard.edu "mkdir tagging-server/$DIR"
scp -r public/questionnaire/* $USER@dvnweb-vm1.hmdc.harvard.edu:tagging-server/$DIR


