package com.example.biyesheji.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log

/**
 * 数据库管理类
 * 单例模式，提供对数据库的全局访问
 */
object DatabaseManager {
    private var dbHelper: DatabaseHelper? = null
    private var db: SQLiteDatabase? = null
    private var openCounter = 0
    
    // 存储当前登录用户的ID
    var currentUserId: String? = null
    
    /**
     * 获取DatabaseManager实例
     */
    fun getInstance(): DatabaseManager {
        return this
    }

    /**
     * 初始化数据库
     * 应在Application的onCreate()中调用
     */
    fun initializeDatabase(context: Context) {
        if (dbHelper == null) {
            try {
                dbHelper = DatabaseHelper.getInstance(context.applicationContext)
                // 预先打开一次数据库，确保表被创建
                val db = dbHelper?.writableDatabase
                db?.close()
                Log.d("DatabaseManager", "数据库初始化成功")
            } catch (e: Exception) {
                Log.e("DatabaseManager", "数据库初始化失败: ${e.message}", e)
            }
        }
    }

    /**
     * 获取数据库帮助类实例
     */
    fun getDatabase(): DatabaseHelper {
        return dbHelper ?: throw IllegalStateException("DatabaseManager must be initialized")
    }
    
    /**
     * 打开数据库并返回可写的数据库对象
     * 使用引用计数来管理数据库连接
     */
    @Synchronized
    fun openDatabase(): SQLiteDatabase {
        openCounter++
        if (db == null || !db!!.isOpen) {
            db = getDatabase().writableDatabase
        }
        return db!!
    }
    
    /**
     * 关闭数据库连接
     * 仅当引用计数为0时才真正关闭
     */
    @Synchronized
    fun closeDatabase() {
        openCounter--
        if (openCounter <= 0) {
            if (db != null && db!!.isOpen) {
                db!!.close()
            }
            openCounter = 0
        }
    }

    /**
     * 关闭数据库
     * 应在Application的onTerminate()中调用
     */
    fun shutdown() {
        if (db != null && db!!.isOpen) {
            db!!.close()
            db = null
        }
        dbHelper?.close()
        dbHelper = null
        openCounter = 0
    }
} 