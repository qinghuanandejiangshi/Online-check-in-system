package com.example.biyesheji.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.core.database.getDoubleOrNull
import androidx.core.database.getStringOrNull
import com.example.biyesheji.model.Course
import com.example.biyesheji.model.Assignment
import com.example.biyesheji.model.Material
import com.example.biyesheji.model.MaterialType
import org.json.JSONArray
import org.json.JSONObject
import java.util.Date
import java.util.UUID
import android.util.Log

/**
 * 课程数据访问对象
 * 提供对课程表的增删改查操作
 */
class CourseDao(private val context: Context) {
    private val dbHelper = DatabaseHelper.getInstance(context)
    private val database = dbHelper.writableDatabase

    companion object {
        const val TABLE_NAME = "courses"
        const val COLUMN_ID = AppDatabase.COL_COURSE_ID
        const val COLUMN_NAME = AppDatabase.COL_COURSE_NAME
        const val COLUMN_TEACHER_ID = AppDatabase.COL_TEACHER_ID
        const val COLUMN_TEACHER_NAME = AppDatabase.COL_TEACHER_NAME
        const val COLUMN_DESCRIPTION = AppDatabase.COL_COURSE_DESCRIPTION
        const val COLUMN_CREDIT = AppDatabase.COL_COURSE_CREDIT
        const val COLUMN_TIME = AppDatabase.COL_COURSE_TIME
        const val COLUMN_LOCATION = AppDatabase.COL_COURSE_LOCATION
        const val COLUMN_ASSIGNMENTS = "assignments"
        const val COLUMN_MATERIALS = "materials"
        const val COLUMN_CODE = "code"
        
        // 创建表SQL语句
        const val CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                $COLUMN_ID TEXT PRIMARY KEY,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_TEACHER_ID TEXT NOT NULL,
                $COLUMN_TEACHER_NAME TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_CREDIT REAL DEFAULT 0.0,
                $COLUMN_TIME TEXT,
                $COLUMN_LOCATION TEXT,
                $COLUMN_ASSIGNMENTS TEXT,
                $COLUMN_MATERIALS TEXT,
                $COLUMN_CODE TEXT
            )
        """
    }

    /**
     * 插入新课程
     */
    fun insertCourse(course: Course): Long {
        val values = ContentValues().apply {
            put(COLUMN_ID, course.id)
            put(COLUMN_NAME, course.name)
            put(COLUMN_TEACHER_ID, course.teacherId)
            put(COLUMN_TEACHER_NAME, course.teacherName)
            put(COLUMN_DESCRIPTION, course.description)
            put(COLUMN_CREDIT, course.credit)
            put(COLUMN_TIME, course.time)
            put(COLUMN_LOCATION, course.location)
            put(COLUMN_ASSIGNMENTS, convertAssignmentsToJson(course.assignments))
            put(COLUMN_MATERIALS, convertMaterialsToJson(course.materials))
            put(COLUMN_CODE, course.code)
        }

        val result = database.insert(TABLE_NAME, null, values)
        
        // 插入选课学生关系
        if (result != -1L && course.enrolledStudents.isNotEmpty()) {
            course.enrolledStudents.forEach { studentId ->
                insertCourseStudentRelation(course.id, studentId)
            }
        }

        return result
    }

    /**
     * 更新课程信息
     */
    fun updateCourse(course: Course): Int {
        val values = ContentValues().apply {
            put(COLUMN_NAME, course.name)
            put(COLUMN_TEACHER_ID, course.teacherId)
            put(COLUMN_TEACHER_NAME, course.teacherName)
            put(COLUMN_DESCRIPTION, course.description)
            put(COLUMN_CREDIT, course.credit)
            put(COLUMN_TIME, course.time)
            put(COLUMN_LOCATION, course.location)
            put(COLUMN_ASSIGNMENTS, convertAssignmentsToJson(course.assignments))
            put(COLUMN_MATERIALS, convertMaterialsToJson(course.materials))
            put(COLUMN_CODE, course.code)
        }

        return database.update(
            TABLE_NAME,
            values,
            "$COLUMN_ID = ?",
            arrayOf(course.id)
        )
    }

    /**
     * 删除课程
     */
    fun deleteCourse(courseId: String): Int {
        // 首先删除课程-学生关系记录
        database.delete(
            AppDatabase.TABLE_COURSE_STUDENT,
            "${AppDatabase.COL_CS_COURSE_ID} = ?",
            arrayOf(courseId)
        )
        
        // 然后删除课程记录
        return database.delete(
            TABLE_NAME,
            "$COLUMN_ID = ?",
            arrayOf(courseId)
        )
    }

    /**
     * 插入课程-学生关系
     */
    fun insertCourseStudentRelation(courseId: String, studentId: String): Long {
        val values = ContentValues().apply {
            put(AppDatabase.COL_CS_COURSE_ID, courseId)
            put(AppDatabase.COL_CS_STUDENT_ID, studentId)
        }

        return database.insert(AppDatabase.TABLE_COURSE_STUDENT, null, values)
    }

    /**
     * 删除课程-学生关系
     */
    fun deleteCourseStudentRelation(courseId: String, studentId: String): Int {
        return database.delete(
            AppDatabase.TABLE_COURSE_STUDENT,
            "${AppDatabase.COL_CS_COURSE_ID} = ? AND ${AppDatabase.COL_CS_STUDENT_ID} = ?",
            arrayOf(courseId, studentId)
        )
    }

    /**
     * 获取特定学生的所有选课ID
     */
    fun getStudentEnrolledCourseIds(studentId: String): List<String> {
        val courseIds = mutableListOf<String>()
        val cursor = database.query(
            AppDatabase.TABLE_COURSE_STUDENT,
            arrayOf(AppDatabase.COL_CS_COURSE_ID),
            "${AppDatabase.COL_CS_STUDENT_ID} = ?",
            arrayOf(studentId),
            null,
            null,
            null
        )

        while (cursor.moveToNext()) {
            val idIndex = cursor.getColumnIndex(AppDatabase.COL_CS_COURSE_ID)
            if (idIndex >= 0) {
                courseIds.add(cursor.getString(idIndex))
            }
        }
        cursor.close()
        return courseIds
    }

    /**
     * 获取特定课程的所有学生ID
     */
    fun getCourseEnrolledStudentIds(courseId: String): List<String> {
        val studentIds = mutableListOf<String>()
        val cursor = database.query(
            AppDatabase.TABLE_COURSE_STUDENT,
            arrayOf(AppDatabase.COL_CS_STUDENT_ID),
            "${AppDatabase.COL_CS_COURSE_ID} = ?",
            arrayOf(courseId),
            null,
            null,
            null
        )

        while (cursor.moveToNext()) {
            val idIndex = cursor.getColumnIndex(AppDatabase.COL_CS_STUDENT_ID)
            if (idIndex >= 0) {
                studentIds.add(cursor.getString(idIndex))
            }
        }
        cursor.close()
        return studentIds
    }

    /**
     * 根据ID查询课程
     */
    fun getCourseById(courseId: String): Course? {
        val cursor = database.query(
            TABLE_NAME,
            null,
            "$COLUMN_ID = ?",
            arrayOf(courseId),
            null,
            null,
            null
        )

        return if (cursor.moveToFirst()) {
            val enrolledStudents = getCourseEnrolledStudentIds(courseId)
            val course = cursorToCourse(cursor, enrolledStudents)
            cursor.close()
            course
        } else {
            cursor.close()
            null
        }
    }

    /**
     * 获取教师的所有课程
     */
    fun getCoursesByTeacherId(teacherId: String): List<Course> {
        val courses = mutableListOf<Course>()
        val cursor = database.query(
            TABLE_NAME,
            null,
            "$COLUMN_TEACHER_ID = ?",
            arrayOf(teacherId),
            null,
            null,
            "$COLUMN_NAME ASC"
        )

        while (cursor.moveToNext()) {
            val courseId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val enrolledStudents = getCourseEnrolledStudentIds(courseId)
            courses.add(cursorToCourse(cursor, enrolledStudents))
        }
        cursor.close()
        return courses
    }

    /**
     * 获取学生已选的所有课程
     */
    fun getCoursesByStudentId(studentId: String): List<Course> {
        val courses = mutableListOf<Course>()
        val courseIds = getStudentEnrolledCourseIds(studentId)
        
        if (courseIds.isEmpty()) {
            return emptyList()
        }
        
        val placeholders = courseIds.joinToString(", ") { "?" }
        val selection = "${AppDatabase.COL_COURSE_ID} IN ($placeholders)"
        val cursor = database.query(
            TABLE_NAME,
            null,
            selection,
            courseIds.toTypedArray(),
            null,
            null,
            null
        )

        while (cursor.moveToNext()) {
            val courseId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val enrolledStudents = getCourseEnrolledStudentIds(courseId)
            courses.add(cursorToCourse(cursor, enrolledStudents))
        }
        cursor.close()
        return courses
    }

    /**
     * 获取所有课程
     */
    fun getAllCourses(): List<Course> {
        val courses = mutableListOf<Course>()
        val cursor = database.query(
            TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_NAME ASC"
        )

        while (cursor.moveToNext()) {
            val courseId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val enrolledStudents = getCourseEnrolledStudentIds(courseId)
            courses.add(cursorToCourse(cursor, enrolledStudents))
        }
        cursor.close()
        return courses
    }

    /**
     * 将Cursor转换为Course对象
     */
    private fun cursorToCourse(cursor: Cursor, enrolledStudents: List<String>): Course {
        val idIndex = cursor.getColumnIndex(COLUMN_ID)
        val nameIndex = cursor.getColumnIndex(COLUMN_NAME)
        val teacherIdIndex = cursor.getColumnIndex(COLUMN_TEACHER_ID)
        val teacherNameIndex = cursor.getColumnIndex(COLUMN_TEACHER_NAME)
        val descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION)
        val creditIndex = cursor.getColumnIndex(COLUMN_CREDIT)
        val timeIndex = cursor.getColumnIndex(COLUMN_TIME)
        val locationIndex = cursor.getColumnIndex(COLUMN_LOCATION)
        val assignmentsIndex = cursor.getColumnIndex(COLUMN_ASSIGNMENTS)
        val materialsIndex = cursor.getColumnIndex(COLUMN_MATERIALS)
        val codeIndex = cursor.getColumnIndex(COLUMN_CODE)

        return Course(
            id = if (idIndex >= 0) cursor.getString(idIndex) else "",
            name = if (nameIndex >= 0) cursor.getString(nameIndex) else "",
            teacherId = if (teacherIdIndex >= 0) cursor.getString(teacherIdIndex) else "",
            teacherName = if (teacherNameIndex >= 0) cursor.getString(teacherNameIndex) else "",
            description = if (descriptionIndex >= 0) cursor.getString(descriptionIndex) else "",
            credit = if (creditIndex >= 0) cursor.getDouble(creditIndex) else 0.0,
            time = if (timeIndex >= 0) cursor.getString(timeIndex) else "",
            location = if (locationIndex >= 0) cursor.getString(locationIndex) else "",
            code = if (codeIndex >= 0) cursor.getString(codeIndex) else "",
            assignments = if (assignmentsIndex >= 0 && !cursor.isNull(assignmentsIndex)) {
                parseAssignmentsFromJson(cursor.getString(assignmentsIndex), cursor.getString(idIndex))
            } else {
                emptyList()
            },
            materials = if (materialsIndex >= 0 && !cursor.isNull(materialsIndex)) {
                parseMaterialsFromJson(cursor.getString(materialsIndex), cursor.getString(idIndex))
            } else {
                emptyList()
            },
            enrolledStudents = enrolledStudents
        )
    }
    
    /**
     * 将List<String>转换为JSON字符串
     */
    private fun convertListToJson(list: List<String>): String {
        val jsonArray = JSONArray()
        for (item in list) {
            jsonArray.put(item)
        }
        return jsonArray.toString()
    }
    
    /**
     * 将List<Assignment>转换为JSON字符串
     */
    private fun convertAssignmentsToJson(assignments: List<Assignment>): String {
        val jsonArray = JSONArray()
        for (assignment in assignments) {
            val jsonObject = JSONObject()
            jsonObject.put("id", assignment.id)
            jsonObject.put("title", assignment.title)
            jsonObject.put("description", assignment.description)
            jsonObject.put("deadline", assignment.deadline)
            jsonArray.put(jsonObject)
        }
        return jsonArray.toString()
    }
    
    /**
     * 将List<Material>转换为JSON字符串
     */
    private fun convertMaterialsToJson(materials: List<Material>): String {
        val jsonArray = JSONArray()
        for (material in materials) {
            val jsonObject = JSONObject()
            jsonObject.put("id", material.id)
            jsonObject.put("title", material.title)
            jsonObject.put("description", material.description)
            jsonObject.put("url", material.url)
            jsonObject.put("type", material.type.name)
            jsonArray.put(jsonObject)
        }
        return jsonArray.toString()
    }
    
    /**
     * 从JSON字符串解析出List<Assignment>
     */
    private fun parseAssignmentsFromJson(json: String, courseId: String): List<Assignment> {
        val assignments = mutableListOf<Assignment>()
        try {
            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val assignment = Assignment(
                    id = jsonObject.optString("id", ""),
                    courseId = courseId,
                    title = jsonObject.optString("title", ""),
                    description = jsonObject.optString("description", ""),
                    deadline = jsonObject.optString("deadline", "")
                )
                assignments.add(assignment)
            }
        } catch (e: Exception) {
            // 解析异常，返回空列表
        }
        return assignments
    }
    
    /**
     * 从JSON字符串解析出List<Material>
     */
    private fun parseMaterialsFromJson(json: String, courseId: String): List<Material> {
        val materials = mutableListOf<Material>()
        try {
            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val material = Material(
                    id = jsonObject.optString("id", ""),
                    courseId = courseId,
                    title = jsonObject.optString("title", ""),
                    description = jsonObject.optString("description", ""),
                    url = jsonObject.optString("url", ""),
                    type = try {
                        MaterialType.valueOf(jsonObject.optString("type", "DOCUMENT"))
                    } catch (e: Exception) {
                        MaterialType.DOCUMENT
                    }
                )
                materials.add(material)
            }
        } catch (e: Exception) {
            // 解析异常，返回空列表
        }
        return materials
    }

    /**
     * 使用数据库管理器获取课程信息
     */
    fun getCourseByIdWithDatabaseManager(courseId: String): Course? {
        val db = DatabaseManager.getInstance().openDatabase()
        var course: Course? = null
        
        try {
            val cursor = db.query(
                AppDatabase.TABLE_COURSES,
                null,
                "${AppDatabase.COL_COURSE_ID} = ?",
                arrayOf(courseId),
                null,
                null,
                null
            )
            
            if (cursor.moveToFirst()) {
                course = cursorToCourse(cursor)
            }
            
            cursor.close()
        } catch (e: Exception) {
            Log.e("CourseDao", "获取课程信息失败: ${e.message}")
        } finally {
            DatabaseManager.getInstance().closeDatabase()
        }
        
        return course
    }
    
    /**
     * 获取学生选择的课程ID列表
     */
    fun getStudentCourseIds(studentId: String): List<String> {
        val db = DatabaseManager.getInstance().openDatabase()
        val courseIds = mutableListOf<String>()
        
        try {
            // 查询学生选择的课程
            val cursor = db.query(
                AppDatabase.TABLE_STUDENT_COURSES,
                arrayOf(AppDatabase.COL_COURSE_ID),
                "${AppDatabase.COL_STUDENT_ID} = ?",
                arrayOf(studentId),
                null,
                null,
                null
            )
            
            while (cursor.moveToNext()) {
                val courseIdIndex = cursor.getColumnIndex(AppDatabase.COL_COURSE_ID)
                if (courseIdIndex >= 0) {
                    courseIds.add(cursor.getString(courseIdIndex))
                }
            }
            
            cursor.close()
        } catch (e: Exception) {
            Log.e("CourseDao", "获取学生课程列表失败: ${e.message}")
        } finally {
            DatabaseManager.getInstance().closeDatabase()
        }
        
        return courseIds
    }
    
    /**
     * 获取教师教授的课程ID列表
     */
    fun getTeacherCourseIds(teacherId: String): List<String> {
        val db = DatabaseManager.getInstance().openDatabase()
        val courseIds = mutableListOf<String>()
        
        try {
            // 查询教师教授的课程
            val cursor = db.query(
                AppDatabase.TABLE_COURSES,
                arrayOf(AppDatabase.COL_COURSE_ID),
                "${AppDatabase.COL_TEACHER_ID} = ?",
                arrayOf(teacherId),
                null,
                null,
                null
            )
            
            while (cursor.moveToNext()) {
                val courseIdIndex = cursor.getColumnIndex(AppDatabase.COL_COURSE_ID)
                if (courseIdIndex >= 0) {
                    courseIds.add(cursor.getString(courseIdIndex))
                }
            }
            
            cursor.close()
        } catch (e: Exception) {
            Log.e("CourseDao", "获取教师课程列表失败: ${e.message}")
        } finally {
            DatabaseManager.getInstance().closeDatabase()
        }
        
        return courseIds
    }
    
    /**
     * 将游标转换为课程对象
     */
    private fun cursorToCourse(cursor: Cursor): Course {
        val idIndex = cursor.getColumnIndex(AppDatabase.COL_COURSE_ID)
        val nameIndex = cursor.getColumnIndex(AppDatabase.COL_COURSE_NAME)
        val descriptionIndex = cursor.getColumnIndex(AppDatabase.COL_COURSE_DESCRIPTION)
        val creditIndex = cursor.getColumnIndex(AppDatabase.COL_COURSE_CREDIT)
        val timeIndex = cursor.getColumnIndex(AppDatabase.COL_COURSE_TIME)
        val locationIndex = cursor.getColumnIndex(AppDatabase.COL_COURSE_LOCATION)
        val teacherIdIndex = cursor.getColumnIndex(AppDatabase.COL_TEACHER_ID)
        val teacherNameIndex = cursor.getColumnIndex(AppDatabase.COL_TEACHER_NAME)
        val codeIndex = cursor.getColumnIndex(COLUMN_CODE)
        
        val id = cursor.getString(idIndex)
        val name = cursor.getString(nameIndex)
        val description = cursor.getString(descriptionIndex)
        val credit = cursor.getDouble(creditIndex)
        val time = cursor.getString(timeIndex)
        val location = cursor.getString(locationIndex)
        val teacherId = cursor.getString(teacherIdIndex)
        val teacherName = cursor.getString(teacherNameIndex)
        val code = if (codeIndex >= 0) cursor.getString(codeIndex) else ""
        
        return Course(
            id = id,
            name = name,
            description = description,
            credit = credit,
            time = time,
            location = location,
            teacherId = teacherId,
            teacherName = teacherName,
            code = code,
            assignments = emptyList(),
            materials = emptyList(),
            enrolledStudents = emptyList()
        )
    }
} 