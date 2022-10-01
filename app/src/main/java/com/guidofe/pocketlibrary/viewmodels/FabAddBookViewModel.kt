package com.guidofe.pocketlibrary.viewmodels

import androidx.lifecycle.ViewModel
import com.guidofe.pocketlibrary.model.repositories.BookMetaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FabAddBookViewModel @Inject constructor(
    private val metaRepo: BookMetaRepository
): ViewModel() {

}