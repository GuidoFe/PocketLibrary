package com.guidofe.pocketlibrary.viewmodels

import androidx.paging.PagingData
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.model.ImportedBookData
import kotlinx.coroutines.flow.Flow

interface ILibraryViewModel: IDestinationViewModel {
    val pager: Flow<PagingData<BookBundle>>
    fun fetchBookForTypedIsbn(isbn: String,
                              callback: (ImportedBookData?) -> Unit,
                              failureCallback: (Int, String) -> Unit = {_,_ ->})
}
