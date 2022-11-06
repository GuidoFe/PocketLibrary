package com.guidofe.pocketlibrary.ui.utils

import kotlinx.coroutines.flow.*

class MultipleSelectionManager<K, V>(
    val getKey: (V) -> K
) {
    private var _isMultipleSelecting: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isMultipleSelecting: StateFlow<Boolean> = _isMultipleSelecting.asStateFlow()
    private val _selectedItems: MutableStateFlow<Map<K, V>> = MutableStateFlow(mapOf())
    val selectedItems: StateFlow<Map<K, V>> = _selectedItems.asStateFlow()
    val selectedKeys: List<K>
        get() = _selectedItems.value.keys.toList()

    fun isItemSelected(item: V): Boolean {
        return if (_isMultipleSelecting.value)
            _selectedItems.value.containsKey(getKey(item))
        else
            false
    }

    fun startMultipleSelection(value: V) {
        _isMultipleSelecting.value = true
        _selectedItems.value += Pair(getKey(value), value)
    }

    fun multipleSelectToggle(value: V) {
        if (!_isMultipleSelecting.value) return
        if (_selectedItems.value.contains(getKey(value))) {
            _selectedItems.value -= getKey(value)
            if(_selectedItems.value.isEmpty())
                _isMultipleSelecting.value = false
        } else {
            _selectedItems.value += Pair(getKey(value), value)
        }
    }

    fun clearSelection() {
        _isMultipleSelecting.value = false
        _selectedItems.value = mapOf()
    }

    val count: Int
        get() =
            if (_isMultipleSelecting.value)
                _selectedItems.value.size
            else
                0
}