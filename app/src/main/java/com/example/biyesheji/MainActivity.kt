package com.example.biyesheji

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.biyesheji.data.DatabaseInitializer
import com.example.biyesheji.data.DatabaseManager
import com.example.biyesheji.ui.navigation.AppNavigation
import com.example.biyesheji.ui.theme.BiyeshejiTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 重置数据库初始化状态（仅在开发阶段使用）
        // DatabaseInitializer.resetInitializationStatus(this)
        
        // 初始化数据库
        CoroutineScope(Dispatchers.IO).launch {
            try {
                DatabaseManager.initializeDatabase(applicationContext)
                DatabaseInitializer.initializeDatabase(applicationContext)
                Log.d("MainActivity", "数据库初始化成功")
            } catch (e: Exception) {
                Log.e("MainActivity", "数据库初始化失败: ${e.message}", e)
            }
        }
        
        enableEdgeToEdge()
        setContent {
            BiyeshejiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 当应用启动时，确保数据库初始化已完成
                    LaunchedEffect(key1 = Unit) {
                        try {
                            DatabaseInitializer.initializeDatabase(applicationContext)
                            Log.d("MainActivity", "LaunchedEffect中确认数据库初始化")
                        } catch (e: Exception) {
                            Log.e("MainActivity", "LaunchedEffect中数据库初始化失败: ${e.message}", e)
                        }
                    }
                    
                    val navController = rememberNavController()
                    AppNavigation(navController = navController)
                }
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // 关闭数据库
        DatabaseManager.closeDatabase()
    }
}

@Composable
fun MainContent() {
    val navController = rememberNavController()
    AppNavigation(navController = navController)
}

object Destinations {
    const val LOGIN = "login"
    const val STUDENT_HOME = "student_home"
    const val STUDENT_FORUM = "student_forum"
    const val TEACHER_HOME = "teacher_home"
}