package com.example.biyesheji.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier

/**
 * 修复clickable函数，提供兼容性接口
 */
fun Modifier.clickableCompat(onClick: () -> Unit): Modifier = this.clickable(onClick = onClick) 