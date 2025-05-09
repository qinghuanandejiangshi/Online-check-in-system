package com.example.biyesheji.data

import android.content.Context
import android.util.Log
import com.example.biyesheji.model.Attendance
import com.example.biyesheji.model.AttendanceRecord
import com.example.biyesheji.model.AttendanceRecordStatus
import com.example.biyesheji.model.AttendanceStatus
import com.example.biyesheji.model.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID

/**
 * 签到数据仓库
 */
class AttendanceRepository(private val context: Context) {
    private val attendanceDao = AttendanceDao(context)
    private val attendanceRecordDao = AttendanceRecordDao(context)
    private val userDao = UserDao(context)
    
    /**
     * 获取课程的所有签到
     */
    suspend fun getAttendanceByCourseId(courseId: String): List<Attendance> = withContext(Dispatchers.IO) {
        val originalAttendances = attendanceDao.getAttendancesByCourseId(courseId)
        val resultAttendances = mutableListOf<Attendance>()
        
        // 为每个签到加载签到记录并创建新的对象
        for (attendance in originalAttendances) {
            val records = attendanceRecordDao.getRecordsByAttendanceId(attendance.id)
            // 将签到记录信息添加到签到对象
            val updatedAttendance = attendance.copy(attendees = records)
            resultAttendances.add(updatedAttendance)
        }
        
        return@withContext resultAttendances
    }
    
    /**
     * 创建新的签到
     */
    suspend fun createAttendance(
        courseId: String,
        title: String,
        description: String = "",
        createdBy: String,
        location: String? = null
    ): Attendance = withContext(Dispatchers.IO) {
        val now = Date()
        val newAttendance = Attendance(
            id = UUID.randomUUID().toString(),
            courseId = courseId,
            title = title,
            description = description,
            createdBy = createdBy,
            createdAt = now,
            endTime = null,
            status = AttendanceStatus.ACTIVE,
            location = location ?: "",
            attendees = emptyList()
        )
        
        val id = attendanceDao.insertAttendance(newAttendance)
        if (id > 0) {
            return@withContext newAttendance
        } else {
            throw Exception("创建签到失败")
        }
    }
    
    /**
     * 结束签到
     */
    suspend fun endAttendance(attendanceId: String): Boolean = withContext(Dispatchers.IO) {
        val result = attendanceDao.endAttendance(attendanceId)
        return@withContext result > 0
    }
    
    /**
     * 获取学生在特定课程中的签到记录
     */
    suspend fun getStudentAttendances(courseId: String, studentId: String): List<Attendance> = withContext(Dispatchers.IO) {
        // 获取所有课程的签到
        val allAttendances = attendanceDao.getAttendancesByCourseId(courseId)
        val result = mutableListOf<Attendance>()
        
        // 获取该学生的所有签到记录
        val studentRecords = attendanceRecordDao.getRecordsByStudentId(studentId)
        
        // 为每个签到添加学生记录信息并筛选出学生有记录的签到
        for (attendance in allAttendances) {
            val records = attendanceRecordDao.getRecordsByAttendanceId(attendance.id)
            val studentRecord = records.find { it.studentId == studentId }
            
            val updatedAttendance = attendance.copy(attendees = records)
            
            if (studentRecord != null) {
                result.add(updatedAttendance)
            }
        }
        
        return@withContext result
    }
    
    /**
     * 获取签到的所有记录
     */
    suspend fun getAttendanceRecords(attendanceId: String): List<AttendanceRecord> = withContext(Dispatchers.IO) {
        return@withContext attendanceRecordDao.getRecordsByAttendanceId(attendanceId)
    }
    
    /**
     * 获取特定学生的签到记录
     */
    suspend fun getStudentAttendanceRecord(attendanceId: String, studentId: String): AttendanceRecord? = withContext(Dispatchers.IO) {
        val records = attendanceRecordDao.getRecordsByAttendanceId(attendanceId)
        return@withContext records.find { it.studentId == studentId }
    }
    
    /**
     * 获取老师所有的签到活动
     */
    suspend fun getTeacherAttendances(teacherId: String): List<Attendance> = withContext(Dispatchers.IO) {
        val originalAttendances = attendanceDao.getAttendancesByTeacherId(teacherId)
        val resultAttendances = mutableListOf<Attendance>()
        
        // 为每个签到加载签到记录
        for (attendance in originalAttendances) {
            val records = attendanceRecordDao.getRecordsByAttendanceId(attendance.id)
            resultAttendances.add(attendance.copy(attendees = records))
        }
        
        return@withContext resultAttendances
    }
    
    /**
     * 获取签到详情
     */
    suspend fun getAttendance(attendanceId: String): Attendance? = withContext(Dispatchers.IO) {
        return@withContext getAttendanceById(attendanceId)
    }

    /**
     * 获取签到详情
     */
    suspend fun getAttendanceById(attendanceId: String): Attendance? = withContext(Dispatchers.IO) {
        val attendance = attendanceDao.getAttendanceById(attendanceId) ?: return@withContext null
        
        // 加载签到记录
        val records = attendanceRecordDao.getRecordsByAttendanceId(attendanceId)
        
        return@withContext attendance.copy(attendees = records)
    }

    /**
     * 更新考勤项
     */
    suspend fun updateAttendance(attendance: Attendance): Boolean = withContext(Dispatchers.IO) {
        Log.d("AttendanceRepository", "更新考勤: ${attendance.id}")
        try {
            val updated = attendanceDao.updateAttendance(attendance)
            if (updated) {
                Log.d("AttendanceRepository", "考勤更新成功")
                return@withContext true
            } else {
                Log.e("AttendanceRepository", "无法找到考勤ID: ${attendance.id}")
            }
        } catch (e: Exception) {
            Log.e("AttendanceRepository", "更新考勤失败: ${e.message}")
        }
        return@withContext false
    }

    /**
     * 更新考勤参与记录
     */
    suspend fun updateAttendanceRecord(updatedRecord: AttendanceRecord): Boolean = withContext(Dispatchers.IO) {
        Log.d("AttendanceRepository", "更新考勤记录: ${updatedRecord.id}")
        try {
            val updated = attendanceRecordDao.updateAttendanceRecordStatus(updatedRecord.id, updatedRecord.status)
            if (updated > 0) {
                Log.d("AttendanceRepository", "考勤记录更新成功")
                return@withContext true
            } else {
                Log.e("AttendanceRepository", "无法找到考勤记录ID: ${updatedRecord.id}")
            }
        } catch (e: Exception) {
            Log.e("AttendanceRepository", "更新考勤记录失败: ${e.message}")
        }
        return@withContext false
    }

    /**
     * 更新签到记录状态
     */
    suspend fun updateAttendanceRecordStatus(recordId: String, status: AttendanceRecordStatus): Boolean = withContext(Dispatchers.IO) {
        return@withContext attendanceRecordDao.updateAttendanceRecordStatus(recordId, status) > 0
    }

    suspend fun insertAttendanceRecord(record: AttendanceRecord): Long = withContext(Dispatchers.IO) {
        return@withContext attendanceRecordDao.insertAttendanceRecord(record)
    }

    suspend fun registerAttendance(
        attendanceId: String,
        studentId: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            // 首先检查签到活动是否存在和有效
            val attendance = getAttendanceById(attendanceId)
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
            if (student == null || student.role != UserRole.STUDENT.name) {
                Log.e("AttendanceRepo", "用户不是学生或不存在")
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
} 