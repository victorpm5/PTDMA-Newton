package com.victor.newton.domain

class Event() {
    var initTime: Long = 0
    var endTime: Long = 0
    var title: String = ""
    var descripcio: String = ""
    var allDay: Boolean = false
    var recurrent: String = ""

    override fun toString(): String {
        return "Event(initTime=$initTime, endTime=$endTime, title='$title', descripcio='$descripcio', allDay=$allDay, recurrent='$recurrent')"
    }
}

