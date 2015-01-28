#!/bin/bash

java -Xmx2G -Xms2G -cp "lib/*" -Djava.library.path=./resources/geniatagger-3.0.1 au.edu.uq.eresearch.biolark.cr.run.CR_Run cr.properties