package com.example.biyesheji.ui.screen.teacher

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biyesheji.data.AttendanceRepository
import com.example.biyesheji.data.CourseRepository
import com.example.biyesheji.model.Attendance
import com.example.biyesheji.model.AttendanceRecord
import com.example.biyesheji.model.AttendanceRecordStatus
import com.example.biyesheji.model.AttendanceStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherAttendanceManagementScreen(
    courseId: String,
    onNavigateBack: () -> Unit,
    onStartAttendance: (String) -> Unit,
    onAttendanceDetail: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    
    var attendances by remember { mutableStateOf<List<Attendance>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    
    // 数据统计
    var activeAttendances by remember { mutableStateOf(0) }
    var completedAttendances by remember { mutableStateOf(0) }
    var totalRecords by remember { mutableStateOf(0) }
    var averageAttendanceRate by remember { mutableStateOf(0f) }
    
    // 加载数据
    LaunchedEffect(courseId) {
        scope.launch {
            try {
                val attendanceRepository = AttendanceRepository(context)
                val courseRepository = CourseRepository(context)
                
                val fetchedAttendances = attendanceRepository.getAttendanceByCourseId(courseId)
                attendances = fetchedAttendances
                
                // 计算统计数据
                activeAttendances = fetchedAttendances.count { it.status == AttendanceStatus.ACTIVE }
                completedAttendances = fetchedAttendances.count { it.status == AttendanceStatus.COMPLETED }
                
                // 获取课程总学生数
                val course = courseRepository.getCourseById(courseId)
                val totalStudents = course?.enrolledStudents?.size ?: 0
                
                // 计算总记录数和平均出勤率
                var records = 0
                var totalRate = 0f
                
                for (attendance in fetchedAttendances) {
                    val attendanceRecords = attendanceRepository.getAttendanceRecords(attendance.id)
                    records += attendanceRecords.size
                    
                    if (totalStudents > 0) {
                        val rate = attendanceRecords.count { it.status == AttendanceRecordStatus.PRESENT } / 
                                   totalStudents.toFloat()
                        totalRate += rate
                    }
                }
                
                totalRecords = records
                averageAttendanceRate = if (completedAttendances > 0) 
                                          totalRate / completedAttendances 
                                        else 0f
                
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
                title = { Text("签到管理") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onStartAttendance(courseId) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "发起签到"
                )
            }
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
                        Icons.Default.Error,
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // 签到统计卡片
                    AttendanceStatsCard(
                        activeCount = activeAttendances,
                        completedCount = completedAttendances,
                        totalRecords = totalRecords,
                        attendanceRate = averageAttendanceRate
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (attendances.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EventBusy,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = Color.Gray
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = "暂无签到记录",
                                    color = Color.Gray
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Button(onClick = { onStartAttendance(courseId) }) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null
                                    )
                                    
                                    Spacer(modifier = Modifier.width(8.dp))
                                    
                                    Text("发起签到")
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "签到列表",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(attendances.sortedByDescending { it.createdAt }) { attendance ->
                                AttendanceCard(
                                    attendance = attendance,
                                    dateFormat = dateFormat,
                                    onClick = { onAttendanceDetail(attendance.id) },
                                    onEndAttendance = { 
                                        scope.launch {
                                            try {
                                                val attendanceRepository = AttendanceRepository(context)
                                                val success = attendanceRepository.endAttendance(attendance.id)
                                                if (success) {
                                                    // 更新列表中的签到状态
                                                    val updatedAttendances = attendances.map {
                                                        if (it.id == attendance.id) {
                                                            it.copy(
                                                                status = AttendanceStatus.COMPLETED,
                                                                endTime = Date()
                                                            )
                                                        } else {
                                                            it
                                                        }
                                                    }
                                                    attendances = updatedAttendances
                                                    activeAttendances--
                                                    completedAttendances++
                                                    
                                                    Toast.makeText(context, "签到已结束", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    Toast.makeText(context, "结束签到失败", Toast.LENGTH_SHORT).show()
                                                }
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "操作失败: ${e.message}", Toast.LENGTH_SHORT).show()
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

@Composable
private fun AttendanceStatsCard(
    activeCount: Int,
    completedCount: Int,
    totalRecords: Int,
    attendanceRate: Float
) {
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
                text = "签到统计",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticsItem(
                    value = activeCount.toString(),
                    label = "进行中",
                    icon = Icons.Default.Pending,
                    tint = MaterialTheme.colorScheme.primary
                )
                
                StatisticsItem(
                    value = completedCount.toString(),
                    label = "已完成",
                    icon = Icons.Default.Done,
                    tint = Color.Green
                )
                
                StatisticsItem(
                    value = totalRecords.toString(),
                    label = "总记录",
                    icon = Icons.Default.List,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                StatisticsItem(
                    value = String.format("%.1f%%", attendanceRate * 100),
                    label = "平均出勤率",
                    icon = Icons.Default.PersonOutline,
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
private fun StatisticsItem(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun AttendanceCard(
    attendance: Attendance,
    dateFormat: SimpleDateFormat,
    onClick: () -> Unit,
    onEndAttendance: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp)
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
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
                // 状态指示器
                val chipColor = when (attendance.status) {
                    AttendanceStatus.ACTIVE -> MaterialTheme.colorScheme.primary
                    AttendanceStatus.COMPLETED -> Color.Gray
                    AttendanceStatus.CANCELLED -> MaterialTheme.colorScheme.error
                }
                
                val statusText = when (attendance.status) {
                    AttendanceStatus.ACTIVE -> "进行中"
                    AttendanceStatus.COMPLETED -> "已结束"
                    AttendanceStatus.CANCELLED -> "已取消"
                }
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(chipColor.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusText,
                        fontSize = 12.sp,
                        color = chipColor
                    )
                }
            }
            
            if (attendance.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = attendance.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                val endTimeText = attendance.endTime?.let { dateFormat.format(it) } ?: "进行中"
                Text(
                    text = "${dateFormat.format(attendance.createdAt)} - $endTimeText",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PersonOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = "已签到: ${attendance.attendees.size}人",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (attendance.location.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = attendance.location,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            if (attendance.status == AttendanceStatus.ACTIVE) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = onEndAttendance,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "结束签到"
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text("结束签到")
                }
            }
        }
    }
} 