package com.example.ui.auth

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MaieutikaHeroHeader
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.SlateGray
import com.example.ui.theme.SoftWhite
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    onAuthSuccess: (userName: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var nameInput by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    fun submit() {
        val trimmed = nameInput.trim()
        if (trimmed.isBlank()) {
            isError = true
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Silakan masukkan nama Anda untuk melanjutkan.")
            }
        } else {
            onAuthSuccess(trimmed)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = ObsidianBlack,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                MaieutikaHeroHeader()

                Spacer(modifier = Modifier.height(28.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                    border = BorderStroke(1.dp, ObsidianBorder),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Gerbang Kesadaran Diri",
                            style = MaterialTheme.typography.titleLarge,
                            color = GoldLight,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Mulai perjalanan kognitif Sokratik Anda. Masukkan nama atau panggilan Anda untuk personalisasi diagnosis dan peta realitas 8 dimensi.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SlateGray,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = {
                                nameInput = it
                                if (isError && it.isNotBlank()) {
                                    isError = false
                                }
                            },
                            label = { Text("Nama atau Panggilan Anda") },
                            placeholder = { Text("Misal: Rian, Maya, Socrates...", color = SlateGray.copy(alpha = 0.6f)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Ikon Profil",
                                    tint = if (isError) MaterialTheme.colorScheme.error else GoldAccent
                                )
                            },
                            singleLine = true,
                            isError = isError,
                            supportingText = {
                                if (isError) {
                                    Text("Nama tidak boleh kosong", color = MaterialTheme.colorScheme.error)
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { submit() }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldAccent,
                                unfocusedBorderColor = ObsidianBorder,
                                focusedLabelColor = GoldLight,
                                unfocusedLabelColor = SlateGray,
                                focusedTextColor = SoftWhite,
                                unfocusedTextColor = SoftWhite,
                                cursorColor = GoldAccent,
                                errorBorderColor = MaterialTheme.colorScheme.error
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("username_input")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { submit() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldAccent,
                                contentColor = ObsidianBlack
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("login_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Masuk & Mulai Eksplorasi",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Akses langsung tanpa verifikasi Gmail. Data Anda tersimpan aman.",
                            style = MaterialTheme.typography.labelSmall,
                            color = SlateGray.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "MAIEUTIKA • Filosofi Sokratik Kognitif AI",
                    style = MaterialTheme.typography.labelSmall,
                    color = SlateGray.copy(alpha = 0.6f)
                )
            }
        }
    }
}
