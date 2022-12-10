package com.guidofe.pocketlibrary.utils

import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine

@OptIn(ExperimentalCoroutinesApi::class)
class TranslationService(private val targetLanguageCode: String) {
    companion object {
        fun deleteDownloadedTranslators() {
            val modelManager = RemoteModelManager.getInstance()
            modelManager.getDownloadedModels(TranslateRemoteModel::class.java)
                .addOnFailureListener {
                    Log.e("debug", "Couldn't get downloaded models to delete")
                    it.printStackTrace()
                }
                .addOnSuccessListener {
                    for (model in it) {
                        modelManager.deleteDownloadedModel(model)
                            .addOnSuccessListener {
                                Log.d("debug", "Model ${model.modelName} deleted")
                            }
                            .addOnFailureListener { e ->
                                Log.e("debug", "Couldn't delete ${model.modelName}")
                                e.printStackTrace()
                            }
                    }
                }
        }
        fun hasTranslator(language: String, onResponse: (Boolean?) -> Unit) {
            val modelManager = RemoteModelManager.getInstance()
            val model = TranslateLanguage.fromLanguageTag(language)
            if (model == null) {
                onResponse(null)
                return
            }
            modelManager.isModelDownloaded(TranslateRemoteModel.Builder(model).build())
                .addOnFailureListener { onResponse(null) }
                .addOnSuccessListener { onResponse(it) }
        }
    }

    private var translator: Translator? = null

    suspend fun initTranslator() = suspendCancellableCoroutine { cont ->
        if (translator != null)
            cont.resume(true) {}
        val targetLanguage = TranslateLanguage.fromLanguageTag(targetLanguageCode)
        if (targetLanguage == null) {
            Log.e("debug", "Target language is null")
            cont.resume(false) {}
        }
        val translationOptions = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(targetLanguage!!)
            .build()
        translator = Translation.getClient(translationOptions)
        val conditions = DownloadConditions.Builder()
            // .requireWifi()
            .build()
        translator!!.downloadModelIfNeeded(conditions)
            .addOnFailureListener { cont.resume(false) { translator?.close() } }
            .addOnSuccessListener { cont.resume(true) { translator?.close() } }
    }

    suspend fun translate(word: String) = suspendCancellableCoroutine { cont ->
        if (translator == null) cont.resume(null) {}
        translator!!.translate(word)
            .addOnFailureListener { cont.resume(null) {} }
            .addOnSuccessListener { cont.resume(it) {} }
    }

    fun close() { translator?.close() }
}