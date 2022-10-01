package com.guidofe.pocketlibrary.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {
    // @Provides
    // @Named("Activity Test")
    // fun provideTestString():String = "Hello"
}