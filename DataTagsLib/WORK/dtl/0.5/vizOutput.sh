#!/bin/bash 
dot -Tpdf definitions.tags.gv > out-definitions.pdf
#dot -Tpdf questionnaire.flow-ast.gv > out-ast.pdf
dot -Tpdf questionnaire.flow-fcs.gv > out-flowchart.pdf
