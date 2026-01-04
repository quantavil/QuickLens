package com.quantavil.quicklens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.app.assist.AssistContent
import android.app.assist.AssistStructure
import android.service.voice.VoiceInteractionSession

class QuickLensSession(context: Context) : VoiceInteractionSession(context) {
    override fun onHandleAssist(data: Bundle?, structure: AssistStructure?, content: AssistContent?) {
        super.onHandleAssist(data, structure, content)
        // Send broadcast to trigger screenshot capture via accessibility service
        val intent = Intent(QuickLensAccessibilityService.ACTION_TRIGGER_CAPTURE)
        intent.setPackage(context.packageName)
        context.sendBroadcast(intent)
        finish() // Close the session immediately
    }
}
