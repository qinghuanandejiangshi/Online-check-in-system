package com.example.biyesheji.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object TeacherHome : Screen("teacher_home")
    object StudentHome : Screen("student_home")
    
    // 教师端页面
    object TeacherCourseList : Screen("teacher_course_list")
    object TeacherCourseDetail : Screen("teacher_course_detail/{courseId}") {
        fun createRoute(courseId: String) = "teacher_course_detail/$courseId"
    }
    object TeacherStudentManagement : Screen("teacher_student_management")
    object TeacherHomeworkGrading : Screen("teacher_homework_grading")
    object TeacherAssignmentGrading : Screen("teacher_assignment_grading/{assignmentId}/{courseId}") {
        fun createRoute(assignmentId: String, courseId: String) = "teacher_assignment_grading/$assignmentId/$courseId"
    }
    object TeacherNotification : Screen("teacher_notification")
    
    // 教师端签到相关页面
    object TeacherAttendanceManagement : Screen("teacher_attendance_management/{courseId}") {
        fun createRoute(courseId: String) = "teacher_attendance_management/$courseId"
    }
    object TeacherAttendanceDetail : Screen("teacher_attendance_detail/{attendanceId}") {
        fun createRoute(attendanceId: String) = "teacher_attendance_detail/$attendanceId"
    }
    object TeacherStartAttendance : Screen("teacher_start_attendance/{courseId}") {
        fun createRoute(courseId: String) = "teacher_start_attendance/$courseId"
    }
    
    // 学生端页面
    object StudentCourseList : Screen("student_course_list")
    object StudentCourseDetail : Screen("student_course_detail/{courseId}") {
        fun createRoute(courseId: String) = "student_course_detail/$courseId"
    }
    object StudentCourseSelection : Screen("student_course_selection")
    object StudentForum : Screen("student_forum")
    object StudentHomework : Screen("student_homework")
    object StudentOnlineQA : Screen("student_online_qa")
    object StudentLearningMaterials : Screen("student_learning_materials")
    object StudentGrade : Screen("student_grade")
    object StudentQrCodeScanner : Screen("student_qr_code_scanner/{courseId}/{studentId}") {
        fun createRoute(courseId: String, studentId: String) = "student_qr_code_scanner/$courseId/$studentId"
    }
    
    object StudentAssignment : Screen("student_assignment/{assignmentId}") {
        fun createRoute(assignmentId: String): String {
            return "student_assignment/$assignmentId"
        }
    }
} 