package com.guidofe.pocketlibrary.di

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.room.Room
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.guidofe.pocketlibrary.data.local.library_db.AppDatabase
import com.guidofe.pocketlibrary.model.repositories.BookMetaRepository
import com.guidofe.pocketlibrary.model.repositories.DefaultBookMetaRepository
import com.guidofe.pocketlibrary.model.repositories.DefaultLocalRepository
import com.guidofe.pocketlibrary.model.repositories.LocalRepository
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.utils.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // @Singleton
    // @Provides
    // @Named("")
    // provideWhat() = what

    @Singleton
    @Provides
    fun provideSnackbarHostState(): SnackbarHostState {
        return SnackbarHostState()
    }

    @Singleton
    @Provides
    fun provideScaffoldState(): ScaffoldState {
        return ScaffoldState()
    }


    @Singleton
    @Provides
    fun provideImageLoader(@ApplicationContext context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }
            .build()
    }

    @Singleton
    @Provides
    fun provideDispatcherProvider(): DispatcherProvider {
        return object : DispatcherProvider {
            override val main: CoroutineDispatcher
                get() = Dispatchers.Main
            override val io: CoroutineDispatcher
                get() = Dispatchers.IO
            override val default: CoroutineDispatcher
                get() = Dispatchers.Default
            override val unconfined: CoroutineDispatcher
                get() = Dispatchers.Unconfined

        }
    }

    @Singleton
    @Provides
    fun provideApplicationContext(@ApplicationContext appContext: Context): Context {
        return appContext
    }

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "PocketLibrary"
        ).fallbackToDestructiveMigration().build()
        //TODO: remove fallback to destructive migration
    }

    @Singleton
    @Provides
    fun providesAuthorDao(db: AppDatabase) = db.authorDao()

    @Singleton
    @Provides
    fun providesBookDao(db: AppDatabase) = db.bookDao()

    @Singleton
    @Provides
    fun providesBookBundleDao(db: AppDatabase) = db.bookBundleDao()


    @Singleton
    @Provides
    fun providesGenreDao(db: AppDatabase) = db.genreDao()

    @Singleton
    @Provides
    fun providesNoteDao(db: AppDatabase) = db.noteDao()

    @Singleton
    @Provides
    fun providesLibraryRepository(db: AppDatabase): LocalRepository = DefaultLocalRepository(db)

    @Singleton
    @Provides
    fun providesBookMetaRepository(): BookMetaRepository = DefaultBookMetaRepository()
}