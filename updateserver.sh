#!/bin/bash

# Variables
SCREEN="dev"
FOLDER="devserver"

# Replace current build with latest
sudo cp -f /home/jenkins/jobs/Minecraft-Slayer/workspace/build/Slayer.jar /home/minecraft/$FOLDER/plugins/Slayer.jar

# Reload server
screen -S $SCREEN -p 0 -X stuff "`printf "/say Updating to the latest Slayer build!\r"`";
screen -S $SCREEN -p 0 -X stuff "`printf "/reload\r"`";
screen -S $SCREEN -p 0 -X stuff "`printf "/say Update complete.\r"`";