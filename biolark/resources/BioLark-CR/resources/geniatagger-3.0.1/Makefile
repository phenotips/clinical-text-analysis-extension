CFLAGS = -O2 -fPIC -DNDEBUG -Wno-deprecated
JNIFLAGS = -shared -pthread -D_REENTRANT -I/usr/lib/jvm/java-7-openjdk-amd64/include -I/usr/lib/jvm/java-7-openjdk-amd64/include/linux
CPP = g++
OBJS = maxent.o tokenize.o bidir.o morph.o chunking.o namedentity.o

geniatagger: $(OBJS)
	$(CPP) -o libgeniajni.so $(CFLAGS) $(JNIFLAGS) au_edu_uq_eresearch_biolark_ta_genia_GeniaTaggerJNI.cpp $(OBJS)
clean:
	/bin/rm -r -f $(OBJS) geniatagger *.so *.o *~ *.flc
.cpp.o:
	$(CPP) -c $(CFLAGS) $<