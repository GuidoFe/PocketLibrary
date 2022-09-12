package com.guidofe.pocketlibrary.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.guidofe.pocketlibrary.model.repositories.google_book.GoogleBooksService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FabAddBookViewModel @Inject constructor(
    private val googleService: GoogleBooksService
): ViewModel() {

}