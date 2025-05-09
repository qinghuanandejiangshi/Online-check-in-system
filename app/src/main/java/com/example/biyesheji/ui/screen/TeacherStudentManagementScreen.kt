package com.example.biyesheji.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biyesheji.model.Course

data class Student(
    val id: String,
    val name: String,
    val studentId: String,
    val avatar: String? = null,
    val major: String,
    val grade: String,
    val email: String? = null,
    val phone: String? = null,
    val attendanceRate: Float,
    val averageScore: Float
)

// 模拟数据仓库
object StudentRepository {
    private val students = listOf(
        Student(
            id = "1",
            name = "李明",
            studentId = "20200101",
            major = "计算机科学与技术",
            grade = "2020级",
            email = "liming@example.com",
            phone = "13900001111",
            attendanceRate = 0.92f,
            averageScore = 86.5f
        ),
        Student(
            id = "2",
            name = "张红",
            studentId = "20200102",
            major = "计算机科学与技术",
            grade = "2020级",
            email = "zhanghong@example.com",
            phone = "13900002222",
            attendanceRate = 0.85f,
            averageScore = 92.0f
        ),
        Student(
            id = "3",
            name = "王刚",
            studentId = "20200103",
            major = "计算机科学与技术",
            grade = "2020级",
            email = "wanggang@example.com",
            phone = "13900003333",
            attendanceRate = 0.75f,
            averageScore = 78.5f
        ),
        Student(
            id = "4",
            name = "赵静",
            studentId = "20200104",
            major = "计算机科学与技术",
            grade = "2020级",
            email = "zhaojing@example.com",
            phone = "13900004444",
            attendanceRate = 0.95f,
            averageScore = 88.0f
        ),
        Student(
            id = "5",
            name = "陈强",
            studentId = "20200105",
            major = "计算机科学与技术",
            grade = "2020级",
            email = "chenqiang@example.com",
            phone = "13900005555",
            attendanceRate = 0.88f,
            averageScore = 82.5f
        )
    )
    
    // 存储课程信息
    data class CourseWithStudents(
        val id: String,
        val name: String,
        val code: String,
        val semester: String,
        val teacherId: String,
        val students: List<Student>
    )
    
    private val courseStudents = listOf(
        CourseWithStudents(
            id = "course1",
            name = "Java程序设计",
            code = "CS101",
            semester = "2023-2024-1",
            teacherId = "teacher1",
            students = students
        ),
        CourseWithStudents(
            id = "course2",
            name = "数据结构",
            code = "CS102",
            semester = "2023-2024-1",
            teacherId = "teacher1",
            students = students.take(3)
        )
    )
    
    fun getCoursesForTeacher(teacherId: String): List<Course> {
        return courseStudents
            .filter { it.teacherId == teacherId }
            .map { 
                Course(
                    id = it.id,
                    name = it.name,
                    teacherId = it.teacherId,
                    description = "",
                    code = it.code,
                    enrolledStudents = it.students.map { student -> student.id }
                )
            }
    }
    
    fun getStudentsForCourse(courseId: String): List<Student> {
        return courseStudents.find { it.id == courseId }?.students ?: emptyList()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherStudentManagementScreen(
    onBackClick: () -> Unit,
    onStudentClick: (String) -> Unit = {},
    onImportStudentClick: () -> Unit = {}
) {
    var selectedCourseId: String by remember { mutableStateOf("course1") }
    val teacherId: String = "teacher1" // 假设当前登录的教师ID
    
    val courses: List<Course> = remember { StudentRepository.getCoursesForTeacher(teacherId) }
    var expandedCourseDropdown: Boolean by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("学生管理") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = onImportStudentClick) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "导入学生")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 课程选择
            ExposedDropdownMenuBox(
                expanded = expandedCourseDropdown,
                onExpandedChange = { expandedCourseDropdown = it }
            ) {
                TextField(
                    value = courses.find { it.id == selectedCourseId }?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    label = { Text("选择课程") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCourseDropdown)
                    }
                )
                
                ExposedDropdownMenu(
                    expanded = expandedCourseDropdown,
                    onDismissRequest = { expandedCourseDropdown = false }
                ) {
                    courses.forEach { course ->
                        DropdownMenuItem(
                            text = { Text(course.name) },
                            onClick = {
                                selectedCourseId = course.id
                                expandedCourseDropdown = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 学生统计卡片
            val students: List<Student> = remember(selectedCourseId) { 
                StudentRepository.getStudentsForCourse(selectedCourseId) 
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(modifier = Modifier.width(100.dp)) {
                    StatisticsCard(
                        title = "学生人数",
                        value = "${students.size}人",
                        icon = Icons.Default.People,
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                }
                
                Box(modifier = Modifier.width(100.dp)) {
                    StatisticsCard(
                        title = "平均出勤率",
                        value = "${(students.map { it.attendanceRate }.average() * 100).toInt()}%",
                        icon = Icons.Default.CheckCircle,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                }
                
                Box(modifier = Modifier.width(100.dp)) {
                    StatisticsCard(
                        title = "平均成绩",
                        value = "${students.map { it.averageScore }.average().toInt()}分",
                        icon = Icons.Default.Star,
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 学生列表
            Text(
                text = "学生列表 (${students.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(students) { student ->
                    StudentCard(
                        student = student,
                        onClick = { onStudentClick(student.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatisticsCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: Color
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = title,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudentCard(
    student: Student,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 头像占位符
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = student.name.take(1),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = student.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Text(
                        text = "学号: ${student.studentId}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "${student.grade} | ${student.major}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(modifier = Modifier.width(200.dp)) {
                        LinearProgressIndicator(
                            progress = student.attendanceRate,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "出勤率: ${(student.attendanceRate * 100).toInt()}%",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 成绩进度条
                    Box(modifier = Modifier.width(200.dp)) {
                        LinearProgressIndicator(
                            progress = student.averageScore / 100,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = when {
                                student.averageScore >= 85 -> MaterialTheme.colorScheme.tertiary
                                student.averageScore >= 70 -> MaterialTheme.colorScheme.secondary
                                else -> MaterialTheme.colorScheme.error
                            },
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "平均分: ${student.averageScore.toInt()}",
                        fontSize = 12.sp,
                        color = when {
                            student.averageScore >= 85 -> MaterialTheme.colorScheme.tertiary
                            student.averageScore >= 70 -> MaterialTheme.colorScheme.secondary
                            else -> MaterialTheme.colorScheme.error
                        }
                    )
                }
            }
        }
    }
} 