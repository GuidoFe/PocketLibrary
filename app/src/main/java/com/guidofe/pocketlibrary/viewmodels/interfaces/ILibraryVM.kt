package com.guidofe.pocketlibrary.viewmodels.interfaces

import androidx.paging.PagingData
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import kotlinx.coroutines.flow.Flow

interface ILibraryVM: IDestinationVM {
    val pager: Flow<PagingData<BookBundle>>
}
