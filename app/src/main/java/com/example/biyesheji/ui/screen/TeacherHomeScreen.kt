package com.example.biyesheji.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biyesheji.ui.navigation.Screen
import com.example.biyesheji.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherHomeScreen(
    onLogout: () -> Unit,
    onCourseManagementClick: () -> Unit,
    onStudentManagementClick: () -> Unit,
    onHomeworkGradingClick: () -> Unit,
    onNotificationClick: () -> Unit,
    teacherName: String = "李教授" // 默认名称，实际应从登录信息获取
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("教师端") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "退出登录"
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "欢迎，$teacherName",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            FunctionCard(
                title = "我的课程",
                description = "查看和管理您的课程",
                icon = Icons.Default.Book,
                onClick = onCourseManagementClick,
                gradientColors = listOf(SkyBlue, BabyBlue)
            )

            Spacer(modifier = Modifier.height(16.dp))

            FunctionCard(
                title = "学生管理",
                description = "管理课程学生和查看学生表现",
                icon = Icons.Default.People,
                onClick = onStudentManagementClick,
                gradientColors = listOf(MintGreen, LightSage)
            )

            Spacer(modifier = Modifier.height(16.dp))

            FunctionCard(
                title = "作业批改",
                description = "查看和评分学生作业提交",
                icon = Icons.Default.Assignment,
                onClick = onHomeworkGradingClick,
                gradientColors = listOf(PastelYellow, SunnyYellow)
            )

            Spacer(modifier = Modifier.height(16.dp))

            FunctionCard(
                title = "通知发布",
                description = "向学生发布课程通知",
                icon = Icons.Default.Notifications,
                onClick = onNotificationClick,
                gradientColors = listOf(FreshLavender, LightLilac)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FunctionCard(
    title: String,
    description: String = "",
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    gradientColors: List<Color> = listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surfaceVariant)
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = gradientColors
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier.size(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.3f)
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            modifier = Modifier.size(30.dp),
                            tint = Color.White
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (description.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = description,
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
                
                Card(
                    modifier = Modifier.size(32.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.2f)
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "前往",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
} 