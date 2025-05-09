package com.example.biyesheji.model

/**
 * 用户数据模型
 */
data class User(
    val id: String? = null,
    val username: String,
    val password: String,
    val userType: UserType,
    val name: String? = null,
    val studentId: String? = null,
    val email: String? = null,
    val avatar: String? = null,
    val phone: String? = null,
    val role: String = ""
)

/**
 * 用户类型枚举
 */
enum class UserType {
    TEACHER,
    STUDENT
}

/**
 * 用户角色枚举
 */
enum class UserRole {
    TEACHER,
    STUDENT
} 