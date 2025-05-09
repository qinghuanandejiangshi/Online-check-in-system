package com.example.biyesheji.model

import java.util.Date

/**
 * 作业提交数据类
 */
data class HomeworkSubmission(
    val id: String = "",
    val homeworkId: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val studentAvatar: String? = null,
    val submittedAt: Date = Date(),
    val submissionDate: Date = Date(),
    val content: String = "",
    val attachments: List<String> = emptyList(),
    val score: Int? = null,
    val feedback: String? = null,
    val status: SubmissionStatus = SubmissionStatus.SUBMITTED
) 