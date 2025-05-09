package com.example.biyesheji.ui.screen.teacher

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherStudentManagementScreen(
    onNavigateBack: () -> Unit
) {
    // 状态管理
    var searchQuery by remember { mutableStateOf("") }
    val students = remember { generateMockStudents() }
    val focusManager = LocalFocusManager.current
    
    // 筛选学生列表
    val filteredStudents = if (searchQuery.isBlank()) {
        students
    } else {
        students.filter { 
            it.name.contains(searchQuery, ignoreCase = true) || 
            it.studentId.contains(searchQuery, ignoreCase = true)
        }
    }
    
    // 分班管理
    var selectedClass by remember { mutableStateOf("全部") }
    val classes = listOf("全部", "计算机科学班", "软件工程班", "人工智能班")
    
    // 详情与操作对话框
    var selectedStudent by remember { mutableStateOf<StudentInfo?>(null) }
    var showStudentDetails by remember { mutableStateOf(false) }
    var showAddStudentDialog by remember { mutableStateOf(false) }
    
    // 分组统计
    val totalStudents = students.size
    val classDistribution = students.groupBy { it.className }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("学生管理") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showAddStudentDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "添加学生"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddStudentDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = "添加学生"
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
            // 搜索栏
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("搜索学生姓名或学号...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "搜索"
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "清除"
                            )
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 班级筛选器
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(classes) { className ->
                    FilterChip(
                        selected = selectedClass == className,
                        onClick = { selectedClass = className },
                        label = { Text(className) },
                        leadingIcon = if (selectedClass == className) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else null
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 学生统计卡片
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "学生统计",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        StatItem(count = totalStudents, label = "总学生数", icon = Icons.Default.People)
                        
                        classDistribution["计算机科学班"]?.let {
                            StatItem(count = it.size, label = "计算机科学班", icon = Icons.Default.Computer)
                        }
                        
                        classDistribution["软件工程班"]?.let {
                            StatItem(count = it.size, label = "软件工程班", icon = Icons.Default.Code)
                        }
                    }
                }
            }
            
            // 学生列表
            if (filteredStudents.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PersonSearch,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Color.Gray
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = if (searchQuery.isNotEmpty()) "未找到匹配的学生" else "暂无学生",
                            color = Color.Gray
                        )
                        
                        if (searchQuery.isEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(onClick = { showAddStudentDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.PersonAdd,
                                    contentDescription = null
                                )
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                Text("添加学生")
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "学生列表（${filteredStudents.size}人）",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    val displayStudents = if (selectedClass == "全部") {
                        filteredStudents
                    } else {
                        filteredStudents.filter { it.className == selectedClass }
                    }
                    
                    items(displayStudents) { student ->
                        StudentItem(
                            student = student,
                            onClick = {
                                selectedStudent = student
                                showStudentDetails = true
                            }
                        )
                    }
                }
            }
        }
    }
    
    // 学生详情对话框
    if (showStudentDetails && selectedStudent != null) {
        StudentDetailsDialog(
            student = selectedStudent!!,
            onDismiss = { showStudentDetails = false },
            onEdit = {
                // 处理编辑逻辑
                showStudentDetails = false
                // TODO: 实现编辑学生信息的功能
            }
        )
    }
    
    // 添加学生对话框
    if (showAddStudentDialog) {
        AddEditStudentDialog(
            student = null,
            onDismiss = { showAddStudentDialog = false },
            onSave = { name, studentId, className, admissionYear, email, phone ->
                // 添加学生逻辑
                // 在实际应用中，这里应该调用API或数据库操作
                showAddStudentDialog = false
            }
        )
    }
}

@Composable
private fun LazyRow(
    horizontalArrangement: Arrangement.HorizontalOrVertical,
    modifier: Modifier = Modifier,
    content: androidx.compose.foundation.lazy.LazyListScope.() -> Unit
) {
    androidx.compose.foundation.lazy.LazyRow(
        horizontalArrangement = horizontalArrangement,
        modifier = modifier,
        content = content
    )
}

@Composable
private fun StatItem(
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
private fun StudentItem(
    student: StudentInfo,
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
            // 头像
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = student.name.first().toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 学生信息
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = student.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = student.studentId,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 班级标签
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = student.className,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // 入学年份
                    Text(
                        text = "${student.admissionYear}级",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            
            // 操作按钮
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "查看详情"
                )
            }
        }
    }
}

@Composable
private fun StudentDetailsDialog(
    student: StudentInfo,
    onDismiss: () -> Unit,
    onEdit: () -> Unit
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
                // 标题栏
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "学生详情",
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
                        
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "关闭"
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 学生基本信息
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // 头像
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = student.name.first().toString(),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = student.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = student.studentId,
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = student.className,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Surface(
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "${student.admissionYear}级",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 详细信息表格
                DetailItem(icon = Icons.Default.Email, label = "电子邮箱", value = student.email)
                
                DetailItem(icon = Icons.Default.Phone, label = "联系电话", value = student.phone)
                
                DetailItem(
                    icon = Icons.Default.CalendarToday, 
                    label = "注册日期", 
                    value = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(student.registrationDate)
                )
                
                DetailItem(icon = Icons.Default.School, label = "所属年级", value = "${student.admissionYear}级")
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 学业情况
                Text(
                    text = "学业情况",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AcademicStatusItem(
                        label = "平均成绩",
                        value = "${student.averageGrade}",
                        color = when {
                            student.averageGrade >= 90 -> Color(0xFF4CAF50) // 优
                            student.averageGrade >= 80 -> Color(0xFF8BC34A) // 良
                            student.averageGrade >= 70 -> Color(0xFFFFC107) // 中
                            student.averageGrade >= 60 -> Color(0xFFFF9800) // 及格
                            else -> Color(0xFFF44336) // 不及格
                        }
                    )
                    
                    AcademicStatusItem(
                        label = "已完成课程",
                        value = "${student.completedCourses}",
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    AcademicStatusItem(
                        label = "缺勤次数",
                        value = "${student.absenceTimes}",
                        color = if (student.absenceTimes > 5) Color(0xFFF44336) else Color(0xFF4CAF50)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 底部按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        Text("编辑信息")
                    }
                    
                    Button(onClick = onDismiss) {
                        Text("关闭")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditStudentDialog(
    student: StudentInfo?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, Int, String, String) -> Unit
) {
    var name by remember { mutableStateOf(student?.name ?: "") }
    var studentId by remember { mutableStateOf(student?.studentId ?: "") }
    var className by remember { mutableStateOf(student?.className ?: "计算机科学班") }
    var admissionYear by remember { mutableStateOf(student?.admissionYear?.toString() ?: "2023") }
    var email by remember { mutableStateOf(student?.email ?: "") }
    var phone by remember { mutableStateOf(student?.phone ?: "") }
    
    var expanded by remember { mutableStateOf(false) }
    val classes = listOf("计算机科学班", "软件工程班", "人工智能班")
    
    // 验证
    val isValid = name.isNotBlank() && studentId.isNotBlank() && email.contains("@")
    
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
                Text(
                    text = if (student == null) "添加学生" else "编辑学生信息",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 表单
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("姓名") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = studentId,
                    onValueChange = { studentId = it },
                    label = { Text("学号") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 班级选择器
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = className,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("班级") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = ExposedDropdownMenuDefaults.textFieldColors()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        classes.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    className = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = admissionYear,
                    onValueChange = { 
                        if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                            admissionYear = it
                        }
                    },
                    label = { Text("入学年份") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("电子邮箱") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = phone,
                    onValueChange = { 
                        if (it.length <= 11 && it.all { char -> char.isDigit() }) {
                            phone = it
                        }
                    },
                    label = { Text("联系电话") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
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
                            onSave(
                                name,
                                studentId,
                                className,
                                admissionYear.toIntOrNull() ?: 2023,
                                email,
                                phone
                            )
                        },
                        enabled = isValid
                    ) {
                        Text(if (student == null) "添加" else "保存")
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray
            )
            
            Text(
                text = value,
                fontSize = 16.sp
            )
        }
    }
    
    Divider(
        modifier = Modifier.padding(start = 40.dp),
        color = Color.LightGray.copy(alpha = 0.5f)
    )
}

@Composable
private fun AcademicStatusItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = color
        )
        
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

// 数据模型
data class StudentInfo(
    val studentId: String,
    val name: String,
    val className: String,
    val admissionYear: Int,
    val email: String,
    val phone: String,
    val registrationDate: Date,
    val averageGrade: Float,
    val completedCourses: Int,
    val absenceTimes: Int
)

// 模拟数据
private fun generateMockStudents(): List<StudentInfo> {
    return listOf(
        StudentInfo(
            studentId = "202301001",
            name = "张三",
            className = "计算机科学班",
            admissionYear = 2023,
            email = "zhangsan@example.com",
            phone = "13800138001",
            registrationDate = Date(System.currentTimeMillis() - 180 * 24 * 60 * 60 * 1000L),
            averageGrade = 92.5f,
            completedCourses = 8,
            absenceTimes = 1
        ),
        StudentInfo(
            studentId = "202301002",
            name = "李四",
            className = "软件工程班",
            admissionYear = 2023,
            email = "lisi@example.com",
            phone = "13800138002",
            registrationDate = Date(System.currentTimeMillis() - 178 * 24 * 60 * 60 * 1000L),
            averageGrade = 85.0f,
            completedCourses = 7,
            absenceTimes = 3
        ),
        StudentInfo(
            studentId = "202301003",
            name = "王五",
            className = "人工智能班",
            admissionYear = 2023,
            email = "wangwu@example.com",
            phone = "13800138003",
            registrationDate = Date(System.currentTimeMillis() - 175 * 24 * 60 * 60 * 1000L),
            averageGrade = 78.5f,
            completedCourses = 6,
            absenceTimes = 2
        ),
        StudentInfo(
            studentId = "202201001",
            name = "赵六",
            className = "计算机科学班",
            admissionYear = 2022,
            email = "zhaoliu@example.com",
            phone = "13800138004",
            registrationDate = Date(System.currentTimeMillis() - 540 * 24 * 60 * 60 * 1000L),
            averageGrade = 88.0f,
            completedCourses = 15,
            absenceTimes = 4
        ),
        StudentInfo(
            studentId = "202201002",
            name = "钱七",
            className = "软件工程班",
            admissionYear = 2022,
            email = "qianqi@example.com",
            phone = "13800138005",
            registrationDate = Date(System.currentTimeMillis() - 538 * 24 * 60 * 60 * 1000L),
            averageGrade = 93.5f,
            completedCourses = 16,
            absenceTimes = 0
        ),
        StudentInfo(
            studentId = "202201003",
            name = "孙八",
            className = "人工智能班",
            admissionYear = 2022,
            email = "sunba@example.com",
            phone = "13800138006",
            registrationDate = Date(System.currentTimeMillis() - 535 * 24 * 60 * 60 * 1000L),
            averageGrade = 75.0f,
            completedCourses = 14,
            absenceTimes = 7
        ),
        StudentInfo(
            studentId = "202101001",
            name = "周九",
            className = "计算机科学班",
            admissionYear = 2021,
            email = "zhoujiu@example.com",
            phone = "13800138007",
            registrationDate = Date(System.currentTimeMillis() - 900 * 24 * 60 * 60 * 1000L),
            averageGrade = 82.0f,
            completedCourses = 24,
            absenceTimes = 5
        ),
        StudentInfo(
            studentId = "202101002",
            name = "吴十",
            className = "软件工程班",
            admissionYear = 2021,
            email = "wushi@example.com",
            phone = "13800138008",
            registrationDate = Date(System.currentTimeMillis() - 898 * 24 * 60 * 60 * 1000L),
            averageGrade = 87.5f,
            completedCourses = 25,
            absenceTimes = 2
        )
    )
} 