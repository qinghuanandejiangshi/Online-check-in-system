package com.example.biyesheji.ui.screen.student

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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentHomeworkSubmitScreen(
    assignmentId: String,
    courseId: String,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 模拟作业数据
    val assignment = remember {
        Assignment(
            id = assignmentId,
            title = "Java基础练习",
            description = "完成教材第1章到第3章的课后习题，并按照要求提交电子版作业。请务必按照格式要求完成作业，否则会影响成绩。",
            deadline = "2023-03-15 23:59",
            courseId = courseId,
            courseName = "Java程序设计",
            teacherName = "张教授",
            submissionStatus = SubmissionStatus.NOT_SUBMITTED
        )
    }

    // 学生提交内容
    var submissionContent by remember { mutableStateOf("") }
    var submissionFiles by remember { mutableStateOf<List<String>>(emptyList()) }
    var showAttachFileDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("作业提交") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 作业信息
            item {
                AssignmentInfoCard(assignment)
            }

            // 提交作业内容
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "提交内容",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = submissionContent,
                            onValueChange = { submissionContent = it },
                            label = { Text("在此输入作业内容") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            maxLines = 10
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 已添加的文件
                        if (submissionFiles.isNotEmpty()) {
                            Text(
                                text = "附件 (${submissionFiles.size})",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                submissionFiles.forEachIndexed { index, file ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.InsertDriveFile,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Text(
                                            text = file,
                                            modifier = Modifier.weight(1f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        IconButton(
                                            onClick = {
                                                val updatedFiles = submissionFiles.toMutableList()
                                                updatedFiles.removeAt(index)
                                                submissionFiles = updatedFiles
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "删除附件",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // 添加附件按钮
                        OutlinedButton(
                            onClick = { showAttachFileDialog = true },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AttachFile,
                                contentDescription = "添加附件"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("添加附件")
                        }
                    }
                }
            }

            // 提交按钮
            item {
                Button(
                    onClick = {
                        // 实际应用中，这里会调用API提交作业
                        Toast.makeText(context, "作业提交成功", Toast.LENGTH_SHORT).show()
                        onNavigateBack()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = submissionContent.isNotEmpty() || submissionFiles.isNotEmpty()
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("提交作业")
                }
            }
        }
    }

    // 附件选择对话框
    if (showAttachFileDialog) {
        AlertDialog(
            onDismissRequest = { showAttachFileDialog = false },
            title = { Text("添加附件") },
            text = {
                Text("在实际应用中，这里会打开文件选择器。目前添加一个模拟文件。")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val mockFile = "作业_${Date().time}.pdf"
                        submissionFiles = submissionFiles + mockFile
                        showAttachFileDialog = false
                    }
                ) {
                    Text("添加文件")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAttachFileDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun AssignmentInfoCard(assignment: Assignment) {
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
                text = assignment.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = assignment.courseName,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "作业要求",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = assignment.description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = "教师: ${assignment.teacherName}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = "截止日期: ${assignment.deadline}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            
            // 提交状态
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val statusColor = when (assignment.submissionStatus) {
                    SubmissionStatus.SUBMITTED -> Color.Green
                    SubmissionStatus.GRADED -> MaterialTheme.colorScheme.tertiary
                    SubmissionStatus.NOT_SUBMITTED -> Color.Red
                    SubmissionStatus.LATE -> Color(0xFFFFA000) // 琥珀色
                }
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(statusColor.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = when (assignment.submissionStatus) {
                            SubmissionStatus.SUBMITTED -> "已提交"
                            SubmissionStatus.GRADED -> "已批改"
                            SubmissionStatus.NOT_SUBMITTED -> "未提交"
                            SubmissionStatus.LATE -> "逾期提交"
                        },
                        fontSize = 12.sp,
                        color = statusColor
                    )
                }
            }
        }
    }
}

// 作业提交状态
enum class SubmissionStatus {
    SUBMITTED,    // 已提交
    GRADED,       // 已批改
    NOT_SUBMITTED,// 未提交
    LATE          // 逾期提交
}

// 作业数据类
data class Assignment(
    val id: String,
    val title: String,
    val description: String,
    val deadline: String,
    val courseId: String,
    val courseName: String,
    val teacherName: String,
    val submissionStatus: SubmissionStatus
) 