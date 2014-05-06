#!/bin/bash

echo Copying Dists
cp bootstrap/dist/css/bootstrap.min.css ../datatags-app/public/css/bootstrap.min.css
cp bootstrap/dist/css/bootstrap-theme.min.css ../datatags-app/public/css/bootstrap-theme.min.css

cp bootstrap/dist/fonts/* ../datatags-app/public/fonts/

cp bootstrap/dist/js/bootstrap.min.js ../datatags-app/public/js/bootstrap.min.js

cp jquery/dist/jquery.min.js ../datatags-app/public/js/jquery.min.js

