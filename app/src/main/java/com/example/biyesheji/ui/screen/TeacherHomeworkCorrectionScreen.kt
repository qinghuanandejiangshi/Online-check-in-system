package com.example.biyesheji.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biyesheji.data.HomeworkRepository
import com.example.biyesheji.model.Homework
import com.example.biyesheji.model.HomeworkStatus
import com.example.biyesheji.model.HomeworkSubmission
import com.example.biyesheji.model.SubmissionStatus
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherHomeworkCorrectionScreen(onBackClick: () -> Unit) {
    val homeworks = remember { HomeworkRepository.getHomeworks() }
    
    // 未批改的作业提交数量
    val uncorrectedCount = remember(homeworks) {
        homeworks.sumOf { homework -> 
            homework.submissions.count { it.status == SubmissionStatus.SUBMITTED || it.status == SubmissionStatus.LATE }
        }
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("作业批改") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
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
                .padding(16.dp)
        ) {
            // 显示未批改的作业数量
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (uncorrectedCount > 0) Icons.Default.Warning else Icons.Default.Check,
                        contentDescription = "未批改作业",
                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (uncorrectedCount > 0) "您有 $uncorrectedCount 份作业待批改" else "所有作业已批改完成",
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "作业列表",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(homeworks) { homework ->
                    HomeworkCard(homework = homework)
                }
            }
        }
    }
}

@Composable
private fun HomeworkCard(homework: Homework) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { /* 导航到作业详情页面 */ }
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
                    text = homework.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                
                val statusColor = when(homework.status) {
                    HomeworkStatus.ACTIVE -> MaterialTheme.colorScheme.primary
                    HomeworkStatus.ENDED -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.secondary
                }
                
                val statusText = when(homework.status) {
                    HomeworkStatus.ACTIVE -> "进行中"
                    HomeworkStatus.ENDED -> "已结束"
                    HomeworkStatus.OPEN -> "开放中"
                    HomeworkStatus.CLOSED -> "已关闭"
                    HomeworkStatus.DRAFT -> "草稿"
                    HomeworkStatus.ARCHIVED -> "已归档"
                }
                
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
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = homework.courseName,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = homework.description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                Text(
                    text = "截止日期: ${dateFormat.format(homework.deadline)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                
                Text(
                    text = "提交: ${homework.submissions.size} | 已批改: ${homework.submissions.count { it.status == SubmissionStatus.GRADED }}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
} 