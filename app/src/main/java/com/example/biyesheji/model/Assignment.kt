package com.example.biyesheji.model

/**
 * 作业提交
 */
data class Submission(
    val id: String = "",
    val assignmentId: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val submitTime: String = "",
    val content: String = "",
    val attachmentUrl: String? = null,
    val score: Int? = null,
    val feedback: String? = null
)

/**
 * 作业数据模型
 */
data class Assignment(
    val id: String,
    val courseId: String,
    val title: String,
    val description: String,
    val deadline: String,
    val score: Int? = null,
    val submissions: List<Submission> = emptyList()
) 