package com.example.biyesheji.model

import com.example.biyesheji.model.Assignment
import com.example.biyesheji.model.Material

/**
 * 课程数据模型
 */
data class Course(
    val id: String,
    val name: String,
    val description: String,
    val credit: Double,
    val time: String,
    val location: String,
    val teacherId: String,
    val teacherName: String,
    val code: String = "",
    val assignments: List<Assignment> = emptyList(),
    val materials: List<Material> = emptyList(),
    val enrolledStudents: List<String> = emptyList()
) 