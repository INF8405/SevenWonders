#! /bin/sh

# Change .gitmodules to point your repo
git submodule init
git submodule update

# Publish multiset
cd  multiset
sbt publish-local
cd ..

cd  sbt-thrift
sbt publish-local

# sbt console intellij
# launchctl setenv ANDROID_SDK_HOME "/usr/local/opt/android-sdk"