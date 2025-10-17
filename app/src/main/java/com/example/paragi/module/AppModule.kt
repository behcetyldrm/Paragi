package com.example.paragi.module

import android.content.Context
import androidx.room.Room
import com.example.paragi.database.ParagiDao
import com.example.paragi.database.ParagiDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) : ParagiDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = ParagiDatabase::class.java,
            name = "paragi_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideDao(db: ParagiDatabase) : ParagiDao {
        return db.paragiDao()
    }
}