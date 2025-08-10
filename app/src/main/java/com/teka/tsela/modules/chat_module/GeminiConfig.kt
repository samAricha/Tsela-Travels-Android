package com.teka.tsela.modules.chat_module

import com.teka.tsela.BuildConfig


object GeminiConfig {
    // Model configurations
    const val MODEL_NAME = "gemini-1.5-flash"
    const val VISION_MODEL_NAME = "gemini-1.5-pro-vision"
    
    // API Key from BuildConfig (secure)
    val GEMINI_API_KEY: String = BuildConfig.GEMINI_API_KEY
    
    // Generation configurations
    const val MAX_OUTPUT_TOKENS = 8192
    const val TEMPERATURE = 0.7f
    const val TOP_P = 0.8f
    const val TOP_K = 40
    
    // Timeout configurations (in milliseconds)
    const val REQUEST_TIMEOUT = 30000L
    const val CONNECTION_TIMEOUT = 15000L
    
    // Chat configurations
    const val MAX_CHAT_HISTORY = 50
    const val MAX_MESSAGE_LENGTH = 4000
    
    // Image processing configurations
    const val MAX_IMAGE_SIZE_MB = 4
    const val SUPPORTED_IMAGE_FORMATS = "jpg,jpeg,png,webp"
    
    // Error handling
    const val MAX_RETRY_ATTEMPTS = 3
    const val RETRY_DELAY_MS = 1000L
}