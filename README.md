# Hubitat → Xbar (For Hubitat & Apple MacOS![macOS logo](https://raw.githubusercontent.com/KurtSanders/Hubitat-Xbar/main/Images/macos_logo.jpg))
-- 
### Version 1.x (Beta Release) 

![Hubitat → Xbar App-V2 logo](https://raw.githubusercontent.com/KurtSanders/Hubitat-Xbar/main/Images/Hubitat-Xbar-Macbook-Pro.png)

![Hubitat → Xbar App-V2 logo](https://raw.githubusercontent.com/KurtSanders/Hubitat-Xbar/main/Images/Hubitat-Xbar-Menu.png)

### Features:

* Designed only for use with [Xbar](https://xbarapp.com/dl), Hubitat® and Apple MacOS
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
* Apple macOS 13.x with Python 3.x
* [XBar](https://xbarapp.com/dl) 
* [Hubitat Package Manager](https://hubitatpackagemanager.hubitatcommunity.com/installing.html)
* Use of Apple Terminal App (/System/Applications/Utilities/Terminal.app)
* [Knowledge of installing and configuring software on macOS](https://www.google.com/search?q=how+to+install+software+on+mac&rlz=1C5CHFA_enUS503US503&oq=how+to+install+softwate&aqs=chrome.2.69i57j0l5.9308j0j4&sourceid=chrome&ie=UTF-8)
* Member of the [Hubitat Community](https://community.hubitat.com/) for support and new release information.


## Installation

* Install [Xbar application](https://xbarapp.com/) base software.
   * [Click to download and install XBar application](https://xbarapp.com/dl) 
   * Set Xbar's preferences as: 
   		* :heavy_check_mark: 'Start at Login'
   		* :heavy_check_mark: 'Update Automatically'
* Install Xbar Python Script [Hubitat_XBar.5m.py](https://raw.githubusercontent.com/KurtSanders/Hubitat-Xbar/main/Hubitat_XBar.5m.py) to your Xbar plugin folder ("~/Library/Application\ Support/xbar/plugins"). 
	* Open the Apple **'Terminal' App** in your Mac Applications folder. 
	* Copy the following Apple Terminal commands below and paste each into the Terminal screen and press Enter:

```Shell
	cd ~/Library/Application\ Support/xbar/plugins
	curl -s -O -J -L "https://raw.githubusercontent.com/KurtSanders/Hubitat-Xbar/main/Hubitat_XBar.5m.py" 
	sudo chmod +x ~/Library/Application\ Support/xbar/plugins/Hubitat_XBar.5m.py
	exit
```

* Install **'Hubitat → Xbar App'** via [Hubitat Package Manager](https://hubitatpackagemanager.hubitatcommunity.com/installing.html) if not already installed.
	* Press 'Add User App' **'Hubitat → Xbar App'** on your Hub Applications view.
	* Launch the installed **'Hubitat → Xbar App'** in Hubitat's Application view and authorize the OAuth API access and copy the generated OAuth API string (format xxx-xxx~http://xxx) for the app.
		* Configure other user preferences in the **'Hubitat → Xbar App'**
* Configure the required OAuth API (format xxx-xxx~http://xxx) in Xbar 'Hubitat_XBar.5m.py'
![Hubitat → Xbar App-V2 logo](https://raw.githubusercontent.com/KurtSanders/Hubitat-Xbar/main/Images/HubitatXbarPluginScreen.jpg)
	* Click on the Xbar Icon in the top Mac menubar
	* Select 'Open Plugin' from xbar> submenu or Press **⌘+E** key while xbar is in focus.
	* Highlight the **Hubitat_XBar.5m.py** plugin
	* Enter your Hubitat's OAuth API string in the 'Hubitat OAuth string' variable field 
* Hubitat™ Hub & devices


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
