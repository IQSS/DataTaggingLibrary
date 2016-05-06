#!/bin/bash 
dot -Tpdf definitions.ts.gv > out-definitions.pdf
dot -Tpdf questionnaire.dg-ast.gv > out-ast.pdf
dot -Tpdf questionnaire.dg-fcs.gv > out-flowchart.pdf
# grep -v "_end" questionnaire.flow-fcs.gv > questionnaire.flow-fcs-ne.gv
# dot -Tpdf questionnaire.flow-fcs-ne.gv > out-flowchart-ne.pdf
