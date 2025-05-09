package com.example.biyesheji.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.core.database.getStringOrNull
import com.example.biyesheji.data.AppDatabase.COL_RECORD_ATTENDANCE_ID
import com.example.biyesheji.data.AppDatabase.COL_RECORD_ATTENDANCE_TIME
import com.example.biyesheji.data.AppDatabase.COL_RECORD_ID
import com.example.biyesheji.data.AppDatabase.COL_RECORD_STATUS
import com.example.biyesheji.data.AppDatabase.COL_RECORD_STUDENT_ID
import com.example.biyesheji.data.AppDatabase.TABLE_ATTENDANCE_RECORDS
import com.example.biyesheji.model.AttendanceRecord
import com.example.biyesheji.model.AttendanceRecordStatus
import java.util.Date
import java.util.UUID

/**
 * 签到记录数据访问对象
 */
class AttendanceRecordDao(private val context: Context, private val db: SQLiteDatabase) {
    
    /**
     * 初始化数据表
     */
    fun init() {
        val dbHelper = DatabaseHelper.getInstance(context)
        dbHelper.createTableIfNotExists(AppDatabase.TABLE_ATTENDANCE_RECORDS)
    }
    
    /**
     * 插入新的签到记录
     */
    fun insert(record: AttendanceRecord): Long {
        val values = ContentValues().apply {
            // 不需要设置ID，因为它是自动递增的
            put(COL_RECORD_ATTENDANCE_ID, record.attendanceId)
            put(COL_RECORD_STUDENT_ID, record.studentId)
            put(COL_RECORD_ATTENDANCE_TIME, record.attendanceTime)
            put(COL_RECORD_STATUS, record.status.toString())
        }
        return db.insert(TABLE_ATTENDANCE_RECORDS, null, values)
    }
    
    /**
     * 更新签到记录状态
     */
    fun update(record: AttendanceRecord): Int {
        val values = ContentValues().apply {
            put(COL_RECORD_ATTENDANCE_ID, record.attendanceId)
            put(COL_RECORD_STUDENT_ID, record.studentId)
            put(COL_RECORD_ATTENDANCE_TIME, record.attendanceTime)
            put(COL_RECORD_STATUS, record.status.toString())
        }
        return db.update(
            TABLE_ATTENDANCE_RECORDS,
            values,
            "$COL_RECORD_ID = ?",
            arrayOf(record.id.toString())
        )
    }
    
    /**
     * 根据签到ID获取所有签到记录
     */
    fun getByAttendanceId(attendanceId: Long): List<AttendanceRecord> {
        val records = mutableListOf<AttendanceRecord>()
        val cursor = db.query(
            TABLE_ATTENDANCE_RECORDS,
            null,
            "$COL_RECORD_ATTENDANCE_ID = ?",
            arrayOf(attendanceId.toString()),
            null,
            null,
            null
        )
        if (cursor.moveToFirst()) {
            do {
                records.add(cursorToAttendanceRecord(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return records
    }
    
    /**
     * 获取学生的所有签到记录
     */
    fun getByStudentId(studentId: Long): List<AttendanceRecord> {
        val records = mutableListOf<AttendanceRecord>()
        val cursor = db.query(
            TABLE_ATTENDANCE_RECORDS,
            null,
            "$COL_RECORD_STUDENT_ID = ?",
            arrayOf(studentId.toString()),
            null,
            null,
            null
        )
        if (cursor.moveToFirst()) {
            do {
                records.add(cursorToAttendanceRecord(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return records
    }
    
    /**
     * 检查学生是否已签到
     */
    fun hasStudentSignedIn(attendanceId: Long, studentId: Long): Boolean {
        val cursor = db.query(
            TABLE_ATTENDANCE_RECORDS,
            arrayOf(COL_RECORD_ID),
            "$COL_RECORD_ATTENDANCE_ID = ? AND $COL_RECORD_STUDENT_ID = ?",
            arrayOf(attendanceId.toString(), studentId.toString()),
            null,
            null,
            null
        )
        val result = cursor.count > 0
        cursor.close()
        return result
    }
    
    /**
     * 删除签到记录
     */
    fun delete(id: Long): Int {
        return db.delete(
            TABLE_ATTENDANCE_RECORDS,
            "$COL_RECORD_ID = ?",
            arrayOf(id.toString())
        )
    }
    
    /**
     * 删除指定考勤下的所有考勤记录
     */
    fun deleteByAttendanceId(attendanceId: Long): Int {
        return db.delete(
            TABLE_ATTENDANCE_RECORDS,
            "$COL_RECORD_ATTENDANCE_ID = ?",
            arrayOf(attendanceId.toString())
        )
    }
    
    /**
     * 将游标转换为签到记录对象
     */
    private fun cursorToAttendanceRecord(cursor: Cursor): AttendanceRecord {
        val idIndex = cursor.getColumnIndexOrThrow(COL_RECORD_ID)
        val attendanceIdIndex = cursor.getColumnIndexOrThrow(COL_RECORD_ATTENDANCE_ID)
        val studentIdIndex = cursor.getColumnIndexOrThrow(COL_RECORD_STUDENT_ID)
        val attendanceTimeIndex = cursor.getColumnIndexOrThrow(COL_RECORD_ATTENDANCE_TIME)
        val statusIndex = cursor.getColumnIndexOrThrow(COL_RECORD_STATUS)
        
        return AttendanceRecord(
            id = cursor.getLong(idIndex),
            attendanceId = cursor.getLong(attendanceIdIndex),
            studentId = cursor.getLong(studentIdIndex),
            attendanceTime = cursor.getString(attendanceTimeIndex),
            status = AttendanceRecordStatus.valueOf(cursor.getString(statusIndex))
        )
    }
} 