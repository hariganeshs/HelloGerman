package com.hellogerman.app.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Close

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.hellogerman.app.ui.viewmodel.LessonViewModel
import com.hellogerman.app.ui.theme.SprechenColor
import com.google.gson.Gson
import com.hellogerman.app.data.entities.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SprechenLessonDetailScreen(
    navController: NavController,
    lessonId: Int,
    lessonViewModel: LessonViewModel = viewModel()
) {
    val currentLesson by lessonViewModel.currentLesson.collectAsState()
    val isLoading by lessonViewModel.isLoading.collectAsState()
    
    var currentStep by remember { mutableStateOf(0) } // 0: prompt, 1: recording, 2: feedback
    var isRecording by remember { mutableStateOf(false) }
    var userTranscript by remember { mutableStateOf("") }
    var score by remember { mutableStateOf(0) }
    var feedback by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    
    // Speech recognition and TTS
    var speechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var isPlayingPrompt by remember { mutableStateOf(false) }
    var isSpeechRecognitionAvailable by remember { mutableStateOf(false) }
    var isNetworkConnected by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val gson = remember { Gson() }
    
    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, can start recording
        } else {
            errorMessage = "Microphone permission is required for speech recognition"
        }
    }
    
    LaunchedEffect(lessonId) {
        try {
            lessonViewModel.loadLessonById(lessonId)
        } catch (e: Exception) {
            errorMessage = "Failed to load lesson: ${e.message}"
        }
    }
    
    // Initialize speech recognition with error handling
    LaunchedEffect(Unit) {
        // Check network connectivity
        isNetworkConnected = isNetworkAvailable(context)
        
        try {
            isSpeechRecognitionAvailable = SpeechRecognizer.isRecognitionAvailable(context)
            if (isSpeechRecognitionAvailable) {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
                speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        // Ready to start listening
                    }
                    
                    override fun onBeginningOfSpeech() {
                        // User started speaking
                    }
                    
                    override fun onRmsChanged(rmsdB: Float) {
                        // Audio level changed
                    }
                    
                    override fun onBufferReceived(buffer: ByteArray?) {
                        // Audio buffer received
                    }
                    
                    override fun onEndOfSpeech() {
                        isRecording = false
                    }
                    
                    override fun onError(error: Int) {
                        isRecording = false
                        when (error) {
                            SpeechRecognizer.ERROR_NO_MATCH -> {
                                userTranscript = "No speech detected. Please try again."
                                errorMessage = "No speech was detected. Please speak clearly and try again."
                            }
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                                userTranscript = "Speech timeout. Please try again."
                                errorMessage = "Speech timeout. Please speak within the time limit."
                            }
                            SpeechRecognizer.ERROR_NETWORK -> {
                                userTranscript = "Network error. Please check your connection."
                                errorMessage = "Network connection required for speech recognition. Please check your internet connection and try again."
                            }
                            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> {
                                userTranscript = "Network timeout. Please try again."
                                errorMessage = "Network timeout. Please check your connection and try again."
                            }
                            SpeechRecognizer.ERROR_SERVER -> {
                                userTranscript = "Server error. Please try again."
                                errorMessage = "Speech recognition server error. Please try again later."
                            }
                            SpeechRecognizer.ERROR_CLIENT -> {
                                userTranscript = "Client error. Please try again."
                                errorMessage = "Speech recognition client error. Please restart the app and try again."
                            }
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> {
                                userTranscript = "Permission error. Please grant microphone permission."
                                errorMessage = "Microphone permission is required. Please grant permission in settings."
                            }
                            else -> {
                                userTranscript = "Speech recognition error. Please try again."
                                errorMessage = "Speech recognition error occurred. Please try again."
                            }
                        }
                    }
                    
                    override fun onResults(results: Bundle?) {
                        try {
                            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            if (!matches.isNullOrEmpty()) {
                                userTranscript = matches[0]
                            } else {
                                userTranscript = "No speech detected. Please try again."
                            }
                        } catch (e: Exception) {
                            userTranscript = "Error processing speech results: ${e.message}"
                        }
                        isRecording = false
                    }
                    
                    override fun onPartialResults(partialResults: Bundle?) {
                        try {
                            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            if (!matches.isNullOrEmpty()) {
                                userTranscript = matches[0]
                            }
                        } catch (e: Exception) {
                            // Ignore partial result errors
                        }
                    }
                    
                    override fun onEvent(eventType: Int, params: Bundle?) {
                        // Event occurred
                    }
                })
            } else {
                errorMessage = "Speech recognition is not available on this device"
            }
        } catch (e: Exception) {
            errorMessage = "Failed to initialize speech recognition: ${e.message}"
        }
        
        // Initialize TTS with error handling
        try {
            tts = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    try {
                        tts?.language = Locale.GERMAN
                    } catch (e: Exception) {
                        errorMessage = "Failed to set TTS language: ${e.message}"
                    }
                } else {
                    errorMessage = "Failed to initialize Text-to-Speech"
                }
            }
        } catch (e: Exception) {
            errorMessage = "Failed to initialize TTS: ${e.message}"
        }
    }
    
    // Cleanup
    DisposableEffect(Unit) {
        onDispose {
            try {
                speechRecognizer?.destroy()
                tts?.stop()
                tts?.shutdown()
            } catch (e: Exception) {
                // Ignore cleanup errors
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentLesson?.title ?: "Speaking Lesson",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Go back to speaking lessons list"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = SprechenColor)
            }
        } else if (errorMessage.isNotEmpty()) {
            // Show error message
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { navController.navigateUp() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Go Back")
                        }
                    }
                }
            }
        } else if (currentLesson == null) {
            // Show loading state when lesson is null
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = SprechenColor)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading lesson...",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            // Lesson is loaded, show content
            val lesson = currentLesson!!
            val lessonContent = try {
                gson.fromJson(lesson.content, SprechenContent::class.java)
            } catch (e: Exception) {
                errorMessage = "Failed to parse lesson content: ${e.message}"
                null
            }
            
            if (lessonContent != null) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    
                    when (currentStep) {
                        0 -> {
                            // Speaking Prompt
                            item {
                                Text(
                                    text = lesson.title ?: "Speaking Lesson",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text(
                                            text = "Speaking Prompt",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = lessonContent.prompt ?: "No prompt available",
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Model Response",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            
                                            IconButton(
                                                onClick = {
                                                    try {
                                                        if (isPlayingPrompt) {
                                                            tts?.stop()
                                                            isPlayingPrompt = false
                                                        } else {
                                                            val responseText = lessonContent.modelResponse ?: "No response available"
                                                            tts?.speak(responseText, TextToSpeech.QUEUE_FLUSH, null, null)
                                                            isPlayingPrompt = true
                                                        }
                                                    } catch (e: Exception) {
                                                        errorMessage = "Failed to play audio: ${e.message}"
                                                    }
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = if (isPlayingPrompt) Icons.Default.Close else Icons.Default.PlayArrow,
                                                    contentDescription = if (isPlayingPrompt) "Stop playing model response" else "Play model response audio",
                                                    tint = SprechenColor
                                                )
                                            }
                                        }
                                        
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = lessonContent.modelResponse ?: "No model response available",
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.background(
                                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                            ).padding(8.dp)
                                        )
                                    }
                                }
                            }
                            
                            item {
                                Button(
                                    onClick = { 
                                        if (isSpeechRecognitionAvailable) {
                                            currentStep = 1
                                        } else {
                                            errorMessage = "Speech recognition is not available on this device"
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SprechenColor
                                    )
                                ) {
                                    Text("Start Recording")
                                }
                            }
                        }
                        
                        1 -> {
                            // Recording Interface
                            item {
                                Text(
                                    text = "Record Your Response",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                                                                 Text(
                                             text = "Tap the microphone to start recording",
                                             fontSize = 16.sp,
                                             color = MaterialTheme.colorScheme.onSurfaceVariant
                                         )
                                         
                                         // Network status indicator
                                         if (!isNetworkConnected) {
                                             Spacer(modifier = Modifier.height(8.dp))
                                             Text(
                                                 text = "⚠ Internet connection required for speech recognition",
                                                 color = MaterialTheme.colorScheme.error,
                                                 style = MaterialTheme.typography.bodySmall
                                             )
                                             Spacer(modifier = Modifier.height(4.dp))
                                             Text(
                                                 text = "✓ Text-to-Speech works offline",
                                                 color = MaterialTheme.colorScheme.primary,
                                                 style = MaterialTheme.typography.bodySmall
                                             )
                                         }
                                        Spacer(modifier = Modifier.height(16.dp))
                                        
                                        Box(
                                            modifier = Modifier.size(80.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isRecording) {
                                                // Recording animation background
                                                Box(
                                                    modifier = Modifier
                                                        .size(80.dp)
                                                        .background(
                                                            MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                                                            shape = androidx.compose.foundation.shape.CircleShape
                                                        )
                                                )
                                            }
                                            
                                            IconButton(
                                                onClick = {
                                                    try {
                                                        if (ContextCompat.checkSelfPermission(
                                                                context,
                                                                Manifest.permission.RECORD_AUDIO
                                                            ) == PackageManager.PERMISSION_GRANTED
                                                        ) {
                                                            if (isRecording) {
                                                                speechRecognizer?.stopListening()
                                                                isRecording = false
                                                            } else {
                                                                // Check network connectivity before starting speech recognition
                                                                if (!isNetworkAvailable(context)) {
                                                                    errorMessage = "Internet connection is required for speech recognition. Please check your network connection and try again."
                                                                    return@IconButton
                                                                }
                                                                
                                                                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "de-DE")
                                                                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
                                                                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                                                                    putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 1000L) // Minimum 1 second
                                                                    putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 3000L) // 3 seconds of silence
                                                                    putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 2000L) // 2 seconds for possible completion
                                                                }
                                                                speechRecognizer?.startListening(intent)
                                                                isRecording = true
                                                            }
                                                        } else {
                                                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                                        }
                                                    } catch (e: Exception) {
                                                        errorMessage = "Failed to start recording: ${e.message}"
                                                    }
                                                },
                                                modifier = Modifier.size(80.dp)
                                            ) {
                                            Icon(
                                                imageVector = if (isRecording) Icons.Default.Close else Icons.Default.Info,
                                                contentDescription = if (isRecording) "Stop voice recording" else "Start voice recording for speaking practice",
                                                tint = if (isRecording) MaterialTheme.colorScheme.error else SprechenColor,
                                                modifier = Modifier.size(48.dp)
                                            )
                                        }
                                    }
                                        
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = if (isRecording) "Recording..." else "Tap to record",
                                            fontSize = 14.sp,
                                            color = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                            
                            if (userTranscript.isNotEmpty()) {
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surface
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp)
                                        ) {
                                            Text(
                                                text = "Your Response",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = userTranscript,
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                                
                                item {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                try {
                                                    // Check network connectivity before starting speech recognition
                                                    if (!isNetworkAvailable(context)) {
                                                        errorMessage = "Internet connection is required for speech recognition. Please check your network connection and try again."
                                                        return@Button
                                                    }
                                                    
                                                    userTranscript = ""
                                                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "de-DE")
                                                        putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
                                                        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                                                        putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 1000L)
                                                        putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 3000L)
                                                        putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 2000L)
                                                    }
                                                    speechRecognizer?.startListening(intent)
                                                    isRecording = true
                                                } catch (e: Exception) {
                                                    errorMessage = "Failed to restart recording: ${e.message}"
                                                }
                                            },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                                            )
                                        ) {
                                            Text("Record Again")
                                        }
                                        
                                        Button(
                                            onClick = {
                                                try {
                                                    // Generate feedback and score
                                                    val feedbackResult = generateSpeakingFeedback(userTranscript, lessonContent)
                                                    feedback = feedbackResult.first
                                                    score = feedbackResult.second
                                                    currentStep = 2
                                                    
                                                    // Update lesson completion
                                                    lessonViewModel.completeLesson(lessonId, score)
                                                } catch (e: Exception) {
                                                    errorMessage = "Failed to process response: ${e.message}"
                                                }
                                            },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = SprechenColor
                                            )
                                        ) {
                                            Text("Submit")
                                        }
                                    }
                                }
                            }
                        }
                        
                        2 -> {
                            // Feedback
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Completed",
                                            tint = SprechenColor,
                                            modifier = Modifier.size(64.dp)
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "Speaking Completed!",
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Your Score: $score%",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = SprechenColor
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(16.dp)
                                            ) {
                                                Text(
                                                    text = "Feedback",
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = feedback,
                                                    fontSize = 14.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )

                                                // Show model response for comparison
                                                if (lessonContent.modelResponse?.isNotEmpty() == true) {
                                                    Spacer(modifier = Modifier.height(12.dp))
                                                    Text(
                                                        text = "Model Response for Comparison:",
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = lessonContent.modelResponse!!,
                                                        fontSize = 13.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        modifier = Modifier.background(
                                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                                        ).padding(8.dp)
                                                    )
                                                }

                                                // Pronunciation tips
                                                val pronunciationTips = generatePronunciationTips(userTranscript)
                                                if (pronunciationTips.isNotEmpty()) {
                                                    Spacer(modifier = Modifier.height(12.dp))
                                                    Text(
                                                        text = "Pronunciation Tips:",
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        color = Color(0xFF1976D2)
                                                    )
                                                    pronunciationTips.forEach { tip ->
                                                        Text(
                                                            text = "• $tip",
                                                            fontSize = 13.sp,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(
                                            onClick = { navController.navigateUp() },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = SprechenColor
                                            )
                                        ) {
                                            Text("Back to Lessons")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
    
    return when {
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}

internal fun generateSpeakingFeedback(userTranscript: String, content: SprechenContent): Pair<String, Int> {
    var score = 0
    val feedback = mutableListOf<String>()

    // Check if user provided any response
    if (userTranscript.isBlank() || userTranscript.contains("Error:") || userTranscript.contains("No speech detected")) {
        return Pair("No speech detected. Please try again.", 0)
    }

    // Basic length check
    val wordCount = userTranscript.split("\\s+".toRegex()).filter { it.isNotEmpty() }.size
    if (wordCount >= 5) {
        score += 20
        feedback.add("✓ Good response length ($wordCount words)")
    } else if (wordCount >= 3) {
        score += 10
        feedback.add("⚠ Response could be longer ($wordCount words)")
    } else {
        feedback.add("✗ Response too short (only $wordCount words)")
    }

    // Enhanced German vocabulary check with A2-specific words
    val germanWords = listOf(
        // Basic pronouns
        "ich", "du", "er", "sie", "es", "wir", "ihr", "Sie", "mich", "dich", "sich",
        // Common conjunctions
        "und", "oder", "aber", "dass", "wenn", "weil", "denn", "sondern",
        // Common verbs (A2 level)
        "sein", "haben", "werden", "können", "müssen", "sollen", "wollen", "mögen",
        "gehen", "kommen", "machen", "sagen", "finden", "geben", "sehen", "stehen",
        // Common nouns (A2 level)
        "hallo", "danke", "bitte", "ja", "nein", "gut", "schlecht", "groß", "klein",
        "haus", "zeit", "tag", "nacht", "arbeit", "schule", "stadt", "land",
        // Question words
        "was", "wer", "wo", "wann", "warum", "wie", "welche", "welcher", "welches",
        // Prepositions (A2 level)
        "in", "auf", "an", "mit", "nach", "von", "zu", "für", "bei", "aus"
    )
    val foundGermanWords = germanWords.count { userTranscript.contains(it, ignoreCase = true) }
    if (foundGermanWords >= 4) {
        score += 25
        feedback.add("✓ Excellent use of German vocabulary ($foundGermanWords German words)")
    } else if (foundGermanWords >= 3) {
        score += 20
        feedback.add("✓ Very good use of German vocabulary ($foundGermanWords German words)")
    } else if (foundGermanWords >= 2) {
        score += 15
        feedback.add("✓ Good use of German vocabulary ($foundGermanWords German words)")
    } else {
        feedback.add("✗ Try to use more German vocabulary (found $foundGermanWords German words)")
    }

    // Check for keywords from the lesson content
    val keywords = content.keywords
    if (keywords.isNotEmpty()) {
        val foundKeywords = keywords.count { userTranscript.contains(it, ignoreCase = true) }
        if (foundKeywords >= keywords.size / 2) {
            score += 20
            feedback.add("✓ Great use of lesson keywords ($foundKeywords/${keywords.size})")
        } else if (foundKeywords >= 1) {
            score += 10
            feedback.add("⚠ Used some lesson keywords ($foundKeywords/${keywords.size})")
        } else {
            feedback.add("✗ Try to use the lesson keywords: ${keywords.take(3).joinToString(", ")}")
        }
    }

    // Advanced similarity scoring with model response
    val modelResponse = content.modelResponse ?: ""
    if (modelResponse.isNotEmpty()) {
        val similarityScore = calculateSimilarity(userTranscript, modelResponse)
        if (similarityScore >= 0.7) {
            score += 20
            feedback.add("✓ Excellent similarity to model response (${(similarityScore * 100).toInt()}%)")
        } else if (similarityScore >= 0.5) {
            score += 15
            feedback.add("✓ Good similarity to model response (${(similarityScore * 100).toInt()}%)")
        } else if (similarityScore >= 0.3) {
            score += 10
            feedback.add("⚠ Moderate similarity to model response (${(similarityScore * 100).toInt()}%)")
        } else {
            feedback.add("✗ Low similarity to model response (${(similarityScore * 100).toInt()}%) - try to follow the model structure")
        }
    }

    // Pronunciation quality assessment (based on speech recognition success)
    if (userTranscript.length > 30 && !userTranscript.contains("Error") && wordCount >= 4) {
        score += 10
        feedback.add("✓ Good pronunciation quality (clear speech recognition)")
    } else if (userTranscript.length > 20 && wordCount >= 3) {
        score += 5
        feedback.add("⚠ Adequate pronunciation quality")
    }

    // Fluency and naturalness check
    val avgWordLength = if (wordCount > 0) userTranscript.length.toFloat() / wordCount else 0f
    if (avgWordLength > 4.0 && wordCount >= 4) {
        score += 5
        feedback.add("✓ Good fluency and natural speech patterns")
    }

    // A2-specific grammar checks
    val grammarIssues = checkA2Grammar(userTranscript)
    if (grammarIssues.isEmpty()) {
        score += 10
        feedback.add("✓ No major grammar issues detected")
    } else {
        score += 5
        feedback.add("⚠ Minor grammar suggestions: ${grammarIssues.joinToString(", ")}")
    }

    // Ensure score doesn't exceed 100
    score = minOf(score, 100)

    return Pair(feedback.joinToString("\n"), score)
}

internal fun calculateSimilarity(userText: String, modelText: String): Double {
    val userWords = userText.lowercase().split("\\s+".toRegex()).filter { it.isNotEmpty() && it.length > 1 }
    val modelWords = modelText.lowercase().split("\\s+".toRegex()).filter { it.isNotEmpty() && it.length > 1 }

    if (userWords.isEmpty() || modelWords.isEmpty()) return 0.0

    val commonWords = userWords.intersect(modelWords.toSet()).size
    val totalUniqueWords = (userWords + modelWords).distinct().size

    // Jaccard similarity coefficient
    return if (totalUniqueWords > 0) commonWords.toDouble() / totalUniqueWords else 0.0
}

internal fun checkA2Grammar(text: String): List<String> {
    val suggestions = mutableListOf<String>()

    // Check for common A2-level errors
    val sentences = text.split("[.!?]+".toRegex()).filter { it.trim().isNotEmpty() }

    sentences.forEach { sentence ->
        // Check for missing articles (common A2 issue)
        val words = sentence.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
        if (words.size > 3) {
            val firstWord = words[0].lowercase()
            // Simple check for nouns that might need articles
            if (firstWord.matches(Regex("^(haus|schule|arbeit|stadt|zeit|tag|mensch|frau|mann|kind)$")) &&
                !sentence.contains("der ") && !sentence.contains("die ") && !sentence.contains("das ")) {
                suggestions.add("Consider using articles (der/die/das)")
            }
        }

        // Check for preposition + article contractions (A2 topic)
        if (sentence.contains(" in der ") || sentence.contains(" in dem ")) {
            suggestions.add("Try 'im' instead of 'in der/dem' where appropriate")
        }
        if (sentence.contains(" an der ") || sentence.contains(" an dem ")) {
            suggestions.add("Try 'am' instead of 'an der/dem' where appropriate")
        }
    }

    // Check for verb conjugation patterns (A2: Perfekt)
    val perfektVerbs = listOf("gehen", "kommen", "machen", "sagen", "finden", "geben", "sehen")
    perfektVerbs.forEach { verb ->
        if (text.contains(verb, ignoreCase = true) &&
            !text.contains("ge$verb", ignoreCase = true) &&
            !text.contains("bin", ignoreCase = true) &&
            !text.contains("habe", ignoreCase = true)) {
            suggestions.add("Consider using Perfekt tense for '$verb'")
        }
    }

    return suggestions.distinct().take(2) // Limit to 2 suggestions
}

internal fun generatePronunciationTips(text: String): List<String> {
    val tips = mutableListOf<String>()

    // Check for common German pronunciation challenges
    val words = text.lowercase().split("\\s+".toRegex()).filter { it.isNotEmpty() }

    words.forEach { word ->
        when {
            // German 'ch' sounds
            word.contains("ach") || word.contains("echt") -> {
                tips.add("'$word': 'ch' after 'a' sounds like the 'ch' in Scottish 'loch'")
            }
            word.contains("ich") || word.contains("licht") -> {
                tips.add("'$word': 'ch' after 'i' sounds like 'h' in 'huge'")
            }
            word.contains("buch") || word.contains("lachen") -> {
                tips.add("'$word': 'ch' after 'u'/'au' sounds like 'sh' in 'ship'")
            }

            // German umlauts and special characters
            word.contains("ä") || word.contains("ae") -> {
                tips.add("'$word': 'ä' sounds like 'e' in 'bed' (not like 'a' in 'bad')")
            }
            word.contains("ö") || word.contains("oe") -> {
                tips.add("'$word': 'ö' sounds like 'i' in 'bird' with rounded lips")
            }
            word.contains("ü") || word.contains("ue") -> {
                tips.add("'$word': 'ü' sounds like 'ee' in 'see' with rounded lips")
            }

            // German 'w' and 'v'
            word.contains("wasser") || word.contains("wie") -> {
                tips.add("'$word': German 'w' sounds like English 'v'")
            }
            word.contains("vater") || word.contains("viel") -> {
                tips.add("'$word': German 'v' sounds like English 'f'")
            }

            // German 'r' sound
            word.contains("rot") || word.contains("recht") -> {
                tips.add("'$word': German 'r' is rolled or sounds like 'ch' in Scottish")
            }

            // German 'z' and 'tz'
            word.contains("zeit") || word.contains("zieht") -> {
                tips.add("'$word': 'z' sounds like 'ts' in 'cats'")
            }
        }
    }

    // General pronunciation tips based on speech patterns
    if (words.size > 3) {
        if (text.contains("sch", ignoreCase = true)) {
            tips.add("Practice the 'sch' sound - it should sound like 'sh' in 'ship'")
        }

        if (text.contains("st", ignoreCase = true) || text.contains("sp", ignoreCase = true)) {
            tips.add("At the beginning of words, 'st'/'sp' sound like 'sht'/'shp'")
        }

        if (text.contains("ng", ignoreCase = true)) {
            tips.add("German 'ng' is like English 'ng' in 'sing' (not like 'n'+'g')")
        }
    }

    return tips.distinct().take(3) // Limit to 3 tips
}
