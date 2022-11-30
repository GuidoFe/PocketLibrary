package com.guidofe.pocketlibrary.ui.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SelectionManager<K, V>(
    val getKey: (V) -> K
) {
    // private var _isMultipleSelecting: MutableStateFlow<Boolean> = MutableStateFlow(false)
    // val isMultipleSelecting: StateFlow<Boolean> = _isMultipleSelecting.asStateFlow()
    var isMultipleSelecting by mutableStateOf(false)
        private set
    var singleSelectedItem: V? by mutableStateOf(null)
    private val _selectedItems: MutableStateFlow<Map<K, V>> = MutableStateFlow(mapOf())
    val selectedItems: StateFlow<Map<K, V>> = _selectedItems.asStateFlow()
    val selectedKeys: List<K>
        get() = selectedItems.value.keys.toList()

    fun startMultipleSelection(value: V) {
        isMultipleSelecting = true
        _selectedItems.value += Pair(getKey(value), value)
    }

    fun multipleSelectToggle(value: V) {
        if (!isMultipleSelecting) return
        if (selectedItems.value.contains(getKey(value))) {
            _selectedItems.value -= getKey(value)
            if (selectedItems.value.isEmpty())
                isMultipleSelecting = false
        } else {
            _selectedItems.value += Pair(getKey(value), value)
        }
    }

    fun clearSelection() {
        singleSelectedItem = null
        isMultipleSelecting = false
        _selectedItems.value = mapOf()
    }

    val count: Int
        get() =
            if (isMultipleSelecting)
                selectedItems.value.size
            else
                0
}