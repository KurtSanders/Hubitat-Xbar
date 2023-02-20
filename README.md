## Hubitat → Xbar (For Use with Hubitat Elevation® & Apple®![macOS logo](https://raw.githubusercontent.com/KurtSanders/Hubitat-Xbar/main/Images/macos_logo.jpg))
-- 
### Version 1.x (Beta Release) 

![Hubitat → Xbar App-V2 logo](https://raw.githubusercontent.com/KurtSanders/Hubitat-Xbar/main/Images/Hubitat-Xbar-Macbook-Pro.png)

![Hubitat → Xbar App-V2 logo](https://raw.githubusercontent.com/KurtSanders/Hubitat-Xbar/main/Images/Hubitat-Xbar-Menu.png)

### Features:

* Designed only for use with [Xbar](https://xbarapp.com/dl), Hubitat Elevation® and Apple®  MacOS
* Quickly view and control various Hubitat®: 
   * Devices & Sensors
   * Hub Modes 
   * Safety Monitor (HSM)

### Overview:
Monitor and control [Hubitat™](https://hubitat.com/) devices, sensors, Hubitat® Safety Monitor, Modes from the Apple MacOS Menu Bar.  This application is controlled via the Hubitat user app named **'Hubitat → XBar App'**.  A Python3 script file is installed locally on the Apple Mac in the Xbar's Application plugin folder (~/Library/Application\ Support/xbar/plugins) and configured with your Hubitat OAuth application string.

The Hubitat → Xbar application works with the [Xbar](https://xbarapp.com/dl) as a custom XBar Plugin and is controlled via the Hubitat → Xbar App.  Hubitat → Xbar App **displays** thermostat information, temperature, relative humidity, event-log statistics, contacts, music players, presence devices, locks, lights, and motion sensors.  It can also **control** color control (RGB) device levels, switch/dimmer level devices, locks and also thermostat control via the MacOS menubar.  The Mac menubar icon can be a thermostat reading, contact sensor, lock sensor or switch sensor and upon clicking the displayed state icon, renders more detailed information on all the sensors selected in the App's GUI.

One can click on any controllable Hubitat device, mode or routine in the Mac's Hubitat → Xbar display to invoke the default action for that device, mode or routine.  Non-controllable devices (eg. presence sensors, motion sensors, temperature sensors) can show their event history.

Sensor battery levels can be displayed for devices that have a battery capability by depressing the Apple {option + R} keys as the Hubitat → Xbar menu is activated {being displayed}.

## Prerequisites 
*  Apple macOS 13.x with Python 3.x
* [XBar](https://xbarapp.com/dl) (Freeware)
* [Hubitat Package Manager](https://hubitatpackagemanager.hubitatcommunity.com/installing.html)
* Use of  Apple Terminal App (/System/Applications/Utilities/Terminal.app)
* [Knowledge of installing and configuring software on macOS](https://www.google.com/search?q=how+to+install+software+on+mac&rlz=1C5CHFA_enUS503US503&oq=how+to+install+softwate&aqs=chrome.2.69i57j0l5.9308j0j4&sourceid=chrome&ie=UTF-8)
* Member of the [Hubitat Community](https://community.hubitat.com/) for support and new release information.


## Installation

* Hubitat© Elevation Steps
	* Install **'Hubitat → Xbar App'** via [Hubitat Package Manager (HPM)](https://hubitatpackagemanager.hubitatcommunity.com/installing.html).
		* Launch HPM
		* Select Install 
		* Search for 'Xbar'
		* Press 'Next'
		* Wait for HPM download and install to complete and **'Hubitat → Xbar App'** should automatically launch for configuration.
		* Enable/Authorize OAuth API String in **'Hubitat → Xbar App'**
		* Select the generated OAuth API string from the screen and (⌘-C) copy it (format xxx-xxx~http://xxx) from the screen.
*  Apple© Computer macOS Steps
	* Install the [Xbar](https://xbarapp.com/) application base software by [clicking here](https://github.com/matryer/xbar/releases/download/v2.1.7-beta/xbar.v2.1.7-beta.dmg) to download and install XBar application  
	   * After Xbar install, there should be a 'xbar' in your macOS menubar as shown below <img src="http://xbarapp.com/public/img/xbar-menu-preview.png" width="300">
	   * Set Xbar's preferences as: 
	   		* :heavy_check_mark: 'Start at Login'
	   		* :heavy_check_mark: 'Update Automatically'
	* Install the Xbar Python Script **Hubitat_XBar.5m.py** to your Xbar plugin script file  folder ("~/Library/Application\ Support/xbar/plugins") using the  Apple Terminal shell commands below. 
		* Launch the  Apple **'Terminal' App** located in your  Apple Applications folder under 'Utilities' sub-folder. 
		* Highlight and copy (⌘-C) each of the following  Apple Terminal commands below and paste (⌘-V) each into the Terminal screen and press Enter:

	```Shell
			cd ~/Library/Application\ Support/xbar/plugins
			curl -s -O -J -L "https://raw.githubusercontent.com/KurtSanders/Hubitat-Xbar/main/Hubitat_XBar.5m.py" 
			sudo chmod +x ~/Library/Application\ Support/xbar/plugins/Hubitat_XBar.5m.py
			exit
	```
	* **Xbar Plugin Configuration Steps** 
		* Configure the required OAuth API (format xxx-xxx~http://xxx) in the Xbar 'Hubitat_XBar.5m.py' plugin
		* Click on the Xbar Icon in the top Mac menubar
			* Select 'Open Plugin' from xbar> submenu or Press **⌘+E** key while xbar is in focus.
			* Highlight the **Hubitat_XBar.5m.py** plugin
			* Paste (⌘-V) your Hubitat's OAuth API string in the 'Hubitat OAuth string' variable field

![Hubitat → Xbar App-V2 logo](https://raw.githubusercontent.com/KurtSanders/Hubitat-Xbar/main/Images/HubitatXbarPluginScreen.jpg)

* Hubitat© Elevation Hub
	* Launch the **'Hubitat → Xbar App'** on your hub.
	* Configure other **'Hubitat → Xbar App'** user preferences to select sensors, devices, and various display options.

* Xbar Plugin Refresh
	* **'Hubitat → Xbar App'** is set to automatically refresh values from Hubitat every 5 minutes.  Any more frequent polling may impact your hub's performance.  You may change the Xbar polling frequency in the Xbar's **Hubitat_XBar.5m.py** plugin options screen (⌘-E)
	* The xbar screen display can be refreshed manually by clicking on the xbar logo or displayed sensor icon/value in the Apple menubar, and pressing (⌘-R)
	* Some hubitat Sensors located in the Xbar displayed screen can be selected to chnage their state. 


## Issues / Limitations 
1. The Hubitat → Xbar  App is capable of cycling through multiple status bar items.  However, this Hubitat → Xbar  Plugin is designed to only display a ** temperature sensor, contact sensor, lock status, or switch sensor** at the top with the rest of the sensors displayed in the dropdown. 
2. The Hubitat → Xbar  app only allows a selection of one temp, lock, contact or switch sensor or a default 'Hubitat → Xbar ' title.   It is not recommended to use the full sensor name since menubar real estate is top dollar.
3. There is no horizontal alignment supported by Hubitat → Xbar  so it’s all done by character spacing, which means using Apple system monospace fonts for data content. Menlo is the default font, but feel free to change it in the ST Hubitat → Xbar  App in the mobile client Display Options.
4. Selection of a proportional spaced font, pitch and color can be used for all other text areas of the display, like the ST Categories and the ...more... sections.  Be aware that some fonts, colors and sizes may cause the menu to become illegible.  Blank field defaults in the options fields will return the display to normal.
5. Most areas of the menu will accomodate extended ascii character sets, but there might be areas that cannot.  If so, please rename these devices with US ascii characters and send a PM to me on the Hubitat Community Forum.
6. Be mindful of the # of devices selected, event history days/number settings as the Hubitat Hub can be affected by a performance slowdown. 

## Misc Features / Tips
* Hold the Apple ⌥ **Option** key while the Hubitat → Xbar  menu is open to display battery information for devices that can report battery level status.
* The max items per sensor category can be set in Hubitat → Xbar  Output SmartApp Menu Options (if you want to save space but still have access to the sensors)
* Use Apple ⌘R **Command-R** key sequence while viewing the Hubitat → Xbar App menu to **Refresh** Xbar's Hubitat sensors and devices, otherwise Xbar will automatically refresh every 5 minutes (default).
* Use Hubitat Package Manager to keep the **Hubitat → Xbar** application code current.  You must manually download the latest version of the Xbar's python plugin code **Hubitat_XBar.5m.py** following the steps in the [Installation](#Installation) section above.
* **Emoji short-names** are special graphical icons that can be displayed for custom device status.  Please note that these short naming convention is ':xxxxx:' and the name must be entered exactly as they are named on the [Emoji Website Valid Names List](http://www.webpagefx.com/tools/emoji-cheat-sheet/)
* Many other display options are provided and activated in the Hubitat → Xbar App and are either optional or required.
