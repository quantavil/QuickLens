package com.quantavil.quicklens

import android.app.Application
import com.quantavil.quicklens.data.AppDatabase

class QuickLensApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
}
