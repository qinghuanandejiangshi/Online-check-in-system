package com.example.biyesheji.data

import android.content.Context
import android.database.Cursor
import android.util.Log
import com.example.biyesheji.model.Material
import com.example.biyesheji.model.MaterialType

/**
 * 学习资料数据访问对象
 */
class MaterialDao(private val context: Context) {
    /**
     * 获取课程的所有学习资料
     */
    fun getMaterialsByCourseId(courseId: String): List<Material> {
        val db = DatabaseManager.getInstance().openDatabase()
        val materials = mutableListOf<Material>()
        
        try {
            val cursor = db.query(
                AppDatabase.TABLE_MATERIALS,
                null,
                "${AppDatabase.COL_COURSE_ID} = ?",
                arrayOf(courseId),
                null,
                null,
                "${AppDatabase.COL_MATERIAL_TITLE} ASC"
            )
            
            while (cursor.moveToNext()) {
                materials.add(cursorToMaterial(cursor))
            }
            
            cursor.close()
        } catch (e: Exception) {
            Log.e("MaterialDao", "获取学习资料列表失败: ${e.message}")
        } finally {
            DatabaseManager.getInstance().closeDatabase()
        }
        
        return materials
    }
    
    /**
     * 获取学习资料详情
     */
    fun getMaterialById(materialId: String): Material? {
        val db = DatabaseManager.getInstance().openDatabase()
        var material: Material? = null
        
        try {
            val cursor = db.query(
                AppDatabase.TABLE_MATERIALS,
                null,
                "${AppDatabase.COL_MATERIAL_ID} = ?",
                arrayOf(materialId),
                null,
                null,
                null
            )
            
            if (cursor.moveToFirst()) {
                material = cursorToMaterial(cursor)
            }
            
            cursor.close()
        } catch (e: Exception) {
            Log.e("MaterialDao", "获取学习资料详情失败: ${e.message}")
        } finally {
            DatabaseManager.getInstance().closeDatabase()
        }
        
        return material
    }
    
    /**
     * 将游标转换为学习资料对象
     */
    private fun cursorToMaterial(cursor: Cursor): Material {
        val idIndex = cursor.getColumnIndex(AppDatabase.COL_MATERIAL_ID)
        val courseIdIndex = cursor.getColumnIndex(AppDatabase.COL_COURSE_ID)
        val titleIndex = cursor.getColumnIndex(AppDatabase.COL_MATERIAL_TITLE)
        val descriptionIndex = cursor.getColumnIndex(AppDatabase.COL_MATERIAL_DESCRIPTION)
        val urlIndex = cursor.getColumnIndex(AppDatabase.COL_MATERIAL_URL)
        val typeIndex = cursor.getColumnIndex(AppDatabase.COL_MATERIAL_TYPE)
        
        val id = cursor.getString(idIndex)
        val courseId = cursor.getString(courseIdIndex)
        val title = cursor.getString(titleIndex)
        val description = cursor.getString(descriptionIndex)
        val url = cursor.getString(urlIndex)
        val typeStr = cursor.getString(typeIndex)
        val type = try {
            MaterialType.valueOf(typeStr)
        } catch (e: Exception) {
            MaterialType.DOCUMENT
        }
        
        return Material(
            id = id,
            courseId = courseId,
            title = title,
            description = description,
            url = url,
            type = type
        )
    }
} 