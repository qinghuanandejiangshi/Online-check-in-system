package com.example.biyesheji.ui.screen.student

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.biyesheji.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

// 成绩数据类
data class GradeItem(
    val courseId: String,
    val courseName: String,
    val score: Float,
    val credit: Float,
    val semester: String,
    val teacher: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentGradeScreen(navController: NavController) {
    val grades = remember {
        listOf(
            GradeItem(
                courseId = "CS101",
                courseName = "计算机基础",
                score = 85f,
                credit = 3f,
                semester = "2023-2024学年第一学期",
                teacher = "张教授"
            ),
            GradeItem(
                courseId = "MATH201",
                courseName = "高等数学",
                score = 92f,
                credit = 4f,
                semester = "2023-2024学年第一学期",
                teacher = "李教授"
            ),
            GradeItem(
                courseId = "ENG103",
                courseName = "大学英语",
                score = 78f,
                credit = 2f,
                semester = "2023-2024学年第一学期",
                teacher = "王教授"
            ),
            GradeItem(
                courseId = "PHY102",
                courseName = "大学物理",
                score = 88f,
                credit = 3.5f,
                semester = "2023-2024学年第一学期",
                teacher = "赵教授"
            )
        )
    }

    // 计算平均成绩和总学分
    val totalCredits = grades.sumOf { it.credit.toDouble() }
    val avgScore = grades.sumOf { it.score * it.credit.toDouble() } / totalCredits

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("我的成绩") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 统计卡片
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatisticsCard(
                    title = "平均成绩",
                    value = String.format("%.1f", avgScore),
                    icon = AppIcons.Score,
                    containerColor = Blue500,
                    modifier = Modifier.weight(1f)
                )
                
                StatisticsCard(
                    title = "总学分",
                    value = String.format("%.1f", totalCredits),
                    icon = AppIcons.School,
                    containerColor = Green500,
                    modifier = Modifier.weight(1f)
                )
            }
            
            // 成绩列表
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(grades) { grade ->
                    GradeCard(grade)
                }
            }
        }
    }
}

@Composable
fun StatisticsCard(
    title: String,
    value: String,
    icon: ImageVector,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(containerColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = containerColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = title,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun GradeCard(grade: GradeItem) {
    val gradeColor = when {
        grade.score >= 90 -> Green500
        grade.score >= 80 -> Blue500
        grade.score >= 70 -> Orange500
        else -> Red500
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = grade.courseName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(gradeColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = grade.score.toInt().toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = gradeColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "课程编号: ${grade.courseId}",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f)
                )
                
                Text(
                    text = "学分: ${grade.credit}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "授课教师: ${grade.teacher}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = grade.semester,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

// 成绩数据类
data class Grade(
    val courseId: String,
    val courseName: String,
    val credit: Double,
    val score: Double,
    val gradePoint: Double,
    val teacherName: String,
    val examDate: Date,
    val natureType: String,
    val evaluationMethod: String
) 