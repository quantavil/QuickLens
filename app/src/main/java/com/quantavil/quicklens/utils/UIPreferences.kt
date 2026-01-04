package com.quantavil.quicklens.utils

import android.content.Context
import android.content.SharedPreferences

class UIPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("ui_prefs", Context.MODE_PRIVATE)
    
    companion object {
        private const val KEY_DESKTOP_MODE = "is_desktop_mode"
        private const val KEY_THEME_MODE = "theme_mode" 

        private const val KEY_OPEN_LINKS_EXTERNALLY = "open_links_externally"
        private const val KEY_BUBBLE_ENABLED = "bubble_enabled"
        private const val KEY_HISTORY_RETENTION_DAYS = "history_retention_days"
        
        const val THEME_SYSTEM = 0
        const val THEME_LIGHT = 1
        const val THEME_DARK = 2
        
        const val HISTORY_NEVER = -1
    }
    
    fun isDesktopMode(): Boolean {
        return prefs.getBoolean(KEY_DESKTOP_MODE, false)
    }
    
    fun setDesktopMode(isEnabled: Boolean) {
        prefs.edit().putBoolean(KEY_DESKTOP_MODE, isEnabled).apply()
    }
    
    fun getThemeMode(): Int {
        return prefs.getInt(KEY_THEME_MODE, THEME_SYSTEM)
    }
    
    fun setThemeMode(mode: Int) {
        prefs.edit().putInt(KEY_THEME_MODE, mode).apply()
    }
    

    
    fun isOpenLinksExternally(): Boolean {
        return prefs.getBoolean(KEY_OPEN_LINKS_EXTERNALLY, false)
    }
    
    fun setOpenLinksExternally(isEnabled: Boolean) {
        prefs.edit().putBoolean(KEY_OPEN_LINKS_EXTERNALLY, isEnabled).apply()
    }
    
    fun isBubbleEnabled(): Boolean {
        return prefs.getBoolean(KEY_BUBBLE_ENABLED, true)
    }
    
    fun setBubbleEnabled(isEnabled: Boolean) {
        prefs.edit().putBoolean(KEY_BUBBLE_ENABLED, isEnabled).apply()
    }
    
    fun getHistoryRetentionDays(): Int {
        return prefs.getInt(KEY_HISTORY_RETENTION_DAYS, HISTORY_NEVER) 
    }
    
    fun setHistoryRetentionDays(days: Int) {
        prefs.edit().putInt(KEY_HISTORY_RETENTION_DAYS, days).apply()
    }
}
