# BitBarApp (For Hubitat & MacOS![macOS logo](https://raw.githubusercontent.com/KurtSanders/STBitBarApp-V2/master/Images/macos_logo.jpg))
-- 
### Version 6.x (Alpha) 
[Change-log & Version Release Features](https://github.com/KurtSanders/STBitBarApp-V2/wiki/Features-by-Version) <img src="https://raw.githubusercontent.com/KurtSanders/STAmbientWeather/master/images/readme.png" width="50">

![STBitBarApp-V2 logo](https://raw.githubusercontent.com/KurtSanders/STBitBarApp-V2/master/Images/STBitBarApp-V2-Macbook-Pro.png)

![STBitBarApp-V2 logo](https://raw.githubusercontent.com/KurtSanders/STBitBarApp-V2/master/Images/STBitBarApp-V2-Menu.png)

### New V6.x Features:

* Designed only for Hubitat® and MacOS Ventura
* Added Hubitat® Safety Monitor (HSM)

## Overview:
Monitor and control [Hubitat™](https://hubitat.com/) devices, sensors, Smart Home Monitor, Modes & Routines from the Apple MacOS Menu Bar.  This application is controlled via the Hubitat user app named **BitBar Output App**.  Selected program scripts and configuration files are installed locally on the Apple Mac in the BitBar plugin folder.

The BitBarApp application works with the [macOS BitBar application](https://getbitbar.com/) as a custom BitBar Plugin and is controlled via the BitBar Output App.  BitBarApp **displays** thermostat information, temperature, relative humidity, event-log statistics, contacts, music players, presence devices, locks, lights, and motion sensors.  It can also **control** color control (RGB) device levels, switch/dimmer level devices, locks and also thermostat control via the MacOS menubar.  The Mac menubar icon can be a thermostat reading, contact sensor, lock sensor or switch sensor and upon clicking the displayed state icon, renders more detailed information on all the sensors selected in the App's GUI.

One can click on any controllable Hubitat device, mode or routine in the Mac's BitBar display to invoke the default action for that device, mode or routine.  Non-controllable devices (eg. presence sensors, motion sensors, temperature sensors) can show their event history.

Sensor battery levels can be displayed for devices that have a battery capability by depressing the Apple {option} key as the BitBar menu is activated {being displayed}.

## Prerequisites

* [Apple macOS 13.x with Python 3.x](https://en.wikipedia.org/wiki/MacOS)
* [BitBar Software Installed and Preferences set as 'Open at Login' and BitBar plugin folder designated \*Freeware\* ](https://getbitbar.com/)
* Hubitat™ Hub & devices
* [Knowledge of installing and configuring software on macOS](https://www.google.com/search?q=how+to+install+software+on+mac&rlz=1C5CHFA_enUS503US503&oq=how+to+install+softwate&aqs=chrome.2.69i57j0l5.9308j0j4&sourceid=chrome&ie=UTF-8)
* Member of the [Hubitat Community](https://community.hubitat.com/) for support and new release information.


## Section 1.0: Required Installation of core BitBar Application on MacOS:

> * Note: Mat Ryer's BitBar core app lets one put the output from any macOS script or program right in your macOS menu bar. And it's completely free. An impressive number of other plugins have already been contributed by a wide range of developers, and [his website](https://getbitbar.com/) makes it easy to find them.  

> * This BitBarApp plugin is uniquely specific to users of Hubitat™ and the installation and configuration are well documented below. 

#### Create/Upgrade BitBar Plugin Folder: <img src=https://raw.githubusercontent.com/KurtSanders/STBitBarApp-V2/master/Images/new-logo.png />

1. Download and Install [BitBar Core Software \*Freeware\* ](https://github.com/matryer/bitbar/releases/download/v1.9.2/BitBar-v1.9.2.zip)
	1. Download [BitBar-v1.9.2.zip](https://github.com/matryer/bitbar/releases/download/v1.9.2/BitBar-v1.9.2.zip) to your Downloads folder.
	2. Launch Finder and navigate to your Downloads folder
	3. Double click the BitBar-v1.9.2.zip file to unzip the file contents.  
	4. Drag/Move the BitBar.app into your Mac's Applications folder
	5. Launch BitBar.app from your Mac's Application folder
	6. Locate the BitBar icon displayed in the Mac's Top Menu Bar.  Click the BitBar icon to display preferences dialogue menu.
	7. In the Mac Menu Bar, click the BitBar Icon and set BitBar preferences to: 
		* √ Open at Login
		* Select the 'Plugin Folder' on the Mac (.ie `/Users/smith/BitBar` )
2. Launch the **Terminal.app** from the Mac Applications **'Utility'** SubFolder
3. In the terminal console window, enter the following:

 	`cd ~\Downloads`
 
4. Press <kbd>Return</kbd> 
5. Select, copy and paste one of the following entire string into the Mac's Terminal Console Window.  

	`curl -s -O -J -L "https://raw.githubusercontent.com/KurtSanders/STBitBarApp-V2/master/installation/HEBitBarInstall.command" && sh ./HEBitBarInstall.command`

6. Press <kbd>Return</kbd>  
7. Read install/upgrade messages from the script.
8. Your BitBar Plugin's directory has been created or upgraded to the latest BitBar release.

---

## Section 2.0: Hubitat™ Installation

### Section 2.1: Installing the BitBar Output App Code

Install the App Code to your Hubitat platform via the [hubitat-packagemanager](https://github.com/dcmeglio/hubitat-packagemanager#hubitat-packagemanager). Do NOT select to launch BitBar Output App after hubitat-packagemanager completes.  The BitBar application is listed in [hubitat-packagemanager](https://github.com/dcmeglio/hubitat-packagemanager#hubitat-packagemanager) under the categories of “Utility”, “Control”, “Convenience” and the tags of “Integrations”, “Monitoring”, “Dashboards”, “Tools & Utilities”.

After install of the BitBar Output App Code on Hubitat, you will need to follow the rest of the manual file & scripts install below where it is specific to Hubitat. After all macOS files & scripts are installed, you may then return to Hubitat™ web page and launch the BitBar Output App  You will need to authorize the Oauth and record the two API strings, add these two API strings to the HE_Python_Logic.cfg file and configure the BitBar Output App preferences for devices and display options.

### Section 2.2: Setting up BitBar and HE Plugin

> * Please make sure that the core BitBar application is installed and configured [per the instructions above](https://github.com/KurtSanders/STBitBarApp-V2#section-1-required-installation-of-core-bitbar-application).
>   
### Example Directory Structure of the BitBar Plugin Folder

<img src=https://raw.githubusercontent.com/KurtSanders/STBitBarApp-V2/master/Images/BitBarFolderHE.jpg width="600"/>
 	
1. Launch the BitBar Output App. Select API Setup and activate it.  Follow the on screen instructions and record the Hubitat URL and secret strings. 
2. Add your Hubitat URL and secret to the HE_Python_Logic.cfg file ***without any quotes***: Open the HE_Python_Logic.cfg with a text editor of your choice (eg. textedit). Put the Hubitat URL that was displayed in step 1 in the smartAppURL variable and Secret in the secret variable without any quotes. 
3. **Save** the **HE_Python_Logic.cfg** file in the ST subfolder.
4. Ensure **execution rights** for the plugins:
	* Launch the MacOS Terminal Application
	* Navigate to your BitBar Plugins directory (eg. cd)
	* Issue the admin commands on the following files: 
		* **chmod +x HE.5m.sh**
		* **chmod +x HE_Python_Logic.py**
	* Exit the MacOS Terminal

### Section 2.3: Configuring the BitBar Output App

1. Select the 'BitBar Output App' from the Hubitat Web Page 'Apps'.
2. Complete/customize your BitBar Top menu preferences by:
	* Selecting Devices: you must choose the devices you want to display/control then tap Done.
	* Selecting Display Options: you must select the display options for the MacOS menu.  Please note that some option values are required.
3. Exit the BitBar Output App and *refresh* the BitBar menu Bar on the macOS
3. You should see your Hubitat™ devices and status states’ in the MacOS menubar!

----

## Color Light Control Features:
* Dimmer and Color Controls in BitBar Sub-Menus

![](https://raw.githubusercontent.com/KurtSanders/STBitBarApp-V2/master/Images/new-logo.png)
![STBitBarApp-V2 logo](https://raw.githubusercontent.com/KurtSanders/STBitBarApp-V2/master/Images/Color-Control.jpg)


## Issues / Limitations 
1. The BitBar App is capable of cycling through multiple status bar items.  However, this ST BitBar Plugin is designed to only display a ** temperature sensor, contact sensor, lock status, or switch sensor** at the top with the rest of the sensors displayed in the dropdown. 
2. The SBitBar app only allows a selection of one temp, lock, contact or switch sensor or a default 'BitBar' title.   It is not recommended to use the full sensor name since menubar real estate is top dollar.
3. There is no horizontal alignment supported by BitBar so it’s all done by character spacing, which means using Apple system monospace fonts for data content. Menlo is the default font, but feel free to change it in the ST BitBar App in the mobile client Display Options.
4. Selection of a proportional spaced font, pitch and color can be used for all other text areas of the display, like the ST Categories and the ...more... sections.  Be aware that some fonts, colors and sizes may cause the menu to become illegible.  Blank field defaults in the options fields will return the display to normal.
5. Most areas of the menu will accomodate extended ascii character sets, but there might be areas that cannot.  If so, please rename these devices with US ascii characters and send a PM to me on the Hubitat Community Forum.
6. Be mindful of the # of devices selected, event history days/number settings as the Hubitat Hub can be affected by a performance slowdown. 

## Misc Features / Tips
* Hold the **Option** key while the BitBar menu is open to display battery information for devices that can report battery level status.
* The max items per sensor category can be set in BitBar Output SmartApp Menu Options (if you want to save space but still have access to the sensors)
* Use **Command-R** while viewing the BitBarApp-V2 menu to **Refresh** the devices, otherwise it will automatically refresh every 5 minutes.
* You can download the latest version of the Python code at the bottom of the BitBarApp  Menu under **BitBarApp Action & Shortcuts**
* **Emoji short-names** are special graphical icons that can be displayed for custom device status.  Please note that these short naming convention is ':xxxxx:' and the name must be entered exactly as they are named on the [Emoji Website Valid Names List](http://www.webpagefx.com/tools/emoji-cheat-sheet/)
* Many other display options are provided and activated in the BitBarApp SmartApp and are either optional or required.
