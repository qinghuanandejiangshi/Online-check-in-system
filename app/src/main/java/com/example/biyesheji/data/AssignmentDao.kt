package com.example.biyesheji.data

import android.content.ContentValues
import android.database.Cursor
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import com.example.biyesheji.model.Assignment
import com.example.biyesheji.model.Submission
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import android.content.Context
import android.util.Log

/**
 * 作业数据访问对象
 * 提供对作业表的增删改查操作
 */
class AssignmentDao(private val context: Context) {
    private val database = DatabaseManager.getDatabase().writableDatabase

    /**
     * 插入新作业
     */
    fun insertAssignment(assignment: Assignment, courseId: String): Long {
        val values = ContentValues().apply {
            put(AppDatabase.COL_ASSIGNMENT_ID, assignment.id.ifEmpty { "assignment_${UUID.randomUUID().toString().substring(0, 8)}" })
            put(AppDatabase.COL_ASSIGNMENT_COURSE_ID, courseId)
            put(AppDatabase.COL_ASSIGNMENT_TITLE, assignment.title)
            put(AppDatabase.COL_ASSIGNMENT_DESCRIPTION, assignment.description)
            put(AppDatabase.COL_DEADLINE, assignment.deadline)
        }

        return database.insert(AppDatabase.TABLE_ASSIGNMENTS, null, values)
    }

    /**
     * 更新作业信息
     */
    fun updateAssignment(assignment: Assignment): Int {
        val values = ContentValues().apply {
            put(AppDatabase.COL_ASSIGNMENT_TITLE, assignment.title)
            put(AppDatabase.COL_ASSIGNMENT_DESCRIPTION, assignment.description)
            put(AppDatabase.COL_DEADLINE, assignment.deadline)
        }

        return database.update(
            AppDatabase.TABLE_ASSIGNMENTS,
            values,
            "${AppDatabase.COL_ASSIGNMENT_ID} = ?",
            arrayOf(assignment.id)
        )
    }

    /**
     * 删除作业
     */
    fun deleteAssignment(assignmentId: String): Int {
        return database.delete(
            AppDatabase.TABLE_ASSIGNMENTS,
            "${AppDatabase.COL_ASSIGNMENT_ID} = ?",
            arrayOf(assignmentId)
        )
    }

    /**
     * 获取课程的所有作业
     */
    fun getAssignmentsByCourseId(courseId: String): List<Assignment> {
        val db = DatabaseManager.getInstance().openDatabase()
        val assignments = mutableListOf<Assignment>()
        
        try {
            val cursor = db.query(
                AppDatabase.TABLE_ASSIGNMENTS,
                null,
                "${AppDatabase.COL_COURSE_ID} = ?",
                arrayOf(courseId),
                null,
                null,
                "${AppDatabase.COL_DEADLINE} ASC"
            )
            
            while (cursor.moveToNext()) {
                assignments.add(cursorToAssignment(cursor))
            }
            
            cursor.close()
        } catch (e: Exception) {
            Log.e("AssignmentDao", "获取作业列表失败: ${e.message}")
        } finally {
            DatabaseManager.getInstance().closeDatabase()
        }
        
        return assignments
    }

    /**
     * 获取作业详情
     */
    fun getAssignmentById(assignmentId: String): Assignment? {
        val db = DatabaseManager.getInstance().openDatabase()
        var assignment: Assignment? = null
        
        try {
            val cursor = db.query(
                AppDatabase.TABLE_ASSIGNMENTS,
                null,
                "${AppDatabase.COL_ASSIGNMENT_ID} = ?",
                arrayOf(assignmentId),
                null,
                null,
                null
            )
            
            if (cursor.moveToFirst()) {
                assignment = cursorToAssignment(cursor)
            }
            
            cursor.close()
        } catch (e: Exception) {
            Log.e("AssignmentDao", "获取作业详情失败: ${e.message}")
        } finally {
            DatabaseManager.getInstance().closeDatabase()
        }
        
        return assignment
    }

    /**
     * 将游标转换为作业对象
     */
    private fun cursorToAssignment(cursor: Cursor): Assignment {
        val idIndex = cursor.getColumnIndex(AppDatabase.COL_ASSIGNMENT_ID)
        val courseIdIndex = cursor.getColumnIndex(AppDatabase.COL_COURSE_ID)
        val titleIndex = cursor.getColumnIndex(AppDatabase.COL_ASSIGNMENT_TITLE)
        val descriptionIndex = cursor.getColumnIndex(AppDatabase.COL_ASSIGNMENT_DESCRIPTION)
        val deadlineIndex = cursor.getColumnIndex(AppDatabase.COL_DEADLINE)
        val scoreIndex = cursor.getColumnIndex(AppDatabase.COL_SCORE)
        
        val id = cursor.getString(idIndex)
        val courseId = cursor.getString(courseIdIndex)
        val title = cursor.getString(titleIndex)
        val description = cursor.getString(descriptionIndex)
        val deadline = cursor.getString(deadlineIndex)
        val score = if (scoreIndex >= 0 && !cursor.isNull(scoreIndex)) cursor.getInt(scoreIndex) else null
        
        return Assignment(
            id = id,
            courseId = courseId,
            title = title,
            description = description,
            deadline = deadline,
            score = score
        )
    }
} 