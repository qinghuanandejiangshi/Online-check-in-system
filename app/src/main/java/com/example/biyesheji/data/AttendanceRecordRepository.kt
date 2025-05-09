package com.example.biyesheji.data

import android.content.Context
import com.example.biyesheji.model.AttendanceRecord
import com.example.biyesheji.model.AttendanceRecordStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID

/**
 * 签到记录仓库类
 */
class AttendanceRecordRepository(private val context: Context) {
    private val attendanceRecordDao = AttendanceRecordDao(context)
    
    /**
     * 插入新的签到记录
     */
    suspend fun insertAttendanceRecord(record: AttendanceRecord): Long = withContext(Dispatchers.IO) {
        return@withContext attendanceRecordDao.insertAttendanceRecord(record)
    }
    
    /**
     * 创建新的签到记录
     */
    suspend fun createAttendanceRecord(
        attendanceId: String,
        studentId: String,
        status: AttendanceRecordStatus = AttendanceRecordStatus.PRESENT
    ): Long = withContext(Dispatchers.IO) {
        val record = AttendanceRecord(
            id = UUID.randomUUID().toString(),
            attendanceId = attendanceId,
            studentId = studentId,
            attendanceTime = Date(),
            status = status
        )
        return@withContext attendanceRecordDao.insertAttendanceRecord(record)
    }
    
    /**
     * 更新签到记录状态
     */
    suspend fun updateAttendanceRecordStatus(recordId: String, status: AttendanceRecordStatus): Int = withContext(Dispatchers.IO) {
        return@withContext attendanceRecordDao.updateAttendanceRecordStatus(recordId, status)
    }
    
    /**
     * 根据签到ID获取所有签到记录
     */
    suspend fun getRecordsByAttendanceId(attendanceId: String): List<AttendanceRecord> = withContext(Dispatchers.IO) {
        return@withContext attendanceRecordDao.getRecordsByAttendanceId(attendanceId)
    }
    
    /**
     * 获取学生的所有签到记录
     */
    suspend fun getRecordsByStudentId(studentId: String): List<AttendanceRecord> = withContext(Dispatchers.IO) {
        return@withContext attendanceRecordDao.getRecordsByStudentId(studentId)
    }
    
    /**
     * 检查学生是否已签到
     */
    suspend fun checkStudentAttendanceExists(attendanceId: String, studentId: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext attendanceRecordDao.checkStudentAttendanceExists(attendanceId, studentId)
    }
    
    /**
     * 删除签到记录
     */
    suspend fun deleteAttendanceRecord(recordId: String): Int = withContext(Dispatchers.IO) {
        return@withContext attendanceRecordDao.deleteAttendanceRecord(recordId)
    }
} 