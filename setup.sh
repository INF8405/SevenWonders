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

# sbt console intellij for mac
# launchctl setenv ANDROID_SDK_HOME "/usr/local/Cellar/android-sdk/r21"
# add => setenv PATH /usr/bin:/bin:/usr/sbin:/sbin:/usr/local/bin
# to /etc/launchd.conf

