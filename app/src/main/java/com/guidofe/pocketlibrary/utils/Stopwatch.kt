package com.guidofe.pocketlibrary.utils

import android.util.Log

class Stopwatch(private val tag: String = "stopwatch") {
    private val start = System.nanoTime()
    private var lastLap = start

    fun lap(label: String) {
        val timeNow = System.nanoTime()
        Log.d(tag, String.format("$label\t%,d", timeNow - lastLap))
        lastLap = timeNow
    }

    fun totalTime(label: String) {
        Log.d(tag, String.format("$label\t%,d", System.nanoTime() - start))
    }
}