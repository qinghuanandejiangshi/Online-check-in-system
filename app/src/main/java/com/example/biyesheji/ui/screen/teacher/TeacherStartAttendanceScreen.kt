package com.example.biyesheji.ui.screen.teacher

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biyesheji.data.AttendanceRepository
import com.example.biyesheji.data.CourseRepository
import com.example.biyesheji.data.DatabaseManager
import com.example.biyesheji.data.UserRepository
import com.example.biyesheji.model.Attendance
import com.example.biyesheji.model.AttendanceStatus
import com.example.biyesheji.model.Course
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherStartAttendanceScreen(
    courseId: String,
    onBack: () -> Unit,
    onAttendanceCreated: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // 当前用户ID状态
    val currentUserId = remember { mutableStateOf("") }
    
    // 课程信息状态
    val courseState = remember { mutableStateOf<Course?>(null) }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    
    // 表单状态
    val title = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val location = remember { mutableStateOf("") }
    val durationOptions = listOf(5, 10, 15, 30, 45, 60)
    val selectedDuration = remember { mutableStateOf(15) }
    
    // 创建状态
    var isCreating by remember { mutableStateOf(false) }
    
    // 获取当前用户ID
    LaunchedEffect(Unit) {
        // 使用DatabaseManager中保存的当前用户ID
        currentUserId.value = DatabaseManager.currentUserId ?: ""
    }
    
    // 加载课程信息
    LaunchedEffect(courseId) {
        isLoading.value = true
        errorMessage.value = null
        
        try {
            val courseRepository = CourseRepository(context)
            val course = courseRepository.getCourseById(courseId)
            
            if (course != null) {
                courseState.value = course
                // 预设标题
                title.value = "${course.name}签到"
            } else {
                errorMessage.value = "找不到课程信息"
            }
        } catch (e: Exception) {
            errorMessage.value = "加载课程信息失败: ${e.message}"
        } finally {
            isLoading.value = false
        }
    }
    
    // 日期格式化器
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val currentTime = remember { Date() }
    
    // 当选择的时长改变时，更新结束时间显示（但不实际设置结束时间，创建时才设置）
    val displayEndTime = remember(selectedDuration) { 
        dateFormat.format(Date(System.currentTimeMillis() + selectedDuration.value * 60 * 1000))
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("发起签到") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 课程信息
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = courseState.value?.name ?: "未知课程",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Text(
                        text = "课程ID: $courseId",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            // 签到标题
            OutlinedTextField(
                value = title.value,
                onValueChange = { title.value = it },
                label = { Text("签到标题") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // 签到描述
            OutlinedTextField(
                value = description.value,
                onValueChange = { description.value = it },
                label = { Text("描述 (可选)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // 位置信息
            OutlinedTextField(
                value = location.value,
                onValueChange = { location.value = it },
                label = { Text("位置 (可选)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // 签到时间信息
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "开始时间: ${dateFormat.format(currentTime)}",
                            fontSize = 16.sp
                        )
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "签到时长: ",
                            fontSize = 16.sp
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // 时长选择
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            durationOptions.forEach { duration ->
                                FilterChip(
                                    selected = selectedDuration.value == duration,
                                    onClick = { selectedDuration.value = duration },
                                    label = { Text("${duration}分钟") }
                                )
                            }
                        }
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "二维码将在创建后生成",
                            fontSize = 16.sp
                        )
                    }
                }
            }
            
            // 创建按钮
            Button(
                onClick = { 
                    if (title.value.isBlank()) {
                        Toast.makeText(context, "请输入签到标题", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isCreating = true
                    coroutineScope.launch {
                        try {
                            val attendanceRepository = AttendanceRepository(context)
                            val attendance = withContext(Dispatchers.IO) {
                                attendanceRepository.createAttendance(
                                    courseId = courseId,
                                    title = title.value,
                                    description = description.value,
                                    createdBy = currentUserId.value ?: "",
                                    location = location.value
                                )
                            }
                            
                            withContext(Dispatchers.Main) {
                                isCreating = false
                                Toast.makeText(context, "创建成功！", Toast.LENGTH_SHORT).show()
                                onAttendanceCreated(attendance.id) // 使用新创建的签到ID进行导航
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                isCreating = false
                                Toast.makeText(context, "创建失败: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                enabled = !isCreating
            ) {
                Text(
                    text = if (isCreating) "创建中..." else "创建签到", 
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
    
    if (isCreating) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
} 