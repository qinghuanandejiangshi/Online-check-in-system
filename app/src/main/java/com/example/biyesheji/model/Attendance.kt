package com.example.biyesheji.model

import java.util.Date
import java.util.UUID

/**
 * 考勤数据类
 */
data class Attendance(
    val id: String,
    val courseId: String,
    val title: String,
    val description: String,
    val createdBy: String,
    val createdAt: Date,
    val endTime: Date?,
    val status: AttendanceStatus,
    val location: String,
    val attendanceCode: String = UUID.randomUUID().toString().substring(0, 6), // 6位签到码
    val qrCodeUrl: String? = null,
    val attendees: List<AttendanceRecord> = emptyList()
) 