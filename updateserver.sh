#!/bin/bash

# Variables
SCREEN="dev"
FOLDER="devserver"

# Replace current build with latest
sudo cp -f /home/jenkins/jobs/Minecraft-Slayer/workspace/build/Slayer.jar /home/minecraft/$FOLDER/plugins/Slayer.jar

# Reload server
sudo screen -S $SCREEN -p 0 -X stuff "`printf "/say Updating to the latest Slayer build!\r"`";
sudo screen -S $SCREEN -p 0 -X stuff "`printf "/reload\r"`";
sudo screen -S $SCREEN -p 0 -X stuff "`printf "/say Update complete.\r"`";