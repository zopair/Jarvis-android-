package com.zubair.jarvis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Midnight = Color(0xFF070B14)
private val Panel = Color(0xFF111A2B)
private val PanelRaised = Color(0xFF172338)
private val Ice = Color(0xFF83D9F7)
private val Muted = Color(0xFF91A1B8)
private val Border = Color(0xFF26364D)

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
            primary = Ice,
            background = Midnight,
            surface = Panel,
            onBackground = Color.White,
            onSurface = Color.White
        ),
        content = content
    )
}

@Composable
private fun JarvisHome() {
    Surface(modifier = Modifier.fillMaxSize(), color = Midnight) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF101D31), Midnight, Color(0xFF080D17))
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 24.dp)
        ) {
            BrandHeader()
            Spacer(Modifier.height(42.dp))
            HeroSection()
            Spacer(Modifier.height(30.dp))
            SystemOverview()
            Spacer(Modifier.height(24.dp))
            CapabilitySection()
            Spacer(Modifier.height(26.dp))
            RuntimeNotice()
            Spacer(Modifier.height(26.dp))
            Footer()
        }
    }
}

@Composable
private fun BrandHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(15.dp),
                color = Color(0xFF16364B),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF32627A))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("J", color = Ice, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                }
            }
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    "JARVIS",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp
                )
                Text("ANDROID INTELLIGENCE", color = Muted, fontSize = 9.sp, letterSpacing = 1.5.sp)
            }
        }
        Surface(
            shape = CircleShape,
            color = Color(0xFF102D2B),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF24554F))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(Color(0xFF62D6A6), CircleShape)
                )
                Text(
                    "FOUNDATION",
                    modifier = Modifier.padding(start = 7.dp),
                    color = Color(0xFF9AE6C8),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
private fun HeroSection() {
    Text(
        "A MORE PERSONAL\nKIND OF INTELLIGENCE.",
        color = Color.White,
        fontSize = 30.sp,
        lineHeight = 37.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.6).sp
    )
    Spacer(Modifier.height(15.dp))
    Text(
        "Built for your device. Designed around your privacy. Ready to grow with the way you work.",
        color = Muted,
        fontSize = 14.sp,
        lineHeight = 22.sp
    )
    Spacer(Modifier.height(22.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ValueTag("LOCAL-FIRST")
        ValueTag("USER-CONTROLLED")
        ValueTag("MODULAR")
    }
}

@Composable
private fun ValueTag(label: String) {
    Surface(
        shape = RoundedCornerShape(7.dp),
        color = Color(0xFF14243A),
        border = androidx.compose.foundation.BorderStroke(1.dp, Border)
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 7.dp),
            color = Ice,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.6.sp
        )
    }
}

@Composable
private fun SystemOverview() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Panel.copy(alpha = 0.94f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Border)
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "SYSTEM OVERVIEW",
                    color = Muted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.7.sp
                )
                Text("v0.1.0", color = Color(0xFF71839B), fontSize = 10.sp)
            }
            Spacer(Modifier.height(16.dp))
            SystemLine("Application shell", "READY", Color(0xFF75D9B0))
            SystemLine("Inference engine", "NOT CONNECTED", Color(0xFFF0C477))
            SystemLine("Persistent memory", "IN DEVELOPMENT", Color(0xFFF0C477))
            SystemLine("Protected tool access", "POLICY BASELINE", Ice)
        }
    }
}

@Composable
private fun SystemLine(label: String, status: String, statusColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color(0xFFD5DFEC), fontSize = 12.sp)
        Text(
            status,
            color = statusColor,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun CapabilitySection() {
    Text(
        "THE FOUNDATION",
        color = Muted,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.8.sp
    )
    Spacer(Modifier.height(12.dp))
    CapabilityCard(
        number = "01",
        title = "Intelligence",
        detail = "A device-aware model catalog and provider-neutral routing."
    )
    CapabilityCard(
        number = "02",
        title = "Engine",
        detail = "A replaceable inference layer for local models and optional cloud providers."
    )
    CapabilityCard(
        number = "03",
        title = "Agents & Tools",
        detail = "Bounded orchestration with permission checks and explicit approval."
    )
    CapabilityCard(
        number = "04",
        title = "Memory & Learning",
        detail = "Private, inspectable memory and measurable execution traces."
    )
}

@Composable
private fun CapabilityCard(number: String, title: String, detail: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 9.dp),
        shape = RoundedCornerShape(15.dp),
        color = PanelRaised.copy(alpha = 0.78f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Border.copy(alpha = 0.8f))
    ) {
        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                number,
                color = Ice,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Column(modifier = Modifier.padding(start = 14.dp)) {
                Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(detail, color = Muted, fontSize = 11.sp, lineHeight = 17.sp)
            }
        }
    }
}

@Composable
private fun RuntimeNotice() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(17.dp),
        color = Color(0xFF182338),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF34445B))
    ) {
        Column(Modifier.padding(17.dp)) {
            Text(
                "THE NEXT MILESTONE",
                color = Ice,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Connect the first verified inference runtime.",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(5.dp))
            Text(
                "The chat interface remains intentionally inactive until a model provider is integrated and tested.",
                color = Muted,
                fontSize = 11.sp,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
private fun Footer() {
    Text(
        "PRIVATE BY DESIGN  •  BUILT FOR ANDROID",
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF65758B),
        fontSize = 8.sp,
        letterSpacing = 1.4.sp
    )
}
