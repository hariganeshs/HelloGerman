package com.hellogerman.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hellogerman.app.data.GrammarMiniGame
import com.hellogerman.app.ui.theme.GrammarColor
import kotlin.math.roundToInt

@Composable
fun GrammarMiniGameScreen(
    miniGame: GrammarMiniGame,
    onGameComplete: (score: Int) -> Unit,
    onSkip: () -> Unit
) {
    var score by remember { mutableStateOf(0) }
    var isCompleted by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Mini-Game",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = GrammarColor
        )
        
        when (miniGame) {
            is GrammarMiniGame.DragDrop -> DragDropGame(
                buckets = miniGame.buckets,
                items = miniGame.items,
                onComplete = { gameScore ->
                    score = gameScore
                    isCompleted = true
                }
            )
            is GrammarMiniGame.Match -> MatchingGame(
                pairs = miniGame.pairs,
                onComplete = { gameScore ->
                    score = gameScore
                    isCompleted = true
                }
            )
            is GrammarMiniGame.FillBlank -> FillBlankGame(
                text = miniGame.text,
                answer = miniGame.answer,
                onComplete = { gameScore ->
                    score = gameScore
                    isCompleted = true
                }
            )
            is GrammarMiniGame.SentenceBuilder -> SentenceBuilderGame(
                words = miniGame.words,
                correctOrder = miniGame.correctOrder,
                onComplete = { gameScore ->
                    score = gameScore
                    isCompleted = true
                }
            )
        }
        
        if (isCompleted) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (score > 0) Color.Green.copy(alpha = 0.1f) else Color(0xFFFF9800).copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        if (score > 0) "🎉 Great job!" else "🤔 Try again!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Score: $score points")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { onGameComplete(score) }) {
                        Text("Continue")
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onSkip,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("Skip Mini-Game")
        }
    }
}

@Composable
fun DragDropGame(
    buckets: List<String>,
    items: List<Pair<String, String>>, // item to correct bucket
    onComplete: (score: Int) -> Unit
) {
    var draggedItem by remember { mutableStateOf<String?>(null) }
    var itemPositions by remember { mutableStateOf(items.map { it.first to Offset.Zero }.toMap()) }
    var bucketAssignments by remember { mutableStateOf(mapOf<String, String>()) } // item to bucket
    var gameCompleted by remember { mutableStateOf(false) }
    
    val unassignedItems = items.map { it.first }.filter { it !in bucketAssignments.keys }
    
    LaunchedEffect(bucketAssignments) {
        if (bucketAssignments.size == items.size && !gameCompleted) {
            gameCompleted = true
            val correctAssignments = bucketAssignments.count { (item, bucket) ->
                items.find { it.first == item }?.second == bucket
            }
            val score = (correctAssignments * 10)
            onComplete(score)
        }
    }
    
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Drag items to the correct categories:", fontWeight = FontWeight.Medium)
        
        // Buckets
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(buckets) { bucket ->
                DragDropBucket(
                    title = bucket,
                    assignedItems = bucketAssignments.filter { it.value == bucket }.keys.toList(),
                    onDrop = { item ->
                        bucketAssignments = bucketAssignments + (item to bucket)
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Draggable items
        Text("Items to drag:", fontWeight = FontWeight.Medium)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(unassignedItems) { item ->
                DraggableItem(
                    text = item,
                    onDragStart = { draggedItem = item },
                    onDragEnd = { draggedItem = null }
                )
            }
        }
    }
}

@Composable
fun DragDropBucket(
    title: String,
    assignedItems: List<String>,
    onDrop: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(150.dp),
        colors = CardDefaults.cardColors(containerColor = GrammarColor.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
            Divider(modifier = Modifier.padding(vertical = 4.dp))
            
            LazyColumn {
                items(assignedItems) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Blue.copy(alpha = 0.1f))
                    ) {
                        Text(
                            item,
                            modifier = Modifier.padding(4.dp),
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DraggableItem(
    text: String,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit
) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        isDragging = true
                        onDragStart()
                    },
                    onDragEnd = {
                        isDragging = false
                        offset = Offset.Zero
                        onDragEnd()
                    }
                ) { change, dragAmount ->
                    offset += dragAmount
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isDragging) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) 
                           else MaterialTheme.colorScheme.surface
        )
    ) {
        Text(
            text,
            modifier = Modifier.padding(12.dp),
            fontSize = 14.sp
        )
    }
}

@Composable
fun MatchingGame(
    pairs: List<Pair<String, String>>,
    onComplete: (score: Int) -> Unit
) {
    var selectedLeft by remember { mutableStateOf<String?>(null) }
    var selectedRight by remember { mutableStateOf<String?>(null) }
    var matches by remember { mutableStateOf(setOf<Pair<String, String>>()) }
    var wrongAttempts by remember { mutableStateOf(0) }
    
    val leftItems = pairs.map { it.first }.shuffled()
    val rightItems = pairs.map { it.second }.shuffled()
    
    LaunchedEffect(selectedLeft, selectedRight) {
        if (selectedLeft != null && selectedRight != null) {
            val isCorrectMatch = pairs.any { it.first == selectedLeft && it.second == selectedRight }
            if (isCorrectMatch) {
                matches = matches + (selectedLeft!! to selectedRight!!)
                if (matches.size == pairs.size) {
                    val score = maxOf(0, (pairs.size * 10) - (wrongAttempts * 2))
                    onComplete(score)
                }
            } else {
                wrongAttempts++
            }
            selectedLeft = null
            selectedRight = null
        }
    }
    
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Match related items:", fontWeight = FontWeight.Medium)
        Text("Wrong attempts: $wrongAttempts", fontSize = 12.sp, color = Color.Red)
        
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Left column
            Column(modifier = Modifier.weight(1f)) {
                leftItems.forEach { item ->
                    val isMatched = matches.any { it.first == item }
                    val isSelected = selectedLeft == item
                    
                    if (!isMatched) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { selectedLeft = if (isSelected) null else item },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                               else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Text(
                                item,
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            
            // Right column
            Column(modifier = Modifier.weight(1f)) {
                rightItems.forEach { item ->
                    val isMatched = matches.any { it.second == item }
                    val isSelected = selectedRight == item
                    
                    if (!isMatched) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { selectedRight = if (isSelected) null else item },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                               else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Text(
                                item,
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FillBlankGame(
    text: String,
    answer: String,
    onComplete: (score: Int) -> Unit
) {
    var userInput by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }
    
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Fill in the blank:", fontWeight = FontWeight.Medium)
        
        Text(
            text.replace("___", "____"),
            fontSize = 16.sp,
            modifier = Modifier.padding(16.dp)
        )
        
        OutlinedTextField(
            value = userInput,
            onValueChange = { userInput = it },
            label = { Text("Your answer") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSubmitted
        )
        
        if (!isSubmitted) {
            Button(
                onClick = {
                    isSubmitted = true
                    val isCorrect = userInput.trim().equals(answer.trim(), ignoreCase = true)
                    onComplete(if (isCorrect) 15 else 0)
                },
                enabled = userInput.isNotBlank()
            ) {
                Text("Submit")
            }
        } else {
            val isCorrect = userInput.trim().equals(answer.trim(), ignoreCase = true)
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isCorrect) Color.Green.copy(alpha = 0.1f) else Color.Red.copy(alpha = 0.1f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        if (isCorrect) "✓ Correct!" else "✗ Incorrect",
                        fontWeight = FontWeight.Bold,
                        color = if (isCorrect) Color.Green else Color.Red
                    )
                    if (!isCorrect) {
                        Text("Correct answer: $answer")
                    }
                }
            }
        }
    }
}

@Composable
fun SentenceBuilderGame(
    words: List<String>,
    correctOrder: List<String>,
    onComplete: (score: Int) -> Unit
) {
    var currentSentence by remember { mutableStateOf(listOf<String>()) }
    var availableWords by remember { mutableStateOf(words.shuffled()) }
    var isSubmitted by remember { mutableStateOf(false) }
    
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Build the correct sentence:", fontWeight = FontWeight.Medium)
        
        // Current sentence area
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            colors = CardDefaults.cardColors(containerColor = GrammarColor.copy(alpha = 0.05f))
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(currentSentence) { word ->
                    Card(
                        modifier = Modifier.clickable {
                            if (!isSubmitted) {
                                currentSentence = currentSentence - word
                                availableWords = availableWords + word
                            }
                        },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    ) {
                        Text(
                            word,
                            modifier = Modifier.padding(8.dp),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
        
        // Available words
        Text("Available words:", fontWeight = FontWeight.Medium)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(availableWords) { word ->
                Card(
                    modifier = Modifier.clickable {
                        if (!isSubmitted) {
                            currentSentence = currentSentence + word
                            availableWords = availableWords - word
                        }
                    },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        word,
                        modifier = Modifier.padding(8.dp),
                        fontSize = 14.sp
                    )
                }
            }
        }
        
        if (!isSubmitted) {
            Button(
                onClick = {
                    isSubmitted = true
                    val isCorrect = currentSentence == correctOrder
                    onComplete(if (isCorrect) 20 else 0)
                },
                enabled = currentSentence.isNotEmpty()
            ) {
                Text("Check Order")
            }
        } else {
            val isCorrect = currentSentence == correctOrder
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isCorrect) Color.Green.copy(alpha = 0.1f) else Color.Red.copy(alpha = 0.1f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        if (isCorrect) "✓ Perfect word order!" else "✗ Incorrect order",
                        fontWeight = FontWeight.Bold,
                        color = if (isCorrect) Color.Green else Color.Red
                    )
                    if (!isCorrect) {
                        Text("Correct order: ${correctOrder.joinToString(" ")}")
                    }
                }
            }
        }
    }
}
