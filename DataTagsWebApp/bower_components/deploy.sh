#!/bin/bash

echo Copying Dists
APP_PUBLIC=../datatags-app/public

echo Bootstrap
cp bootstrap/dist/css/bootstrap.min.css $APP_PUBLIC/css/bootstrap.min.css
cp bootstrap/dist/css/bootstrap-theme.min.css $APP_PUBLIC/css/bootstrap-theme.min.css
cp bootstrap/dist/fonts/* $APP_PUBLIC/fonts/
cp bootstrap/dist/js/bootstrap.min.js $APP_PUBLIC/js/bootstrap.min.js

echo JQuery
cp jquery/dist/jquery.min.js $APP_PUBLIC/js/jquery.min.js

echo Font-Awesome
cp font-awesome/css/font-awesome.min.css $APP_PUBLIC/css/
cp font-awesome/fonts/* $APP_PUBLIC/fonts/
