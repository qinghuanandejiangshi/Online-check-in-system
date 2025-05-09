package com.example.biyesheji.data

import android.content.Context
import android.util.Log
import com.example.biyesheji.model.User
import com.example.biyesheji.model.UserType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * 用户数据仓库
 */
class UserRepository(private val context: Context) {
    private val TAG = "UserRepository"
    private val userDao = UserDao(context)
    private val dbHelper = DatabaseHelper.getInstance(context)
    
    /**
     * 初始化函数，应在应用启动时调用
     */
    fun initialize() {
        try {
            Log.d(TAG, "初始化UserRepository")
            
            // 确保users表存在
            dbHelper.createTableIfNotExists(AppDatabase.TABLE_USERS)
            
            Log.d(TAG, "UserRepository初始化完成")
        } catch (e: Exception) {
            Log.e(TAG, "初始化UserRepository失败: ${e.message}", e)
        }
    }
    
    /**
     * 验证用户登录
     */
    suspend fun validateUser(username: String, password: String): User? = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "验证用户登录: $username")
            val user = userDao.getUserByUsernameAndPassword(username, password)
            if (user != null) {
                Log.d(TAG, "用户登录成功: $username, 类型: ${user.userType}")
            } else {
                Log.d(TAG, "用户登录失败: $username")
            }
            user
        } catch (e: Exception) {
            Log.e(TAG, "验证用户登录异常: ${e.message}", e)
            null
        }
    }
    
    /**
     * 添加新用户
     */
    suspend fun addUser(username: String, password: String, userType: UserType): Boolean = withContext(Dispatchers.IO) {
        try {
            val newUser = User(
                id = "${userType.name.lowercase()}_${UUID.randomUUID().toString().substring(0, 8)}",
                username = username,
                password = password,
                userType = userType
            )
            
            val result = userDao.insertUser(newUser) > 0
            if (result) {
                Log.d(TAG, "添加用户成功: $username, 类型: $userType")
            } else {
                Log.d(TAG, "添加用户失败: $username")
            }
            result
        } catch (e: Exception) {
            Log.e(TAG, "添加用户异常: ${e.message}", e)
            false
        }
    }
    
    /**
     * 根据ID获取用户
     */
    suspend fun getUserById(userId: String): User? = withContext(Dispatchers.IO) {
        try {
            userDao.getUserById(userId)
        } catch (e: Exception) {
            Log.e(TAG, "根据ID获取用户异常: ${e.message}", e)
            null
        }
    }
    
    /**
     * 获取所有用户
     */
    suspend fun getAllUsers(): List<User> = withContext(Dispatchers.IO) {
        try {
            userDao.getAllUsers()
        } catch (e: Exception) {
            Log.e(TAG, "获取所有用户异常: ${e.message}", e)
            emptyList()
        }
    }
    
    /**
     * 获取所有老师
     */
    suspend fun getAllTeachers(): List<User> = withContext(Dispatchers.IO) {
        try {
            userDao.getUsersByType(UserType.TEACHER)
        } catch (e: Exception) {
            Log.e(TAG, "获取所有老师异常: ${e.message}", e)
            emptyList()
        }
    }
    
    /**
     * 获取所有学生
     */
    suspend fun getAllStudents(): List<User> = withContext(Dispatchers.IO) {
        try {
            userDao.getUsersByType(UserType.STUDENT)
        } catch (e: Exception) {
            Log.e(TAG, "获取所有学生异常: ${e.message}", e)
            emptyList()
        }
    }
    
    /**
     * 更新用户信息
     */
    suspend fun updateUser(user: User): Boolean = withContext(Dispatchers.IO) {
        try {
            userDao.updateUser(user) > 0
        } catch (e: Exception) {
            Log.e(TAG, "更新用户信息异常: ${e.message}", e)
            false
        }
    }
    
    /**
     * 删除用户
     */
    suspend fun deleteUser(userId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            userDao.deleteUser(userId) > 0
        } catch (e: Exception) {
            Log.e(TAG, "删除用户异常: ${e.message}", e)
            false
        }
    }
    
    /**
     * 获取指定课程的所有学生
     * 注意：这个方法需要与CourseStudentDao配合使用
     */
    suspend fun getStudentsByCourseId(courseId: String): List<User> = withContext(Dispatchers.IO) {
        try {
            val courseDao = CourseDao(context)
            val studentIds = courseDao.getCourseEnrolledStudentIds(courseId)
            val allStudents = userDao.getUsersByType(UserType.STUDENT)
            
            // 筛选属于该课程的学生
            allStudents.filter { student -> studentIds.contains(student.id) }
        } catch (e: Exception) {
            Log.e(TAG, "获取课程学生异常: ${e.message}", e)
            emptyList()
        }
    }
} 