package com.guidofe.pocketlibrary.ui.pages.editbookpage

import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.model.ImportedBookData

data class EditBookPageNavArgs(val bookBundle: BookBundle? = null, val importedBookData: ImportedBookData? = null)
