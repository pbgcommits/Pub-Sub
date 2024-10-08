#!/bin/zsh

mains=(broker directory publisher subscriber)
jars=(Broker Directory Publisher Subscriber)
#
#for folder in $folders;
#  do javac -d ~/Desktop/jars/ src/"$folder"/*.java src/Shared/*.java;
#
#done;
#
cd ~/Desktop/jars || (echo failure && exit);
for jar in $jars;
  do jar -cf "$jar".jar "$jar"Main.class;

done;


