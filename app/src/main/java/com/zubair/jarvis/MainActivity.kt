package com.zubair.jarvis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JarvisTheme {
                JarvisHome()
            }
        }
    }
}

@Composable
private fun JarvisTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF7DD3FC),
            background = Color(0xFF080D18),
            surface = Color(0xFF111827)
        ),
        content = content
    )
}

@Composable
private fun JarvisHome() {
    var prompt by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "JARVIS",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 4.sp
                        )
                        Text(
                            text = "Personal AI",
                            color = Color.White,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Surface(
                        color = Color(0xFF123047),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = "LOCAL-FIRST",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            color = Color(0xFF7DD3FC),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(Modifier.height(48.dp))

                Text(
                    text = "Your assistant.\nYour device.\nYour control.",
                    color = Color.White,
                    fontSize = 31.sp,
                    lineHeight = 39.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(14.dp))
                Text(
                    text = "A private AI workspace designed around on-device intelligence, useful tools and user-controlled memory.",
                    color = Color(0xFF9CA3AF),
                    fontSize = 15.sp,
                    lineHeight = 23.sp
                )

                Spacer(Modifier.height(32.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(Modifier.padding(18.dp)) {
                        Text(
                            text = "SYSTEM STATUS",
                            color = Color(0xFF9CA3AF),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(Modifier.height(12.dp))
                        StatusLine("Conversation interface", "Ready")
                        StatusLine("Local model runtime", "Not connected")
                        StatusLine("Android tools", "Permission-gated")
                        StatusLine("Long-term memory", "Setup pending")
                    }
                }
            }

            Column {
                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ask Jarvis anything…") },
                    shape = RoundedCornerShape(18.dp),
                    singleLine = false,
                    maxLines = 4
                )
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = { /* Chat orchestration is added in the next implementation phase. */ },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                ) {
                    Text("Start a conversation")
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Foundation build · Inference is not connected yet",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color(0xFF6B7280),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun StatusLine(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color(0xFFD1D5DB), fontSize = 13.sp)
        Text(value, color = Color(0xFF9CA3AF), fontSize = 12.sp)
    }
}
