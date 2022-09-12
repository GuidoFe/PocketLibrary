package com.guidofe.pocketlibrary.model.repositories.google_book

import com.guidofe.pocketlibrary.model.repositories.ImportedBookData
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.*
import java.lang.NumberFormatException

data class BookResponse (val rawVolumeResponse: RawVolumeResponse, val rawSaleInfo: RawSaleInfo) {

}