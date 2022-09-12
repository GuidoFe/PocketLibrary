package com.guidofe.pocketlibrary.ui.pages.editbookpage

import com.guidofe.pocketlibrary.model.repositories.ImportedBookData
import com.guidofe.pocketlibrary.model.repositories.local_db.BookBundle

data class EditBookPageNavArgs(val bookBundle: BookBundle? = null, val importedBookData: ImportedBookData? = null)
