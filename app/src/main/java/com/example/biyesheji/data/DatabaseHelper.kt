package com.example.biyesheji.data

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.biyesheji.model.User
import com.example.biyesheji.model.UserType

/**
 * 数据库助手类，提供SQLite数据库的管理
 * 使用单例模式确保只有一个数据库连接
 */
class DatabaseHelper private constructor(context: Context) : 
    SQLiteOpenHelper(context.applicationContext, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val TAG = "DatabaseHelper"
        const val DATABASE_NAME = "biyesheji.db"
        const val DATABASE_VERSION = 1

        @Volatile
        private var INSTANCE: DatabaseHelper? = null

        /**
         * 获取DatabaseHelper实例
         */
        fun getInstance(context: Context): DatabaseHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DatabaseHelper(context).also { INSTANCE = it }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d(TAG, "开始创建数据库表")
        
        // 创建用户表
        val createUserTable = """
            CREATE TABLE IF NOT EXISTS ${AppDatabase.TABLE_USERS} (
                ${AppDatabase.COL_USER_ID} TEXT PRIMARY KEY,
                ${AppDatabase.COL_USER_USERNAME} TEXT,
                ${AppDatabase.COL_USER_PASSWORD} TEXT,
                ${AppDatabase.COL_USER_TYPE} TEXT,
                ${AppDatabase.COL_USER_NAME} TEXT,
                ${AppDatabase.COL_USER_STUDENT_ID} TEXT,
                ${AppDatabase.COL_USER_EMAIL} TEXT,
                ${AppDatabase.COL_USER_AVATAR} TEXT,
                ${AppDatabase.COL_USER_PHONE} TEXT,
                ${AppDatabase.COL_USER_ROLE} TEXT
            )
        """.trimIndent()
        
        // 执行创建用户表语句
        db.execSQL(createUserTable)
        Log.d(TAG, "创建用户表完成")
        
        // 添加默认用户
        createDefaultUsers(db)
        
        // 创建课程表和其他表
        val createCourseTable = """
            CREATE TABLE IF NOT EXISTS ${AppDatabase.TABLE_COURSES} (
                ${AppDatabase.COL_COURSE_ID} TEXT PRIMARY KEY,
                ${AppDatabase.COL_COURSE_NAME} TEXT,
                ${AppDatabase.COL_TEACHER_ID} TEXT,
                ${AppDatabase.COL_TEACHER_NAME} TEXT,
                ${AppDatabase.COL_COURSE_DESCRIPTION} TEXT,
                ${AppDatabase.COL_COURSE_CREDIT} REAL,
                ${AppDatabase.COL_COURSE_TIME} TEXT,
                ${AppDatabase.COL_COURSE_LOCATION} TEXT,
                ${AppDatabase.COL_COURSE_CODE} TEXT
            )
        """.trimIndent()

        val createAttendanceTable = """
            CREATE TABLE IF NOT EXISTS ${AppDatabase.TABLE_ATTENDANCES} (
                ${AppDatabase.COL_ATTENDANCE_ID} TEXT PRIMARY KEY,
                ${AppDatabase.COL_COURSE_ID} TEXT,
                ${AppDatabase.COL_ATTENDANCE_TITLE} TEXT,
                ${AppDatabase.COL_ATTENDANCE_DESCRIPTION} TEXT,
                ${AppDatabase.COL_ATTENDANCE_CREATED_BY} TEXT,
                ${AppDatabase.COL_ATTENDANCE_CREATED_AT} INTEGER,
                ${AppDatabase.COL_ATTENDANCE_END_TIME} INTEGER,
                ${AppDatabase.COL_ATTENDANCE_STATUS} TEXT,
                ${AppDatabase.COL_ATTENDANCE_LOCATION} TEXT
            )
        """.trimIndent()

        val createAttendanceRecordTable = """
            CREATE TABLE IF NOT EXISTS ${AppDatabase.TABLE_ATTENDANCE_RECORDS} (
                ${AppDatabase.COL_RECORD_ID} TEXT PRIMARY KEY,
                ${AppDatabase.COL_RECORD_ATTENDANCE_ID} TEXT,
                ${AppDatabase.COL_RECORD_STUDENT_ID} TEXT,
                ${AppDatabase.COL_RECORD_ATTENDANCE_TIME} INTEGER,
                ${AppDatabase.COL_RECORD_STATUS} TEXT
            )
        """.trimIndent()
        
        db.execSQL(createCourseTable)
        db.execSQL(createAttendanceTable)
        db.execSQL(createAttendanceRecordTable)
        
        // 创建课程-学生关系表
        val createCourseStudentTable = """
            CREATE TABLE IF NOT EXISTS ${AppDatabase.TABLE_COURSE_STUDENT} (
                ${AppDatabase.COL_CS_COURSE_ID} TEXT,
                ${AppDatabase.COL_CS_STUDENT_ID} TEXT,
                PRIMARY KEY (${AppDatabase.COL_CS_COURSE_ID}, ${AppDatabase.COL_CS_STUDENT_ID})
            )
        """.trimIndent()
        
        db.execSQL(createCourseStudentTable)
        Log.d(TAG, "所有表创建完成")
    }
    
    /**
     * 创建默认用户
     */
    private fun createDefaultUsers(db: SQLiteDatabase) {
        try {
            Log.d(TAG, "开始创建默认用户")
            
            // 教师用户
            val insertTeacher = """
                INSERT OR REPLACE INTO ${AppDatabase.TABLE_USERS} 
                (${AppDatabase.COL_USER_ID}, ${AppDatabase.COL_USER_USERNAME}, ${AppDatabase.COL_USER_PASSWORD}, 
                ${AppDatabase.COL_USER_TYPE}, ${AppDatabase.COL_USER_NAME}, ${AppDatabase.COL_USER_STUDENT_ID}, 
                ${AppDatabase.COL_USER_EMAIL})
                VALUES 
                ('teacher_1', 'teacher1', '123456', '${UserType.TEACHER}', '张教授', 'T2001', 'teacher1@example.com')
            """.trimIndent()
            
            // 学生用户1
            val insertStudent1 = """
                INSERT OR REPLACE INTO ${AppDatabase.TABLE_USERS} 
                (${AppDatabase.COL_USER_ID}, ${AppDatabase.COL_USER_USERNAME}, ${AppDatabase.COL_USER_PASSWORD}, 
                ${AppDatabase.COL_USER_TYPE}, ${AppDatabase.COL_USER_NAME}, ${AppDatabase.COL_USER_STUDENT_ID}, 
                ${AppDatabase.COL_USER_EMAIL})
                VALUES 
                ('student_1', 'student1', '123456', '${UserType.STUDENT}', '张三', '20210001', 'student1@example.com')
            """.trimIndent()
            
            // 学生用户2
            val insertStudent2 = """
                INSERT OR REPLACE INTO ${AppDatabase.TABLE_USERS} 
                (${AppDatabase.COL_USER_ID}, ${AppDatabase.COL_USER_USERNAME}, ${AppDatabase.COL_USER_PASSWORD}, 
                ${AppDatabase.COL_USER_TYPE}, ${AppDatabase.COL_USER_NAME}, ${AppDatabase.COL_USER_STUDENT_ID}, 
                ${AppDatabase.COL_USER_EMAIL})
                VALUES 
                ('student_2', 'student2', '123456', '${UserType.STUDENT}', '李四', '20210002', 'student2@example.com')
            """.trimIndent()
            
            db.execSQL(insertTeacher)
            db.execSQL(insertStudent1)
            db.execSQL(insertStudent2)
            
            Log.d(TAG, "默认用户创建完成")
        } catch (e: Exception) {
            Log.e(TAG, "创建默认用户失败: ${e.message}", e)
        }
    }
    
    /**
     * 检查表是否存在
     */
    fun isTableExists(tableName: String): Boolean {
        val db = readableDatabase
        val cursor: Cursor?
        val query = "SELECT name FROM sqlite_master WHERE type='table' AND name='$tableName'"
        
        var tableExists = false
        
        try {
            cursor = db.rawQuery(query, null)
            tableExists = cursor.moveToFirst()
            cursor.close()
        } catch (e: Exception) {
            Log.e(TAG, "检查表是否存在时出错: ${e.message}", e)
        }
        
        return tableExists
    }
    
    /**
     * 如果表不存在，则创建表
     */
    fun createTableIfNotExists(tableName: String) {
        if (!isTableExists(tableName)) {
            val db = writableDatabase
            Log.d(TAG, "表 $tableName 不存在，开始创建")
            
            when (tableName) {
                AppDatabase.TABLE_USERS -> {
                    // 创建用户表
                    val createUserTable = """
                        CREATE TABLE IF NOT EXISTS ${AppDatabase.TABLE_USERS} (
                            ${AppDatabase.COL_USER_ID} TEXT PRIMARY KEY,
                            ${AppDatabase.COL_USER_USERNAME} TEXT,
                            ${AppDatabase.COL_USER_PASSWORD} TEXT,
                            ${AppDatabase.COL_USER_TYPE} TEXT,
                            ${AppDatabase.COL_USER_NAME} TEXT,
                            ${AppDatabase.COL_USER_STUDENT_ID} TEXT,
                            ${AppDatabase.COL_USER_EMAIL} TEXT,
                            ${AppDatabase.COL_USER_AVATAR} TEXT,
                            ${AppDatabase.COL_USER_PHONE} TEXT,
                            ${AppDatabase.COL_USER_ROLE} TEXT
                        )
                    """.trimIndent()
                    
                    db.execSQL(createUserTable)
                    createDefaultUsers(db)
                    Log.d(TAG, "用户表创建完成")
                }
                
                AppDatabase.TABLE_ATTENDANCE_RECORDS -> {
                    // 创建考勤记录表
                    val createAttendanceRecordTable = """
                        CREATE TABLE IF NOT EXISTS ${AppDatabase.TABLE_ATTENDANCE_RECORDS} (
                            ${AppDatabase.COL_RECORD_ID} TEXT PRIMARY KEY,
                            ${AppDatabase.COL_RECORD_ATTENDANCE_ID} TEXT,
                            ${AppDatabase.COL_RECORD_STUDENT_ID} TEXT,
                            ${AppDatabase.COL_RECORD_ATTENDANCE_TIME} INTEGER,
                            ${AppDatabase.COL_RECORD_STATUS} TEXT
                        )
                    """.trimIndent()
                    
                    db.execSQL(createAttendanceRecordTable)
                    Log.d(TAG, "考勤记录表创建完成")
                }
                
                // 可以添加其他表的创建逻辑
            }
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // 升级数据库时的操作
        if (oldVersion < 2) {
            // 将来版本2的升级操作
        }
    }
} 