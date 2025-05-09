package com.example.biyesheji.data

import android.content.Context
import com.example.biyesheji.model.Assignment
import com.example.biyesheji.model.Course
import com.example.biyesheji.model.Material
import com.example.biyesheji.model.MaterialType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * 课程数据仓库，提供课程数据的访问接口
 */
class CourseRepository(private val context: Context) {
    private val courseDao = CourseDao(context)
    private val assignmentDao = AssignmentDao(context)
    private val materialDao = MaterialDao(context)
    
    init {
        // 检查是否需要初始化默认课程数据
        initDefaultCoursesIfNeeded()
    }
    
    /**
     * 初始化默认课程数据（如果表是空的）
     */
    private fun initDefaultCoursesIfNeeded() {
        val courses = courseDao.getAllCourses()
        
        if (courses.isEmpty()) {
            // 添加默认课程
            val course1 = Course(
                id = "course_1",
                name = "Java程序设计",
                teacherId = "teacher_1",
                teacherName = "张教授",
                description = "本课程介绍Java编程基础、面向对象编程概念以及Java应用开发。适合编程初学者和希望掌握Java技术的学生。",
                credit = 3.0,
                time = "周一 08:00-10:00",
                location = "教学楼A101"
            )
            
            val course2 = Course(
                id = "course_2",
                name = "数据结构与算法",
                teacherId = "teacher_1",
                teacherName = "张教授",
                description = "本课程介绍常用数据结构与算法，包括数组、链表、栈、队列、树、图以及各种排序和搜索算法。",
                credit = 4.0,
                time = "周三 14:00-16:00",
                location = "教学楼B202"
            )
            
            val course3 = Course(
                id = "course_3",
                name = "移动应用开发",
                teacherId = "teacher_1",
                teacherName = "张教授",
                description = "本课程介绍Android平台应用开发基础，包括界面设计、数据存储、网络通信等核心技术。",
                credit = 3.0,
                time = "周二 10:00-12:00",
                location = "实验楼C303"
            )
            
            val course4 = Course(
                id = "course_4",
                name = "数据库系统",
                teacherId = "teacher_1",
                teacherName = "张教授",
                description = "本课程介绍数据库设计、SQL语言、事务处理、并发控制以及数据库安全等内容。",
                credit = 3.0,
                time = "周四 14:00-16:00",
                location = "教学楼A203"
            )
            
            val course5 = Course(
                id = "course_5",
                name = "软件工程",
                teacherId = "teacher_1",
                teacherName = "张教授",
                description = "本课程介绍软件开发流程、需求分析、设计模式、测试方法以及项目管理等知识。",
                credit = 4.0,
                time = "周五 08:00-10:00",
                location = "教学楼B305"
            )
            
            courseDao.insertCourse(course1)
            courseDao.insertCourse(course2)
            courseDao.insertCourse(course3)
            courseDao.insertCourse(course4)
            courseDao.insertCourse(course5)
        }
    }
    
    /**
     * 获取所有课程
     */
    suspend fun getAllCourses(): List<Course> = withContext(Dispatchers.IO) {
        courseDao.getAllCourses()
    }
    
    /**
     * 获取指定ID的课程
     */
    suspend fun getCourseById(courseId: String): Course? = withContext(Dispatchers.IO) {
        courseDao.getCourseById(courseId)
    }
    
    /**
     * 获取教师的所有课程
     */
    suspend fun getCoursesByTeacherId(teacherId: String): List<Course> = withContext(Dispatchers.IO) {
        courseDao.getCoursesByTeacherId(teacherId)
    }
    
    /**
     * 获取学生已选的所有课程
     */
    suspend fun getCoursesByStudentId(studentId: String): List<Course> = withContext(Dispatchers.IO) {
        courseDao.getCoursesByStudentId(studentId)
    }
    
    /**
     * 创建新课程
     */
    suspend fun createCourse(course: Course): Boolean = withContext(Dispatchers.IO) {
        val courseWithId = if (course.id.isEmpty()) {
            course.copy(id = "course_${UUID.randomUUID().toString().substring(0, 8)}")
        } else {
            course
        }
        courseDao.insertCourse(courseWithId) > 0
    }
    
    /**
     * 更新课程信息
     */
    suspend fun updateCourse(course: Course): Boolean = withContext(Dispatchers.IO) {
        courseDao.updateCourse(course) > 0
    }
    
    /**
     * 删除课程
     */
    suspend fun deleteCourse(courseId: String): Boolean = withContext(Dispatchers.IO) {
        courseDao.deleteCourse(courseId) > 0
    }
    
    /**
     * 学生选课
     */
    suspend fun enrollStudent(courseId: String, studentId: String): Boolean = withContext(Dispatchers.IO) {
        // 获取课程
        val course = courseDao.getCourseById(courseId) ?: return@withContext false
        
        // 检查学生是否已选该课程
        val enrolledStudents = courseDao.getCourseEnrolledStudentIds(courseId)
        if (enrolledStudents.contains(studentId)) {
            return@withContext false
        }
        
        // 创建课程-学生关系
        val result = courseDao.insertCourseStudentRelation(courseId, studentId)
        
        return@withContext result > 0
    }
    
    /**
     * 学生退课
     */
    suspend fun unenrollStudent(courseId: String, studentId: String): Boolean = withContext(Dispatchers.IO) {
        // 获取课程
        val course = courseDao.getCourseById(courseId) ?: return@withContext false
        
        // 检查学生是否已选该课程
        val enrolledStudents = courseDao.getCourseEnrolledStudentIds(courseId)
        if (!enrolledStudents.contains(studentId)) {
            return@withContext false
        }
        
        // 删除课程-学生关系
        val result = courseDao.deleteCourseStudentRelation(courseId, studentId)
        
        return@withContext result > 0
    }
    
    /**
     * 获取教师课程统计信息
     */
    suspend fun getTeacherCoursesStatistics(teacherId: String): Map<String, Int> = withContext(Dispatchers.IO) {
        val courses = courseDao.getCoursesByTeacherId(teacherId)
        val studentCount = courses.flatMap { it.enrolledStudents }.distinct().size
        
        mapOf(
            "courseCount" to courses.size,
            "studentCount" to studentCount
        )
    }
    
    /**
     * 获取教师分配的课程
     */
    suspend fun getTeacherAssignedCourses(teacherId: String): List<Course> = withContext(Dispatchers.IO) {
        courseDao.getCoursesByTeacherId(teacherId)
    }
    
    /**
     * 生成测试数据
     */
    suspend fun generateSampleData(teacherId: String) = withContext(Dispatchers.IO) {
        val currentDate = Date()
        
        // 示例课程数据
        val courses = listOf(
            Course(
                id = "course_cs101",
                name = "计算机基础",
                description = "计算机科学与技术专业的基础课程，介绍计算机的基本原理和操作。",
                teacherId = teacherId,
                teacherName = "张教授",
                time = "周一 8:00-10:00, 周三 10:00-12:00",
                location = "A-101",
                credit = 3.0
            ),
            Course(
                id = "course_math201",
                name = "高等数学",
                description = "理工科专业必修的高等数学课程，包括微积分、线性代数等内容。",
                teacherId = teacherId,
                teacherName = "李教授",
                time = "周二 14:00-16:00, 周四 14:00-16:00",
                location = "B-202",
                credit = 4.0
            ),
            Course(
                id = "course_eng301",
                name = "专业英语",
                description = "针对各专业特点的英语课程，提高专业英语阅读和写作能力。",
                teacherId = teacherId,
                teacherName = "王教授",
                time = "周五 8:00-12:00",
                location = "C-303",
                credit = 2.0
            )
        )
        
        // 存储示例数据
        courses.forEach { course ->
            courseDao.insertCourse(course)
        }
    }
    
    /**
     * 获取课程详情
     */
    suspend fun getCourse(courseId: String): Course? = withContext(Dispatchers.IO) {
        val course = courseDao.getCourseByIdWithDatabaseManager(courseId) ?: return@withContext null
        
        // 加载课程相关的作业和资料
        val assignments = assignmentDao.getAssignmentsByCourseId(courseId)
        val materials = materialDao.getMaterialsByCourseId(courseId)
        
        return@withContext course.copy(
            assignments = assignments,
            materials = materials
        )
    }
    
    /**
     * 获取学生选择的课程
     */
    suspend fun getStudentCourses(studentId: String): List<Course> = withContext(Dispatchers.IO) {
        val courseIds = courseDao.getStudentCourseIds(studentId)
        val courses = mutableListOf<Course>()
        
        for (courseId in courseIds) {
            val course = getCourse(courseId)
            if (course != null) {
                courses.add(course)
            }
        }
        
        return@withContext courses
    }
    
    /**
     * 获取教师教授的课程
     */
    suspend fun getTeacherCourses(teacherId: String): List<Course> = withContext(Dispatchers.IO) {
        val courseIds = courseDao.getTeacherCourseIds(teacherId)
        val courses = mutableListOf<Course>()
        
        for (courseId in courseIds) {
            val course = getCourse(courseId)
            if (course != null) {
                courses.add(course)
            }
        }
        
        return@withContext courses
    }
} 