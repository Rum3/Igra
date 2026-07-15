package noirdetective.game.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import noirdetective.game.data.local.AppDatabase
import noirdetective.game.data.local.dao.ChapterVisitDao
import noirdetective.game.data.local.dao.EvidenceDao
import noirdetective.game.data.local.dao.GameProgressDao
import noirdetective.game.data.local.dao.NotebookDao
import noirdetective.game.util.Constants
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            Constants.DATABASE_NAME
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideNotebookDao(database: AppDatabase): NotebookDao = database.notebookDao()

    @Provides
    fun provideEvidenceDao(database: AppDatabase): EvidenceDao = database.evidenceDao()

    @Provides
    fun provideGameProgressDao(database: AppDatabase): GameProgressDao = database.gameProgressDao()

    @Provides
    fun provideChapterVisitDao(database: AppDatabase): ChapterVisitDao = database.chapterVisitDao()
}
