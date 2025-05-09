package com.example.biyesheji.ui.screen.student

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.text.SimpleDateFormat
import java.util.*
import androidx.navigation.NavController

// 问题状态枚举
enum class QuestionStatus {
    PENDING,     // 待回答
    ANSWERED,    // 已回答
    CLOSED       // 已关闭
}

// 问题数据类
data class Question(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val courseId: String,
    val courseName: String,
    val studentId: String,
    val studentName: String,
    val createdAt: Date = Date(),
    val status: QuestionStatus = QuestionStatus.PENDING,
    val answers: List<Answer> = emptyList()
)

// 回答数据类
data class Answer(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val teacherId: String,
    val teacherName: String,
    val createdAt: Date = Date()
)

// 论坛帖子数据类
data class ForumPost(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val authorId: String,
    val authorName: String,
    val createTime: Date = Date(),
    val likes: Int = 0,
    val comments: List<ForumComment> = emptyList()
)

// 论坛评论数据类
data class ForumComment(
    val id: String = UUID.randomUUID().toString(),
    val postId: String,
    val content: String,
    val authorId: String,
    val authorName: String,
    val createTime: Date = Date()
)

// 问题仓库对象，模拟数据和操作
object QuestionRepository {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    
    private val questions = mutableStateListOf(
        Question(
            title = "关于线性代数特征值的计算问题",
            content = "在求解特征值时，我遇到了一个3x3矩阵，其特征多项式计算很复杂，是否有简化的方法？",
            courseId = "MATH201",
            courseName = "高等数学",
            studentId = "2020001",
            studentName = "张三",
            createdAt = dateFormat.parse("2023-06-10 14:30")!!,
            status = QuestionStatus.ANSWERED,
            answers = listOf(
                Answer(
                    content = "对于3x3矩阵，你可以先计算行列式，然后用特征多项式的系数公式。也可以使用QR分解简化计算。建议查看课本第五章的例题。",
                    teacherId = "T001",
                    teacherName = "李教授",
                    createdAt = dateFormat.parse("2023-06-11 09:15")!!
                )
            )
        ),
        Question(
            title = "Java中的多线程同步问题",
            content = "在实现生产者-消费者模式时，除了使用synchronized关键字，还有什么更高效的方法？",
            courseId = "CS302",
            courseName = "Java程序设计",
            studentId = "2020001",
            studentName = "张三",
            createdAt = dateFormat.parse("2023-06-15 16:45")!!,
            status = QuestionStatus.PENDING
        ),
        Question(
            title = "数据库索引优化问题",
            content = "在设计有大量查询的数据库表时，应该如何选择合适的索引类型和组合？",
            courseId = "CS401",
            courseName = "数据库原理",
            studentId = "2020001",
            studentName = "张三",
            createdAt = dateFormat.parse("2023-06-18 10:20")!!,
            status = QuestionStatus.CLOSED,
            answers = listOf(
                Answer(
                    content = "索引选择要考虑查询频率、字段选择性和表大小。对于你的问题，建议使用B+树索引，并对常用查询条件创建组合索引。",
                    teacherId = "T005",
                    teacherName = "王教授",
                    createdAt = dateFormat.parse("2023-06-19 11:30")!!
                )
            )
        )
    )

    // 获取所有问题
    fun getAllQuestions(): List<Question> = questions

    // 根据状态获取问题
    fun getQuestionsByStatus(status: QuestionStatus): List<Question> = 
        questions.filter { it.status == status }
        
    // 提交新问题
    fun addQuestion(question: Question) {
        questions.add(0, question)
    }
    
    // 获取各状态问题的数量
    fun getStatusCount(): Map<QuestionStatus, Int> {
        return QuestionStatus.values().associateWith { status ->
            questions.count { it.status == status }
        }
    }
}

// 模拟论坛数据
object ForumRepository {
    private val _posts = mutableStateListOf(
        ForumPost(
            title = "关于Java多线程的问题",
            content = "我在学习Java多线程时遇到了一些问题，特别是关于线程安全的部分，有人能帮忙解释一下吗？",
            authorId = "S001",
            authorName = "李明",
            likes = 5,
            comments = listOf(
                ForumComment(
                    postId = "1",
                    content = "线程安全主要关注的是共享资源的访问控制，你可以使用synchronized关键字或者Lock接口来实现。",
                    authorId = "S002",
                    authorName = "张华"
                ),
                ForumComment(
                    postId = "1",
                    content = "推荐你看一下《Java并发编程实战》这本书，很详细地讲解了这部分内容。",
                    authorId = "T001",
                    authorName = "王老师"
                )
            )
        ),
        ForumPost(
            title = "Android Studio安装问题",
            content = "我在安装Android Studio时遇到了内存不足的提示，有没有解决方案？",
            authorId = "S003",
            authorName = "李小花",
            likes = 3,
            comments = listOf(
                ForumComment(
                    postId = "2",
                    content = "可以尝试增加JVM的堆内存大小，在studio64.exe.vmoptions文件中修改-Xmx参数。",
                    authorId = "S004",
                    authorName = "赵强"
                )
            )
        ),
        ForumPost(
            title = "求推荐学习资源",
            content = "有没有好的Android开发学习资源推荐？特别是关于Jetpack Compose的。",
            authorId = "S005",
            authorName = "王小明",
            likes = 8,
            comments = listOf()
        )
    )
    
    val posts: List<ForumPost> get() = _posts
    
    fun addPost(post: ForumPost) {
        _posts.add(post)
    }
    
    fun addComment(postId: String, comment: ForumComment) {
        val index = _posts.indexOfFirst { it.id == postId }
        if (index != -1) {
            val post = _posts[index]
            val updatedComments = post.comments + comment
            _posts[index] = post.copy(comments = updatedComments)
        }
    }
    
    fun likePost(postId: String) {
        val index = _posts.indexOfFirst { it.id == postId }
        if (index != -1) {
            val post = _posts[index]
            _posts[index] = post.copy(likes = post.likes + 1)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentForumScreen(navController: NavController) {
    var showNewPostDialog by remember { mutableStateOf(false) }
    var selectedPost by remember { mutableStateOf<ForumPost?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("学习论坛") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showNewPostDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "发布新帖子")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(ForumRepository.posts) { post ->
                    ForumPostCard(
                        post = post,
                        onPostClick = { selectedPost = post },
                        onLikeClick = { ForumRepository.likePost(post.id) }
                    )
                }
            }
        }
    }
    
    // 新帖子对话框
    if (showNewPostDialog) {
        NewPostDialog(
            onDismiss = { showNewPostDialog = false },
            onPostCreated = { title, content ->
                val newPost = ForumPost(
                    title = title,
                    content = content,
                    authorId = "S001", // 假设当前用户ID
                    authorName = "李明" // 假设当前用户名
                )
                ForumRepository.addPost(newPost)
                showNewPostDialog = false
            }
        )
    }
    
    // 帖子详情对话框
    selectedPost?.let { post ->
        PostDetailDialog(
            post = post,
            onDismiss = { selectedPost = null },
            onCommentAdded = { content ->
                val comment = ForumComment(
                    postId = post.id,
                    content = content,
                    authorId = "S001", // 假设当前用户ID
                    authorName = "李明" // 假设当前用户名
                )
                ForumRepository.addComment(post.id, comment)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumPostCard(
    post: ForumPost,
    onPostClick: () -> Unit,
    onLikeClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onPostClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "作者: ${post.authorName}",
                    style = MaterialTheme.typography.bodySmall
                )
                Row {
                    IconButton(onClick = onLikeClick) {
                        Icon(Icons.Default.ThumbUp, contentDescription = "点赞")
                    }
                    Text(text = "${post.likes}")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.Comment, contentDescription = "评论")
                    Text(text = "${post.comments.size}")
                }
            }
        }
    }
}

@Composable
fun NewPostDialog(
    onDismiss: () -> Unit,
    onPostCreated: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("发布新帖子") },
        text = {
            Column {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("标题") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("内容") },
                    modifier = Modifier.fillMaxWidth().height(120.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onPostCreated(title, content) },
                enabled = title.isNotBlank() && content.isNotBlank()
            ) {
                Text("发布")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
fun PostDetailDialog(
    post: ForumPost,
    onDismiss: () -> Unit,
    onCommentAdded: (String) -> Unit
) {
    var commentText by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(post.title) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                Text(
                    text = post.content,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "作者: ${post.authorName}",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Text(
                    text = "评论 (${post.comments.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(post.comments) { comment ->
                        CommentItem(comment)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        label = { Text("添加评论") },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                onCommentAdded(commentText)
                                commentText = ""
                            }
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "发表评论")
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("关闭")
            }
        }
    )
}

@Composable
fun CommentItem(comment: ForumComment) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = comment.authorName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = comment.createTime.toString().substring(0, 16),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
} 