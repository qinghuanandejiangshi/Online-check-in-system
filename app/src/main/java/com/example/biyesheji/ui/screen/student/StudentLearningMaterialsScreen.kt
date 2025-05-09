package com.example.biyesheji.ui.screen.student

import android.widget.Toast
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biyesheji.ui.common.clickableCompat
import com.example.biyesheji.ui.theme.AppIcons
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentLearningMaterialsScreen(
    courseId: String,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // 模拟学习资料数据
    val materials = remember {
        listOf(
            LearningMaterial(
                id = "material_1",
                title = "Java编程基础",
                description = "本资料涵盖Java编程的基础知识，包括变量、数据类型、控制流程、函数和类的概念等。适合Java初学者使用。",
                fileType = FileType.PDF,
                fileSize = "2.5 MB",
                uploadedBy = "李教授",
                uploadDate = Date(System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000),
                downloadUrl = "https://example.com/java_basics.pdf",
                courseId = courseId,
                courseName = "Java程序设计"
            ),
            LearningMaterial(
                id = "material_2",
                title = "面向对象编程讲义",
                description = "详细讲解面向对象编程的三大特性：封装、继承和多态，以及如何在Java中实现它们。包含丰富的代码示例和练习。",
                fileType = FileType.PPT,
                fileSize = "5.8 MB",
                uploadedBy = "李教授",
                uploadDate = Date(System.currentTimeMillis() - 20 * 24 * 60 * 60 * 1000),
                downloadUrl = "https://example.com/oop_slides.ppt",
                courseId = courseId,
                courseName = "Java程序设计"
            ),
            LearningMaterial(
                id = "material_3",
                title = "Java集合框架实战",
                description = "详细介绍Java集合框架的使用，包括List、Set、Map等接口及其实现类，以及如何在实际项目中选择合适的集合类型。",
                fileType = FileType.WORD,
                fileSize = "1.2 MB",
                uploadedBy = "李教授",
                uploadDate = Date(System.currentTimeMillis() - 15 * 24 * 60 * 60 * 1000),
                downloadUrl = "https://example.com/java_collections.docx",
                courseId = courseId,
                courseName = "Java程序设计"
            ),
            LearningMaterial(
                id = "material_4",
                title = "线程与并发编程",
                description = "讲解Java中的多线程编程，包括线程的创建、线程安全、同步机制、Lock接口、线程池等内容。",
                fileType = FileType.VIDEO,
                fileSize = "120 MB",
                uploadedBy = "李教授",
                uploadDate = Date(System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000),
                downloadUrl = "https://example.com/java_threading.mp4",
                courseId = courseId,
                courseName = "Java程序设计"
            ),
            LearningMaterial(
                id = "material_5",
                title = "Java期末复习资料",
                description = "涵盖本学期Java课程所有重点知识点，包括各章节的知识总结和经典习题解析。适合期末复习使用。",
                fileType = FileType.ZIP,
                fileSize = "15.7 MB",
                uploadedBy = "李教授",
                uploadDate = Date(System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000),
                downloadUrl = "https://example.com/java_final_review.zip",
                courseId = courseId,
                courseName = "Java程序设计"
            )
        )
    }
    
    var selectedMaterial by remember { mutableStateOf<LearningMaterial?>(null) }
    var showMaterialDetailDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("学习资料") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // 实际应用中可能会跳转到搜索页面
                        Toast.makeText(context, "搜索功能尚未实现", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "搜索"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (materials.isEmpty()) {
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
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "暂无学习资料",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            // 资料列表
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(materials) { material ->
                    MaterialCard(
                        material = material,
                        onClick = {
                            selectedMaterial = material
                            showMaterialDetailDialog = true
                        },
                        onDownload = {
                            coroutineScope.launch {
                                // 实际应用中这里会调用下载API
                                Toast.makeText(context, "开始下载: ${material.title}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }
    
    // 资料详情对话框
    if (showMaterialDetailDialog && selectedMaterial != null) {
        MaterialDetailDialog(
            material = selectedMaterial!!,
            onDismiss = {
                showMaterialDetailDialog = false
                selectedMaterial = null
            },
            onDownload = {
                coroutineScope.launch {
                    // 实际应用中这里会调用下载API
                    Toast.makeText(context, "开始下载: ${selectedMaterial!!.title}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}

@Composable
private fun MaterialCard(
    material: LearningMaterial,
    onClick: () -> Unit,
    onDownload: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
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
            // 文件类型图标和标题
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 文件类型图标
                Icon(
                    imageVector = getFileTypeIcon(material.fileType),
                    contentDescription = null,
                    tint = getFileTypeColor(material.fileType),
                    modifier = Modifier.size(28.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // 标题和上传信息
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = material.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "上传者: ${material.uploadedBy} · ${dateFormat.format(material.uploadDate)}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                
                // 下载按钮
                IconButton(onClick = onDownload) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "下载",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 描述
            Text(
                text = material.description,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Color.DarkGray
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 文件信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${material.fileType.displayName} · ${material.fileSize}",
                    fontSize = 12.sp,
                    color = Color.Gray
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
private fun MaterialDetailDialog(
    material: LearningMaterial,
    onDismiss: () -> Unit,
    onDownload: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = material.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = material.courseName,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 文件信息部分
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = getFileTypeIcon(material.fileType),
                        contentDescription = null,
                        tint = getFileTypeColor(material.fileType),
                        modifier = Modifier.size(36.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = "${material.fileType.displayName} 文件",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                        
                        Text(
                            text = "大小: ${material.fileSize}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 描述
                Text(
                    text = "描述:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = material.description,
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 上传信息
                Text(
                    text = "上传信息:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "上传者: ${material.uploadedBy}",
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "上传时间: ${dateFormat.format(material.uploadDate)}",
                    fontSize = 14.sp
                )
            }
        },
        confirmButton = {
            Button(onClick = onDownload) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text("下载")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("关闭")
            }
        }
    )
}

// 获取文件类型对应的图标
@Composable
private fun getFileTypeIcon(fileType: FileType) = when (fileType) {
    FileType.PDF -> Icons.Default.PictureAsPdf
    FileType.WORD -> Icons.Default.Description
    FileType.PPT -> Icons.Default.Slideshow
    FileType.EXCEL -> Icons.Default.TableChart
    FileType.VIDEO -> Icons.Default.Videocam
    FileType.IMAGE -> Icons.Default.Image
    FileType.AUDIO -> Icons.Default.AudioFile
    FileType.ZIP -> Icons.Default.Folder
    FileType.OTHER -> Icons.Default.InsertDriveFile
}

// 获取文件类型对应的颜色
private fun getFileTypeColor(fileType: FileType) = when (fileType) {
    FileType.PDF -> Color(0xFFF44336) // 红色
    FileType.WORD -> Color(0xFF2196F3) // 蓝色
    FileType.PPT -> Color(0xFFFF9800) // 橙色
    FileType.EXCEL -> Color(0xFF4CAF50) // 绿色
    FileType.VIDEO -> Color(0xFFE91E63) // 粉色
    FileType.IMAGE -> Color(0xFF9C27B0) // 紫色
    FileType.AUDIO -> Color(0xFF00BCD4) // 青色
    FileType.ZIP -> Color(0xFF795548) // 棕色
    FileType.OTHER -> Color(0xFF607D8B) // 蓝灰色
}

// 文件类型枚举
enum class FileType(val displayName: String) {
    PDF("PDF"),
    WORD("Word"),
    PPT("PowerPoint"),
    EXCEL("Excel"),
    VIDEO("视频"),
    IMAGE("图片"),
    AUDIO("音频"),
    ZIP("压缩包"),
    OTHER("其他")
}

// 学习资料数据类
data class LearningMaterial(
    val id: String,
    val title: String,
    val description: String,
    val fileType: FileType,
    val fileSize: String,
    val uploadedBy: String,
    val uploadDate: Date,
    val downloadUrl: String,
    val courseId: String,
    val courseName: String
) 