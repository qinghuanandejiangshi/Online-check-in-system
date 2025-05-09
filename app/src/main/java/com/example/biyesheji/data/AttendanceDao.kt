package com.example.biyesheji.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.biyesheji.model.Attendance
import com.example.biyesheji.model.AttendanceStatus
import java.util.Date

/**
 * 签到数据访问对象
 */
class AttendanceDao(private val context: Context) {
    
    /**
     * 插入新的签到记录
     */
    fun insertAttendance(attendance: Attendance): Long {
        val db = DatabaseManager.getInstance().openDatabase()
        
        val values = ContentValues().apply {
            put(AppDatabase.COL_ATTENDANCE_ID, attendance.id)
            put(AppDatabase.COL_COURSE_ID, attendance.courseId)
            put(AppDatabase.COL_ATTENDANCE_TITLE, attendance.title)
            put(AppDatabase.COL_ATTENDANCE_DESCRIPTION, attendance.description)
            put(AppDatabase.COL_ATTENDANCE_CREATED_BY, attendance.createdBy)
            put(AppDatabase.COL_ATTENDANCE_CREATED_AT, attendance.createdAt.time)
            attendance.endTime?.let { put(AppDatabase.COL_ATTENDANCE_END_TIME, it.time) }
            put(AppDatabase.COL_ATTENDANCE_STATUS, attendance.status.name)
            put(AppDatabase.COL_ATTENDANCE_LOCATION, attendance.location)
        }
        
        val result = db.insert(AppDatabase.TABLE_ATTENDANCES, null, values)
        DatabaseManager.getInstance().closeDatabase()
        return result
    }
    
    /**
     * 结束签到
     */
    fun endAttendance(attendanceId: String): Int {
        val db = DatabaseManager.getInstance().openDatabase()
        
        val values = ContentValues().apply {
            put(AppDatabase.COL_ATTENDANCE_STATUS, AttendanceStatus.COMPLETED.name)
            put(AppDatabase.COL_ATTENDANCE_END_TIME, System.currentTimeMillis())
        }
        
        val result = db.update(
            AppDatabase.TABLE_ATTENDANCES,
            values,
            "${AppDatabase.COL_ATTENDANCE_ID} = ?",
            arrayOf(attendanceId)
        )
        
        DatabaseManager.getInstance().closeDatabase()
        return result
    }
    
    /**
     * 获取课程的所有签到
     */
    fun getAttendancesByCourseId(courseId: String): List<Attendance> {
        val db = DatabaseManager.getInstance().openDatabase()
        val attendances = mutableListOf<Attendance>()
        
        val cursor = db.query(
            AppDatabase.TABLE_ATTENDANCES,
            null,
            "${AppDatabase.COL_COURSE_ID} = ?",
            arrayOf(courseId),
            null,
            null,
            "${AppDatabase.COL_ATTENDANCE_CREATED_AT} DESC"
        )
        
        while (cursor.moveToNext()) {
            attendances.add(cursorToAttendance(cursor))
        }
        
        cursor.close()
        DatabaseManager.getInstance().closeDatabase()
        return attendances
    }
    
    /**
     * 获取教师创建的所有签到
     */
    fun getAttendancesByTeacherId(teacherId: String): List<Attendance> {
        val db = DatabaseManager.getInstance().openDatabase()
        val attendances = mutableListOf<Attendance>()
        
        val cursor = db.query(
            AppDatabase.TABLE_ATTENDANCES,
            null,
            "${AppDatabase.COL_ATTENDANCE_CREATED_BY} = ?",
            arrayOf(teacherId),
            null,
            null,
            "${AppDatabase.COL_ATTENDANCE_CREATED_AT} DESC"
        )
        
        while (cursor.moveToNext()) {
            attendances.add(cursorToAttendance(cursor))
        }
        
        cursor.close()
        DatabaseManager.getInstance().closeDatabase()
        return attendances
    }
    
    /**
     * 通过ID获取签到
     */
    fun getAttendanceById(attendanceId: String): Attendance? {
        val db = DatabaseManager.getInstance().openDatabase()
        
        val cursor = db.query(
            AppDatabase.TABLE_ATTENDANCES,
            null,
            "${AppDatabase.COL_ATTENDANCE_ID} = ?",
            arrayOf(attendanceId),
            null,
            null,
            null
        )
        
        val attendance = if (cursor.moveToFirst()) cursorToAttendance(cursor) else null
        cursor.close()
        DatabaseManager.getInstance().closeDatabase()
        
        return attendance
    }
    
    /**
     * 删除签到
     */
    fun deleteAttendance(attendanceId: String): Int {
        val db = DatabaseManager.getInstance().openDatabase()
        val result = db.delete(
            AppDatabase.TABLE_ATTENDANCES,
            "${AppDatabase.COL_ATTENDANCE_ID} = ?",
            arrayOf(attendanceId)
        )
        DatabaseManager.getInstance().closeDatabase()
        return result
    }
    
    /**
     * 更新签到状态
     */
    fun updateAttendance(attendance: Attendance): Boolean {
        val db = DatabaseManager.getInstance().openDatabase()
        
        val values = ContentValues().apply {
            put(AppDatabase.COL_ATTENDANCE_TITLE, attendance.title)
            put(AppDatabase.COL_ATTENDANCE_DESCRIPTION, attendance.description)
            put(AppDatabase.COL_ATTENDANCE_STATUS, attendance.status.name)
            put(AppDatabase.COL_ATTENDANCE_LOCATION, attendance.location)
            attendance.endTime?.let {
                put(AppDatabase.COL_ATTENDANCE_END_TIME, it.time)
            }
        }
        
        val result = db.update(
            AppDatabase.TABLE_ATTENDANCES,
            values,
            "${AppDatabase.COL_ATTENDANCE_ID} = ?",
            arrayOf(attendance.id)
        )
        
        DatabaseManager.getInstance().closeDatabase()
        return result > 0
    }
    
    /**
     * 将游标转换为签到对象
     */
    private fun cursorToAttendance(cursor: Cursor): Attendance {
        val idIndex = cursor.getColumnIndex(AppDatabase.COL_ATTENDANCE_ID)
        val courseIdIndex = cursor.getColumnIndex(AppDatabase.COL_COURSE_ID)
        val titleIndex = cursor.getColumnIndex(AppDatabase.COL_ATTENDANCE_TITLE)
        val descriptionIndex = cursor.getColumnIndex(AppDatabase.COL_ATTENDANCE_DESCRIPTION)
        val createdByIndex = cursor.getColumnIndex(AppDatabase.COL_ATTENDANCE_CREATED_BY)
        val createdAtIndex = cursor.getColumnIndex(AppDatabase.COL_ATTENDANCE_CREATED_AT)
        val endTimeIndex = cursor.getColumnIndex(AppDatabase.COL_ATTENDANCE_END_TIME)
        val statusIndex = cursor.getColumnIndex(AppDatabase.COL_ATTENDANCE_STATUS)
        val locationIndex = cursor.getColumnIndex(AppDatabase.COL_ATTENDANCE_LOCATION)
        
        val id = cursor.getString(idIndex)
        val courseId = cursor.getString(courseIdIndex)
        val title = cursor.getString(titleIndex)
        val description = cursor.getString(descriptionIndex)
        val createdBy = cursor.getString(createdByIndex)
        val createdAt = Date(cursor.getLong(createdAtIndex))
        
        val endTime = if (cursor.isNull(endTimeIndex)) null 
                     else Date(cursor.getLong(endTimeIndex))
                     
        val statusStr = cursor.getString(statusIndex)
        val status = try {
            AttendanceStatus.valueOf(statusStr)
        } catch (e: IllegalArgumentException) {
            AttendanceStatus.COMPLETED
        }
        
        val location = cursor.getString(locationIndex)
        
        return Attendance(
            id = id,
            courseId = courseId,
            title = title,
            description = description,
            createdBy = createdBy,
            createdAt = createdAt,
            endTime = endTime,
            status = status,
            location = location,
            attendees = emptyList() // 签到记录需要单独加载
        )
    }
} 