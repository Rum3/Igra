package noirdetective.game.data.repository

import noirdetective.game.data.local.dao.ChapterVisitDao
import noirdetective.game.data.local.dao.GameProgressDao
import noirdetective.game.data.local.entity.ChapterVisit
import noirdetective.game.data.local.entity.GameProgress

class GameStateRepository(
    private val gameProgressDao: GameProgressDao,
    private val chapterVisitDao: ChapterVisitDao
) {
    suspend fun getProgress(storyId: String): GameProgress? = gameProgressDao.getProgress(storyId)

    suspend fun saveProgress(progress: GameProgress) = gameProgressDao.saveProgress(progress)

    suspend fun recordChapterVisit(chapterId: String) {
        chapterVisitDao.recordVisit(ChapterVisit(chapterId, System.currentTimeMillis()))
    }

    suspend fun hasVisitedAll(chapterIds: List<String>): Boolean {
        return chapterVisitDao.getVisitedCount(chapterIds) >= chapterIds.size
    }
}
