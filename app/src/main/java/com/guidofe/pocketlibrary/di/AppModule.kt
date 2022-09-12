package com.guidofe.pocketlibrary.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.guidofe.pocketlibrary.model.repositories.local_db.AppDatabase
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
    fun providesBookshelfDao(db: AppDatabase) = db.bookshelfDao()

    @Singleton
    @Provides
    fun providesFavoriteDao(db: AppDatabase) = db.favoriteDao()

    @Singleton
    @Provides
    fun providesGenreDao(db: AppDatabase) = db.genreDao()

    @Singleton
    @Provides
    fun providesNoteDao(db: AppDatabase) = db.noteDao()

    @Singleton
    @Provides
    fun providesPlaceDao(db: AppDatabase) = db.placeDao()

    @Singleton
    @Provides
    fun providesRoomDao(db: AppDatabase) = db.roomDao()

    @Singleton
    @Provides
    fun providesShelfDao(db: AppDatabase) = db.shelfDao()

    @Singleton
    @Provides
    fun providesWhishlistDao(db: AppDatabase) = db.wishlistDao()
}