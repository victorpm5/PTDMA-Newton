package com.victor.newton.services


import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract
import com.victor.newton.domain.Event
import java.time.*
import java.util.*


class CalendarService(private val context: Context) {

    val EVENT_PROJECTION: Array<String> = arrayOf(
        CalendarContract.Instances.BEGIN,
        CalendarContract.Instances.END,
        CalendarContract.Instances.TITLE,
        CalendarContract.Instances.ALL_DAY
    )

    private val PROJECTION_BEGIN_INDEX: Int = 0
    private val PROJECTION_END_INDEX: Int = 1
    private val PROJECTION_TITLE_INDEX: Int = 2
    private val PROJECTION_ALLDAY_INDEX: Int = 3


    fun getEvents(today: Boolean): ArrayList<Event> {

        val result = ArrayList<Event>()

        val startTimeMilis: Long
        val endTimeMilis: Long

        if(today) {
            startTimeMilis = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            endTimeMilis = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        } else {
            startTimeMilis = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            endTimeMilis = LocalDate.now().plusDays(2).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }

        val builder: Uri.Builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
        ContentUris.appendId(builder, startTimeMilis)
        ContentUris.appendId(builder, endTimeMilis)

        val cursor: Cursor = context.contentResolver.query(builder.build(), EVENT_PROJECTION,
            null,null, CalendarContract.Instances.BEGIN
        )!!

        while (cursor.moveToNext()) {
            val event = Event()
            event.initTime = cursor.getLong(PROJECTION_BEGIN_INDEX)
            event.endTime = cursor.getLong(PROJECTION_END_INDEX)
            event.title = cursor.getString(PROJECTION_TITLE_INDEX)
            event.allDay = (cursor.getInt(PROJECTION_ALLDAY_INDEX) > 0)

            result.add(event)
        }

        cursor.close()
        return result
    }

    @SuppressLint("MissingPermission")
    fun createEvent(event: Event){

        val calendarId = getCalendarId(context)

//        FREQ=WEEKLY;BYDAY=MO,WE,FR;INTERVAL=1
//        FREQ=MONTHLY;BYMONTHDAY=6;INTERVAL=2
//        RRULE:FREQ=WEEKLY;COUNT=5;BYDAY=TU,FR

        val values = ContentValues().apply {
            put(CalendarContract.Events.CALENDAR_ID, calendarId)
            put(CalendarContract.Events.EVENT_TIMEZONE, ZoneId.systemDefault().id)
            put(CalendarContract.Events.TITLE, event.title)
            put(CalendarContract.Events.DESCRIPTION, event.descripcio)
            put(CalendarContract.Events.DTSTART, event.initTime)

            if(event.recurrent.isNotBlank()){
                put(CalendarContract.Events.DURATION, event.endTime)
                put(CalendarContract.Events.RRULE, event.recurrent)
            } else{
                put(CalendarContract.Events.DTEND, event.endTime)
            }

            if(event.allDay){
                put(CalendarContract.Events.ALL_DAY, 1)
            }
        }

        val uri: Uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)!!

        val eventID: Long = uri.lastPathSegment!!.toLong()
        System.out.println("EventID = " + eventID)
        System.out.println("uri = " + uri)

    }

    @SuppressLint("MissingPermission")
    private fun getCalendarId(context: Context) : Long? {
        val projection = arrayOf(CalendarContract.Calendars._ID, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME)

        var calCursor = context.contentResolver.query(CalendarContract.Calendars.CONTENT_URI, projection,
            CalendarContract.Calendars.VISIBLE + " = 1 AND " + CalendarContract.Calendars.IS_PRIMARY + "=1",
            null, CalendarContract.Calendars._ID + " ASC"
        )

        if (calCursor != null && calCursor.count <= 0) {
            calCursor = context.contentResolver.query(CalendarContract.Calendars.CONTENT_URI, projection,
                CalendarContract.Calendars.VISIBLE + " = 1",
                null, CalendarContract.Calendars._ID + " ASC"
            )
        }

        if (calCursor != null) {
//            calCursor.moveToFirst()
            if (calCursor.moveToLast()) {
                val calName: String
                val calID: String
                val nameCol = calCursor.getColumnIndex(projection[1])
                val idCol = calCursor.getColumnIndex(projection[0])

                calName = calCursor.getString(nameCol)
                calID = calCursor.getString(idCol)

                System.out.println("Calendar name = $calName Calendar ID = $calID")

                calCursor.close()
                return calID.toLong()
            }
        }
        return null
    }

}
