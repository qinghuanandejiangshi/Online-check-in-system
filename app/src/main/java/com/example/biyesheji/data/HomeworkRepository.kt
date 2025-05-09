package com.example.biyesheji.data

import com.example.biyesheji.model.Homework
import com.example.biyesheji.model.HomeworkStatus
import com.example.biyesheji.model.HomeworkSubmission
import com.example.biyesheji.model.SubmissionStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 作业数据仓库
 */
object HomeworkRepository {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    
    private val homeworks = listOf(
        Homework(
            id = "hw1",
            courseId = "course1",
            title = "Java编程基础作业",
            description = "完成教材第3章的练习题1-5",
            createdAt = dateFormat.parse("2023-10-01 10:00") ?: Date(),
            deadline = dateFormat.parse("2023-10-10 23:59") ?: Date(),
            submissionCount = 23,
            gradedCount = 15,
            status = HomeworkStatus.OPEN
        ),
        Homework(
            id = "hw2",
            courseId = "course1",
            title = "面向对象编程实践",
            description = "设计一个简单的学生管理系统，包含学生信息的增删改查功能",
            createdAt = dateFormat.parse("2023-10-15 14:30") ?: Date(),
            deadline = dateFormat.parse("2023-10-25 23:59") ?: Date(),
            submissionCount = 22,
            gradedCount = 0,
            status = HomeworkStatus.OPEN
        ),
        Homework(
            id = "hw3",
            courseId = "course2",
            title = "链表操作实现",
            description = "实现单链表的创建、插入、删除和查找操作",
            createdAt = dateFormat.parse("2023-09-20 09:15") ?: Date(),
            deadline = dateFormat.parse("2023-09-30 23:59") ?: Date(),
            status = HomeworkStatus.CLOSED,
            submissionCount = 18,
            gradedCount = 18
        ),
        Homework(
            id = "hw4",
            courseId = "course2",
            title = "排序算法比较",
            description = "实现冒泡排序、选择排序和插入排序，比较它们的性能",
            createdAt = dateFormat.parse("2023-10-05 08:45") ?: Date(),
            deadline = dateFormat.parse("2023-10-15 23:59") ?: Date(),
            submissionCount = 17,
            gradedCount = 10,
            status = HomeworkStatus.OPEN
        )
    )
    
    private val submissions = listOf(
        HomeworkSubmission(
            id = "sub1",
            homeworkId = "hw1",
            studentId = "1",
            studentName = "李明",
            submittedAt = dateFormat.parse("2023-10-05 15:30") ?: Date(),
            content = "已完成所有题目，请查看附件",
            attachments = listOf("homework1_liming.zip"),
            score = 92,
            feedback = "整体完成得很好，第5题有小错误，建议再思考一下",
            status = SubmissionStatus.GRADED
        ),
        HomeworkSubmission(
            id = "sub2",
            homeworkId = "hw1",
            studentId = "2",
            studentName = "张红",
            submittedAt = dateFormat.parse("2023-10-07 20:15") ?: Date(),
            content = "作业已完成",
            attachments = listOf("homework1_zhanghong.pdf"),
            score = 88,
            feedback = "代码格式需要规范，逻辑正确",
            status = SubmissionStatus.GRADED
        ),
        HomeworkSubmission(
            id = "sub3",
            homeworkId = "hw1",
            studentId = "3",
            studentName = "王刚",
            submittedAt = dateFormat.parse("2023-10-09 23:50") ?: Date(),
            content = "抱歉提交得有点晚，已完成全部作业",
            attachments = listOf("homework1_wanggang.docx"),
            status = SubmissionStatus.SUBMITTED
        ),
        HomeworkSubmission(
            id = "sub4",
            homeworkId = "hw2",
            studentId = "1",
            studentName = "李明",
            submittedAt = dateFormat.parse("2023-10-20 14:20") ?: Date(),
            content = "已完成学生管理系统的设计与实现，程序截图见附件",
            attachments = listOf("studentSystem_liming.zip", "screenshots.pdf"),
            status = SubmissionStatus.SUBMITTED
        ),
        HomeworkSubmission(
            id = "sub5",
            homeworkId = "hw2",
            studentId = "4",
            studentName = "赵静",
            submittedAt = dateFormat.parse("2023-10-22 09:10") ?: Date(),
            content = "完成了基本功能，UI设计见附件",
            attachments = listOf("studentSystem_zhaojing.rar"),
            status = SubmissionStatus.SUBMITTED
        )
    )
    
    fun getHomeworks(): List<Homework> {
        return homeworks
    }
    
    fun getHomeworksForTeacher(courseId: String): List<Homework> {
        return homeworks.filter { it.courseId == courseId }
    }
    
    fun getSubmissionsForHomework(homeworkId: String): List<HomeworkSubmission> {
        return submissions.filter { it.homeworkId == homeworkId }
    }
    
    fun getHomeworkById(homeworkId: String): Homework? {
        return homeworks.find { it.id == homeworkId }
    }
} 