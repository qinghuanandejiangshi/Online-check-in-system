package com.example.biyesheji.ui.screen.student

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.biyesheji.ui.common.clickableCompat
import com.example.biyesheji.ui.theme.Blue500
import com.example.biyesheji.ui.theme.Green500
import com.example.biyesheji.ui.theme.Orange500
import com.example.biyesheji.ui.theme.Red500
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

// 作业状态枚举
enum class HomeworkStatus {
    NOT_SUBMITTED,   // 未提交
    SUBMITTED,       // 已提交
    GRADED           // 已批改
}

// 作业数据类
data class StudentHomework(
    val id: String = UUID.randomUUID().toString(),
    val courseId: String,
    val courseName: String,
    val title: String,
    val description: String,
    val deadline: Date,
    val releaseDate: Date = Date(System.currentTimeMillis() - Random.nextLong(1, 30) * 24 * 60 * 60 * 1000),
    val weight: Int, // 占总成绩的权重
    val status: HomeworkStatus = HomeworkStatus.NOT_SUBMITTED,
    val score: Double? = null,
    val feedback: String? = null,
    val submissionDate: Date? = null,
    val submissionContent: String? = null,
    val attachments: List<String> = emptyList()
)

// 作业仓库对象
object StudentHomeworkRepository {
    // 模拟作业数据
    val homeworks = mutableStateListOf(
        StudentHomework(
            courseId = "COMP0001",
            courseName = "Java程序设计",
            title = "Java面向对象编程实践",
            description = "实现一个简单的学生信息管理系统，要求使用面向对象的编程思想，包含至少3个类，实现学生信息的增删改查功能。",
            deadline = Date(System.currentTimeMillis() + 5 * 24 * 60 * 60 * 1000),
            weight = 15,
            status = HomeworkStatus.NOT_SUBMITTED
        ),
        StudentHomework(
            courseId = "COMP0002",
            courseName = "数据结构",
            title = "链表操作实现",
            description = "实现单链表的创建、插入、删除、查找等基本操作，并编写测试用例验证你的实现。",
            deadline = Date(System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000),
            weight = 20,
            status = HomeworkStatus.NOT_SUBMITTED
        ),
        StudentHomework(
            courseId = "COMP0003",
            courseName = "计算机网络",
            title = "Socket编程实验",
            description = "使用Socket编程实现一个简单的客户端-服务器通信程序，要求能够实现基本的消息收发功能。",
            deadline = Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000),
            weight = 25,
            status = HomeworkStatus.NOT_SUBMITTED
        ),
        StudentHomework(
            courseId = "COMP0001",
            courseName = "Java程序设计",
            title = "Java集合框架应用",
            description = "使用Java集合框架（如ArrayList、HashMap等）解决实际问题，并比较不同集合类的性能差异。",
            deadline = Date(System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000),
            weight = 15,
            status = HomeworkStatus.SUBMITTED,
            submissionDate = Date(System.currentTimeMillis() - 11 * 24 * 60 * 60 * 1000),
            submissionContent = "我的作业内容：\n1. 实现了ArrayList和LinkedList的性能对比\n2. 分析了HashMap和TreeMap的查找效率\n3. 使用集合框架实现了简单的图书管理系统"
        ),
        StudentHomework(
            courseId = "COMP0002",
            courseName = "数据结构",
            title = "排序算法实现与分析",
            description = "实现至少3种排序算法，并对它们的时间复杂度和空间复杂度进行分析比较。要求编写测试用例，比较它们在不同数据规模和数据分布情况下的性能。",
            deadline = Date(System.currentTimeMillis() - 15 * 24 * 60 * 60 * 1000),
            weight = 20,
            status = HomeworkStatus.GRADED,
            submissionDate = Date(System.currentTimeMillis() - 16 * 24 * 60 * 60 * 1000),
            submissionContent = "我的作业内容：\n1. 实现了冒泡排序、快速排序和归并排序\n2. 分析了三种算法的时间和空间复杂度\n3. 使用随机数据和有序数据进行了性能测试\n4. 图表展示了不同数据规模下的性能差异",
            score = 92.0,
            feedback = "算法实现正确，分析透彻，性能测试全面。建议可以再多考虑一些特殊情况下的优化策略。"
        ),
        StudentHomework(
            courseId = "COMP0004",
            courseName = "软件工程",
            title = "软件需求分析文档",
            description = "选择一个实际的软件项目，编写完整的软件需求规格说明书(SRS)，包括功能需求、非功能需求、用例图和用例描述等。",
            deadline = Date(System.currentTimeMillis() - 20 * 24 * 60 * 60 * 1000),
            weight = 30,
            status = HomeworkStatus.GRADED,
            submissionDate = Date(System.currentTimeMillis() - 21 * 24 * 60 * 60 * 1000),
            submissionContent = "我的作业内容：\n完成了一个在线教育平台的需求分析文档，包含：\n1. 系统概述\n2. 详细功能需求\n3. 非功能需求分析\n4. 用例图和15个核心用例\n5. 数据字典\n6. 系统界面原型",
            score = 88.0,
            feedback = "需求分析较为全面，用例描述清晰。建议增加更多的约束条件和边界情况的考虑，并完善系统性能相关的非功能需求。"
        )
    )
    
    // 提交作业
    fun submitHomework(homeworkId: String, content: String, attachments: List<String> = emptyList()): Boolean {
        val index = homeworks.indexOfFirst { it.id == homeworkId }
        if (index != -1) {
            val homework = homeworks[index]
            homeworks[index] = homework.copy(
                status = HomeworkStatus.SUBMITTED,
                submissionDate = Date(),
                submissionContent = content,
                attachments = attachments
            )
            return true
        }
        return false
    }
    
    // 获取未提交的作业数量
    fun getNotSubmittedCount(): Int {
        return homeworks.count { it.status == HomeworkStatus.NOT_SUBMITTED }
    }
    
    // 获取已提交未批改的作业数量
    fun getSubmittedCount(): Int {
        return homeworks.count { it.status == HomeworkStatus.SUBMITTED }
    }
    
    // 获取已批改的作业数量
    fun getGradedCount(): Int {
        return homeworks.count { it.status == HomeworkStatus.GRADED }
    }
    
    // 获取平均分数
    fun getAverageScore(): Double {
        val gradedHomeworks = homeworks.filter { it.status == HomeworkStatus.GRADED && it.score != null }
        return if (gradedHomeworks.isNotEmpty()) {
            gradedHomeworks.sumOf { it.score!! } / gradedHomeworks.size
        } else {
            0.0
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentHomeworkScreen(
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("未提交", "已提交", "已批改")
    
    var selectedHomework by remember { mutableStateOf<StudentHomework?>(null) }
    var showSubmitDialog by remember { mutableStateOf(false) }
    var showDetailDialog by remember { mutableStateOf(false) }
    
    var submissionContent by remember { mutableStateOf("") }
    
    // 根据当前选中的标签过滤作业
    val filteredHomeworks = when (selectedTab) {
        0 -> StudentHomeworkRepository.homeworks.filter { it.status == HomeworkStatus.NOT_SUBMITTED }
        1 -> StudentHomeworkRepository.homeworks.filter { it.status == HomeworkStatus.SUBMITTED }
        2 -> StudentHomeworkRepository.homeworks.filter { it.status == HomeworkStatus.GRADED }
        else -> emptyList()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("作业管理") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
        ) {
            // 统计卡片
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 未提交作业
                StatisticCard(
                    count = StudentHomeworkRepository.getNotSubmittedCount(),
                    label = "未提交",
                    color = Red500,
                    icon = Icons.Default.AssignmentLate,
                    modifier = Modifier.weight(1f)
                )
                
                // 已提交作业
                StatisticCard(
                    count = StudentHomeworkRepository.getSubmittedCount(),
                    label = "已提交",
                    color = Orange500,
                    icon = Icons.Default.Assignment,
                    modifier = Modifier.weight(1f)
                )
                
                // 已批改作业
                StatisticCard(
                    count = StudentHomeworkRepository.getGradedCount(),
                    label = "已批改",
                    color = Green500,
                    icon = Icons.Default.AssignmentTurnedIn,
                    modifier = Modifier.weight(1f)
                )
            }
            
            // 分数统计
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Score,
                        contentDescription = null,
                        tint = Blue500
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "平均得分",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        
                        Text(
                            text = String.format("%.1f", StudentHomeworkRepository.getAverageScore()),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    // 简单的进度条
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.LightGray)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(120.dp * (StudentHomeworkRepository.getAverageScore() / 100).toFloat())
                                .background(
                                    when {
                                        StudentHomeworkRepository.getAverageScore() >= 90 -> Green500
                                        StudentHomeworkRepository.getAverageScore() >= 80 -> Blue500
                                        StudentHomeworkRepository.getAverageScore() >= 70 -> Orange500
                                        else -> Red500
                                    }
                                )
                        )
                    }
                }
            }
            
            // 标签栏
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }
            
            // 作业列表
            if (filteredHomeworks.isEmpty()) {
                // 空状态
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
                            imageVector = when (selectedTab) {
                                0 -> Icons.Default.AssignmentLate
                                1 -> Icons.Default.Assignment
                                else -> Icons.Default.AssignmentTurnedIn
                            },
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "暂无${tabs[selectedTab]}的作业",
                            fontSize = 18.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(filteredHomeworks) { homework ->
                        HomeworkCard(
                            homework = homework,
                            onClick = {
                                selectedHomework = homework
                                if (homework.status == HomeworkStatus.NOT_SUBMITTED) {
                                    submissionContent = ""
                                    showSubmitDialog = true
                                } else {
                                    showDetailDialog = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }
    
    // 提交作业对话框
    if (showSubmitDialog && selectedHomework != null) {
        SubmitHomeworkDialog(
            homework = selectedHomework!!,
            content = submissionContent,
            onContentChange = { submissionContent = it },
            onSubmit = {
                StudentHomeworkRepository.submitHomework(selectedHomework!!.id, submissionContent)
                showSubmitDialog = false
            },
            onDismiss = { showSubmitDialog = false }
        )
    }
    
    // 查看作业详情对话框
    if (showDetailDialog && selectedHomework != null) {
        HomeworkDetailDialog(
            homework = selectedHomework!!,
            onDismiss = { showDetailDialog = false }
        )
    }
}

@Composable
private fun StatisticCard(
    count: Int,
    label: String,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = count.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun HomeworkCard(
    homework: StudentHomework,
    onClick: () -> Unit
) {
    val statusColor = when (homework.status) {
        HomeworkStatus.NOT_SUBMITTED -> {
            if (homework.deadline.before(Date())) Red500 else Orange500
        }
        HomeworkStatus.SUBMITTED -> Blue500
        HomeworkStatus.GRADED -> Green500
    }
    
    val statusText = when (homework.status) {
        HomeworkStatus.NOT_SUBMITTED -> {
            if (homework.deadline.before(Date())) "已截止" else "未提交"
        }
        HomeworkStatus.SUBMITTED -> "已提交"
        HomeworkStatus.GRADED -> "已批改"
    }
    
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
            // 课程信息和状态
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = homework.courseName,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Box(
                    modifier = Modifier
                        .border(1.dp, statusColor, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusText,
                        fontSize = 12.sp,
                        color = statusColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 作业标题
            Text(
                text = homework.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 作业描述
            Text(
                text = homework.description,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Color.DarkGray
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Divider()
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 日期信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 发布日期
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = "发布: ${dateFormat.format(homework.releaseDate)}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // 截止日期
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (homework.deadline.before(Date())) Red500 else Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = "截止: ${dateFormat.format(homework.deadline)}",
                        fontSize = 12.sp,
                        color = if (homework.deadline.before(Date())) Red500 else Color.Gray
                    )
                }
            }
            
            // 如果已批改，显示分数
            if (homework.status == HomeworkStatus.GRADED && homework.score != null) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Score,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Green500
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = "得分: ${homework.score}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Green500
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Text(
                        text = "权重: ${homework.weight}%",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            } else {
                // 只显示权重
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "权重: ${homework.weight}%",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
private fun SubmitHomeworkDialog(
    homework: StudentHomework,
    content: String,
    onContentChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // 标题
                Text(
                    text = "提交作业",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 作业信息
                Text(
                    text = homework.courseName,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = homework.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 截止日期
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (homework.deadline.before(Date())) Red500 else Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = "截止日期: ${dateFormat.format(homework.deadline)}",
                        fontSize = 14.sp,
                        color = if (homework.deadline.before(Date())) Red500 else Color.Gray
                    )
                    
                    if (homework.deadline.before(Date())) {
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "已截止",
                            fontSize = 14.sp,
                            color = Red500,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 作业描述
                Text(
                    text = "作业要求:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = homework.description,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 输入框
                OutlinedTextField(
                    value = content,
                    onValueChange = onContentChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    label = { Text("作业内容") },
                    placeholder = { Text("在此输入您的作业内容...") }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 附件按钮
                OutlinedButton(
                    onClick = { /* TODO: 添加附件功能 */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = null
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text("添加附件")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text("取消")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = onSubmit,
                        enabled = content.isNotBlank()
                    ) {
                        Text("提交")
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeworkDetailDialog(
    homework: StudentHomework,
    onDismiss: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // 标题
                Text(
                    text = if (homework.status == HomeworkStatus.GRADED) "已批改作业" else "已提交作业",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 作业信息
                Text(
                    text = homework.courseName,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = homework.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 日期信息
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = "截止日期: ${dateFormat.format(homework.deadline)}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                
                // 提交时间
                homework.submissionDate?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        Text(
                            text = "提交时间: ${dateFormat.format(it)}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Divider()
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 如果已批改，显示分数和反馈
                if (homework.status == HomeworkStatus.GRADED && homework.score != null) {
                    // 分数
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "得分:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = homework.score.toString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                homework.score >= 90 -> Green500
                                homework.score >= 80 -> Blue500
                                homework.score >= 70 -> Orange500
                                else -> Red500
                            }
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        Text(
                            text = "权重: ${homework.weight}%",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 教师反馈
                    homework.feedback?.let {
                        Text(
                            text = "教师反馈:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF5F5F5)
                        ) {
                            Text(
                                text = it,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                
                // 作业内容
                homework.submissionContent?.let {
                    Text(
                        text = "提交内容:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF5F5F5)
                    ) {
                        Text(
                            text = it,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
                
                // 附件列表
                if (homework.attachments.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "附件:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    homework.attachments.forEach { attachment ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
                                .clickable { /* TODO: 查看附件 */ }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.InsertDriveFile,
                                contentDescription = null,
                                tint = Blue500
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(
                                text = attachment,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                            
                            IconButton(
                                onClick = { /* TODO: 下载附件 */ },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = "下载",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 关闭按钮
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("关闭")
                }
            }
        }
    }
} 