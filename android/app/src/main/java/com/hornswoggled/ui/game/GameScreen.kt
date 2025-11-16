package com.hornswoggled.ui.game

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameScreen(
    gameId: String,
    onNavigateBack: () -> Unit
) {
    var roundWord by remember { mutableStateOf("HAPPINESS") }
    var currentSubmission by remember { mutableStateOf("") }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Round info
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Define this word:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        roundWord,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Timer
            LinearProgressIndicator(
                progress = 0.7f,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Submission input
            OutlinedTextField(
                value = currentSubmission,
                onValueChange = { currentSubmission = it },
                label = { Text("Your definition") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            Text(
                "${currentSubmission.length}/200",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    // Submit answer
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = currentSubmission.isNotBlank()
            ) {
                Text("Submit", fontSize = 18.sp)
            }
        }
    }
}
