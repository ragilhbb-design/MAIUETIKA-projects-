package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ChatMessage
import com.example.ui.ChatViewModel
import com.example.ui.MaieutikaViewModel
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldLight
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.SlateGray
import com.example.ui.theme.SoftWhite

@Composable
fun ChatScreen(
    viewModel: MaieutikaViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onBack()
    }

    val uiState by viewModel.uiState.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.messages.size, uiState.isTyping) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .imePadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("chat_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = GoldAccent
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "DIALOG SOKRATIK",
                    style = MaterialTheme.typography.titleMedium,
                    color = GoldLight,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "MAIEUTIKA • Co-Partner Kognitif",
                    style = MaterialTheme.typography.labelSmall,
                    color = SlateGray,
                    fontSize = 10.sp
                )
            }

            IconButton(
                onClick = { viewModel.clearChat() },
                modifier = Modifier.testTag("clear_chat_button")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Reset Percakapan",
                    tint = SlateGray
                )
            }
        }

        // Subtitle banner
        Surface(
            color = Color(0xFF13131D),
            border = BorderStroke(1.dp, ObsidianBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "\"Saya tidak akan memberi Anda jawaban. Saya akan membantu Anda melihat.\"",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontStyle = FontStyle.Italic,
                        color = GoldLight
                    )
                )
            }
        }

        // Message List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.messages, key = { it.id }) { message ->
                ChatBubble(message = message)
            }

            if (uiState.isTyping) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(GoldContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                            border = BorderStroke(1.dp, ObsidianBorder),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    color = GoldAccent,
                                    strokeWidth = 1.5.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Menimbang realitas & merumuskan pertanyaan penyelidik...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SlateGray
                                )
                            }
                        }
                    }
                }
            }
        }

        // Suggested Socratic Probing Question Chips
        if (uiState.suggestedProbingQuestions.isNotEmpty() && !uiState.isTyping) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Pertanyaan Penyelidik (Probing Inquiry):",
                    style = MaterialTheme.typography.labelSmall,
                    color = SlateGray,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 4.dp)
                ) {
                    items(uiState.suggestedProbingQuestions) { question ->
                        Surface(
                            color = Color(0xFF1B1B28),
                            border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .clickable {
                                    viewModel.selectSuggestedQuestion(question)
                                }
                                .testTag("probing_question_chip")
                        ) {
                            Text(
                                text = question,
                                style = MaterialTheme.typography.bodySmall,
                                color = GoldLight,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // Input Area
        Surface(
            color = ObsidianCard,
            border = BorderStroke(1.dp, ObsidianBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            "Ketikkan pemikiran atau dilema Anda...",
                            color = SlateGray,
                            fontSize = 13.sp
                        )
                    },
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = ObsidianBorder,
                        focusedTextColor = SoftWhite,
                        unfocusedTextColor = SoftWhite
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field")
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        val toSend = inputText
                        inputText = ""
                        viewModel.sendMessage(toSend)
                    },
                    enabled = inputText.isNotBlank() && !uiState.isTyping,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (inputText.isNotBlank() && !uiState.isTyping) GoldAccent else ObsidianBorder)
                        .testTag("send_chat_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Kirim",
                        tint = if (inputText.isNotBlank() && !uiState.isTyping) ObsidianBlack else SlateGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// Backward-compatible overload for ChatViewModel
@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    ChatScreen(
        viewModel = chatViewModel,
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val isUser = message.isUser

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(GoldContainer)
                    .border(1.dp, GoldAccent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) Color(0xFF282414) else ObsidianCard
            ),
            border = BorderStroke(
                1.dp,
                if (isUser) GoldAccent else ObsidianBorder
            ),
            shape = RoundedCornerShape(
                topStart = 14.dp,
                topEnd = 14.dp,
                bottomStart = if (isUser) 14.dp else 2.dp,
                bottomEnd = if (isUser) 2.dp else 14.dp
            ),
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = if (isUser) "Anda" else "MAIEUTIKA",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isUser) GoldLight else GoldAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftWhite,
                    lineHeight = 22.sp
                )
            }
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2C2C3E)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = SoftWhite,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
