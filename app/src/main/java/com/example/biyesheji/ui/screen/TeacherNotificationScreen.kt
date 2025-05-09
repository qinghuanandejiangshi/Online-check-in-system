package com.example.biyesheji.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

data class Notification(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: Date,
    val createdBy: String,
    val targetCourseId: String? = null,
    val targetCourseName: String? = null,
    val isPublished: Boolean
)

// 模拟数据仓库
object NotificationRepository {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    
    fun getNotifications(): List<Notification> {
        return listOf(
            Notification(
                id = "1",
                title = "期末考试通知",
                content = "计算机科学与技术专业本学期期末考试将于2023年12月25日开始，请各位同学提前做好准备。",
                createdAt = dateFormat.parse("2023-11-20 14:30")!!,
                createdBy = "张教授",
                isPublished = true
            ),
            Notification(
                id = "2",
                title = "Java课程实验调整",
                content = "由于实验室设备维护，本周Java课程实验调整到下周一进行，请各位同学注意时间安排。",
                createdAt = dateFormat.parse("2023-11-22 09:15")!!,
                createdBy = "张教授",
                targetCourseId = "course1",
                targetCourseName = "Java程序设计",
                isPublished = true
            ),
            Notification(
                id = "3",
                title = "教学评估通知",
                content = "本学期教学评估将于下周开始，请各位同学在规定时间内完成评估。",
                createdAt = dateFormat.parse("2023-11-25 16:40")!!,
                createdBy = "张教授",
                isPublished = false
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherNotificationScreen(
    onBackClick: () -> Unit,
    onCreateNotification: () -> Unit = {}
) {
    val notifications = remember { NotificationRepository.getNotifications() }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("通知管理") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateNotification) {
                Icon(Icons.Default.Add, contentDescription = "创建通知")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 显示通知统计信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(modifier = Modifier.width(150.dp)) {
                    NotificationStatCard(
                        title = "已发布",
                        count = notifications.count { it.isPublished },
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                }
                
                Box(modifier = Modifier.width(150.dp)) {
                    NotificationStatCard(
                        title = "草稿",
                        count = notifications.count { !it.isPublished },
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "通知列表",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notifications) { notification ->
                    NotificationCard(notification = notification)
                }
            }
        }
    }
}

@Composable
private fun NotificationStatCard(
    title: String,
    count: Int,
    containerColor: androidx.compose.ui.graphics.Color
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
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = title,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun NotificationCard(notification: Notification) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { /* 导航到通知详情页面 */ }
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
                    text = notification.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                
                val statusColor = if (notification.isPublished) 
                    MaterialTheme.colorScheme.primary
                else 
                    MaterialTheme.colorScheme.tertiary
                
                val statusText = if (notification.isPublished) "已发布" else "草稿"
                
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = statusText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = statusColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            notification.targetCourseName?.let {
                Text(
                    text = "课程: $it",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            Text(
                text = notification.content,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "创建时间: ${dateFormat.format(notification.createdAt)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                
                Row {
                    IconButton(
                        onClick = { /* 编辑通知 */ },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Create,
                            contentDescription = "编辑",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    IconButton(
                        onClick = { /* 删除通知 */ },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "删除",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
} 