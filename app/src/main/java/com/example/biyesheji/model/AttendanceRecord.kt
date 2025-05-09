package com.example.biyesheji.model

enum class AttendanceRecordStatus {
    PRESENT,
    LATE,
    ABSENT,
    LEAVE
}

/**
 * 签到记录类
 */
data class AttendanceRecord(
    val id: Long,
    val attendanceId: Long,
    val studentId: Long,
    val attendanceTime: String,
    val status: AttendanceRecordStatus
) 