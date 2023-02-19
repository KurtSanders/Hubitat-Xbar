/**
 *  Hubitat → XBar Plugin
 *
 *  Copyright 2018,2019,2020,2021,2022,2023 Kurt Sanders
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
// Major XBar Version requires a change to the Python3 Version, Minor XBar Version numbering will still be compatible with lower minor Python versions
import groovy.json.JsonSlurper
import java.util.ArrayList;
import groovy.time.*

definition(
    name: "Hubitat → XBar Plugin",
    namespace: "kurtsanders",
    author: "kurtsanders",
    description: "Display and control Hubitat device information in the top macOS Menu Bar application using Xbar",
    category: "My Apps",
    iconUrl:    "https://raw.githubusercontent.com/KurtSanders/Hubitat-Xbar/master/Images/Hubitat-Xbar.png",
    iconX2Url:  "https://raw.githubusercontent.com/KurtSanders/Hubitat-Xbar/master/Images/Hubitat-Xbar.png",
    iconX3Url:  "https://raw.githubusercontent.com/KurtSanders/Hubitat-Xbar/master/Images/Hubitat-Xbar-120.png")

preferences {
  page(name:"mainPage")
  page(name:"devicesManagementPage")
  page(name:"devicesPage")
  page(name:"devicesTopMenuBarPage")
  page(name:"optionsPage")
  page(name:"fontsPage")
  page(name:"iconsPage")
  page(name:"categoryPage")
  page(name:"eventsPage")
  page(name:"batteryPage")
  page(name:"disableAPIPage")
  page(name:"enableAPIPage")
  page(name:"APIPage")
  page(name:"APISendSMSPage")
}

mappings {

    path("/GetStatus/") {
        action: [
            GET: "getStatus"
        ]
    }
    path("/ToggleSwitch/") {
        action: [
            GET: "toggleSwitch"
        ]
    }
    path("/SetMusicPlayer/") {
        action: [
            GET: "setMusicPlayer"
        ]
    }
    path("/SetLevel/") {
        action: [
            GET: "setLevel"
        ]
    }
    path("/SetColor/") {
        action: [
            GET: "setColor"
        ]
    }
    path("/SetThermo/") {
        action: [
            GET: "setThermo"
        ]
    }
    path("/SetMode/") {
        action: [
            GET: "setMode"
        ]
    }
    path("/SetHSM/") {
        action: [
            GET: "setHSM"
        ]
    }
    path("/ToggleLock/") {
        action: [
            GET: "toggleLock"
        ]
    }
    path("/ToggleValve/") {
        action: [
            GET: "toggleCloseOpen"
        ]
    }
    path("/Test/") {
        action: [
            GET: "test"
        ]
    }

}
def test() {
    def msg = ["Http API Test Success!"]
    if (infoBool) log.info msg
    return msg
}

def installed() {
    if (debugBool) log.debug "Installed with settings: ${settings}\n"
	initialize()
}
def uninstalled() {
	if (state.endpoint) {
		try {
			if (infoBool) log.info "Revoking API access token"
			revokeAccessToken()
		}
		catch (e) {
			if (debugBool) log.warn "Unable to revoke API access token: $e"
		}
	}
}
def updated() {
	// Added additional logging from @kurtsanders
    log.info "#####################################################################################"
    log.info "${state.accessToken}~${state.endpointURL}"
    log.info "The Xbar Oauth API has been setup. Add the following API string to the Xbar Plugin Browser Hubitat Oauth String field"
    log.info "#####################################################################################"
	unsubscribe()
    if (infoBool) {
        log.info "Info logging messages has been activated for the next 30 minutes."
        runIn(1800,infoOff)
    }
    if (debugBool) runIn(1800,debugOff) {
        log.info "Debug logging messages has been activated for the next 30 minutes."
    }
    if (debugDevices) runIn(1800,debugDevicesOff){
        log.info "Debug Devices logging messages has been activated for the next 30 minutes."
    }
	initialize()
}

def initialize() {
	if(thermos)
    	thermos.each {
			subscribe(it, "thermostatOperatingState", thermostatOperatingStateHandler)
        }
    state.lastThermostatOperatingState = now()
    state.eventsDays = eventsDays
    state.eventsMax = eventsMax
}
def thermostatOperatingStateHandler(evt) {
	if (debugBool) log.debug "thermostatOperatingStateHandler received event"
	state.lastThermostatOperatingState = now()
}

def infoOff() {
    app.updateSetting("infoBool",[value:"false",type:"bool"])
    if (infoBool) {log.warn "Info logging disabled."}
}

def debugOff() {
    app.updateSetting("debugBool",[value:"false",type:"bool"])
    if (debugBool) {log.warn "Debug logging disabled."}
}

def debugDevicesOff() {
    app.updateSetting("debugDevices",[value:"false",type:"bool"])
    if (debugDevices) {log.warn "Debug Devices logging disabled."}
}

def setHSM() {
	def command = params.id
    if (infoBool) log.info "setHSM called with id ${command}"
    if (infoBool) "Current HSM State: ${location.hsmStatus}"
    sendLocationEvent (name: "hsmSetArm", value: command)
    if (infoBool) "Changed HSM State: ${location.hsmStatus}"
}

def setMode() {
	def command = params.id
    if (debugBool) log.debug "CurrentMode Before setMode : ${location.mode}"
	if (debugBool) log.debug "setMode called with id ${command}"
    setLocationMode(command)
    if (debugBool) log.debug "CurrentMode After setMode  : ${location.mode}"
}

def toggleSwitch() {
    def command = params.id
    if (infoBool) log.info "toggleSwitch called with id ${command}"
    switches.each {
        if(it.id == command)
        {
            if (infoBool) log.info "Found switch ${it.displayName} with id ${it.id} with current value ${it.currentSwitch}"
            if(it.currentSwitch == "on") {
                if (infoBool) log.info "Turning ${it.displayName} OFF"
                it.off()
            } else {
                if (infoBool) log.info "Turning ${it.displayName} ON"
                it.on()
            }
            return
        }
    }
}

def toggleValve() {
	def command = params.id
	if (infoBool) log.info "toggleValve called with id ${command}"
    valves.each {
    	if(it.id == command)
        {
            if (infoBool) log.info "Found Valve ${it.displayName} with id ${it.id} with current value ${it.currentValve}"
            if(it.currentValve == "close")
            	it.open()
            else
            	it.close()
            return
		}
    }
}
def setMusicPlayer() {
    def id 			= params.id
	def command 	= params.command
    def level 		= params.level
    def presetid 	= params.presetid
    if (infoBool) log.info "setMusicPlayer called with command ${command} for musicplayer id ${id} VolumeParm=${level} and presetid=${presetid}"
    musicplayersWebSocket.each {
        if(it.id == id)  {
            if (debugBool) log.debug "Found Music Player: ${it.displayName} with id: ${it.id} with current volume level: ${it.currentVolume}"
            switch (command) {
                case 'level':
                if (debugBool) log.debug "Setting New Volume level from ${it.currentVolume} to ${level}"
                it.setVolume(level)
                return
                break
                case 'preset':
                if (debugBool) log.debug "Setting Preset to id: ${presetid}"
                it.playPreset(presetid)
                return
                break
                default:
                    if (supportedMusicPlayerDeviceCommands().contains(command)) {
                        if (debugBool) log.debug "The Music Playback device was sent command: '${command}'"
                        it."$command"()
                    } else {
                        if (debugBool) log.debug "The Music Playback device was sent and invalid playback/track control command: '${command}'"
                    }
                break
                return
            }
        }
    }
}
def setLevel() {
	def command = params.id
    def level = params.level
	if (debugBool) log.debug "setLevel called with id ${command} and level ${level}"
    switches.each {
    	if(it.id == command)
        {
        	if (debugBool) log.debug "Found switch ${it.displayName} with id ${it.id} with current value ${it.currentSwitch}"
            def fLevel = Float.valueOf(level)
            if (debugBool) log.debug "Setting level to ${fLevel}"
            it.setLevel(fLevel)
            return
		}
    }
}
def setColor() {
	def pid = params.id
    def colorName = params?.colorName
    def saturation = params?.saturation
    if (debugBool) log.debug "========================================================="
	if (debugBool) log.debug "setColor() called with id ${pid}, colorName ${colorName} and saturation ${saturation}"
    switches.each {
        if(it.id == pid)
        {
            if (debugBool) log.debug "Found switch ${it.displayName} with id ${it.id} with current color of ${it.currentColor} and is ${it.currentSwitch}"
            if (it.currentSwitch=="off"){it.on()}
			if (it.hasCommand('setColor') & colorName  != null) {
                if (debugBool) log.debug "Setting ${it.displayName}'s color to ${colorName}, Hue: ${getHueSatLevel(colorName)} and RGB values: ${colorUtil.hexToRgb(it.currentColor)}"
                it.setColor(getHueSatLevel(colorName))
            }
            if (it.hasCommand('setSaturation') & saturation != null) {
                if (debugBool) log.debug "Setting ${it.displayName}'s saturation to: ${saturation.toInteger()}"
                it.setSaturation(saturation.toInteger())
            }
            if (debugBool) log.debug "${it.displayName}'s color is now RGB value: ${it.currentColor} = ${colorUtil.hexToRgb(it.currentColor)}, saturation level ${it.currentSaturation}, dimmer level ${it.currentLevel}"
            return
        }
    }
    if (debugBool) log.debug "========================================================="
}
def setThermo() {
	def id = params.id
    def cmdType = params.type
    def val = params.val.toInteger()
	if (debugBool) log.debug "setThermo called with id ${id} command ${cmdType} and value ${val}"
    if(thermos) {
    thermos.each {
            if(it.id == id) {
            if (debugBool) log.debug "setThermo found id ${id}"
                if(cmdType == "mode") {
                    if(val == "auto") {
                        if (debugBool) log.debug "Setting thermo to auto"
                        it.auto()
                    }
                    if(val == "heat") {
                        if (debugBool) log.debug "Setting thermo to heat"
                        it.heat()
                    }
                    if(val == "cool") {
                        if (debugBool) log.debug "Setting thermo to cool"
                        it.cool()
                    }
                    if(val == "off") {
                        if (debugBool) log.debug "Setting thermo to off"
                        it.off()
                    }
                }
                if(cmdType == "heatingSetpoint") {
                    if (debugBool) log.debug "Setting Heat Setpoint to ${val}"
                    it.setHeatingSetpoint(val)
                }
                if(cmdType == "coolingSetpoint") {
                    if (debugBool) log.debug "Setting Cool Setpoint to ${val}"
                    it.setCoolingSetpoint(val)
                }
            }
        }
    }
}
def toggleLock() {
	def command = params.id
	if (debugBool) log.debug "toggleLock called with id ${command}"
    def counter = 0

    locks.each {
        if(it.id == command)
        {
            if (debugBool) log.debug "Found your lock '${it.displayName}' with device id ${it.id} with current value '${it.currentLock}'"
            if(it.currentLock == "locked") {
                it.unlock()
            }
            else if(it.currentLock == "unlocked") {
                it.lock()
            }
            else {
                if (debugBool) log.debug "Non-supported toggle state for lock ${it.displayName} state ${it.currentLock} let's not do anything"
                return
            }
            if (debugBool) log.debug "Lock Refresh() Loop: ${counter} & LockStatus: ${it.currentLock}"
            return
        }
    }
}

def getBatteryInfo(dev) {
//    if (dev.capabilities.any { it.name.contains('Lock') } == true) log.debug "${dev} Attributes/Capabilities: ${dev.supportedAttributes}"

//    if (debugBool) log.debug "batteryExcludedDevices = ${batteryExcludedDevices}"
    if (batteryExcludedDevices) {
        if (batteryExcludedDevices.contains(dev.id)) {
            if (debugBool) log.debug "${dev} Battery Level: Excluded in ${app.name} Preferences"
            return "N/A"
        }
    }
    if (dev.capabilities.any { it.name.contains('Battery') } == true) {
        def batteryMap
        try {
//            batteryMap = dev.currentBattery
            batteryMap = dev.currentValue("battery")
//            if (debugBool) log.debug "${dev} Battery Level: ${batteryMap}%"
        }
        catch (all) {
            return "N/A"
        }
        if(batteryMap) {
            if(state.batteryWarningPct == null || state.batteryWarningPct.toInteger() < 0 || state.batteryWarningPct.toInteger() > 100 ) {
                state.batteryWarningPct = 50
            }
            if(batteryMap.toInteger() <= state.batteryWarningPct.toInteger()){
                return ["${batteryMap}%", batteryWarningPctEmoji == null?" :grimacing: ":" " + batteryWarningPctEmoji + " "]
            }
            return ["${batteryMap}%", ""]
        }
    }
    return "N/A"
}

def getTempData() {
	def resp = []
    temps.each {
        resp << [name: it.displayName, value: it.currentTemperature, battery: getBatteryInfo(it), eventlog: getEventsOfDevice(it)];
    }
    resp.sort { -it.value }
    return resp
}
def getContactData() {
	def resp = []
    contacts.each {
        resp << [name: it.displayName, value: it.currentContact, battery: getBatteryInfo(it), eventlog: getEventsOfDevice(it)];
    }
    return resp
}
def getRelativeHumidityMeasurementData() {
	def resp = []
    relativeHumidityMeasurements.each {
        resp << [name: it.displayName, value: it.currentHumidity, battery: getBatteryInfo(it), eventlog: getEventsOfDevice(it)];
    }
    return resp
}
def getPresenceData() {
	def resp = []
    def eventlogData = []
    def timeFormatString = timeFormatBool?'EEE MMM dd HH:mm z':'EEE MMM dd hh:mm a z'
    presences.each {
		it.events().each {
        }
        resp << [name: it.displayName, value: it.currentPresence, battery: getBatteryInfo(it), eventlog: getEventsOfDevice(it)];
    }
    return resp
}
def getMotionData() {
	def resp = []
    motions.each {
        resp << [name: it.displayName, value: it.currentMotion, battery: getBatteryInfo(it), eventlog: getEventsOfDevice(it)];
    }
    return resp
}
def getSwitchData() {
    def resp = []
    switches.each {
        def x, isRGBbool, hue, saturation, colorRGBName, r, g, b, RGBHex, colorRGBList = null
        isRGBbool = it.hasCommand('setColor')
        hue = it.currentHue
        saturation = it.currentSaturation
        if (isRGBbool) {
            if (it.hasCommand('refresh')) {it.refresh()}
            RGBHex = it.currentColor
            try {
                colorRGBList = colorUtil.hexToRgb(RGBHex)
            } catch (all) {
                if (debugBool) log.debug "Trapped Error: colorRGBList = colorUtil.hexToRgb(RGBHex): RGBHex = ${RGBHex}"
                colorRGBList=[0,0,0]
            }
            r = colorRGBList[0]
            g = colorRGBList[1]
            b = colorRGBList[2]
            if      (r>=g & g>=b) {colorRGBName = "Red–yellow"}
            else if (g>r  & r>=b) {colorRGBName = "Yellow–green"}
            else if (g>=b & b>r ) {colorRGBName = "Green–cyan"}
            else if (b>g  & g>r ) {colorRGBName = "Cyan–blue"}
            else if (b>r  & r>=g) {colorRGBName = "Blue–magenta"}
            else if (r>=b & b>g ) {colorRGBName = "Magenta–red"}
            else {colorRGBName = ""}
//            if (debugBool) log.debug "colorRGBName = ${colorRGBName.padRight(20,"-")}"
            x = [
                name		: it.displayName,
                value		: it.currentSwitch,
                colorRGBList: colorRGBList,
                RGBHex		: RGBHex,
                dimmerLevel : it.currentLevel,
                hue			: hue ? hue.toFloat().round() : hue,
                saturation	: saturation ? saturation.toFloat().round() : saturation
            ]
//            x.each {k, v -> log.debug "${k.padRight(20,"-")}: ${v}" }
        }
        resp << [
            name		: it.displayName,
            value		: it.currentSwitch,
            id 			: it.id,
            isDimmer 	: it.hasCommand('setLevel'),
            colorRGBName: colorRGBName,
            dimmerLevel : it.currentLevel,
            isRGB		: isRGBbool,
            hue			: hue ? hue.toFloat().round() : hue,
            saturation	: saturation ? saturation.toFloat().round() : saturation,
            eventlog	: getEventsOfDevice(it)
        ]
    }
    return resp
}
def getLockData() {
	def resp = []
    locks.each {
        resp << [name: it.displayName, value: it.currentLock, id : it.id, battery: getBatteryInfo(it), eventlog: getEventsOfDevice(it)];
    }
    return resp
}
def getMusicPlayerData() {
    def resp = []
    musicplayersWebSocket.each {
        resp << [
            name							: it.displayName,
            manufacturer					: it?.getManufacturerName()?:it?.currentDeviceStyle?:null,
            groupRolePrimary				: it?.currentGroupRole=='primary',
            groupRole						: it?.currentGroupRole,
            id 								: it.id,
            level							: it.currentVolume,
            mute							: it.currentMute,
            presets							: it.currentPresets?:null,
            status							: it.currentPlaybackStatus,
            audioTrackData					: it.currentAudioTrackData?:null,
            alexaPlaylists 					: it.currentAlexaPlaylists?:null,
            supportedCommands				: supportedMusicPlayerDeviceCommands(),
            groupVolume						: it.currentGroupVolume?:null
        ];
    }

    return resp
}
def getThermoData() {

	def resp = []
    if(thermos) {
        thermos.each {
            def timespan = now() - state.lastThermostatOperatingState
            resp << [displayName: it.displayName,
                    id: it.id,
                    thermostatOperatingState: it.currentThermostatOperatingState,
                    thermostatMode: it.currentThermostatMode,
                    coolingSetpoint: it.currentCoolingSetpoint,
                    heatingSetpoint: it.currentHeatingSetpoint,
                    lastOperationEvent: timespan
                    ];
        }
    }
//    if (debugBool) log.debug "getThermoData: ${resp[2]}"
    return resp
}
def getWaterData() {
	def resp = []
    waters.each {
        resp << [name: it.displayName, value: it.currentWater, id : it.id, battery: getBatteryInfo(it), eventlog: getEventsOfDevice(it)];
    }
    return resp
}
def getValveData() {
	def resp = []
    valves.each {
        resp << [name: it.displayName, value: it.currentValve, id : it.id, battery: getBatteryInfo(it), eventlog: getEventsOfDevice(it)];
    }
    return resp
}

def getMainDisplayData() {
    def displaySensorCapabilitySize = displaySensorCapability?displaySensorCapability.size():0
    def returnCapability
    def returnName
    def returnValue
    def returnEmoji
    def fieldName
    def fieldValue
    def resp = []

    if (displaySensorShowName) {
        resp << [name: "hsm", label: "HSM Status", value: location.hsmStatus, capability: "hsm", emoji: ":ok:"];
    }
    if (displaySensorCapabilitySize == 0) {
        resp << [name: 'N/A', label: 'N/A', value: 'N/A', capability: 'N/A', emoji: 'unknown'];
    } else {
        for(int i = 0;i<displaySensorCapabilitySize;i++) {
            fieldName = "displaySensor${i}"
            fieldValue = settings.find{ it.key == fieldName }?.value
            if(fieldValue) {
                // if (debugBool) log.debug "${i} --> ${key} : ${fieldValue.displayName}"
                // if (debugBool) log.debug "${i} --> ${key} : ${fieldValue.currentValue(displaySensorCapability[i].replaceAll(/Measurement$|Sensor$/,''))}"
                returnName = fieldValue.displayName
                returnCapability = displaySensorCapability[i]
                returnValue = fieldValue.currentValue(displaySensorCapability[i].replaceAll(/Measurement$|Sensor$/,''))
                // if (debugBool) log.debug "${i}-> returnValue = ${returnValue}"

                switch (returnValue) {
                    case ~/[0-9]*\.?[0-9]+/:
                    returnEmoji = 'number'
                    break
                    case 'on':
                    case 'off':
                    case 'open':
                    case 'closed':
                    case 'locked':
                    case 'unlocked':
                    returnEmoji = returnValue
                    break
                    default:
                        returnEmoji = 'unknown'
                    break
                }
                resp << [name: returnName, label: fieldValue.displayName, 'value' : returnValue, capability: returnCapability, emoji: returnEmoji];
            }
        }
    }
    return resp
}

def getStatus() {
    def timeStamp = new Date().format("h:mm:ss a", location.timeZone);
    def lastUpdateTime = new Date().format("EEE, MMM d, h:mm a", location.timeZone)
    log.info "${app.name} getStatus() started at ${timeStamp} by ${params.nodename}"
    state.path = params.path
    if (params.text != null) {
        log.debug "API test was successfull"
        return
    }
    def newLabel = "${app.name}<span style='color:green;'> <font size='-1'>(Polled by ${params.nodename}@ ${lastUpdateTime.replace("AM", "am").replace("PM","pm")})</font></span>"
    app.updateLabel(newLabel)
    def tempData = getTempData()
    def contactData = getContactData()
    def presenceData = getPresenceData()
    def motionData = getMotionData()
    def switchData = getSwitchData()
    def lockData = getLockData()
    def thermoData = getThermoData()
    def mainDisplay = getMainDisplayData()
    def musicData = getMusicPlayerData()
    def relativeHumidityMeasurementData = getRelativeHumidityMeasurementData()
    def waterData = getWaterData()
    def valveData = getValveData()
    def optionsData = [ "useImages" 				: useImages,
                       "useAlbumArtworkImages" 		: useAlbumArtworkImages,
                       "sortSensorsName" 			: sortSensorsName,
                       "sortSensorsActive" 			: sortSensorsActive,
                       "mainMenuMaxItemsTemps" 		: mainMenuMaxItemsTemps,
                       "mainMenuMaxItemsMusicPlayers": mainMenuMaxItemsMusicPlayers,
                       "mainMenuMaxItemsContacts" 	: mainMenuMaxItemsContacts,
                       "mainMenuMaxItemsSwitches" 	: mainMenuMaxItemsSwitches,
                       "mainMenuMaxItemsMotion" 	: mainMenuMaxItemsMotion,
                       "mainMenuMaxItemsLocks" 		: mainMenuMaxItemsLocks,
                       "mainMenuMaxItemsWaters" 	: mainMenuMaxItemsWaters,
                       "mainMenuMaxItemsValves" 	: mainMenuMaxItemsValves,
                       "mainMenuMaxItemsRelativeHumidityMeasurements" : mainMenuMaxItemsRelativeHumidityMeasurements,
                       "showSensorCount"			: showSensorCount,
                       "mainMenuMaxItemsPresences" 	: mainMenuMaxItemsPresences,
                       "motionActiveEmoji"			: motionActiveEmoji,
                       "motionInactiveEmoji"		: motionInactiveEmoji,
                       "contactOpenEmoji"		 	: contactOpenEmoji,
                       "contactClosedEmoji"		 	: contactClosedEmoji,
                       "presenscePresentEmoji"		: presenscePresentEmoji,
                       "presensceNotPresentEmoji"	: presensceNotPresentEmoji,
                       "colorBulbEmoji"				: colorBulbEmoji,
                       "dimmerBulbEmoji"			: dimmerBulbEmoji,
                       "presenceDisplayMode"		: presenceDisplayMode,
                       "numberOfDecimals"			: numberOfDecimals,
                       "matchOutputNumberOfDecimals": matchOutputNumberOfDecimals,
                       "dimmerValueOnMainMenu"		: dimmerValueOnMainMenu,
                       "mainFontName"				: mainFontName,
                       "mainFontSize"				: mainFontSize,
                       "subMenuFontName"			: subMenuFontName,
                       "subMenuFontSize"			: subMenuFontSize,
                       "subMenuFontColor"			: subMenuFontColor,
                       "subMenuMoreColor"			: subMenuMoreColor,
                       "subMenuCompact"				: subMenuCompact,
                       "fixedPitchFontName"			: fixedPitchFontName,
                       "fixedPitchFontSize"			: fixedPitchFontSize,
                       "fixedPitchFontColor"		: fixedPitchFontColor,
                       "hortSeparatorBarBool"       : hortSeparatorBarBool,
                       "batteryWarningPct"			: batteryWarningPct,
                       "batteryWarningPctEmoji"		: batteryWarningPctEmoji,
                       "hsmDisplayBool"				: hsmDisplayBool,
                       "eventsTimeFormat"			: eventsTimeFormat,
                       "favoriteDevices"			: favoriteDevices,
                       "eventsShow"					: eventsShow,
                       "colorChoices"				: colorChoices ? colorChoices : [
                           "Soft White","White","Daylight","Warm White","Red","Green","Blue","Yellow","Orange","Purple","Pink","Cyan"
                       ],
                       "sortTemperatureAscending"	: (sortTemperatureAscending == null) ? false : sortTemperatureAscending
                      ]
    def resp = ["hsmState" : location.hsmStatus,
                "hubName"    : location.hub.name,
                "Temp Sensors" : tempData,
                "Contact Sensors" : contactData,
                "Presence Sensors" : presenceData,
                "Motion Sensors" : motionData,
                "Switches" : switchData,
                "Locks" : lockData,
                "Music Players" : musicData,
                "Thermostats" : thermoData,
                "Modes" : modesDisplayBool?location.modes:null,
                "CurrentMode" : modesDisplayBool?["name":location.mode]:null,
                "MainDisplay" : mainDisplay,
                "RelativeHumidityMeasurements" : relativeHumidityMeasurementData,
                "Waters" : waterData,
                "Valves" : valveData,
                "Options" : optionsData]

    if (debugDevices != null) {
        if (debugBool) log.debug "debugDevices = ${resp."${debugDevices}"}"
        resp."${debugDevices}".each{
            k ->
            k.each{
                m, n ->
                if (m == "name") {return}
                if (debugBool) log.debug "${k.name}-> ${m} : ${n}"
            }
        }
    }
    log.info "The 'getStatus()' routine has completed. Returning ${resp.size()} keys, Hub mode is '${location.mode.capitalize()}', HSM is '${location.hsmStatus.capitalize()}' states."
    return resp
}

private mainPage() {
    if (app.getInstallationState()=='INCOMPLETE'){
        def na="Value not initialized, awiting first run of ${app.name}"
    }
    def currentYear = new Date().format("yyyy", location.timeZone)
    dynamicPage(name: "mainPage", uninstall:true, install:true, submitOnChange: true) {
        def apiSetupState = (state.accessToken==null)?'Please complete API setup!':'API Setup is complete!'
        section( "API Access Setup" ) {
            app.updateSetting("testAPI", false)
            app.updateSetting("okSend", false)
            app.updateSetting("sendAPI", false)
            app.updateSetting("pushoverDevices", false)
            href name: "APIPageLink", title: "${apiSetupState}", description: (state.endpoint == null)?"You must add these two API strings to ${app.name}":"", page: "APIPage"
        }
        section("Sensor/Device Management & Setup") {
            href name: "devicesManagementPageLink", title: "Select sensors/devices", description: "", page: "devicesManagementPage"
        }
        section("Mac Menu Bar & SubMenu Display Options") {
            href name: "optionsPageLink", title: "Select display options", description: "", page: "optionsPage"
        }
        section(hideable: true, hidden: true, "Set a Custom Application Name") {
            def newLabel = app.name.takeWhile { it != '<' }
            input "changeLabel", "bool",
                title: "Change Application Name?  Default: ${newLabel}",
                default: false,
                submitOnChange: true,
                required: false
        }
        if (changeLabel) {
            section() {
                app.updateLabel(newLabel)
                label title: "Assign a Custom App Name", required: false
                app.updateSetting("changeLabel",[value:"false",type:"bool"])
            }
        }
        section(title: "Logging Options:", hideable: true, hidden: true) {
            paragraph "Enable Info logging for 30 minutes will enable info logs to show up in the Hubitat logs for 30 minutes after which it will turn them off. Useful for checking if the app is performing actions as expected."
            input "infoBool", "bool", title: "Enable Info logging for 30 minutes", submitOnChange: false, required:false, defaultValue: false
            paragraph "Enable Debug logging for 30 minutes will enable debug logs to show up in the Hubitat logs for 30 minutes after which it will turn them off. Useful for troubleshooting problems."
            input "debugBool", "bool", title: "Enable debug logging for 30 minutes", submitOnChange: false, required:false, defaultValue: false
            paragraph "Enable Debug Device logging for 30 minutes will enable debug logs to show up in the Hubitat logs for 30 minutes after which it will turn them off. Useful for troubleshooting problems."
            input "debugDevices", "enum",
                title: "Select a Sensor capability category to send debuging information to IDE Live Logging Window",
                options: ["Temp Sensors", "Contact Sensors", "Presence Sensors", "Motion Sensors", "Switches", "Locks",
                          "Music Players", "Thermostats", "RelativeHumidityMeasurements"].sort(),
                required: false,
                default: false,
                multiple: false
        }
        section {
            section() {
                paragraph("${bitBarLogo}" +
                          "<a href='https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=D4PYWK33KARSS&source=url'>Please consider donating to this application via PayPal™.</a><br>" +
                          "<small><i>Copyright \u00a9 2018-${currentYear} SandersSoft™ Inc - All rights reserved.</i></small><br>")
            }
        }
    }
}

def APIPage() {
    dynamicPage(name: "APIPage") {
        section("API Setup") {
            if (!state.accessToken) {
                paragraph "Required: The API has not been setup. Tap below to enable it."
                href name: "enableAPIPageLink", title: "Enable API", description: "", page: "enableAPIPage"
            }
            state.endpoint=getFullLocalApiServerUrl()+ "/?access_token=${state.accessToken}"
            def localUri = getFullLocalApiServerUrl()+ "/Test/" + "?access_token=${state.accessToken}"
            def XbarAPIString = "${state.accessToken}~${state.endpointURL}"
            paragraph "The Xbar API has been created below!<br><br>Activate Xbar on the Mac.  Copy & paste the following API string from the red box below into Xbar's Plugin Browser View for <u>Hubitat XBar for MacOS</u> <b>Hubitat Oauth String</b>"
            paragraph "<html><head></b><style>p.ex1{border: 5px solid red; padding-left: 10px;}</style></head><body><p class='ex1'><b>${XbarAPIString}/<br></b></body></html>"
        }
        section("API Testing Options") {
            input "testAPI", "bool",
                title: "Select to test the oauth http API access to this Hubitat application",
                submitOnChange: true,
                required: false
            if (testAPI) {
                def hubHttpIp = "http://${location.hub.localIP}:8080"
                try {
                    if (debugBool) log.debug "Testing http GET app access: '${localUri}'"
                    def params = [
                        uri        : hubHttpIp,
                        path       : "/apps/api/${app.getId()}/Test/",
                        query      : ["access_token": "${state.accessToken}"]
                    ]
                    // log.debug "getHttp params Map = '${params}'"
                    // "http://XXX.XXXX.XXX.XXX:8080/apps/api/NNN/Test/?access_token=xxxxxxxx"
                    httpGet(params) { resp ->
                        if (resp.success) {
                            paragraph("${resp.data}")
                            paragraph(doneImage)
                        } else {
                            paragraph("Failed: ${resp.data}")
                        }
                    }
                } catch (Exception e) {
                    if (debugBool) log.warn "Test Call to on failed: ${e.message}"
                    paragraph("Test Call to on failed: ${e.message}")
                }
                app.updateSetting("testAPI", false)
            }
            input "sendAPI", "bool",
                title: "Select to send this Xbar API string as a notification text message",
                submitOnChange: true,
                required: false
            if (sendAPI) {
                paragraph "Enable Pushover™ and/or Twilio™ service(s). (Must install virtual device(s) and have an active service account):"
                input ("pushoverEnabled", "bool", title: "Use Pushover™ and/or Twilio™ Service(s) for Alert Notifications", required: false, submitOnChange: true)
                if (pushoverEnabled) {
                    input(name: "pushoverDevices", type: "capability.notification", title: "Select a device below", required: false, multiple: true,
                          description: "Select notification device(s)", submitOnChange: true)
                    paragraph ""
                }
                if (pushoverDevices) {
                    input ("okSend", "bool", title: "Select to send API strings NOW to ${pushoverDevices}?", defaultValue: false, required: false, submitOnChange: true)
                    if (okSend) {
                        def msgData = "Add the following API string below to the ${app.name} Plugin in the 'Xbar Plugin Browser View'"
                        msgData += "\n\n${state.accessToken}"+"~${state.endpointURL}"
                        if (settings.pushoverDevices != null) {
                            settings.pushoverDevices.each {							// Use notification devices on Hubitat
                                it.deviceNotification(msgData)
                                paragraph "Sent API String to ${pushoverDevices.join(', ')}"
                            }
                            app.updateSetting("okSend", false)
                            app.updateSetting("sendAPI", false)
                        }
                    }
                }
            }
        }
        section() {
            href "disableAPIPage", title: "Disable/Reset API (Only use this if you want to generate a new oauth secret)", description: ""
        }
    }
}
def APISendSMSPage() {
    dynamicPage(name: "APISendSMSPage") {
        section() {
            if (sendAPI && phone) {
                paragraph "${app.name} API string sent to ${phone.replaceFirst('(\\d{3})(\\d{3})(\\d+)', '($1) $2-$3')}"
                sendPush("${app.name} API string sent to ${phone.replaceFirst('(\\d{3})(\\d{3})(\\d+)', '($1) $2-$3')}")
                sendSms(phone, "Add the following API string to the Xbar Plugin Browser Hubitat Oauth String field")
                sendSms(phone, "${state.accessToken}~${state.endpointURL}")
            }
        }
    }
}


def enableAPIPage() {
    dynamicPage(name: "enableAPIPage") {
        section() {
            if (initializeAppEndpoint()) {
                paragraph "Woo hoo! The API is now enabled. Brace yourself, though. I hope you don't mind typing long strings of gobbledygook. Sorry I don't know of an easier way to transfer this to the PC. Anyways, tap Done to continue"
            }
            else {
                paragraph "It looks like OAuth is not enabled in the ${app.name}. Please verify the OAuth setting in the Apps Code view.", title: "Looks like we have to enable OAuth.", required: true, state: null
            }
        }
    }
}

def disableAPIPage() {
    dynamicPage(name: "disableAPIPage", title: "") {
        section() {
            if (state.endpoint) {
                try {
                    revokeAccessToken()
				}
				catch (e) {
					if (debugBool) log.debug "Unable to revoke access token: $e"
				}
				state.endpoint = null
			}
			paragraph "It has been done. Your token has been REVOKED. You're no longer allowed in API Town (I mean, you can always have a new token). Tap Done to continue."
		}
	}
}

def devicesManagementPage() {
    dynamicPage(name:"devicesManagementPage") {

        section("Sensors & Devices (Required)") {
            href name: "devicesPageLink", title: "Select the sensors to display/control", required: true, description: "", page: "devicesPage"
        }
        section('Main Menu Bar Icons (Required)') {
            href name: "devicesTopMenuBarPageLink", title: "Select icons for the Main Menu Bar", required: true, description: "", page: "devicesTopMenuBarPage"
        }
        section("Favorite Sensors (Mix & Match) to display in separate 1st category section on subMenu") {
            paragraph "The 'Favorite' sensors submenu is a section on the BitBar submenu to locate a few sensors that you wish to display/monitor quickly.  You cannot control them from this menu, just display."
            input "favoriteDevices", "enum",
                title: "Favorite sensors (Optional)",
                options: getAllDevices(),
                required: false,
                multiple: true
        }
    }
}

def devicesTopMenuBarPage() {
    dynamicPage(name:"devicesTopMenuBarPage") {
        section("MacOS Main Menu BitBar: Select one device to display a status.") {
            paragraph "The MacOS Main Menu Bar runs along the top of the screen on your Mac"
            input name: "displaySensorCapability", type: "enum",
                title: "Mac Menu Bar: Select up to 4 Sensor Capabilities for the Menu Bar Icons",
                options: ['contactSensor':'Contact','lock':'Lock','switch':'Switch','temperatureMeasurement':'Temperature Measurement'],
                multiple: true,
                submitOnChange: true,
                required: false
            if (displaySensorCapability) {
                def displaySensorCapabilitySize = displaySensorCapability.size()
                if (displaySensorCapabilitySize>1) {
                    paragraph "The ${displaySensorCapabilitySize} device status icons will cycle every few seconds in the MacOS Main Menu Bar"
                }
                for(int i = 0;i<displaySensorCapabilitySize;i++) {
                    input "displaySensor${i}", "capability.${displaySensorCapability[i]}",
                        title: "Select the one ${displaySensorCapability[i].replaceAll(/Measurement$|Sensor$/,'').toUpperCase()} sensor to place in the Mac Main Menu Bar",
                        multiple: false,
                        submitOnChange: true,
                        required: true
                }
            }
        }
        section("Display Hubitat Safety Monitor in Main Menu Bar") {
            def displaySensorCapabilityMessage = displaySensorCapability?"along with the ${displaySensorCapability.size()} sensor(s) selected above":''
            input "displaySensorShowName", "bool",
                title: "Add the Hubitat Safety Monitor status icon to the Main Menu Bar ${displaySensorCapabilityMessage}",
                submitOnChange: true,
                default: false,
                required: true
        }
    }
}


def devicesPage() {
    dynamicPage(name:"devicesPage") {
        section ("Choose the sensor devices to be displayed & controlled in the ${app.name} menu") {
            paragraph "Select devices that you want to be displayed below the top MacOS Main Menu Bar (in the Xbar Sub-menu)."
            input "contacts", "capability.contactSensor",
                title: "Which Contact Sensors?",
                multiple: true,
                hideWhenEmpty: true,
                required: false
            input "relativeHumidityMeasurements", "capability.relativeHumidityMeasurement",
                title: "Which Relative Humidity Measurement Sensors?",
                multiple: true,
                hideWhenEmpty: true,
                required: false
            input "locks", "capability.lock",
                title: "Which Locks?",
                multiple: true,
                hideWhenEmpty: true,
                required: false
            input "musicplayersWebSocket", "capability.AudioTrackData",
                title: "Which Media Playback Devices (ie. Sonos, Amazon, Google, etc)?",
                multiple: true,
                hideWhenEmpty: true,
                required: false
            input "motions", "capability.motionSensor",
                title: "Which Motion Sensors?",
                multiple: true,
                hideWhenEmpty: true,
                required: false
            input "presences", "capability.presenceSensor",
                title: "Which Presence Sensors?",
                multiple: true,
                hideWhenEmpty: true,
                required: false
            input "switches", "capability.switch",
                title: "Which Lights & Switches?",
                multiple: true,
                hideWhenEmpty: true,
                required: false
            input "temps", "capability.temperatureMeasurement",
                title: "Which Temperature Sensors?",
                multiple: true,
                hideWhenEmpty: true,
                required: false
            input "thermos", "capability.thermostat",
                title: "Which Thermostats?",
                multiple: true,
                hideWhenEmpty: true,
                required: false
            input "waters", "capability.waterSensor",
                title: "Which Water Sensors?",
                multiple: true,
                hideWhenEmpty: true,
                required: false
            input "valves", "capability.valve",
                title: "Which Valves?",
                multiple: true,
                hideWhenEmpty: true,
                required: false
                }
    }
}

def iconsPage() {
    dynamicPage(name:"iconsPage", hideWhenEmpty: true) {
        section("Emoji Picker") {
            href(name: "hrefNotRequired",
                 title: "BROWSE Emoji's",
                 required: false,
                 image: "http://emojipedia.org/static/img/favicons/mstile-144x144.png",
                 style: "external",
                 url: "https://github-emoji-picker.vercel.app/",
                 description: "Tap here to view valid list of Emoji names and cut & paste into the fields below"
                )
        }
        section("Optional: Customize Sensor Type/Status Emoji Naming Display Options") {
            input "motionActiveEmoji", "text",
                title: "Emoji ShortCode ':xxx:' to Display for Motion Sensor = 'Active' Default='⇠⇢'",
                required: false
            input "motionInactiveEmoji", "text",
                title: "Emoji ShortCode ':xxx:' to Display for Motion Sensor = 'Inactive' Default='⇢⇠'",
                required: false
            input "contactOpenEmoji", "text",
                title: "Emoji ShortCode ':xxx:' to Display for Contact Sensor = 'Open' Default='⇠⇢'",
                required: false
            input "contactClosedEmoji", "text",
                title: "Emoji ShortCode ':xxx:' to Display for Contact Sensor = 'Closed' Default='⇢⇠'",
                required: false
            input "presenscePresentEmoji", "text",
                title: "Emoji ShortCode ':xxx:' to Display for Presence Sensor = 'Present' Default=':home:'",
                required: false
            input "presensceNotPresentEmoji", "text",
                title: "Emoji ShortCode ':xxx:' to Display for Presence Sensor = 'Not Present' Default=':x:'",
                required: false
            input "dimmerBulbEmoji", "text",
                title: "Emoji ShortCode ':xxx:' to Display for Switch DTH='Dimmer Capable Bulb'. Default=':sunny:'",
                required: false
            input "colorBulbEmoji", "text",
                title: "Emoji ShortCode ':xxx:' to Display for Switch DTH='Color Capable Bulb'. Default=':rainbow:'",
                required: false
            input "presenceDisplayMode", "enum",
                title: "Presence Display Mode Number (See Numeric Values Explanation Below)",
                multiple: false,
                options: [0,1,2,3],
                required: false
            paragraph image: "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d1/Emblem-notice.svg/2000px-Emblem-notice.svg.png",
                title: "Presence Display Mode",
                "0 = Default Mode: Do not sort or hide sensors by presence status\n" +
                "1 = Sort by Presence: Sensors will be sorted by their status; 'Present' shown first\n" +
                "2 = Show Sensors Not Present in Submenu: All sensors that are not present will be moved to a submenu\n" +
                "3 = Hide Sensors Not Present: Sensors that are currently not present will be hidden"
        }
    }
}

def fontsPage() {
    dynamicPage(name:"fontsPage", hideWhenEmpty: true) {
        section("Optional Main Menu: DISPLAY FONTS, COLORS & SEPARATOR BARS: Mac Font Name for Display (warning: The Font Name MUST exist on the Mac.  Leave blank for 'Arial'") {
            input "mainFontName", "enum",
                title: "Mac 'Main-Menu Bar' Font Name (default font is 'Arial' if field is left empty).  Color auto-changes based on Primary Thermostat Operation Mode (Heat/Cool) from/to Red/Blue",
                default: "Arial",
                options: fontChoiceList(),
                multiple: false,
                required: false
            input "mainFontSize", "number",
                title: "Mac 'Main-Menu Bar' Font Pitch Size (default is '14').",
                default: 14,
                required: false
        }
        section("Optional Data Display: DISPLAY FONTS, COLORS & SEPARATOR BARS: Mac Font Name for Display (warning: The Font Name MUST exist on the Mac.  Leave blank for 'Menlo'") {
            input "fixedPitchFontName", "enum",
                title: "Data: Fixed Font Name (default font is 'Menlo' if field is left empty)",
                default: "Menlo",
                options: ["Menlo","Monaco","Consolas","Courier","MingLIU"],
                multiple: false,
                required: false
            input "fixedPitchFontSize", "number",
                title: "Data: Fixed Font Pitch Size (default is '14').",
                default: 14,
                required: false
            input "fixedPitchFontColor", "enum",
                title: "Data: Fixed Font Color (default Color is 'Black' if field left blank.  If Mac is in 'Dark Mode', Font Color will be set to 'White')",
                default: "black",
                options: colorChoiceList(),
                multiple: false,
                required: false
        }
        section("Sub-Menu Display Options") {
            input "subMenuCompact", "bool",
                title: "Compact Sub-Menus (No repeating title in the submenu header or breakout menus)",
                required: false
            input "hortSeparatorBarBool", "bool",
                title: "Use Horizontal Separator Lines to Separate Device Categories (Default is True)",
                required: false
            input "subMenuFontName", "enum",
                title: "Mac BitBar Sub-Menu Category Names: Font Name (default font is 'Arial' if field is left empty).",
                default: "Arial",
                options: fontChoiceList(),
                multiple: false,
                required: false
            input "subMenuFontSize", "number",
                title: "Mac BitBar Sub-Menu Category Names: Font Pitch Size (default is '12').",
                default: 12,
                required: false
            input "subMenuFontColor", "enum",
                title: "Mac BitBar Sub-Menu Category Names: Font Color (default Color is 'red' if field left blank.  If Mac is in 'Dark Mode', Font Color will be set to 'White')",
                default: "red",
                options: colorChoiceList(),
                multiple: false,
                required: false
            input "subMenuMoreColor", "enum",
                title: "More... sub-menu: Font Color (default 'purple')",
                default: "purple",
                options: colorChoiceList(),
                multiple: false,
                required: false
        }

    }
}

def categoryPage() {
    dynamicPage(name:"categoryPage", hideWhenEmpty: true) {
        section("Optional: Number of Items per Sensor category to display in the Main Menu before moving additional sensors to a More... sub-menu.  Leave these number fields blank for Auto-Size (Show only Active Sensors in Main Menu)") {
            input "mainMenuMaxItemsContacts", "number",
                title: "Min Number of Contact Sensors to Display - Leave blank to Auto-Size",
                required: false
            input "mainMenuMaxItemsRelativeHumidityMeasurements", "number",
                title: "Min Number of Relative Humidity Measurement Sensors to Display - Leave blank to Auto-Size",
                required: false
            input "mainMenuMaxItemsLocks", "number",
                title: "Min Number of Locks to Display - Leave blank to Auto-Size",
                required: false
            input "mainMenuMaxItemsMusicPlayers", "number",
                title: "Min Number of Music Players to Display - Leave blank to Auto-Size",
                required: false
            input "mainMenuMaxItemsMotion", "number",
                title: "Min Number of Motion Sensors to Display - Leave blank to Auto-Size",
                required: false
            input "mainMenuMaxItemsPresences", "number",
                title: "Min Number of Presence Sensors to Display - Leave blank to Auto-Size",
                required: false
            input "mainMenuMaxItemsSwitches", "number",
                title: "Min Number of Switch Sensors to Display - Leave blank to Auto-Size",
                required: false
            input "mainMenuMaxItemsTemps", "number",
                title: "Min Number of Temperature Sensors to Display - Leave blank to Show All Temperature Sensors",
                required: false
            input "mainMenuMaxItemsValves", "number",
                title: "Min Number of Valves to Display - Leave blank to Show All Valves",
                required: false
            input "mainMenuMaxItemsWaters", "number",
                title: "Min Number of Water Sensors to Display - Leave blank to Show All Water Sensors",
                required: false
        }
    }
}

def eventsPage() {
    dynamicPage(name:"eventsPage", hideWhenEmpty: true) {
        section("Optional: Display Event History for Devices") {
            input "eventsShow", "bool",
                title: "Events: Show Event History?",
                required: false
                }
        section(hideable: true, hidden: true, "Optional: Event History Options for Devices") {
            input "eventsMax", "number",
                title: "Events: Maximum number of Device Events to Display [Default: 10, Max 100]",
                default: "10",
                required: false
            input "eventsDays", "number",
                title: "Events: Number of Days from Today to Retrieve Device Events [Default: 1, Max 7]",
                default: "1",
                required: false
            input "eventsTimeFormat", "enum",
                title: "Events: DateTime Format: [Default = 12 Hour Clock Format with AM/PM]",
                options: ["12 Hour Clock Format with AM/PM", "Military 24 Hour Clock Format"],
                default: "12 Hour Clock Format with AM/PM",
                required: true
        }
        section("BROWSE Emoji Website Valid 'ShortCodes' List") {
            href(name: "hrefNotRequired",
                 title: "BROWSE Emoji Website Valid 'ShortCodes' List",
                 required: false,
                 image: "http://emojipedia.org/static/img/favicons/mstile-144x144.png",
                 style: "external",
                 url: "http://www.webpagefx.com/tools/emoji-cheat-sheet/",
                 description: "tap here to view valid list of Emoji names in your mobile browser"
                )
        }
    }
}
def batteryPage() {
    dynamicPage(name:"batteryPage", hideWhenEmpty: true) {
        section("Optional: Battery Check for Selected Devices") {
            input "batteryExcludedDevices", "enum",
                title: "Battery: Select Any Devices to Exclude from Battery Check [Default='None']",
                options: getAllDevices(),
                required: false,
                multiple: true
        }
        section("Optional: Battery Level Options for Devices") {
            input "batteryWarningPctEmoji", "text",
                title: "Battery: Emoji ShortCode ':xxx:' to Display for Low Battery Warning [Default=':grimacing:']",
                required: false
            input "batteryWarningPct", "number",
                title: "Battery: Percent Level to Warn for Low Battery in Device [Default: 50, Min 0, Max 100]",
                default: "50",
                required: false
        }
    }
}

def optionsPage() {
    dynamicPage(name:"optionsPage", hideWhenEmpty: true) {
        section("Required: Smart Home Monitor, Modes & Routines") {
        paragraph "Display/Hide Hub Modes, Routines and SHM to the BitBar SubMenu to display state & allow control?"
            input "modesDisplayBool", "bool",
                title: "Show Hub Modes?",
                default: false,
                required: true
            input "hsmDisplayBool", "bool",
                title: "Show Hubitat Safety Monitor",
                default: false,
                required: true
        }
        section("Required: Use High-Res Images (More CPU required) or Low-Res Emojis") {
			input "useImages", "bool",
				title: "Use high-res images for switch & lock status (green/red images) instead of low-res emojis",
                default: true,
				required: true
			input "useAlbumArtworkImages", "bool",
				title: "Display hi-res album artwork images instead of open new browser tab to display	",
                default: false,
				required: true
        }
        section {
            input "showSensorCount", "bool",
                title: "Show the number of sensors in each sensor category title",
                default: false,
                required: true
        }
        section("Required: Sort sensors in menu by Name and Active Status") {
			input "sortSensorsName", "bool",
				title: "Sort sensors in menu by name",
                default: true,
				required: true
			input "sortSensorsActive", "bool",
				title: "Sort sensors in menu by Active Status",
                default: true,
				required: true
        }
        section("Optional: Temperature Values Display Options") {
            input "numberOfDecimals", "number",
                title: "Round temperature values with the following number of decimals",
                default: 1,
                required: true
            input "matchOutputNumberOfDecimals", "bool",
                title: "Match all temperature sensor values with the same amount of decimals",
                default: false,
                required: true
            input "sortTemperatureAscending", "bool",
                title: "Sort all temperature sensor values in High -> Low (decending) direction. Default: Low -> High",
                default: false,
                required: false
        }
        section("Optional: Dimmer Level Main Menu Display Option for Dimmer/Level Capable Switches/Lights") {
            input "dimmerValueOnMainMenu", "bool",
                title: "Display the dimmer level % value on the Main Menu after the name of the device? Note: The dimmer % value is always displayed on the sub-menu of the dimmer where the level can be changed.",
                default: false,
                required: false
        }
        section("Optional: Number of Devices per Sensor category to display") {
            href name: "categoryPageLink", title: "Number of Devices per Sensor Categories to Display Options", description: "", page: "categoryPage"
        }
        section("Optional: Battery Options") {
            href name: "batteryPageLink", title: "Battery Check/Display/Level", description: "", page: "batteryPage"
        }
        section("Optional: Event History Options for Devices") {
            href name: "eventsPageLink", title: "Event History", description: "", page: "eventsPage"
        }
        section("Optional: Font Names, Pitch Size and Colors") {
            href name: "fontsPageLink", title: "${app.name} Menu Font Text Display Settings", description: "", page: "fontsPage"
        }
        section("Optional: Customize Sensor Status & Type Emoji Display Options") {
            href name: "iconsPageLink", title: "Customize Emoji's for Sensor Status", description: "", page: "iconsPage"
        }
        section("Optional: Limit Color Choices for 'Color Control' capable devices") {
            input "colorChoices", "enum",
                title: "Only show these checked color choices in list (Default: All]",
                options: [["Soft White":"Soft White - Default"],
                ["White":"White - Concentrate"],
                ["Daylight":"Daylight - Energize"],
                ["Warm White":"Warm White - Relax"],
                "Red","Green","Blue","Yellow","Orange","Purple","Pink","Cyan"],
                required: false,
                multiple: true
        }
    }
}

private initializeAppEndpoint() {
    if(!state.accessToken) {
        createAccessToken()
    }
    try {
        state.endpoint=getFullLocalApiServerUrl()+ "/?access_token=${state.accessToken}"
        state.endpointURL=getFullLocalApiServerUrl()
    }
    catch(e) {
        state.remove('endpoint')
    }
}

def getEventsOfDevice(device) {
    def deviceHistory

    if (eventsShow==null || eventsShow==false) {return}

    if (state.eventsDays==null) {
        state.eventsDays = 1
    }
    if (state.eventsMax==null) {
        state.eventsMax = 10
    }
    if (state.eventsDays > 7) {state.eventsDays = 1}
    if (state.eventsMax > 100) {state.eventsMax = 100}

    def today = new Date()
    def then = timeToday(today.format("HH:mm"), TimeZone.getTimeZone('UTC')) - state.eventsDays
    def timeFormatString = eventsTimeFormat=="12 Hour Clock Format with AM/PM"?'EEE MMM dd hh:mm a z':'EEE MMM dd HH:mm z'
    try {
        deviceHistory = device.eventsBetween(then, today, [max: state.eventsMax])?.findAll{"$it.source" == "DEVICE"}?.collect{[date: it.date.format(timeFormatString , location.timeZone), name: it.name, value: it.value]}
    }
    catch (all) {
        return deviceHistory
    }
    return deviceHistory
}

private getAllDevices(BatteryCapabilityOnly=false) {
    def devicePickMap = [:]
    def dev_list =
        ([] + switches
         + dimmers
         + motions
         + accelerations
         + contacts
         + illuminants
         + temps
         + relativeHumidityMeasurements
         + locks
         + batteries
         + thermos
         + medias
         + musicplayers
         + speeches
         + colors
         + valves
         + waters
         + presences
         + leaks)?.findAll()?.unique { it.id }
    dev_list.each {
        if (BatteryCapabilityOnly) {
            if (it.capabilities.any { it.name.contains('Battery') } == true) {
                devicePickMap << ["${it.id}": "${it.displayName}"]
            }
        } else {
            devicePickMap << ["${it.id}": "${it.displayName}"]
        }
    }
    //    dev_list = dev_list.collect{ it.toString() }
    //    return dev_list.sort()
    //    if (debugBool) log.debug "devicePickMap: ${devicePickMap}"
    //    if (debugBool) log.debug "devicePickMap.sort() {it.value} = ${devicePickMap.sort() {it.value}}"
    return devicePickMap.sort() {it.value}
}

def getHueSatLevel(color) {
    def hueColor = 0
    def saturation = 100
	switch(color) {
		case "White":
			hueColor = 52
			saturation = 19
			break;
		case "Daylight":
			hueColor = 53
			saturation = 91
			break;
		case "Soft White":
			hueColor = 23
			saturation = 56
			break;
		case "Warm White":
			hueColor = 20
			saturation = 80 //83
			break;
		case "Blue":
			hueColor = 69
			saturation = 95
			break;
		case "DarkBlue":
			hueColor = 70
			break;
		case "Green":
			hueColor = 39
			break;
		case "Yellow":
			hueColor = 25
			break;
		case "Orange":
			hueColor = 10
			break;
		case "Purple":
			hueColor = 75
			break;
		case "Cyan":
			hueColor = 180
			break;
		case "Pink":
			hueColor = 83
			break;
		case "Red":
			hueColor = 100
			break;
	}
    return [hue: hueColor, saturation: saturation, level:100]
}

def fontChoiceList() {
 return ['Arial','Arial Black','Helvetica','Verdana','Tahoma','Trebuchet MS','Gill Sans','Times New Roman','Georgia','Palatino','Baskerville','Courier','Lucida Console','Monaco','Optima']
}

def colorChoiceList() {
    return ["lightseagreen","floralwhite","lightgray","darkgoldenrod","paleturquoise","goldenrod","skyblue","indianred","darkgray","khaki","blue",
            "darkred","lightyellow","midnightblue","chartreuse","lightsteelblue","slateblue","firebrick","moccasin","salmon","sienna","slategray","teal","lightsalmon",
            "pink","burlywood","gold","springgreen","lightcoral","black","blueviolet","chocolate","aqua","darkviolet","indigo","darkcyan","orange","antiquewhite","peru",
            "silver","purple","saddlebrown","lawngreen","dodgerblue","lime","linen","lightblue","darkslategray","lightskyblue","mintcream","olive","hotpink","papayawhip",
            "mediumseagreen","mediumspringgreen","cornflowerblue","plum","seagreen","palevioletred","bisque","beige","darkorchid","royalblue","darkolivegreen","darkmagenta",
            "orange red","lavender","fuchsia","darkseagreen","lavenderblush","wheat","steelblue","lightgoldenrodyellow","lightcyan","mediumaquamarine","turquoise","dark blue",
            "darkorange","brown","dimgray","deeppink","powderblue","red","darkgreen","ghostwhite","white","navajowhite","navy","ivory","palegreen","whitesmoke","gainsboro",
            "mediumslateblue","olivedrab","mediumpurple","darkslateblue","blanchedalmond","darkkhaki","green","limegreen","snow","tomato","darkturquoise","orchid","yellow",
            "green yellow","azure","mistyrose","cadetblue","oldlace","gray","honeydew","peachpuff","tan","thistle","palegoldenrod","mediumorchid","rosybrown","mediumturquoise",
            "lemonchiffon","maroon","mediumvioletred","violet","yellow green","coral","lightgreen","cornsilk","mediumblue","aliceblue","forestgreen","aquamarine","deepskyblue",
            "lightslategray","darksalmon","crimson","sandybrown","lightpink","seashell"].sort()
}

def supportedMusicPlayerDeviceCommands() {
    return ['nextTrack','pause','play','previousTrack','stop']
}

String getBitBarLogo(){ return "<img src=https://raw.githubusercontent.com/KurtSanders/STBitBarApp-V2/master/Images/STBitBarApp-V2.png width=60 style='float: left; padding: 0px 10px 0px 0px;' alt='BitBar Logo' height=60 align=left /><br>"}
String getDoneImage(){ return "<img src=https://raw.githubusercontent.com/KurtSanders/STBitBarApp-V2/master/Images/done.png width=60 style='float: left; padding: 0px 10px 0px 0px;' alt='Done!' height=60 align=left /><br>"}