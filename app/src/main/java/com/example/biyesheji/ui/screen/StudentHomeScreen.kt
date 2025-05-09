package com.example.biyesheji.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biyesheji.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentHomeScreen(
    onLogout: () -> Unit,
    onMyCourseClick: () -> Unit,
    onForumClick: () -> Unit,
    onHomeworkClick: () -> Unit,
    onOnlineQAClick: () -> Unit,
    onLearningMaterialsClick: () -> Unit,
    onGradeClick: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("学生端") },
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
                text = "欢迎，学生用户",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            FunctionCard(
                title = "我的课程",
                icon = Icons.Default.MenuBook,
                onClick = onMyCourseClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            FunctionCard(
                title = "作业提交",
                icon = Icons.Default.Assignment,
                onClick = onHomeworkClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            FunctionCard(
                title = "在线答疑",
                icon = Icons.Default.QuestionAnswer,
                onClick = onOnlineQAClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            FunctionCard(
                title = "学习论坛",
                icon = Icons.Default.Forum,
                onClick = onForumClick
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            FunctionCard(
                title = "学习资料",
                icon = Icons.Default.LibraryBooks,
                onClick = onLearningMaterialsClick
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            FunctionCard(
                title = "成绩查询",
                icon = Icons.Default.Assessment,
                onClick = onGradeClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FunctionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "前往",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
} 