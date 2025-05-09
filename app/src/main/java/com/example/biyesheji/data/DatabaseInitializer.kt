package com.example.biyesheji.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.biyesheji.model.Course
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * 数据库初始化工具类
 * 用于首次启动应用时预填充数据库
 */
object DatabaseInitializer {
    private const val TAG = "DatabaseInitializer"
    private const val PREF_NAME = "db_init_prefs"
    private const val KEY_DB_INITIALIZED = "database_initialized"

    /**
     * 初始化数据库，填充初始数据
     */
    suspend fun initializeDatabase(context: Context) = withContext(Dispatchers.IO) {
        try {
            // 检查数据库是否已初始化
            val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val isInitialized = preferences.getBoolean(KEY_DB_INITIALIZED, false)

            if (!isInitialized) {
                Log.d(TAG, "开始初始化数据库...")

                // 确保所有表都存在
                val dbHelper = DatabaseHelper.getInstance(context)
                dbHelper.createTableIfNotExists(AppDatabase.TABLE_USERS)
                dbHelper.createTableIfNotExists(AppDatabase.TABLE_COURSES)
                dbHelper.createTableIfNotExists(AppDatabase.TABLE_ATTENDANCES)
                dbHelper.createTableIfNotExists(AppDatabase.TABLE_ATTENDANCE_RECORDS)
                dbHelper.createTableIfNotExists(AppDatabase.TABLE_COURSE_STUDENT)
                
                // 初始化签到记录DAO
                val attendanceRecordDao = AttendanceRecordDao(context)
                attendanceRecordDao.init()
                
                // 初始化课程数据
                initializeCourseData(context)
                
                // 标记为已初始化
                preferences.edit().putBoolean(KEY_DB_INITIALIZED, true).apply()
                Log.d(TAG, "数据库初始化完成")
            } else {
                Log.d(TAG, "数据库已初始化，跳过初始化步骤")
            }
        } catch (e: Exception) {
            Log.e(TAG, "数据库初始化错误: ${e.message}", e)
            throw e
        }
    }

    /**
     * 初始化课程数据
     */
    private suspend fun initializeCourseData(context: Context) {
        val courseDao = CourseDao(context)
        val courses = courseDao.getAllCourses()
        
        if (courses.isEmpty()) {
            Log.d(TAG, "开始初始化课程数据...")
            
            // 添加默认课程
            val coursesList = listOf(
                Course(
                    id = "course_1",
                    name = "Java程序设计",
                    teacherId = "teacher_1",
                    teacherName = "张教授",
                    description = "本课程介绍Java编程基础、面向对象编程概念以及Java应用开发。适合编程初学者和希望掌握Java技术的学生。",
                    credit = 3.0,
                    time = "周一 08:00-10:00",
                    location = "教学楼A101",
                    code = "CS101",
                    enrolledStudents = listOf("student_1", "student_2")
                ),
                Course(
                    id = "course_2",
                    name = "数据结构与算法",
                    teacherId = "teacher_1",
                    teacherName = "张教授",
                    description = "本课程介绍常用数据结构与算法，包括数组、链表、栈、队列、树、图以及各种排序和搜索算法。",
                    credit = 4.0,
                    time = "周三 14:00-16:00",
                    location = "教学楼B202",
                    code = "CS202",
                    enrolledStudents = listOf("student_1")
                ),
                Course(
                    id = "course_3",
                    name = "移动应用开发",
                    teacherId = "teacher_1",
                    teacherName = "张教授",
                    description = "本课程介绍Android平台应用开发基础，包括界面设计、数据存储、网络通信等核心技术。",
                    credit = 3.5,
                    time = "周二 10:00-12:00",
                    location = "实验楼C303",
                    code = "CS303",
                    enrolledStudents = listOf("student_1", "student_2")
                ),
                Course(
                    id = "course_4",
                    name = "数据库系统",
                    teacherId = "teacher_1",
                    teacherName = "张教授",
                    description = "本课程介绍数据库设计、SQL语言、事务处理、并发控制以及数据库安全等内容。",
                    credit = 3.0,
                    time = "周四 14:00-16:00",
                    location = "教学楼A203",
                    code = "CS304",
                    enrolledStudents = listOf("student_2")
                ),
                Course(
                    id = "course_5",
                    name = "软件工程",
                    teacherId = "teacher_1",
                    teacherName = "张教授",
                    description = "本课程介绍软件开发流程、需求分析、设计模式、测试方法以及项目管理等知识。",
                    credit = 4.0,
                    time = "周五 08:00-10:00",
                    location = "教学楼B305",
                    code = "CS305",
                    enrolledStudents = listOf("student_1", "student_2")
                )
            )
            
            // 存储课程数据到数据库
            coursesList.forEach { course ->
                try {
                    courseDao.insertCourse(course)
                    Log.d(TAG, "成功添加课程: ${course.name}")
                } catch (e: Exception) {
                    Log.e(TAG, "添加课程失败: ${course.name}, 错误: ${e.message}")
                }
            }
            
            Log.d(TAG, "课程数据初始化完成")
        } else {
            Log.d(TAG, "课程数据已存在，跳过初始化")
        }
    }

    /**
     * 重置数据库初始化状态（谨慎使用，一般用于开发阶段）
     */
    fun resetInitializationStatus(context: Context) {
        val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        preferences.edit().putBoolean(KEY_DB_INITIALIZED, false).apply()
        Log.d(TAG, "已重置数据库初始化状态")
        
        // 清除原有数据库
        try {
            context.deleteDatabase(AppDatabase.DATABASE_NAME)
            Log.d(TAG, "成功删除原有数据库")
        } catch (e: Exception) {
            Log.e(TAG, "删除数据库失败: ${e.message}")
        }
    }
} 