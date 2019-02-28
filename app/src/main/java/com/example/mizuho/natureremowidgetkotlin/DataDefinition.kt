package com.example.mizuho.natureremowidgetkotlin


data class SettingData(var temp: String = "", var mode: String = "", var vol: String = "", var dir: String = "", var button: String = "power-off") {
    fun toRequestBody(): String {
        return "temperature=$temp&operation_mode=$mode&air_volume=$vol&air_direction=$dir&button=$button"
    }
}

data class Status(val te: Double = 0.0, val il: Double = 0.0, val hu: Double = 0.0)



// ウィジェットの設定データ
data class IRWidgetSettingData(
    val button0: ButtonInfo,
    val button1: ButtonInfo,
    val button2: ButtonInfo,
    val button3: ButtonInfo,
    val icon: String,
    val name: String
){
    override fun toString(): String {
        return """{
            |"icon": "$icon",
            |"name": "$name",
            |"button0": $button0,
            |"button1": $button1,
            |"button2": $button2,
            |"button3": $button3
            |}
        """.trimMargin()
    }
}

data class ButtonInfo(
    val buttonIcon: String,
    val signalId: String
) {
    override fun toString(): String {
        return """{
            |"buttonIcon": "$buttonIcon",
            |"signalId": "$signalId"
            |}
        """.trimMargin()
    }
}




// data format of response from https://api.nature.global/1/appliances
// use in IRWidgetSetting and ACWidgetSetting
data class Appliances(
    val aircon: Aircon?,
    val device: Any,
    val id: String,
    val image: String,
    val model: Any?,
    val nickname: String,
    val settings: Any?,
    val signals: List<Signal>?,
    val type: String
)

data class Device(
    val created_at: String,
    val firmware_version: String,
    val humidity_offset: Int,
    val id: String,
    val mac_address: String,
    val name: String,
    val serial_number: String,
    val temperature_offset: Int,
    val updated_at: String
)

data class Signal(
    val id: String,
    val image: String,
    val name: String
)


data class Aircon(
    val range: Range,
    val tempUnit: String
)

data class Range(
    val fixedButtons: List<String>,
    val modes: Modes
)

data class Modes(
    val auto: Auto = Auto(),
    val cool: Cool = Cool(),
    val dry:  Dry  = Dry(),
    val warm: Warm = Warm(),
    val blow: Blow = Blow()
)

data class Warm(
    val dir:  List<String> = listOf(),
    val temp: List<String> = listOf(),
    val vol:  List<String> = listOf()
)

data class Cool(
    val dir:  List<String> = listOf(),
    val temp: List<String> = listOf(),
    val vol:  List<String> = listOf()
)

data class Auto(
    val dir:  List<String> = listOf(),
    val temp: List<String> = listOf(),
    val vol:  List<String> = listOf()
)

data class Dry(
    val dir:  List<String> = listOf(),
    val temp: List<String> = listOf(),
    val vol:  List<String> = listOf()
)

data class Blow(
    val dir:  List<String> = listOf(),
    val temp: List<String> = listOf(),
    val vol:  List<String> = listOf()
)





// data format of response from https://api.nature.global/1/devices
// use in MonitorWidget
data class Devices(
    val created_at: String = "",
    val firmware_version: String = "",
    val humidity_offset: Int = 0,
    val id: String = "",
    val mac_address: String = "",
    val name: String = "",
    val newest_events: NewestEvents = NewestEvents(),
    val serial_number: String = "",
    val temperature_offset: Int = 0,
    val updated_at: String = "",
    val users: List<User> = listOf()
)

data class NewestEvents(
    val hu: Hu = Hu(),
    val il: Il = Il(),
    val te: Te = Te()
)

data class Hu(
    val created_at: String = "",
    val `val`: Any = 0
)

data class Il(
    val created_at: String = "",
    val `val`: Any = 0
)

data class Te(
    val created_at: String = "",
    val `val`: Any = 0
)

data class User(
    val id: String = "",
    val nickname: String = "",
    val superuser: Boolean = false
)