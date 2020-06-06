package com.victor.newton.services


import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract
import com.victor.newton.domain.Event
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.*


class CalendarService(private val context: Context) {

    val EVENT_PROJECTION: Array<String> = arrayOf(
        CalendarContract.Instances.BEGIN,
        CalendarContract.Instances.END,
        CalendarContract.Instances.TITLE
    )

    private val PROJECTION_BEGIN_INDEX: Int = 0
    private val PROJECTION_END_INDEX: Int = 1
    private val PROJECTION_TITLE_INDEX: Int = 2


    fun getEvents(today: Boolean): ArrayList<Event> {

        val result = ArrayList<Event>()

        val startTimeMilis: Long
        val endTimeMilis: Long

        if(today) {
            startTimeMilis = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
            endTimeMilis = LocalDate.now().plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        } else {
            startTimeMilis = LocalDate.now().plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
            endTimeMilis = LocalDate.now().plusDays(2).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        }

        val builder: Uri.Builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
        ContentUris.appendId(builder, startTimeMilis)
        ContentUris.appendId(builder, endTimeMilis)

        val cursor: Cursor = context.contentResolver.query(
            builder.build(),
            EVENT_PROJECTION,
            null,null, CalendarContract.Instances.BEGIN
        )!!

        while (cursor.moveToNext()) {
            val event = Event()
            event.initTime = cursor.getLong(PROJECTION_BEGIN_INDEX)
            event.endTime = cursor.getLong(PROJECTION_END_INDEX)
            event.descripcio = cursor.getString(PROJECTION_TITLE_INDEX)

            result.add(event)
        }

        cursor.close()
        return result
    }

    @SuppressLint("MissingPermission")
    fun createEvent(){
        val calID: Long = 3
        val startMillis: Long = Calendar.getInstance().run {
            set(2020, 6, 15, 17, 30)
            timeInMillis
        }
        val endMillis: Long = Calendar.getInstance().run {
            set(2020, 6, 15, 18, 30)
            timeInMillis
        }

//        FREQ=WEEKLY;BYDAY=MO,WE,FR;INTERVAL=1
//        FREQ=MONTHLY;BYMONTHDAY=6;INTERVAL=2
//        RRULE:FREQ=WEEKLY;COUNT=5;BYDAY=TU,FR

        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, startMillis)
            put(CalendarContract.Events.DTEND, endMillis)
//            put(CalendarContract.Events.DURATION, endMillis)
//            put(CalendarContract.Events.RRULE, )
            put(CalendarContract.Events.TITLE, "test test")
            put(CalendarContract.Events.DESCRIPTION, "testEvent")
            put(CalendarContract.Events.CALENDAR_ID, calID)
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID())
        }
        val uri: Uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)!!

        val eventID: Long = uri.lastPathSegment!!.toLong()
        System.out.println("EventID = " + eventID)
        System.out.println("uri = " + uri)

    }

//    @SuppressLint("MissingPermission")
//    private fun getCalendarId(){
//
//        var calCursor: Cursor = context.getContentResolver().query(
//            CalendarContract.Calendars.CONTENT_URI,
//            projection,
//            CalendarContract.Calendars.VISIBLE + " = 1 AND " + CalendarContract.Calendars.IS_PRIMARY + "=1",
//            null,
//            CalendarContract.Calendars._ID + " ASC"
//        )!!
//        if (calCursor.count <= 0) {
//            calCursor = mContext.getContentResolver().query(
//                CalendarContract.Calendars.CONTENT_URI,
//                projection,
//                CalendarContract.Calendars.VISIBLE + " = 1",
//                null,
//                CalendarContract.Calendars._ID + " ASC"
//            )
//        }
//
//    }

}
