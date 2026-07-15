package noirdetective.game.data.local.dao

import androidx.room.*
import noirdetective.game.data.local.entity.GameProgress

@Dao
interface GameProgressDao {
    @Query("SELECT * FROM game_progress WHERE storyId = :storyId")
    suspend fun getProgress(storyId: String): GameProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: GameProgress)
}
