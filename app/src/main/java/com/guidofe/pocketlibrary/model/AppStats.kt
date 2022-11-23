package com.guidofe.pocketlibrary.model

data class AppStats (
    val libraryBooksCount: Int,
    val borrowedBooksCount: Int,
    val lentBooksCount: Int,
    val readBooksCount: Int,
    val inProgressCount: Int,
    val dnfCount: Int
)