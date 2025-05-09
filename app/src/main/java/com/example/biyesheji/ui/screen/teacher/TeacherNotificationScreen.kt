package com.example.biyesheji.ui.screen.teacher

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherNotificationScreen(
    onNavigateBack: () -> Unit
) {
    // 状态管理
    val notifications = remember { generateMockNotifications() }
    var selectedNotification by remember { mutableStateOf<NotificationInfo?>(null) }
    var showNotificationDetails by remember { mutableStateOf(false) }
    var showAddNotificationDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("通知管理") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showAddNotificationDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "添加通知"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddNotificationDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "添加通知"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 通知统计卡片
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    NotificationStatItem(
                        count = notifications.size,
                        label = "总通知数",
                        icon = Icons.Default.Notifications
                    )
                    
                    NotificationStatItem(
                        count = notifications.count { it.isPinned },
                        label = "置顶通知",
                        icon = Icons.Default.PushPin
                    )
                    
                    NotificationStatItem(
                        count = notifications.count { !it.isPublished },
                        label = "草稿",
                        icon = Icons.Default.Edit
                    )
                }
            }
            
            // 通知列表
            if (notifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Color.Gray
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "暂无通知",
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(onClick = { showAddNotificationDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text("创建通知")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(notifications.sortedByDescending { it.isPinned }) { notification ->
                        NotificationItem(
                            notification = notification,
                            onClick = {
                                selectedNotification = notification
                                showNotificationDetails = true
                            }
                        )
                    }
                }
            }
        }
    }
    
    // 显示通知详情对话框
    if (showNotificationDetails && selectedNotification != null) {
        NotificationDetailsDialog(
            notification = selectedNotification!!,
            onDismiss = { showNotificationDetails = false },
            onEdit = {
                // 关闭详情对话框，打开编辑对话框
                showNotificationDetails = false
                // 在这里实现编辑逻辑
            },
            onDelete = {
                showNotificationDetails = false
                showDeleteConfirmation = true
            }
        )
    }
    
    // 添加新通知对话框
    if (showAddNotificationDialog) {
        AddEditNotificationDialog(
            notification = null,
            onDismiss = { showAddNotificationDialog = false },
            onSave = { title, content, isPinned, isPublished, targetGroups ->
                // 添加新通知的逻辑
                // 在实际应用中，此处应调用API或存储到数据库
                showAddNotificationDialog = false
            }
        )
    }
    
    // 删除确认对话框
    if (showDeleteConfirmation && selectedNotification != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("确认删除") },
            text = { Text("您确定要删除通知「${selectedNotification!!.title}」吗？此操作不可撤销。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // 删除通知的逻辑
                        // 在实际应用中，此处应调用API或从数据库中删除
                        showDeleteConfirmation = false
                    }
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun NotificationStatItem(
    count: Int,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "$count",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun NotificationItem(
    notification: NotificationInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // 标题行
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (notification.isPinned) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "置顶",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    
                    Text(
                        text = notification.title,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // 通知内容预览
                Text(
                    text = notification.content,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // 底部信息栏
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 状态标签
                    val statusColor = if (notification.isPublished) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color.Gray
                    }
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(statusColor.copy(alpha = 0.1f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (notification.isPublished) "已发布" else "草稿",
                            fontSize = 10.sp,
                            color = statusColor
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // 目标群体
                    Text(
                        text = notification.targetGroups.joinToString(", "),
                        fontSize = 10.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // 发布日期
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    Text(
                        text = dateFormat.format(notification.createdAt),
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationDetailsDialog(
    notification: NotificationInfo,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // 标题和操作按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "通知详情",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    
                    Row {
                        IconButton(onClick = onEdit) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "编辑"
                            )
                        }
                        
                        IconButton(onClick = onDelete) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "删除"
                            )
                        }
                        
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "关闭"
                            )
                        }
                    }
                }
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                // 通知内容
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    // 通知标题
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (notification.isPinned) {
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = "置顶",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        
                        Text(
                            text = notification.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 通知详细内容
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    ) {
                        Text(
                            text = notification.content,
                            modifier = Modifier.padding(16.dp),
                            fontSize = 16.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 通知元数据
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                            Text(
                                text = "创建时间: ${dateFormat.format(notification.createdAt)}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            
                            if (notification.updatedAt != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                Text(
                                    text = "更新时间: ${dateFormat.format(notification.updatedAt)}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "状态: ${if (notification.isPublished) "已发布" else "草稿"}",
                                fontSize = 12.sp,
                                color = if (notification.isPublished) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    Color.Gray
                                }
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                text = "发布对象: ${notification.targetGroups.joinToString(", ")}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 底部按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text("关闭")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditNotificationDialog(
    notification: NotificationInfo?,
    onDismiss: () -> Unit,
    onSave: (String, String, Boolean, Boolean, List<String>) -> Unit
) {
    var title by remember { mutableStateOf(notification?.title ?: "") }
    var content by remember { mutableStateOf(notification?.content ?: "") }
    var isPinned by remember { mutableStateOf(notification?.isPinned ?: false) }
    var isPublished by remember { mutableStateOf(notification?.isPublished ?: true) }
    var selectedTargetGroups by remember { mutableStateOf(notification?.targetGroups ?: listOf("全部学生")) }
    var showTargetGroupSelector by remember { mutableStateOf(false) }
    
    // 验证
    val isValid = title.isNotBlank() && content.isNotBlank()
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // 标题
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (notification == null) "创建新通知" else "编辑通知",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "关闭"
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 标题输入
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("通知标题") },
                    placeholder = { Text("输入通知标题") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 内容输入
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("通知内容") },
                    placeholder = { Text("输入通知内容...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    minLines = 5
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 发布对象选择器按钮
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showTargetGroupSelector = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "发布对象",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                text = selectedTargetGroups.joinToString(", "),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "选择"
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 开关选项
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("置顶通知")
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Switch(
                            checked = isPinned,
                            onCheckedChange = { isPinned = it }
                        )
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isPublished) "立即发布" else "保存为草稿"
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Switch(
                            checked = isPublished,
                            onCheckedChange = { isPublished = it }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 底部按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("取消")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            onSave(title, content, isPinned, isPublished, selectedTargetGroups)
                        },
                        enabled = isValid
                    ) {
                        Text(if (notification == null) "创建" else "保存")
                    }
                }
            }
        }
    }
    
    // 目标群体选择器对话框
    if (showTargetGroupSelector) {
        TargetGroupSelectorDialog(
            selectedGroups = selectedTargetGroups,
            onDismiss = { showTargetGroupSelector = false },
            onConfirm = { groups ->
                selectedTargetGroups = groups
                showTargetGroupSelector = false
            }
        )
    }
}

@Composable
private fun TargetGroupSelectorDialog(
    selectedGroups: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (List<String>) -> Unit
) {
    val availableGroups = listOf("全部学生", "2020级", "2021级", "2022级", "2023级", "计算机科学班", "软件工程班", "人工智能班")
    var selected by remember { mutableStateOf(selectedGroups.toSet()) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择发布对象") },
        text = {
            Column {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                ) {
                    items(availableGroups) { group ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selected = if (selected.contains(group)) {
                                        selected - group
                                    } else {
                                        selected + group
                                    }
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selected.contains(group),
                                onCheckedChange = { isChecked ->
                                    selected = if (isChecked) {
                                        selected + group
                                    } else {
                                        selected - group
                                    }
                                }
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(text = group)
                        }
                    }
                }
                
                if (selected.isEmpty()) {
                    Text(
                        text = "请至少选择一个发布对象",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(selected.toList()) },
                enabled = selected.isNotEmpty()
            ) {
                Text("确认")
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
data class NotificationInfo(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: Date,
    val updatedAt: Date? = null,
    val isPinned: Boolean = false,
    val isPublished: Boolean = true,
    val targetGroups: List<String> = listOf("全部学生")
)

// 模拟数据
private fun generateMockNotifications(): List<NotificationInfo> {
    val calendar = Calendar.getInstance()
    
    // 当前日期
    val now = calendar.time
    
    // 第一个通知 - 置顶
    calendar.add(Calendar.DAY_OF_MONTH, -2)
    val notification1Date = calendar.time
    
    // 第二个通知 - 普通
    calendar.add(Calendar.DAY_OF_MONTH, -3)
    val notification2Date = calendar.time
    
    // 第三个通知 - 草稿
    calendar.add(Calendar.DAY_OF_MONTH, -1)
    val notification3Date = calendar.time
    
    return listOf(
        NotificationInfo(
            id = "not1",
            title = "期末考试安排通知",
            content = """
                各位同学：
                
                本学期期末考试将于6月15日至6月25日举行，具体考试安排如下：
                
                1. 移动应用开发：6月15日上午9:00-11:00，教学楼A401
                2. 数据结构与算法：6月17日下午2:00-4:00，教学楼B302
                3. 计算机网络：6月20日上午9:00-11:00，教学楼A501
                4. 软件工程：6月22日下午2:00-4:00，教学楼C201
                5. 人工智能导论：6月25日上午9:00-11:00，教学楼B401
                
                请各位同学务必按时参加考试，携带学生证和黑色签字笔，不要迟到。如有特殊情况需要调整考试时间，请提前一周与教务处联系。
                
                祝大家考试顺利！
            """.trimIndent(),
            createdAt = notification1Date,
            updatedAt = now,
            isPinned = true,
            isPublished = true,
            targetGroups = listOf("全部学生")
        ),
        NotificationInfo(
            id = "not2",
            title = "第三次实验报告提交要求",
            content = """
                各位同学：
                
                第三次实验报告请按照以下要求提交：
                
                1. 文件命名：学号_姓名_实验3.pdf
                2. 提交内容：实验报告、源代码、运行截图
                3. 提交方式：通过学习平台上传
                4. 截止时间：5月20日晚上23:59
                
                请注意按时提交，逾期将扣分处理。如有疑问，可在课后与助教联系。
            """.trimIndent(),
            createdAt = notification2Date,
            isPinned = false,
            isPublished = true,
            targetGroups = listOf("2022级", "计算机科学班")
        ),
        NotificationInfo(
            id = "not3",
            title = "关于举办科技创新大赛的通知（草稿）",
            content = """
                各位同学：
                
                我院将于下月举办年度科技创新大赛，欢迎各位同学积极报名参加。
                
                大赛主题：数字化时代的创新应用
                报名时间：待定
                比赛时间：待定
                奖项设置：
                - 一等奖：2名，奖金5000元/队
                - 二等奖：5名，奖金3000元/队
                - 三等奖：10名，奖金1000元/队
                
                具体报名方式和比赛要求将在正式通知中公布，敬请期待。
                
                [注：这是草稿，发布前请完善比赛时间和报名方式]
            """.trimIndent(),
            createdAt = notification3Date,
            isPinned = false,
            isPublished = false,
            targetGroups = listOf("全部学生")
        )
    )
} 