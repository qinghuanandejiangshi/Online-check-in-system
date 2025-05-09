package com.example.biyesheji.ui.screen.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherAssignmentGradingScreen(
    assignmentId: String,
    courseId: String,
    onNavigateBack: () -> Unit
) {
    // 模拟的作业信息
    val assignment = remember {
        AssignmentInfo(
            id = assignmentId,
            title = "期末论文",
            description = "请提交一篇关于Android开发的论文，不少于3000字。",
            courseId = courseId,
            deadline = Date(),
            totalPoints = 100,
            submissions = generateMockSubmissions()
        )
    }
    
    var selectedSubmission by remember { mutableStateOf<SubmissionInfo?>(null) }
    var showGradingDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    
    // 根据搜索查询过滤提交
    val filteredSubmissions = if (searchQuery.isBlank()) {
        assignment.submissions
    } else {
        assignment.submissions.filter { it.studentName.contains(searchQuery, ignoreCase = true) }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("作业批改 - ${assignment.title}") },
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
                .padding(16.dp)
        ) {
            // 作业概览
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = assignment.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = assignment.description,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "总分: ${assignment.totalPoints}",
                            fontWeight = FontWeight.Medium
                        )
                        
                        Text(
                            text = "截止日期: ${assignment.deadline}",
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 搜索栏
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("搜索学生...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "搜索")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "清除")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 统计信息
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "已提交: ${assignment.submissions.size}",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "未批改: ${assignment.submissions.count { it.score == null }}",
                    fontWeight = FontWeight.Bold,
                    color = if (assignment.submissions.any { it.score == null }) 
                        MaterialTheme.colorScheme.error else Color.Gray
                )
            }
            
            Divider()
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 提交列表
            if (filteredSubmissions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isNotEmpty()) "没有找到相关学生的提交" else "暂无提交",
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredSubmissions) { submission ->
                        SubmissionItem(
                            submission = submission,
                            onGradeClick = {
                                selectedSubmission = submission
                                showGradingDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
    
    // 评分对话框
    if (showGradingDialog && selectedSubmission != null) {
        GradingDialog(
            submission = selectedSubmission!!,
            totalPoints = assignment.totalPoints,
            onDismiss = { 
                showGradingDialog = false 
                selectedSubmission = null
            },
            onGradeSubmit = { score, feedback ->
                // 在实际应用中，这里应该调用Repository的方法更新评分
                // 此处仅作界面演示
                val index = assignment.submissions.indexOf(selectedSubmission)
                if (index != -1) {
                    val updatedSubmission = selectedSubmission!!.copy(
                        score = score,
                        feedback = feedback,
                        gradedAt = Date()
                    )
                    
                    assignment.submissions[index] = updatedSubmission
                }
                
                showGradingDialog = false
                selectedSubmission = null
            }
        )
    }
}

@Composable
private fun SubmissionItem(
    submission: SubmissionInfo,
    onGradeClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 顶部行：学生信息和状态
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = submission.studentName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (submission.score != null) 
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    else 
                        MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = if (submission.score != null) "已批改" else "未批改",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = if (submission.score != null) 
                            MaterialTheme.colorScheme.primary
                        else 
                            MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 提交时间
            Text(
                text = "提交时间: ${submission.submittedAt}",
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 提交内容
            Text(
                text = "提交内容:",
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = submission.content,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 底部区域
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 分数信息
                if (submission.score != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        Text(
                            text = "得分: ${submission.score}",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                // 评分按钮
                Button(
                    onClick = onGradeClick,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Icon(
                        imageVector = if (submission.score != null) 
                            Icons.Default.Edit else Icons.Default.Add,
                        contentDescription = if (submission.score != null) "修改评分" else "评分"
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = if (submission.score != null) "修改评分" else "评分"
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GradingDialog(
    submission: SubmissionInfo,
    totalPoints: Int,
    onDismiss: () -> Unit,
    onGradeSubmit: (Int, String) -> Unit
) {
    var score by remember { mutableStateOf(submission.score?.toString() ?: "") }
    var feedback by remember { mutableStateOf(submission.feedback ?: "") }
    var scoreError by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("评分 - ${submission.studentName}") },
        text = {
            Column {
                // 学生提交内容
                Text(
                    text = "提交内容:",
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Text(
                        text = submission.content,
                        modifier = Modifier.padding(12.dp),
                        fontSize = 14.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 分数输入
                OutlinedTextField(
                    value = score,
                    onValueChange = { 
                        score = it
                        scoreError = ""
                    },
                    label = { Text("分数 (满分 $totalPoints)") },
                    singleLine = true,
                    isError = scoreError.isNotEmpty(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                if (scoreError.isNotEmpty()) {
                    Text(
                        text = scoreError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 反馈输入
                OutlinedTextField(
                    value = feedback,
                    onValueChange = { feedback = it },
                    label = { Text("反馈意见") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (score.isBlank()) {
                        scoreError = "请输入分数"
                        return@TextButton
                    }
                    
                    val scoreValue = score.toIntOrNull()
                    
                    when {
                        scoreValue == null -> {
                            scoreError = "请输入有效的数字"
                        }
                        scoreValue < 0 -> {
                            scoreError = "分数不能为负"
                        }
                        scoreValue > totalPoints -> {
                            scoreError = "分数不能超过满分 $totalPoints"
                        }
                        else -> {
                            onGradeSubmit(scoreValue, feedback)
                        }
                    }
                }
            ) {
                Text("提交")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

// 数据模型
data class AssignmentInfo(
    val id: String,
    val title: String,
    val description: String,
    val courseId: String,
    val deadline: Date,
    val totalPoints: Int,
    val submissions: MutableList<SubmissionInfo>
)

data class SubmissionInfo(
    val id: String,
    val assignmentId: String,
    val studentId: String,
    val studentName: String,
    val content: String,
    val submittedAt: Date,
    val score: Int? = null,
    val feedback: String? = null,
    val gradedAt: Date? = null
)

// 模拟数据生成
private fun generateMockSubmissions(): MutableList<SubmissionInfo> {
    return mutableListOf(
        SubmissionInfo(
            id = "1",
            assignmentId = "assignment1",
            studentId = "student1",
            studentName = "张三",
            content = "我的期末论文内容，详细讨论了Android开发中的架构模式以及最佳实践。主要分析了MVVM、MVP以及MVC的优缺点，并给出了实践建议。",
            submittedAt = Date(System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000),
            score = 85,
            feedback = "整体不错，但缺乏一些具体的代码示例和实际案例分析。",
            gradedAt = Date(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000)
        ),
        SubmissionInfo(
            id = "2",
            assignmentId = "assignment1",
            studentId = "student2",
            studentName = "李四",
            content = "本文探讨了Android Jetpack组件的应用及其对开发效率的提升。详细分析了ViewModel、LiveData、Room和WorkManager等组件的使用场景和注意事项。",
            submittedAt = Date(System.currentTimeMillis() - 4 * 24 * 60 * 60 * 1000),
            score = 92,
            feedback = "内容全面，结构清晰，示例丰富。",
            gradedAt = Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000)
        ),
        SubmissionInfo(
            id = "3",
            assignmentId = "assignment1",
            studentId = "student3",
            studentName = "王五",
            content = "分析了Kotlin协程在Android开发中的应用，包括基本概念、使用方法、优势以及常见问题的解决方案。同时对比了RxJava等其他异步处理方案。",
            submittedAt = Date(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000)
        ),
        SubmissionInfo(
            id = "4",
            assignmentId = "assignment1",
            studentId = "student4",
            studentName = "赵六",
            content = "探讨了移动应用开发中的UI/UX设计原则，分析了Material Design的理念和实践方法，并结合具体案例讨论了良好用户体验的重要性。",
            submittedAt = Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000)
        ),
        SubmissionInfo(
            id = "5",
            assignmentId = "assignment1",
            studentId = "student5",
            studentName = "孙七",
            content = "研究了Android应用性能优化的各种技术，包括布局优化、内存管理、电量优化和网络优化等方面，并提供了实用的优化建议和工具介绍。",
            submittedAt = Date(System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000)
        )
    )
} 