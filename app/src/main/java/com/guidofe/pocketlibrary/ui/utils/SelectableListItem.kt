package com.guidofe.pocketlibrary.ui.utils

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class SelectableListItem<T>(val value: T, val isSelected: Boolean = false)