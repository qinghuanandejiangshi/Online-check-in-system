package com.example.biyesheji.ui.screen.teacher

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.biyesheji.data.AttendanceRepository
import com.example.biyesheji.data.CourseRepository
import com.example.biyesheji.model.Assignment
import com.example.biyesheji.model.Attendance
import com.example.biyesheji.model.AttendanceStatus
import com.example.biyesheji.model.Course
import com.example.biyesheji.model.Material
import com.example.biyesheji.model.MaterialType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.shape.CircleShape
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherCourseDetailScreen(
    courseId: String,
    onNavigateBack: () -> Unit,
    onNavigateToAssignment: (String) -> Unit,
    onNavigateToAddAssignment: (String) -> Unit,
    onNavigateToAddMaterial: (String) -> Unit,
    onNavigateToStudentList: (String) -> Unit,
    onStartAttendanceClick: (String) -> Unit,
    onAttendanceHistoryClick: (String) -> Unit,
    onAttendanceDetailClick: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var course by remember { mutableStateOf<Course?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(courseId) {
        coroutineScope.launch {
            try {
                val courseRepository = CourseRepository(context)
                course = courseRepository.getCourseById(courseId)
                isLoading = false
            } catch (e: Exception) {
                isLoading = false
            }
        }
    }
    
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    
    if (course == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("课程不存在或加载失败")
        }
        return
    }
    
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("课程信息", "学生", "作业", "资料", "签到")
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(course!!.name) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }
            
            when (selectedTabIndex) {
                0 -> CourseInfoTab(course!!)
                1 -> StudentTab(
                    course = course!!,
                    onStudentListClick = { onNavigateToStudentList(course!!.id) }
                )
                2 -> AssignmentTab(
                    course = course!!,
                    onAddAssignment = { onNavigateToAddAssignment(course!!.id) },
                    onAssignmentClick = onNavigateToAssignment
                )
                3 -> MaterialTab(
                    course = course!!,
                    onAddMaterial = { onNavigateToAddMaterial(course!!.id) }
                )
                4 -> AttendanceTab(
                    courseId = course!!.id,
                    onStartAttendanceClick = { onStartAttendanceClick(course!!.id) },
                    onAttendanceHistoryClick = { onAttendanceHistoryClick(course!!.id) },
                    onAttendanceDetailClick = onAttendanceDetailClick
                )
            }
        }
    }
}

@Composable
private fun CourseInfoTab(course: Course) {
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
private fun AssignmentTab(
    course: Course,
    onAddAssignment: () -> Unit,
    onAssignmentClick: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (course.assignments.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂无作业，点击 + 添加新作业",
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
                this.items(
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
        
        FloatingActionButton(
            onClick = onAddAssignment,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "添加作业",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AssignmentCard(
    assignment: Assignment,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
            Text(
                text = assignment.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "截止时间: ${assignment.deadline}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                
                Text(
                    text = "提交数: ${assignment.submissions.size}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MaterialTab(
    course: Course,
    onAddMaterial: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (course.materials.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂无学习资料，点击 + 添加新资料",
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
                this.items(
                    items = course.materials,
                    key = { it.id }
                ) { material ->
                    MaterialCard(material = material)
                }
            }
        }
        
        FloatingActionButton(
            onClick = onAddMaterial,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "添加资料",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MaterialCard(material: Material) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (material.type) {
                    MaterialType.DOCUMENT -> Icons.Default.Description
                    MaterialType.VIDEO -> Icons.Default.VideoLibrary
                    MaterialType.LINK -> Icons.Default.Link
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = material.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = material.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun StudentTab(
    course: Course,
    onStudentListClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "已选学生人数: ${course.enrolledStudents.size}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            Button(
                onClick = onStudentListClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("查看学生名单")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 这里仅显示人数，点击按钮查看详细学生列表
        // 实际学生列表在单独的页面中展示
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttendanceTab(
    courseId: String,
    onStartAttendanceClick: (String) -> Unit,
    onAttendanceHistoryClick: (String) -> Unit,
    onAttendanceDetailClick: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // 获取课程的签到记录
    var attendances by remember { mutableStateOf<List<Attendance>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(courseId) {
        coroutineScope.launch {
            try {
                val attendanceRepository = AttendanceRepository(context)
                val fetchedAttendances = attendanceRepository.getAttendanceByCourseId(courseId)
                // 按时间降序排序
                attendances = fetchedAttendances.sortedByDescending { it.createdAt }
                isLoading = false
            } catch (e: Exception) {
                error = e.message
                isLoading = false
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 添加"进入签到管理"按钮，将替换原有的onAttendanceHistoryClick跳转
        Button(
            onClick = { onAttendanceHistoryClick(courseId) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = null
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text("签到管理")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { onStartAttendanceClick(courseId) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text("发起新签到")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "最近签到记录",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(top = 32.dp)
                    .size(32.dp)
            )
        } else if (error != null) {
            Text(
                text = "加载失败: $error",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 32.dp)
            )
        } else if (attendances.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EventBusy,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "暂无签到记录",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        } else {
            // 只显示最近的3条记录
            val recentAttendances = attendances.take(3)
            
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recentAttendances) { attendance ->
                    AttendanceListItem(
                        attendance = attendance,
                        onClick = { onAttendanceDetailClick(attendance.id) }
                    )
                }
            }
            
            if (attendances.size > 3) {
                Spacer(modifier = Modifier.height(16.dp))
                
                TextButton(
                    onClick = { onAttendanceHistoryClick(courseId) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("查看全部签到记录")
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AttendanceListItem(
    attendance: Attendance,
    onClick: () -> Unit
) {
    // 格式化日期
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 状态指示器
            val statusColor = when (attendance.status) {
                AttendanceStatus.ACTIVE -> MaterialTheme.colorScheme.primary
                AttendanceStatus.COMPLETED -> Color.Gray
                AttendanceStatus.CANCELLED -> MaterialTheme.colorScheme.error
            }
            
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(statusColor)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = attendance.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = dateFormat.format(attendance.createdAt),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            Text(
                text = when (attendance.status) {
                    AttendanceStatus.ACTIVE -> "进行中"
                    AttendanceStatus.COMPLETED -> "已结束"
                    AttendanceStatus.CANCELLED -> "已取消"
                },
                fontSize = 14.sp,
                color = statusColor
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
} 