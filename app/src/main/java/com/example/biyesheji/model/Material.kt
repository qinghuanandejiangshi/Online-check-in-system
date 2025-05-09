package com.example.biyesheji.model

/**
 * 学习资料类型
 */
enum class MaterialType {
    DOCUMENT,  // 文档
    VIDEO,     // 视频
    LINK       // 链接
}

/**
 * 学习资料数据模型
 */
data class Material(
    val id: String,
    val courseId: String,
    val title: String,
    val description: String,
    val url: String,
    val type: MaterialType = MaterialType.DOCUMENT
) 