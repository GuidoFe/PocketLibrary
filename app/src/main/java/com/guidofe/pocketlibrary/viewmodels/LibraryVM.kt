package com.guidofe.pocketlibrary.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.guidofe.pocketlibrary.model.repositories.BookMetaRepository
import com.guidofe.pocketlibrary.model.repositories.LibraryRepository
import com.guidofe.pocketlibrary.model.repositories.pagingsources.BookPagingSource
import com.guidofe.pocketlibrary.ui.modules.AppBarState
import com.guidofe.pocketlibrary.utils.AppBarStateDelegate
import com.guidofe.pocketlibrary.viewmodels.interfaces.ILibraryVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class LibraryVM @Inject constructor(
    private val repo: LibraryRepository,
    private val metaRepo: BookMetaRepository,
    private val appBarState: MutableStateFlow<AppBarState?>
): ViewModel(), ILibraryVM {
    private val pageSize = 50
    override val pager = Pager(PagingConfig(pageSize)) {
        BookPagingSource(repo)
    }.flow.cachedIn(viewModelScope)

    override val appBarDelegate: AppBarStateDelegate = AppBarStateDelegate(appBarState)


}
