package com.guidofe.pocketlibrary.di

import com.guidofe.pocketlibrary.model.repositories.google_book.GoogleBooksService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {
    @Provides
    fun provideGoogleBooksService(): GoogleBooksService {
        return GoogleBooksService()
    }
}