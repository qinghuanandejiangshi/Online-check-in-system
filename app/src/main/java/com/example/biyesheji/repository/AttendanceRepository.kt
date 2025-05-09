package com.example.biyesheji.repository

import android.content.Context
import android.util.Log
import com.example.biyesheji.data.AttendanceDao
import com.example.biyesheji.data.AttendanceRecordDao
import com.example.biyesheji.data.CourseDao
import com.example.biyesheji.data.DatabaseManager
import com.example.biyesheji.data.UserDao
import com.example.biyesheji.model.Attendance
import com.example.biyesheji.model.AttendanceRecord
import com.example.biyesheji.model.AttendanceRecordStatus
import com.example.biyesheji.model.AttendanceStatus
import com.example.biyesheji.model.User
import com.example.biyesheji.model.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID

class AttendanceRepository(private val context: Context) {
    private val attendanceDao = AttendanceDao(context)
    private val attendanceRecordDao = AttendanceRecordDao(context)
    private val courseDao = CourseDao(context)
    private val userDao = UserDao(context)

    // 创建新的签到活动
    suspend fun createAttendance(
        title: String,
        description: String,
        courseId: String,
        locationName: String,
        createdBy: String,
        durationMinutes: Int
    ): String = withContext(Dispatchers.IO) {
        val currentTime = System.currentTimeMillis()
        val endTime = currentTime + (durationMinutes * 60 * 1000)
        
        val attendance = Attendance(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            courseId = courseId,
            location = locationName,
            createdBy = createdBy,
            createdAt = Date(currentTime),
            endTime = Date(endTime),
            status = AttendanceStatus.ACTIVE,
            qrCodeUrl = generateQrCodeUrl(courseId, currentTime.toString()),
            attendees = emptyList()
        )
        
        val result = attendanceDao.insertAttendance(attendance)
        if (result > 0) {
            return@withContext attendance.id
        } else {
            throw Exception("创建签到活动失败")
        }
    }
    
    // 生成二维码URL
    private fun generateQrCodeUrl(courseId: String, timestamp: String): String {
        // 这里应该是生成一个唯一的URL用于二维码
        return "attendance://$courseId/$timestamp"
    }
    
    // 学生扫码签到
    suspend fun registerAttendance(
        attendanceId: String,
        studentId: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            // 首先检查签到活动是否存在和有效
            val attendance = getAttendance(attendanceId)
            if (attendance == null) {
                Log.e("AttendanceRepo", "签到活动不存在: $attendanceId")
                return@withContext false
            }
            
            // 检查签到活动是否已经结束
            if (attendance.status == AttendanceStatus.COMPLETED || 
                (attendance.endTime != null && System.currentTimeMillis() > attendance.endTime.time)) {
                Log.e("AttendanceRepo", "签到活动已结束")
                return@withContext false
            }
            
            // 检查学生是否已经签过到
            if (attendanceRecordDao.checkStudentAttendanceExists(attendanceId, studentId)) {
                Log.e("AttendanceRepo", "学生已经签过到")
                return@withContext false
            }
            
            // 检查学生是否注册了这门课程
            val student = userDao.getUserById(studentId)
            if (student == null) {
                Log.e("AttendanceRepo", "用户不存在")
                return@withContext false
            }
            
            // 确定签到状态（正常/迟到）
            val currentTime = System.currentTimeMillis()
            // 假设迟到时间为10分钟
            val lateThreshold = attendance.createdAt.time + (10 * 60 * 1000)
            val status = if (currentTime <= lateThreshold) {
                AttendanceRecordStatus.PRESENT
            } else {
                AttendanceRecordStatus.LATE
            }
            
            // 创建签到记录
            val record = AttendanceRecord(
                id = UUID.randomUUID().toString(),
                attendanceId = attendanceId,
                studentId = studentId,
                attendanceTime = Date(currentTime),
                status = status
            )
            
            val id = attendanceRecordDao.insertAttendanceRecord(record)
            return@withContext id > 0
        } catch (e: Exception) {
            Log.e("AttendanceRepo", "签到失败", e)
            return@withContext false
        }
    }
    
    // 获取特定签到活动
    suspend fun getAttendance(attendanceId: String): Attendance? = withContext(Dispatchers.IO) {
        return@withContext attendanceDao.getAttendanceById(attendanceId)
    }
    
    // 关闭签到活动
    suspend fun closeAttendance(attendanceId: String): Boolean = withContext(Dispatchers.IO) {
        val result = attendanceDao.endAttendance(attendanceId)
        return@withContext result > 0
    }
    
    // 获取课程的所有签到活动
    suspend fun getAttendanceByCourseId(courseId: String): List<Attendance> = withContext(Dispatchers.IO) {
        val attendances = attendanceDao.getAttendancesByCourseId(courseId)
        val result = mutableListOf<Attendance>()
        
        for (attendance in attendances) {
            // 如果签到时间已经结束但状态还是ACTIVE，更新为COMPLETED
            if (attendance.status == AttendanceStatus.ACTIVE && 
                attendance.endTime != null && 
                System.currentTimeMillis() > attendance.endTime.time) {
                val updatedAttendance = attendance.copy(status = AttendanceStatus.COMPLETED)
                attendanceDao.updateAttendance(updatedAttendance)
            }
            result.add(attendance)
        }
        
        return@withContext result
    }
    
    // 获取老师创建的所有签到活动
    suspend fun getTeacherAttendances(teacherId: String): List<Attendance> = withContext(Dispatchers.IO) {
        val attendances = attendanceDao.getAttendancesByTeacherId(teacherId)
        val result = mutableListOf<Attendance>()
        
        for (attendance in attendances) {
            // 如果签到时间已经结束但状态还是ACTIVE，更新为COMPLETED
            if (attendance.status == AttendanceStatus.ACTIVE && 
                attendance.endTime != null && 
                System.currentTimeMillis() > attendance.endTime.time) {
                val updatedAttendance = attendance.copy(status = AttendanceStatus.COMPLETED)
                attendanceDao.updateAttendance(updatedAttendance)
            }
            
            // 获取课程信息
            val course = courseDao.getCourseById(attendance.courseId)
            if (course != null) {
                result.add(attendance)
            }
        }
        
        return@withContext result
    }
    
    // 获取签到记录
    suspend fun getAttendanceRecords(attendanceId: String): List<AttendanceRecord> = withContext(Dispatchers.IO) {
        return@withContext attendanceRecordDao.getRecordsByAttendanceId(attendanceId)
    }
    
    // 更新签到记录状态
    suspend fun updateAttendanceRecordStatus(
        recordId: String, 
        status: AttendanceRecordStatus
    ): Boolean = withContext(Dispatchers.IO) {
        val result = attendanceRecordDao.updateAttendanceRecordStatus(recordId, status)
        return@withContext result > 0
    }
    
    // 获取学生的所有签到记录
    suspend fun getStudentAttendanceRecords(studentId: String): List<AttendanceRecord> = withContext(Dispatchers.IO) {
        return@withContext attendanceRecordDao.getRecordsByStudentId(studentId)
    }
    
    // 结束签到活动方法，供UI调用
    suspend fun endAttendance(attendanceId: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext closeAttendance(attendanceId)
    }
} 