#!/bin/bash

g++ -Wno-deprecated -o libgeniajni.so -shared -mt -D_REENTRANT -I/usr/local/jdk1.7.0_13/include/ -I/usr/local/jdk1.7.0_13/include/linux/ au_edu_uq_eresearch_biolark_ta_genia_GeniaJNI.cpp maxent.o tokenize.o bidir.o morph.o chunking.o namedentity.o