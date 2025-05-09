package com.example.biyesheji.ui.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.biyesheji.model.UserType
import com.example.biyesheji.ui.navigation.Screen
import com.example.biyesheji.ui.screen.LoginScreen
import com.example.biyesheji.ui.screen.RegisterScreen
import com.example.biyesheji.ui.screen.StudentHomeScreen
import com.example.biyesheji.ui.screen.TeacherHomeScreen
import com.example.biyesheji.ui.screen.student.StudentCourseDetailScreen
import com.example.biyesheji.ui.screen.student.StudentCourseListScreen
import com.example.biyesheji.ui.screen.student.StudentCourseSelectionScreen
import com.example.biyesheji.ui.screen.student.StudentQrCodeScannerScreen
import com.example.biyesheji.ui.screen.teacher.TeacherCourseDetailScreen
import com.example.biyesheji.ui.screen.teacher.TeacherCourseListScreen
import com.example.biyesheji.ui.screen.teacher.TeacherStudentManagementScreen
import com.example.biyesheji.ui.screen.teacher.TeacherHomeworkGradingScreen
import com.example.biyesheji.ui.screen.teacher.TeacherNotificationScreen
import com.example.biyesheji.ui.screen.teacher.TeacherAttendanceManagementScreen
import com.example.biyesheji.ui.screen.teacher.TeacherAttendanceDetailScreen
import com.example.biyesheji.ui.screen.teacher.TeacherStartAttendanceScreen
import com.example.biyesheji.ui.screen.student.StudentForumScreen
import com.example.biyesheji.ui.screen.student.StudentHomeworkScreen
import com.example.biyesheji.ui.screen.student.StudentOnlineQAScreen
import com.example.biyesheji.ui.screen.student.StudentLearningMaterialsScreen
import com.example.biyesheji.ui.screen.student.StudentGradeScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { userType ->
                    val route = if (userType == UserType.TEACHER) {
                        Screen.TeacherHome.route
                    } else {
                        Screen.StudentHome.route
                    }
                    navController.navigate(route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.TeacherHome.route) {
            TeacherHomeScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                onCourseManagementClick = {
                    navController.navigate(Screen.TeacherCourseList.route)
                },
                onStudentManagementClick = {
                    navController.navigate(Screen.TeacherStudentManagement.route)
                },
                onHomeworkGradingClick = {
                    navController.navigate(Screen.TeacherHomeworkGrading.route)
                },
                onNotificationClick = {
                    navController.navigate(Screen.TeacherNotification.route)
                }
            )
        }

        composable(Screen.StudentHome.route) {
            StudentHomeScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                onMyCourseClick = {
                    navController.navigate(Screen.StudentCourseList.route)
                },
                onForumClick = {
                    navController.navigate(Screen.StudentForum.route)
                },
                onHomeworkClick = {
                    navController.navigate(Screen.StudentHomework.route)
                },
                onOnlineQAClick = {
                    navController.navigate(Screen.StudentOnlineQA.route)
                },
                onLearningMaterialsClick = {
                    navController.navigate(Screen.StudentLearningMaterials.route)
                },
                onGradeClick = {
                    navController.navigate(Screen.StudentGrade.route)
                }
            )
        }
        
        // 教师端页面
        composable(Screen.TeacherCourseList.route) {
            TeacherCourseListScreen(
                teacherId = "teacher_1", // 固定使用默认教师ID
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToCourseDetail = { courseId ->
                    navController.navigate(Screen.TeacherCourseDetail.createRoute(courseId))
                }
            )
        }
        
        composable(
            Screen.TeacherCourseDetail.route,
            arguments = listOf(navArgument("courseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
            TeacherCourseDetailScreen(
                courseId = courseId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAssignment = { },
                onNavigateToAddAssignment = { },
                onNavigateToAddMaterial = { },
                onNavigateToStudentList = { },
                onStartAttendanceClick = { courseId -> 
                    navController.navigate(Screen.TeacherStartAttendance.createRoute(courseId)) 
                },
                onAttendanceHistoryClick = { courseId -> 
                    navController.navigate(Screen.TeacherAttendanceManagement.createRoute(courseId)) 
                },
                onAttendanceDetailClick = { attendanceId ->
                    navController.navigate(Screen.TeacherAttendanceDetail.createRoute(attendanceId))
                }
            )
        }
        
        composable(Screen.TeacherStudentManagement.route) {
            TeacherStudentManagementScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.TeacherHomeworkGrading.route) {
            TeacherHomeworkGradingScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            Screen.TeacherAssignmentGrading.route,
            arguments = listOf(
                navArgument("assignmentId") { type = NavType.StringType },
                navArgument("courseId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val assignmentId = backStackEntry.arguments?.getString("assignmentId") ?: ""
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
            // 暂时注释掉这个界面，等实现后再启用
            // TeacherAssignmentGradingScreen(
            //     assignmentId = assignmentId,
            //     courseId = courseId,
            //     onNavigateBack = { navController.popBackStack() }
            // )
        }
        
        composable(Screen.TeacherNotification.route) {
            TeacherNotificationScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // 学生端页面
        composable(Screen.StudentCourseList.route) {
            StudentCourseListScreen(
                studentId = "STUDENT",
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCourseDetail = { courseId ->
                    navController.navigate(Screen.StudentCourseDetail.createRoute(courseId))
                },
                onNavigateToCourseSelection = {
                    navController.navigate(Screen.StudentCourseSelection.route)
                }
            )
        }
        
        composable(
            route = Screen.StudentCourseDetail.route,
            arguments = listOf(
                navArgument("courseId") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
            StudentCourseDetailScreen(
                courseId = courseId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAssignment = { assignmentId ->
                    navController.navigate("student_assignment/$assignmentId")
                },
                onNavigateToQrCodeScanner = { studentId ->
                    navController.navigate("student_qrcode_scanner/$courseId/$studentId")
                }
            )
        }
        
        composable(Screen.StudentCourseSelection.route) {
            StudentCourseSelectionScreen(
                studentId = "STUDENT",
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 添加学生论坛页面路由
        composable(Screen.StudentForum.route) {
            StudentForumScreen(navController = navController)
        }
        
        // 添加学生作业提交页面路由
        composable(Screen.StudentHomework.route) {
            StudentHomeworkScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // 添加学生在线答疑页面路由
        composable(Screen.StudentOnlineQA.route) {
            StudentOnlineQAScreen(
                courseId = "all", // 默认显示所有课程的问答
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // 添加学生学习资料页面路由
        composable(Screen.StudentLearningMaterials.route) {
            StudentLearningMaterialsScreen(
                courseId = "all", // 默认显示所有课程的学习资料
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // 添加学生成绩查询页面路由
        composable(Screen.StudentGrade.route) {
            StudentGradeScreen(navController = navController)
        }

        // 添加新的签到相关路由
        composable(
            Screen.TeacherAttendanceManagement.route,
            arguments = listOf(navArgument("courseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
            TeacherAttendanceManagementScreen(
                courseId = courseId,
                onNavigateBack = { navController.popBackStack() },
                onStartAttendance = { courseId ->
                    navController.navigate(Screen.TeacherStartAttendance.createRoute(courseId))
                },
                onAttendanceDetail = { attendanceId ->
                    navController.navigate(Screen.TeacherAttendanceDetail.createRoute(attendanceId))
                }
            )
        }
        
        composable(
            Screen.TeacherAttendanceDetail.route,
            arguments = listOf(navArgument("attendanceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val attendanceId = backStackEntry.arguments?.getString("attendanceId") ?: ""
            TeacherAttendanceDetailScreen(
                attendanceId = attendanceId,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.TeacherStartAttendance.route + "/{courseId}",
            arguments = listOf(
                navArgument("courseId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
            val context = LocalContext.current
            TeacherStartAttendanceScreen(
                courseId = courseId,
                onBack = { navController.popBackStack() },
                onAttendanceCreated = { attendanceId ->
                    navController.popBackStack()
                    Toast.makeText(context, "考勤创建成功", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // 添加学生扫码签到路由
        composable(
            route = Screen.StudentQrCodeScanner.route,
            arguments = listOf(
                navArgument("courseId") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("studentId") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
            val studentId = backStackEntry.arguments?.getString("studentId") ?: ""
            val context = LocalContext.current
            
            if (courseId.isEmpty() || studentId.isEmpty()) {
                LaunchedEffect(Unit) {
                    Toast.makeText(context, "参数错误，无法进行签到", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
                return@composable
            }
            
            StudentQrCodeScannerScreen(
                courseId = courseId,
                studentId = studentId,
                onNavigateBack = { navController.popBackStack() },
                onCheckInSuccess = {
                    Toast.makeText(context, "签到成功", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
fun StudentCourseDetailRoute(navController: NavHostController) {
    val backStackEntry = navController.currentBackStackEntry
    val arguments = backStackEntry?.arguments
    val courseId = arguments?.getString("courseId") ?: ""
    
    StudentCourseDetailScreen(
        courseId = courseId,
        onNavigateBack = { navController.popBackStack() },
        onNavigateToAssignment = { assignmentId ->
            navController.navigate("student_assignment/$assignmentId")
        },
        onNavigateToQrCodeScanner = { studentId ->
            navController.navigate("student_qrcode_scanner/$courseId/$studentId")
        }
    )
} 