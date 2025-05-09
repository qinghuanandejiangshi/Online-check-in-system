package com.example.biyesheji.ui.screen.student

import android.widget.Toast
import androidx.compose.foundation.background
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
import com.example.biyesheji.ui.common.clickableCompat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentOnlineQAScreen(
    courseId: String,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    
    // 模拟QA数据
    val qaList = remember {
        listOf(
            QAItem(
                id = "qa_1",
                question = "Java中如何实现多线程？",
                answer = "Java中实现多线程有两种方式：1. 继承Thread类并重写run方法；2. 实现Runnable接口并实现run方法。Thread类本质上也是实现了Runnable接口的一个实例，代表一个线程的实例。启动线程的唯一方法就是通过Thread类的start()实例方法。",
                courseId = courseId,
                courseName = "Java程序设计",
                studentName = "张三",
                teacherName = "李教授",
                status = QAStatus.ANSWERED,
                createdAt = Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000),
                answeredAt = Date(System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000)
            ),
            QAItem(
                id = "qa_2",
                question = "Java的垃圾回收机制是什么？",
                answer = null,
                courseId = courseId,
                courseName = "Java程序设计",
                studentName = "李四",
                teacherName = "李教授",
                status = QAStatus.PENDING,
                createdAt = Date(System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000),
                answeredAt = null
            ),
            QAItem(
                id = "qa_3",
                question = "如何使用Java实现单例模式？",
                answer = "单例模式确保一个类只有一个实例，并提供一个全局访问点。实现方法有：\n1. 懒汉式（线程不安全）\n2. 懒汉式（线程安全，使用synchronized关键字）\n3. 饿汉式（静态常量）\n4. 饿汉式（静态代码块）\n5. 双重检查锁\n6. 静态内部类\n7. 枚举方式",
                courseId = courseId,
                courseName = "Java程序设计",
                studentName = "王五",
                teacherName = "李教授",
                status = QAStatus.ANSWERED,
                createdAt = Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000),
                answeredAt = Date(System.currentTimeMillis() - 6 * 24 * 60 * 60 * 1000)
            )
        )
    }
    
    // 新问题状态
    var showAskDialog by remember { mutableStateOf(false) }
    var selectedQA by remember { mutableStateOf<QAItem?>(null) }
    var showQADetailDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("在线答疑") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showAskDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.QuestionAnswer,
                            contentDescription = "提问"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAskDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "提问"
                )
            }
        }
    ) { paddingValues ->
        if (qaList.isEmpty()) {
            // 空状态
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.QuestionAnswer,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "暂无答疑记录",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(onClick = { showAskDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.QuestionAnswer,
                            contentDescription = null
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text("提出问题")
                    }
                }
            }
        } else {
            // QA列表
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(qaList) { qa ->
                    QAItemCard(
                        qa = qa,
                        onClick = {
                            selectedQA = qa
                            showQADetailDialog = true
                        }
                    )
                }
            }
        }
    }
    
    // 提问对话框
    if (showAskDialog) {
        AskQuestionDialog(
            onDismiss = { showAskDialog = false },
            onSubmit = { question ->
                // 实际应用中，这里会调用API提交问题
                Toast.makeText(context, "问题已提交", Toast.LENGTH_SHORT).show()
                showAskDialog = false
            }
        )
    }
    
    // 问答详情对话框
    if (showQADetailDialog && selectedQA != null) {
        QADetailDialog(
            qa = selectedQA!!,
            onDismiss = {
                showQADetailDialog = false
                selectedQA = null
            }
        )
    }
}

@Composable
private fun QAItemCard(
    qa: QAItem,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickableCompat(onClick = onClick),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 用户头像
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = qa.studentName.first().toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = qa.studentName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Text(
                        text = dateFormat.format(qa.createdAt),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                
                // 状态标签
                val statusColor = when (qa.status) {
                    QAStatus.ANSWERED -> MaterialTheme.colorScheme.primary
                    QAStatus.PENDING -> Color(0xFFFFA000) // 琥珀色
                }
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(statusColor.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = when (qa.status) {
                            QAStatus.ANSWERED -> "已回答"
                            QAStatus.PENDING -> "待回答"
                        },
                        fontSize = 12.sp,
                        color = statusColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 问题内容
            Text(
                text = qa.question,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 答案预览（如果有）
            if (qa.status == QAStatus.ANSWERED && qa.answer != null) {
                Text(
                    text = qa.answer,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 底部信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = qa.courseName,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                
                TextButton(onClick = onClick) {
                    Text("查看详情")
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun QADetailDialog(
    qa: QAItem,
    onDismiss: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "问题详情",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "发布于: ${dateFormat.format(qa.createdAt)}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 问题部分
                Text(
                    text = "问题:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = qa.question,
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 回答部分（如果有）
                if (qa.status == QAStatus.ANSWERED && qa.answer != null) {
                    Text(
                        text = "回答:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "由 ${qa.teacherName} 回答于 ${qa.answeredAt?.let { dateFormat.format(it) }}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = qa.answer,
                        fontSize = 14.sp
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "教师尚未回答此问题",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("关闭")
            }
        }
    )
}

@Composable
private fun AskQuestionDialog(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var questionText by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "提出问题",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "请输入您的问题:",
                    fontSize = 16.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = questionText,
                    onValueChange = { questionText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    placeholder = { Text("请详细描述您的问题...") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(questionText) },
                enabled = questionText.isNotEmpty()
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

enum class QAStatus {
    ANSWERED,   // 已回答
    PENDING     // 待回答
}

data class QAItem(
    val id: String,
    val question: String,
    val answer: String?,
    val courseId: String,
    val courseName: String,
    val studentName: String,
    val teacherName: String,
    val status: QAStatus,
    val createdAt: Date,
    val answeredAt: Date?
) 