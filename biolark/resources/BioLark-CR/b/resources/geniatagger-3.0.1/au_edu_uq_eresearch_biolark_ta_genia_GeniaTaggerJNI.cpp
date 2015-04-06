#include <jni.h>
#include <iostream>
#include <stdio.h>
#include <string.h>
#include <fstream>
#include <map>
#include <list>
#include <sstream>
#include "maxent.h"
#include "common.h"
#include "au_edu_uq_eresearch_biolark_ta_genia_GeniaTaggerJNI.h"

using namespace std;

string bidir_postag(const string & s, const vector<ME_Model> & vme, const vector<ME_Model> & cvme, bool dont_tokenize);
void bidir_chunking(vector<Sentence> & vs, const vector<ME_Model> & vme);
bool init_morphdic(const string & path);

extern bool load_ne_models(const string & path);

vector<ME_Model> vme(16);
vector<ME_Model> vme_chunking(16);

JNIEXPORT jstring JNICALL Java_au_edu_uq_eresearch_biolark_ta_genia_GeniaTaggerJNI_initialize(JNIEnv *env, jobject obj, jstring s) {
  const char *path = env->GetStringUTFChars(s, NULL);
  string result = "OK";

  bool load_dict = init_morphdic(path);
  if (!load_dict) {
    result = "Unable to load morph dictionaries ...";
    env->ReleaseStringUTFChars(s, path);
    return env->NewStringUTF(result.c_str());
  }

  for (int i = 0; i < 16; i++) {
    char buf[1000];
    sprintf(buf, "%s/models_medline/model.bidir.%d", path, i);
    vme[i].load_from_file(buf);
  }

  for (int i = 0; i < 8; i +=2 ) {
    char buf[1000];
    sprintf(buf, "%s/models_chunking/model.bidir.%d", path, i);
    vme_chunking[i].load_from_file(buf);
  }

  bool load_models = load_ne_models(path);
  if (!load_models) {
    result = "Unable to load NE models ...";
    env->ReleaseStringUTFChars(s, path);
    return env->NewStringUTF(result.c_str());
  }

  env->ReleaseStringUTFChars(s, path);
  return env->NewStringUTF(result.c_str());
}

JNIEXPORT jstring JNICALL Java_au_edu_uq_eresearch_biolark_ta_genia_GeniaTaggerJNI_tagSentence (JNIEnv *env, jobject obj, jstring sentence) {
  const char *sent = env->GetStringUTFChars(sentence, NULL);

  string result = bidir_postag(sent, vme, vme_chunking, false);

  env->ReleaseStringUTFChars(sentence, sent);
  return env->NewStringUTF(result.c_str());
}