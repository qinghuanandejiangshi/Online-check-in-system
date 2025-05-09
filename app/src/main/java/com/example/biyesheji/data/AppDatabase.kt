package com.example.biyesheji.data

/**
 * 数据库常量定义
 */
object AppDatabase {
    // 数据库名称和版本
    const val DATABASE_NAME = "biyesheji.db"
    const val DATABASE_VERSION = 1

    // 用户表
    const val TABLE_USERS = "users"
    const val COL_USER_ID = "id"
    const val COL_USER_USERNAME = "username"
    const val COL_USER_PASSWORD = "password"
    const val COL_USER_TYPE = "type"
    const val COL_USER_NAME = "name"
    const val COL_USER_STUDENT_ID = "student_id"
    const val COL_USER_EMAIL = "email"
    const val COL_USER_AVATAR = "avatar"
    const val COL_USER_PHONE = "phone"
    const val COL_USER_ROLE = "role"

    // 课程表
    const val TABLE_COURSES = "courses"
    const val COL_COURSE_ID = "id"
    const val COL_COURSE_NAME = "name"
    const val COL_COURSE_TEACHER_ID = "teacher_id"
    const val COL_COURSE_TEACHER_NAME = "teacher_name"
    const val COL_COURSE_CREDIT = "credit"
    const val COL_COURSE_TIME = "time"
    const val COL_COURSE_LOCATION = "location"
    const val COL_COURSE_DESCRIPTION = "description"

    // 课程-学生关系表
    const val TABLE_COURSE_STUDENT = "course_student"
    const val COL_CS_COURSE_ID = "course_id"
    const val COL_CS_STUDENT_ID = "student_id"

    // 考勤表
    const val TABLE_ATTENDANCE = "attendance"
    const val COL_ATTENDANCE_ID = "id"
    const val COL_ATTENDANCE_COURSE_ID = "course_id"
    const val COL_ATTENDANCE_TITLE = "title"
    const val COL_ATTENDANCE_CREATED_AT = "created_at"
    const val COL_ATTENDANCE_END_TIME = "end_time"
    const val COL_ATTENDANCE_STATUS = "status"
    const val COL_ATTENDANCE_LOCATION = "location"

    // 考勤记录表
    const val TABLE_ATTENDANCE_RECORDS = "attendance_records"
    const val COL_RECORD_ID = "id"
    const val COL_RECORD_ATTENDANCE_ID = "attendance_id"
    const val COL_RECORD_STUDENT_ID = "student_id"
    const val COL_RECORD_ATTENDANCE_TIME = "attendance_time"
    const val COL_RECORD_STATUS = "status"

    // 作业表
    const val TABLE_ASSIGNMENTS = "assignments"
    const val COL_ASSIGNMENT_ID = "id"
    const val COL_ASSIGNMENT_COURSE_ID = "course_id"
    const val COL_ASSIGNMENT_TITLE = "title"
    const val COL_ASSIGNMENT_DESCRIPTION = "description"
    const val COL_ASSIGNMENT_CREATED_AT = "created_at"
    const val COL_ASSIGNMENT_DEADLINE = "deadline"
    const val COL_ASSIGNMENT_STATUS = "status"

    // 作业提交表
    const val TABLE_ASSIGNMENT_SUBMISSIONS = "assignment_submissions"
    const val COL_SUBMISSION_ID = "id"
    const val COL_SUBMISSION_ASSIGNMENT_ID = "assignment_id"
    const val COL_SUBMISSION_STUDENT_ID = "student_id"
    const val COL_SUBMISSION_CONTENT = "content"
    const val COL_SUBMISSION_CREATED_AT = "created_at"
    const val COL_SUBMISSION_STATUS = "status"
    const val COL_SUBMISSION_GRADE = "grade"
    const val COL_SUBMISSION_FEEDBACK = "feedback"

    // 教材表
    const val TABLE_MATERIALS = "materials"
    const val COL_MATERIAL_ID = "id"
    const val COL_MATERIAL_COURSE_ID = "course_id"
    const val COL_MATERIAL_TITLE = "title"
    const val COL_MATERIAL_DESCRIPTION = "description"
    const val COL_MATERIAL_FILE_URL = "file_url"
    const val COL_MATERIAL_CREATED_AT = "created_at"
} 