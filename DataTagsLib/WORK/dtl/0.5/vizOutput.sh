#!/bin/bash 

circo -Tpdf definitions.dtl.gv > out-definitions.pdf
dot -Tpdf Questionnaire.dtf-ast.gv > out-ast.pdf
dot -Tpdf Questionnaire.dtf-fcs.gv > out-flowchart.pdf
