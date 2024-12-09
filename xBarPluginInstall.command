#!/bin/bash
# Author: SandersSoft (c) 2023
# Hubitat → Xbar Plugin
# https://raw.githubusercontent.com/KurtSanders/Hubitat-Xbar/xBarPluginInstall.command
version="1.0.0"

echo "xBarPluginInstall Installer/Upgrader (c) SanderSoft $(date +%Y)"
echo "====================================================================="
echo "Version: ${version}"

# Begin Define Variables #
HExBarGitHubRawHttp="https://raw.githubusercontent.com/KurtSanders/Hubitat-Xbar/main"
HExBarPluginScriptFilename="Hubitat_XBar.5m.py"
HExBarPluginInstallDirectory="${HOME}/Library/Application Support/xbar/plugins"
downloadsFolder="$HOME/Downloads"
debugMode=true

# Check to see if Xbar is installed and plugin folder exists
printf "Checking for the existance of the xBar plugin folder: %s" "'${HExBarPluginInstallDirectory}'"

if [ ! -d "${HExBarPluginInstallDirectory}" ]; then 
    printf ".....NOT FOUND\n\n"
	echo "The xBar Plugin directory was not found at '$HExBarPluginInstallDirectory', Exiting..."
	echo "Please install the Xbar application and set the preferences and 'Open at Login' and 'Update Automatically'"
	echo "https://xbarapp.com/dl"
	exit 1
else
	printf ".....Success\n"
fi

### TESTING MODE CHECK
if [[ "${debugMode}" == "true" ]]
then
	HExBarPluginInstallDirectory="${downloadsFolder}"
	echo "TESTING DEBUG MODE.. Using '${HExBarPluginInstallDirectory}' folder as xBar Plugins folder"
fi
read -p "Please confirm to download '${HExBarPluginScriptFilename}' to '${HExBarPluginInstallDirectory}' Are you sure? Y|N " -n 1 -r
echo    # (optional) move to a new line
if [[ ! $REPLY =~ ^[Yy]$ ]]
then
    echo "Cancelled"
    exit 1
fi

echo "Changing to the ${HExBarPluginInstallDirectory} folder.."
cd "${HExBarPluginInstallDirectory}"
echo "Downloading ${HExBarPluginScriptFilename} from ${HExBarGitHubRawHttp}/${HExBarPluginScriptFilename}"
curl --progress-bar -OLJ  "${HExBarGitHubRawHttp}/${HExBarPluginScriptFilename}"
echo "Changing ${HExBarPluginScriptFilename} to executible with chmod +x"
sudo chmod +x "${HExBarPluginInstallDirectory}/${HExBarPluginScriptFilename}"
echo "Install/Update completed..."
