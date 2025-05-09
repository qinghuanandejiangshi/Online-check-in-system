package com.example.biyesheji.model

import java.util.Date

/**
 * 作业数据类
 */
data class Homework(
    val id: String = "",
    val courseId: String = "",
    val courseName: String = "",
    val title: String = "",
    val description: String = "",
    val createdAt: Date = Date(),
    val deadline: Date = Date(),
    val totalScore: Int = 100,
    val status: HomeworkStatus = HomeworkStatus.OPEN,
    val submissionCount: Int = 0,
    val gradedCount: Int = 0,
    val submissions: List<HomeworkSubmission> = emptyList()
) 