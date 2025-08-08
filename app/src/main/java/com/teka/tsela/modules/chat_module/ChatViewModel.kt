package com.teka.tsela.modules.chat_module

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val isLoading: Boolean = false
)

data class ChatSession(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val messages: List<ChatMessage> = emptyList(),
    val lastUpdated: Long = System.currentTimeMillis(),
    val isActive: Boolean = false
)

data class ChatUiState(
    val chatSessions: List<ChatSession> = emptyList(),
    val currentSession: ChatSession? = null,
    val isLoading: Boolean = false,
    val isSending: Boolean = false,
    val showSidebar: Boolean = false,
    val inputText: String = "",
    val error: String? = null
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    // You'll need to inject your Gemini AI service here
    // private val geminiService: GeminiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        // Load existing chat sessions or create a default one
        createNewChatSession()
    }

    fun createNewChatSession(title: String = "New Chat") {
        viewModelScope.launch {
            val newSession = ChatSession(
                title = title,
                isActive = true
            )

            _uiState.update { currentState ->
                currentState.copy(
                    chatSessions = currentState.chatSessions.map { it.copy(isActive = false) } + newSession,
                    currentSession = newSession,
                    showSidebar = false
                )
            }
        }
    }

    fun selectChatSession(session: ChatSession) {
        _uiState.update { currentState ->
            currentState.copy(
                chatSessions = currentState.chatSessions.map { 
                    it.copy(isActive = it.id == session.id) 
                },
                currentSession = session,
                showSidebar = false
            )
        }
    }

    fun deleteChatSession(sessionId: String) {
        _uiState.update { currentState ->
            val updatedSessions = currentState.chatSessions.filter { it.id != sessionId }
            val newCurrentSession = if (currentState.currentSession?.id == sessionId) {
                updatedSessions.firstOrNull()?.copy(isActive = true)
            } else {
                currentState.currentSession
            }

            currentState.copy(
                chatSessions = updatedSessions,
                currentSession = newCurrentSession
            )
        }
    }

    fun updateInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun toggleSidebar() {
        _uiState.update { it.copy(showSidebar = !it.showSidebar) }
    }

    fun sendMessage(content: String) {
        if (content.isBlank() || _uiState.value.isSending) return

        viewModelScope.launch {
            val currentSession = _uiState.value.currentSession ?: return@launch
            
            // Add user message
            val userMessage = ChatMessage(
                content = content,
                isFromUser = true
            )

            // Add loading AI message
            val loadingMessage = ChatMessage(
                content = "",
                isFromUser = false,
                isLoading = true
            )

            val updatedMessages = currentSession.messages + userMessage + loadingMessage
            val updatedSession = currentSession.copy(
                messages = updatedMessages,
                lastUpdated = System.currentTimeMillis(),
                title = if (currentSession.messages.isEmpty()) {
                    generateChatTitle(content)
                } else currentSession.title
            )

            _uiState.update { currentState ->
                currentState.copy(
                    currentSession = updatedSession,
                    chatSessions = currentState.chatSessions.map { session ->
                        if (session.id == updatedSession.id) updatedSession else session
                    },
                    inputText = "",
                    isSending = true
                )
            }

            try {
                // TODO: Replace with actual Gemini API call
                val aiResponse = simulateGeminiResponse(content)
                
                // Remove loading message and add actual response
                val finalMessages = updatedMessages.dropLast(1) + ChatMessage(
                    content = aiResponse,
                    isFromUser = false
                )

                val finalSession = updatedSession.copy(messages = finalMessages)

                _uiState.update { currentState ->
                    currentState.copy(
                        currentSession = finalSession,
                        chatSessions = currentState.chatSessions.map { session ->
                            if (session.id == finalSession.id) finalSession else session
                        },
                        isSending = false
                    )
                }

            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error sending message", e)
                // Remove loading message on error
                val errorMessages = updatedMessages.dropLast(1)
                val errorSession = updatedSession.copy(messages = errorMessages)

                _uiState.update { currentState ->
                    currentState.copy(
                        currentSession = errorSession,
                        chatSessions = currentState.chatSessions.map { session ->
                            if (session.id == errorSession.id) errorSession else session
                        },
                        isSending = false,
                        error = e.message ?: "Failed to send message"
                    )
                }
            }
        }
    }

    private fun generateChatTitle(firstMessage: String): String {
        return firstMessage.take(30).trim().let {
            if (it.length < firstMessage.length) "$it..." else it
        }
    }

    // TODO: Replace with actual Gemini API integration
    private suspend fun simulateGeminiResponse(userMessage: String): String {
        kotlinx.coroutines.delay(2000) // Simulate network delay
        
        return when {
            userMessage.contains("hello", ignoreCase = true) -> 
                "Hello! I'm your AI assistant powered by Gemini. How can I help you today?"
            userMessage.contains("weather", ignoreCase = true) -> 
                "I'd be happy to help with weather information, but I don't have access to real-time weather data at the moment. You might want to check a weather app for current conditions."
            userMessage.contains("code", ignoreCase = true) -> 
                "I can definitely help you with coding! What programming language or specific problem are you working with?"
            else -> 
                "That's an interesting question! I'm here to help with a wide variety of topics including answering questions, helping with tasks, creative writing, analysis, and much more. What would you like to explore?"
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun renameChatSession(sessionId: String, newTitle: String) {
        _uiState.update { currentState ->
            val updatedSessions = currentState.chatSessions.map { session ->
                if (session.id == sessionId) {
                    session.copy(title = newTitle.take(50))
                } else session
            }
            
            currentState.copy(
                chatSessions = updatedSessions,
                currentSession = if (currentState.currentSession?.id == sessionId) {
                    currentState.currentSession?.copy(title = newTitle.take(50))
                } else currentState.currentSession
            )
        }
    }
}