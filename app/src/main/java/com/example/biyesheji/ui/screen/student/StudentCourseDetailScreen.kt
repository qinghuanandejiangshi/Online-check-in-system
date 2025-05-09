package com.example.biyesheji.ui.screen.student

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biyesheji.model.*
import com.example.biyesheji.data.AttendanceRepository
import com.example.biyesheji.data.AttendanceRecordRepository
import com.example.biyesheji.data.CourseRepository
import com.example.biyesheji.data.UserDao
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentCourseDetailScreen(
    courseId: String,
    onNavigateBack: () -> Unit,
    onNavigateToAssignment: (String) -> Unit,
    onNavigateToQrCodeScanner: (String) -> Unit
) {
    val context = LocalContext.current
    val courseRepository = remember { CourseRepository(context) }
    val userRepository = remember { UserDao(context) }
    val attendanceRepository = remember { AttendanceRepository(context) }
    
    // 课程信息
    var courseState by remember { mutableStateOf<Course?>(null) }
    // 当前用户ID
    var currentUserId by remember { mutableStateOf<String?>(null) }
    // 加载状态
    var isLoading by remember { mutableStateOf(true) }
    // 错误信息
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // 选项卡状态
    val tabTitles = listOf("课程介绍", "作业", "资料", "考勤")
    var selectedTabIndex by remember { mutableStateOf(0) }
    
    // 加载课程和用户信息
    LaunchedEffect(key1 = courseId) {
        isLoading = true
        try {
            // 加载课程信息
            val course = courseRepository.getCourse(courseId)
            courseState = course
            
            // 获取当前用户ID
            currentUserId = userRepository.getCurrentUserId()
            
            isLoading = false
        } catch (e: Exception) {
            Log.e("StudentCourseDetail", "Error loading course: ${e.message}", e)
            errorMessage = "加载课程信息失败: ${e.message}"
            isLoading = false
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = courseState?.name ?: "课程详情") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // 签到按钮
                    IconButton(
                        onClick = {
                            currentUserId?.let { userId ->
                                onNavigateToQrCodeScanner(userId)
                            }
                        },
                        enabled = currentUserId != null
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scan QR Code"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "未知错误",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            } else if (courseState == null) {
                Text(
                    text = "课程不存在",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // 选项卡
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        tabTitles.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = { Text(text = title) }
                            )
                        }
                    }
                    
                    // 选项卡内容
                    when (selectedTabIndex) {
                        0 -> StudentCourseInfoTab(courseState!!)
                        1 -> StudentAssignmentTab(courseState!!, onNavigateToAssignment)
                        2 -> StudentMaterialTab(courseState!!)
                        3 -> StudentAttendanceTab(courseId, currentUserId ?: "")
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentCourseInfoTab(course: Course) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        CourseInfoItem(
            label = "课程名称",
            value = course.name
        )
        
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        
        CourseInfoItem(
            label = "授课教师",
            value = course.teacherName
        )
        
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        
        CourseInfoItem(
            label = "课程学分",
            value = "${course.credit}学分"
        )
        
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        
        CourseInfoItem(
            label = "上课时间",
            value = course.time
        )
        
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        
        CourseInfoItem(
            label = "上课地点",
            value = course.location
        )
        
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        
        Text(
            text = "课程描述",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = course.description,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun CourseInfoItem(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(80.dp)
        )
        
        Text(
            text = value,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudentAssignmentTab(
    course: Course,
    onAssignmentClick: (String) -> Unit
) {
    if (course.assignments.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "暂无作业",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(
                items = course.assignments,
                key = { it.id }
            ) { assignment ->
                AssignmentCard(
                    assignment = assignment,
                    onClick = { onAssignmentClick(assignment.id) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AssignmentCard(
    assignment: com.example.biyesheji.model.Assignment,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = assignment.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = assignment.description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "截止日期: ${assignment.deadline}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudentMaterialTab(course: Course) {
    if (course.materials.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "暂无学习资料",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(
                items = course.materials,
                key = { it.id }
            ) { material ->
                MaterialCard(material = material)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MaterialCard(material: Material) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = when (material.type) {
                        MaterialType.DOCUMENT -> Icons.Default.Description
                        MaterialType.VIDEO -> Icons.Default.VideoFile
                        MaterialType.LINK -> Icons.Default.Link
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = material.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = material.description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun StudentAttendanceTab(courseId: String, currentUserId: String) {
    val context = LocalContext.current
    val attendanceRepository = remember { AttendanceRepository(context) }
    val attendanceRecordRepository = remember { AttendanceRecordRepository(context) }
    val coroutineScope = rememberCoroutineScope()
    
    var attendances by remember { mutableStateOf<List<Attendance>>(emptyList()) }
    var attendanceRecords by remember { mutableStateOf<List<AttendanceRecord>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(courseId) {
        isLoading = true
        // 获取课程的所有考勤
        attendances = attendanceRepository.getAttendanceByCourseId(courseId)
        
        // 获取学生的考勤记录
        if (currentUserId.isNotEmpty()) {
            attendanceRecords = attendanceRecordRepository.getRecordsByStudentId(currentUserId)
        }
        
        isLoading = false
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (attendances.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("暂无考勤记录")
            }
        } else {
            LazyColumn {
                items(attendances) { attendance ->
                    // 查找该考勤的学生记录
                    val record = attendanceRecords.find { it.attendanceId == attendance.id }
                    AttendanceCard(attendance, record)
                }
            }
        }
    }
}

@Composable
private fun AttendanceCard(attendance: Attendance, record: AttendanceRecord?) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = attendance.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                AttendanceStatusBadge(status = record?.status ?: AttendanceRecordStatus.ABSENT)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "签到时间: ${dateFormat.format(attendance.createdAt)}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (record != null) {
                Text(
                    text = "签到结果: ${dateFormat.format(record.attendanceTime)}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AttendanceStatusBadge(status: AttendanceRecordStatus) {
    val (backgroundColor, textColor, statusText) = when (status) {
        AttendanceRecordStatus.PRESENT -> Triple(Color(0xFF81C784), Color.White, "已到")
        AttendanceRecordStatus.LATE -> Triple(Color(0xFFFFB74D), Color.White, "迟到")
        AttendanceRecordStatus.ABSENT -> Triple(Color(0xFFE57373), Color.White, "缺席")
        AttendanceRecordStatus.LEAVE -> Triple(Color(0xFF9E9E9E), Color.White, "请假")
    }
    
    Box(
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = statusText,
            color = textColor,
            fontSize = 12.sp
        )
    }
}

private fun getAttendanceStatusText(status: AttendanceRecordStatus): String {
    return when (status) {
        AttendanceRecordStatus.PRESENT -> "已签到"
        AttendanceRecordStatus.LATE -> "迟到"
        AttendanceRecordStatus.ABSENT -> "缺席"
        AttendanceRecordStatus.LEAVE -> "请假"
    }
}

private fun getAttendanceStatusColor(status: AttendanceRecordStatus): Color {
    return when (status) {
        AttendanceRecordStatus.PRESENT -> Color(0xFF4CAF50)
        AttendanceRecordStatus.LATE -> Color(0xFFFF9800)
        AttendanceRecordStatus.ABSENT -> Color(0xFFF44336)
        AttendanceRecordStatus.LEAVE -> Color(0xFF2196F3)
    }
} 