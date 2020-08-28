#!/bin/bash

gradle clean
gradle :capsule:compileTestJava
gradle :capsule:copyToLib
