package com.example.biyesheji.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.biyesheji.model.User
import com.example.biyesheji.model.UserType
import com.example.biyesheji.model.UserRole

/**
 * 用户数据访问对象
 */
class UserDao(private val context: Context) {
    
    /**
     * 根据用户ID获取用户信息
     */
    fun getUserById(userId: String): User? {
        val db = DatabaseManager.getInstance().openDatabase()
        var user: User? = null
        
        try {
            val cursor = db.query(
                AppDatabase.TABLE_USERS,
                null,
                "${AppDatabase.COL_USER_ID} = ?",
                arrayOf(userId),
                null,
                null,
                null
            )
            
            if (cursor.moveToFirst()) {
                user = cursorToUser(cursor)
            }
            
            cursor.close()
        } catch (e: Exception) {
            Log.e("UserDao", "获取用户信息失败: ${e.message}")
        } finally {
            DatabaseManager.getInstance().closeDatabase()
        }
        
        return user
    }
    
    /**
     * 根据用户名和密码验证用户
     */
    fun getUserByUsernameAndPassword(username: String, password: String): User? {
        val db = DatabaseManager.getInstance().openDatabase()
        var user: User? = null
        
        try {
            val cursor = db.query(
                AppDatabase.TABLE_USERS,
                null,
                "${AppDatabase.COL_USER_USERNAME} = ? AND ${AppDatabase.COL_USER_PASSWORD} = ?",
                arrayOf(username, password),
                null,
                null,
                null
            )
            
            if (cursor.moveToFirst()) {
                user = cursorToUser(cursor)
            }
            
            cursor.close()
        } catch (e: Exception) {
            Log.e("UserDao", "用户验证失败: ${e.message}")
        } finally {
            DatabaseManager.getInstance().closeDatabase()
        }
        
        return user
    }
    
    /**
     * 根据用户名获取用户信息
     */
    fun getUserByUsername(username: String): User? {
        val db = DatabaseManager.getInstance().openDatabase()
        var user: User? = null
        
        try {
            val cursor = db.query(
                AppDatabase.TABLE_USERS,
                null,
                "${AppDatabase.COL_USER_USERNAME} = ?",
                arrayOf(username),
                null,
                null,
                null
            )
            
            if (cursor.moveToFirst()) {
                user = cursorToUser(cursor)
            }
            
            cursor.close()
        } catch (e: Exception) {
            Log.e("UserDao", "获取用户信息失败: ${e.message}")
        } finally {
            DatabaseManager.getInstance().closeDatabase()
        }
        
        return user
    }
    
    /**
     * 根据用户类型获取用户列表
     */
    fun getUsersByType(userType: UserType): List<User> {
        val db = DatabaseManager.getInstance().openDatabase()
        val users = mutableListOf<User>()
        
        try {
            val cursor = db.query(
                AppDatabase.TABLE_USERS,
                null,
                "${AppDatabase.COL_USER_TYPE} = ?",
                arrayOf(userType.name),
                null,
                null,
                null
            )
            
            while (cursor.moveToNext()) {
                users.add(cursorToUser(cursor))
            }
            
            cursor.close()
        } catch (e: Exception) {
            Log.e("UserDao", "获取用户列表失败: ${e.message}")
        } finally {
            DatabaseManager.getInstance().closeDatabase()
        }
        
        return users
    }
    
    /**
     * 获取所有用户
     */
    fun getAllUsers(): List<User> {
        val db = DatabaseManager.getInstance().openDatabase()
        val users = mutableListOf<User>()
        
        try {
            val cursor = db.query(
                AppDatabase.TABLE_USERS,
                null,
                null,
                null,
                null,
                null,
                null
            )
            
            while (cursor.moveToNext()) {
                users.add(cursorToUser(cursor))
            }
            
            cursor.close()
        } catch (e: Exception) {
            Log.e("UserDao", "获取所有用户失败: ${e.message}")
        } finally {
            DatabaseManager.getInstance().closeDatabase()
        }
        
        return users
    }
    
    /**
     * 获取所有学生用户
     */
    fun getAllStudents(): List<User> {
        return getUsersByType(UserType.STUDENT)
    }
    
    /**
     * 插入新用户
     */
    fun insertUser(user: User): Long {
        val db = DatabaseManager.getInstance().openDatabase()
        
        val values = ContentValues().apply {
            put(AppDatabase.COL_USER_ID, user.id)
            put(AppDatabase.COL_USER_USERNAME, user.username)
            put(AppDatabase.COL_USER_PASSWORD, user.password)
            put(AppDatabase.COL_USER_TYPE, user.userType.name)
            put(AppDatabase.COL_USER_NAME, user.name)
            put(AppDatabase.COL_USER_STUDENT_ID, user.studentId)
            put(AppDatabase.COL_USER_EMAIL, user.email)
            put(AppDatabase.COL_USER_AVATAR, user.avatar)
            put(AppDatabase.COL_USER_PHONE, user.phone)
            put(AppDatabase.COL_USER_ROLE, user.role)
        }
        
        val result = db.insert(AppDatabase.TABLE_USERS, null, values)
        DatabaseManager.getInstance().closeDatabase()
        return result
    }
    
    /**
     * 更新用户信息
     */
    fun updateUser(user: User): Int {
        val db = DatabaseManager.getInstance().openDatabase()
        
        val values = ContentValues().apply {
            put(AppDatabase.COL_USER_USERNAME, user.username)
            put(AppDatabase.COL_USER_PASSWORD, user.password)
            put(AppDatabase.COL_USER_TYPE, user.userType.name)
            put(AppDatabase.COL_USER_NAME, user.name)
            put(AppDatabase.COL_USER_STUDENT_ID, user.studentId)
            put(AppDatabase.COL_USER_EMAIL, user.email)
            put(AppDatabase.COL_USER_AVATAR, user.avatar)
            put(AppDatabase.COL_USER_PHONE, user.phone)
            put(AppDatabase.COL_USER_ROLE, user.role)
        }
        
        val result = db.update(
            AppDatabase.TABLE_USERS,
            values,
            "${AppDatabase.COL_USER_ID} = ?",
            arrayOf(user.id)
        )
        
        DatabaseManager.getInstance().closeDatabase()
        return result
    }
    
    /**
     * 删除用户
     */
    fun deleteUser(userId: String): Int {
        val db = DatabaseManager.getInstance().openDatabase()
        val result = db.delete(
            AppDatabase.TABLE_USERS,
            "${AppDatabase.COL_USER_ID} = ?",
            arrayOf(userId)
        )
        DatabaseManager.getInstance().closeDatabase()
        return result
    }
    
    /**
     * 将游标转换为用户对象
     */
    private fun cursorToUser(cursor: Cursor): User {
        val idIndex = cursor.getColumnIndex(AppDatabase.COL_USER_ID)
        val usernameIndex = cursor.getColumnIndex(AppDatabase.COL_USER_USERNAME)
        val passwordIndex = cursor.getColumnIndex(AppDatabase.COL_USER_PASSWORD)
        val typeIndex = cursor.getColumnIndex(AppDatabase.COL_USER_TYPE)
        val nameIndex = cursor.getColumnIndex(AppDatabase.COL_USER_NAME)
        val studentIdIndex = cursor.getColumnIndex(AppDatabase.COL_USER_STUDENT_ID)
        val emailIndex = cursor.getColumnIndex(AppDatabase.COL_USER_EMAIL)
        val avatarIndex = cursor.getColumnIndex(AppDatabase.COL_USER_AVATAR)
        val phoneIndex = cursor.getColumnIndex(AppDatabase.COL_USER_PHONE)
        val roleIndex = cursor.getColumnIndex(AppDatabase.COL_USER_ROLE)
        
        val id = cursor.getString(idIndex)
        val username = cursor.getString(usernameIndex)
        val password = cursor.getString(passwordIndex)
        val userType = UserType.valueOf(cursor.getString(typeIndex))
        
        // 可选字段，需要检查是否为null
        val name = if (nameIndex >= 0 && !cursor.isNull(nameIndex)) cursor.getString(nameIndex) else null
        val studentId = if (studentIdIndex >= 0 && !cursor.isNull(studentIdIndex)) cursor.getString(studentIdIndex) else null
        val email = if (emailIndex >= 0 && !cursor.isNull(emailIndex)) cursor.getString(emailIndex) else null
        val avatar = if (avatarIndex >= 0 && !cursor.isNull(avatarIndex)) cursor.getString(avatarIndex) else null
        val phone = if (phoneIndex >= 0 && !cursor.isNull(phoneIndex)) cursor.getString(phoneIndex) else null
        val role = if (roleIndex >= 0 && !cursor.isNull(roleIndex)) cursor.getString(roleIndex) else UserRole.STUDENT.name
        
        return User(
            id = id,
            username = username,
            password = password,
            userType = userType,
            name = name,
            studentId = studentId,
            email = email,
            avatar = avatar,
            phone = phone,
            role = role
        )
    }

    // 添加获取当前用户ID的方法
    fun getCurrentUserId(): String? {
        // 从应用程序的偏好设置或其他地方获取当前登录的用户ID
        // 这里假设我们有一个存储在偏好设置中的登录用户ID
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("current_user_id", null)
    }
} 