package noirdetective.game.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import noirdetective.game.data.local.entity.ChapterVisit

@Dao
interface ChapterVisitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordVisit(visit: ChapterVisit)

    @Query("SELECT COUNT(*) FROM chapter_visits WHERE chapterId IN (:ids)")
    suspend fun getVisitedCount(ids: List<String>): Int

    @Query("SELECT EXISTS(SELECT 1 FROM chapter_visits WHERE chapterId = :id)")
    suspend fun hasVisited(id: String): Boolean

    @Query("SELECT chapterId FROM chapter_visits")
    suspend fun getAllVisitedIds(): List<String>
}
