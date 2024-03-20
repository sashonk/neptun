#include "./ru_asocial_games_neptun_dungeonmaker_DungeonMaker.h"
#include "DungeonMaker.h"
#include <cassert>
#include <cstdlib>     //for rand()
#include <cstdio>      //for file handling
#include <iostream>   //for console traffic
#include <fstream>

JNIEXPORT void JNICALL Java_ru_asocial_games_neptun_dungeonmaker_DungeonMaker_generateDungeon
                         (JNIEnv * env, jobject, jstring jDesignFileName, jstring jOutFileName) {
    using namespace alifegames;
    using namespace std;

    const char* cDesignFileName = env->GetStringUTFChars(jDesignFileName, NULL);
    DungeonMaker theDungeonMaker;
    theDungeonMaker.Init_fromFile(cDesignFileName, NULL);
    theDungeonMaker.generate();
    env->ReleaseStringUTFChars(jDesignFileName, cDesignFileName);

    const char *cOutFileName = env->GetStringUTFChars(jOutFileName, NULL);
    std::string cppOutFileName(cOutFileName);

	std::fstream f;
	f.open(cppOutFileName, std::ios_base::out);
	env->ReleaseStringUTFChars(jOutFileName, cOutFileName);

	for (int indX = 0; indX < theDungeonMaker.GetDimX(); indX++) {
		for (int indY = 0; indY < theDungeonMaker.GetDimY(); indY++) {
			SquareData dat = theDungeonMaker.GetMap(indX, indY);
			f << dat << ",";
		}
		f << std::endl;
	}
	f.close();
  }