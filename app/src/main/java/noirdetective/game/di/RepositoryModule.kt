package noirdetective.game.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import noirdetective.game.data.content.ContentLoader
import noirdetective.game.data.local.dao.ChapterVisitDao
import noirdetective.game.data.local.dao.EvidenceDao
import noirdetective.game.data.local.dao.GameProgressDao
import noirdetective.game.data.local.dao.NotebookDao
import noirdetective.game.data.repository.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideContentLoader(@ApplicationContext context: Context): ContentLoader {
        return ContentLoader(context)
    }

    @Provides
    @Singleton
    fun provideStoryRepository(contentLoader: ContentLoader): StoryRepository {
        return StoryRepository(contentLoader)
    }

    @Provides
    @Singleton
    fun provideNotebookRepository(notebookDao: NotebookDao): NotebookRepository {
        return NotebookRepository(notebookDao)
    }

    @Provides
    @Singleton
    fun provideEvidenceRepository(evidenceDao: EvidenceDao): EvidenceRepository {
        return EvidenceRepository(evidenceDao)
    }

    @Provides
    @Singleton
    fun provideGameStateRepository(
        gameProgressDao: GameProgressDao,
        chapterVisitDao: ChapterVisitDao
    ): GameStateRepository {
        return GameStateRepository(gameProgressDao, chapterVisitDao)
    }
}
