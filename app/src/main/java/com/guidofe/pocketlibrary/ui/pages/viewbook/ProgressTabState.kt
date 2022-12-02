package com.guidofe.pocketlibrary.ui.pages.viewbook

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.guidofe.pocketlibrary.data.local.library_db.entities.Progress
import com.guidofe.pocketlibrary.data.local.library_db.entities.ProgressPhase

class ProgressTabState() {
    var isDropdownExpanded by mutableStateOf(false)
    var isReadPagesError by mutableStateOf(false)
    var isTotalPagesError by mutableStateOf(false)
    var selectedPhase: ProgressPhase? by mutableStateOf(null)
    var pagesReadValue by mutableStateOf(0)
        private set
    var totalPagesValue by mutableStateOf(0)
        private set
    var pagesReadString by mutableStateOf("0")
        private set
    var totalPagesString by mutableStateOf("0")
        private set
    var trackPages by mutableStateOf(false)

    fun changeTotalPagesString(s: String) {
        totalPagesString = s
        s.toIntOrNull()?.let { totalPagesValue = it }
    }

    fun changePagesReadString(s: String) {
        pagesReadString = s
        s.toIntOrNull()?.let { pagesReadValue = it }
    }

    fun changePagesReadValue(value: Int) {
        pagesReadValue = value
        pagesReadString = value.toString()
    }

    fun init(progress: Progress?, totalPages: Int?) {
        selectedPhase = progress?.phase
        trackPages = progress?.trackPages ?: false
        totalPagesValue = totalPages ?: 0
        totalPagesString = totalPagesValue.toString()
        pagesReadValue = progress?.pagesRead ?: 0
        pagesReadString = pagesReadValue.toString()
    }
}