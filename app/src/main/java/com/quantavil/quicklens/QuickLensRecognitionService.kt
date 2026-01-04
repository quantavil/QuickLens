package com.quantavil.quicklens

import android.os.Bundle
import android.service.voice.VoiceInteractionService
import android.service.voice.VoiceInteractionSession
import android.service.voice.VoiceInteractionSessionService

class QuickLensRecognitionService : VoiceInteractionService() {
    override fun onReady() {
        super.onReady()
        // Service is ready  
    }
}
