package com.guidofe.pocketlibrary.model.repositories.google_book;

import java.util.List;

data class RawVolumeListResponse(val items: List<RawArrayItemResponse>, val rawSaleInfo: RawSaleInfo)
