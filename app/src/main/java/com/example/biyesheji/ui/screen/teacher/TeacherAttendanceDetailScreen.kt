package com.example.biyesheji.ui.screen.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biyesheji.data.AttendanceRepository
import com.example.biyesheji.data.DatabaseManager
import com.example.biyesheji.data.UserDao
import com.example.biyesheji.model.Attendance
import com.example.biyesheji.model.AttendanceRecord
import com.example.biyesheji.model.AttendanceRecordStatus
import com.example.biyesheji.model.AttendanceStatus
import com.example.biyesheji.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 添加临时的User数据类
private data class UserInfo(val id: String, val name: String, val studentId: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherAttendanceDetailScreen(
    attendanceId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    
    var attendance by remember { mutableStateOf<Attendance?>(null) }
    var attendanceRecords by remember { mutableStateOf<List<AttendanceRecord>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var attendeesCount by remember { mutableStateOf(0) }
    var totalStudentsCount by remember { mutableStateOf(0) }
    
    // 添加用户数据记忆状态
    var studentsMap by remember { mutableStateOf<Map<String, User>>(emptyMap()) }
    
    LaunchedEffect(attendanceId) {
        scope.launch {
            try {
                val attendanceRepository = AttendanceRepository(context)
                val fetchedAttendance = withContext(Dispatchers.IO) {
                    attendanceRepository.getAttendance(attendanceId)
                }
                
                attendance = fetchedAttendance
                
                if (fetchedAttendance != null) {
                    // 获取签到记录
                    attendanceRecords = fetchedAttendance.attendees
                    attendeesCount = attendanceRecords.size
                    
                    // 获取课程学生数据
                    val userDao = UserDao(context)
                    val students = withContext(Dispatchers.IO) {
                        userDao.getAllStudents()
                    }
                    
                    // 创建学生ID到学生对象的映射
                    studentsMap = students.associateBy { it.id }
                    
                    // 获取课程总学生数
                    totalStudentsCount = students.size
                }
                
                isLoading = false
            } catch (e: Exception) {
                error = e.message
                isLoading = false
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("签到详情") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    // 如果签到还在进行中，添加一个结束签到的按钮
                    attendance?.let {
                        if (it.status == AttendanceStatus.ACTIVE) {
                            TextButton(
                                onClick = {
                                    scope.launch {
                                        try {
                                            val attendanceRepository = AttendanceRepository(context)
                                            val success = attendanceRepository.endAttendance(attendanceId)
                                            if (success) {
                                                attendance = attendance?.copy(
                                                    status = AttendanceStatus.COMPLETED,
                                                    endTime = java.util.Date()
                                                )
                                            }
                                        } catch (e: Exception) {
                                            error = e.message
                                        }
                                    }
                                }
                            ) {
                                Text("结束签到", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (error != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "加载失败: $error",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                attendance?.let { attendance ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // 签到信息卡片
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
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = attendance.title,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // 状态指示器
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val statusColor = when (attendance.status) {
                                        AttendanceStatus.ACTIVE -> MaterialTheme.colorScheme.primary
                                        AttendanceStatus.COMPLETED -> Color.Gray
                                        AttendanceStatus.CANCELLED -> MaterialTheme.colorScheme.error
                                    }
                                    
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .background(statusColor)
                                    )
                                    
                                    Spacer(modifier = Modifier.width(8.dp))
                                    
                                    Text(
                                        text = when (attendance.status) {
                                            AttendanceStatus.ACTIVE -> "进行中"
                                            AttendanceStatus.COMPLETED -> "已结束"
                                            AttendanceStatus.CANCELLED -> "已取消"
                                        },
                                        color = statusColor
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                    )
                                    
                                    Spacer(modifier = Modifier.width(8.dp))
                                    
                                    val endTimeText = attendance.endTime?.let { dateFormat.format(it) } ?: "未结束"
                                    Text(
                                        text = "${dateFormat.format(attendance.createdAt)} 至 $endTimeText",
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.QrCode,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                    )
                                    
                                    Spacer(modifier = Modifier.width(8.dp))
                                    
                                    // 使用qrCodeUrl或生成一个随机签到码
                                    val displayCode = attendance.qrCodeUrl?.substringAfterLast("/") ?: "未生成"
                                    Text(
                                        text = "签到码: $displayCode",
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // 签到统计
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "$attendeesCount",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    
                                    Text(
                                        text = "已签到",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                }
                                
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "${totalStudentsCount - attendeesCount}",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (totalStudentsCount - attendeesCount > 0) MaterialTheme.colorScheme.error else Color.Gray
                                    )
                                    
                                    Text(
                                        text = "未签到",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                }
                                
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    val attendanceRate = if (totalStudentsCount > 0) {
                                        (attendeesCount.toFloat() / totalStudentsCount) * 100
                                    } else {
                                        0f
                                    }
                                    
                                    Text(
                                        text = String.format("%.1f%%", attendanceRate),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when {
                                            attendanceRate >= 90 -> Color(0xFF4CAF50) // Green
                                            attendanceRate >= 70 -> Color(0xFFFFC107) // Yellow
                                            else -> MaterialTheme.colorScheme.error
                                        }
                                    )
                                    
                                    Text(
                                        text = "出勤率",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // 签到学生列表
                        Text(
                            text = "签到记录",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        if (attendanceRecords.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "暂无签到记录",
                                    color = Color.Gray
                                )
                            }
                        } else {
                            LazyColumn {
                                this.items(
                                    items = attendanceRecords,
                                    key = { it.id }
                                ) { record ->
                                    val student = studentsMap[record.studentId]
                                    AttendanceRecordItem(
                                        record = record,
                                        student = student,
                                        onStatusChanged = { recordId, newStatus ->
                                            scope.launch {
                                                try {
                                                    val attendanceRepo = AttendanceRepository(context)
                                                    val success = withContext(Dispatchers.IO) {
                                                        attendanceRepo.updateAttendanceRecordStatus(recordId, newStatus)
                                                    }
                                                    
                                                    if (success) {
                                                        // 更新本地状态
                                                        attendanceRecords = attendanceRecords.map { 
                                                            if (it.id == recordId) it.copy(status = newStatus) else it 
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    error = "更新状态失败: ${e.message}"
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceRecordItem(
    record: AttendanceRecord,
    student: User?,
    onStatusChanged: (String, AttendanceRecordStatus) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 学生头像和信息
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = student?.username.orEmpty().ifEmpty { "未知学生" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "签到时间: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(record.attendanceTime)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // 状态切换部分
            var expanded by remember { mutableStateOf(false) }
            
            // 状态颜色和文本映射
            val statusColorMap = mapOf(
                AttendanceRecordStatus.PRESENT to MaterialTheme.colorScheme.primary,
                AttendanceRecordStatus.LATE to Color(0xFFFF9800),  // 橙色
                AttendanceRecordStatus.ABSENT to MaterialTheme.colorScheme.error,
                AttendanceRecordStatus.LEAVE to Color(0xFF2196F3)  // 蓝色
            )
            
            val statusTextMap = mapOf(
                AttendanceRecordStatus.PRESENT to "出勤",
                AttendanceRecordStatus.LATE to "迟到",
                AttendanceRecordStatus.ABSENT to "缺勤",
                AttendanceRecordStatus.LEAVE to "请假"
            )
            
            Box {
                Button(
                    onClick = { expanded = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = statusColorMap[record.status] ?: MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(statusTextMap[record.status] ?: "未知")
                }
                
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    AttendanceRecordStatus.values().forEach { status ->
                        DropdownMenuItem(
                            text = { Text(statusTextMap[status] ?: "未知") },
                            onClick = {
                                onStatusChanged(record.id, status)
                                expanded = false
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = statusColorMap[status] ?: MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }
        }
    }
} 