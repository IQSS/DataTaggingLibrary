#!/bin/bash
LIB_DIR='../../DataTagsLib/WORK/dtl/0.5'

rm public/questionnaire/*
cp $LIB_DIR/definitions.tags public/questionnaire/
cp $LIB_DIR/questionnaire.flow public/questionnaire/

