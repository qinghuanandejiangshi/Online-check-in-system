package com.example.biyesheji

import android.app.Application
import android.util.Log
import com.example.biyesheji.data.DatabaseHelper
import com.example.biyesheji.data.DatabaseInitializer
import com.example.biyesheji.data.DatabaseManager
import com.example.biyesheji.data.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 应用主类
 */
class BiyeshejiApplication : Application() {
    private val applicationScope = CoroutineScope(Dispatchers.Default)
    private val TAG = "BiyeshejiApplication"

    override fun onCreate() {
        super.onCreate()
        
        // 初始化数据库
        try {
            Log.d(TAG, "开始初始化数据库")
            
            // 1. 初始化DatabaseManager，以便后续获取数据库连接
            DatabaseManager.initializeDatabase(this)
            Log.d(TAG, "DatabaseManager初始化完成")
            
            // 2. 初始化DatabaseHelper，确保数据库表结构创建
            val dbHelper = DatabaseHelper.getInstance(this)
            Log.d(TAG, "DatabaseHelper初始化完成")
            
            // 3. 确保用户表存在并包含默认数据
            dbHelper.createTableIfNotExists("users")
            Log.d(TAG, "用户表创建检查完成")

            // 3.1 确保考勤记录表存在
            dbHelper.createTableIfNotExists(AppDatabase.TABLE_ATTENDANCE_RECORDS)
            Log.d(TAG, "考勤记录表创建检查完成")
            
            // 4. 初始化用户仓库
            UserRepository.initialize(this)
            Log.d(TAG, "UserRepository初始化完成")
            
            // 5. 初始化其他数据库数据
            applicationScope.launch {
                try {
                    DatabaseInitializer.initializeDatabase(this@BiyeshejiApplication)
                    Log.d(TAG, "数据库初始化完成")
                } catch (e: Exception) {
                    Log.e(TAG, "数据库初始化失败: ${e.message}", e)
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "数据库初始化出错: ${e.message}", e)
            e.printStackTrace()
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        
        // 关闭数据库
        DatabaseManager.closeDatabase()
    }
} 