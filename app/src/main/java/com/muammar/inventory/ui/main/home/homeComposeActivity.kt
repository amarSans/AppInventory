package com.muammar.inventory.ui.main.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muammar.inventory.R
import com.muammar.inventory.ui.main.home.ui.theme.InventoryTheme
import java.time.LocalDateTime

class homeComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InventoryTheme {

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { padding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CardTime()

                    }
                }
            }
        }
    }

    @Composable
    @Preview(showBackground = true, showSystemUi = true)
    fun CardTime(modifier: Modifier = Modifier) {
        var current by remember { mutableStateOf(LocalDateTime.now()) }
        LaunchedEffect(Unit) {
            while (true) {
                current = java.time.LocalDateTime.now()
                kotlinx.coroutines.delay(60000)
            }
        }
        val dayName = current.format(java.time.format.DateTimeFormatter.ofPattern("EEEE"))
        val formattedDate =
            current.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy"))
        val formattedTime = current.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(6.dp)


        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,

                ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,

                ) {
                    Text(
                        text = formattedTime,
                        color = Color(0xFF2196F3),
                        fontSize = 30.sp,
                        fontFamily = FontFamily(Font(R.font.poppins_semibold, FontWeight.Bold)),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = dayName,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.poppins_semibold, FontWeight.Bold)),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = formattedDate,
                        color = Color(0xFF757575),
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.poppins_semibold)),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}