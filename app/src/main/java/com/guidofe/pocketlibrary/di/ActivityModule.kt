package com.guidofe.pocketlibrary.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Named

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {
    // @Provides
    // @Named("Activity Test")
    // fun provideTestString():String = "Hello"
}