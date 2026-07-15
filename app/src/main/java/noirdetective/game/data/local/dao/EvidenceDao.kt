package noirdetective.game.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import noirdetective.game.data.local.entity.EvidenceItem

@Dao
interface EvidenceDao {
    @Query("SELECT * FROM evidence_items WHERE isCollected = 1")
    fun getAllEvidence(): Flow<List<EvidenceItem>>

    @Query("SELECT * FROM evidence_items WHERE id = :id")
    suspend fun getEvidenceById(id: String): EvidenceItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveEvidence(evidence: EvidenceItem)

    @Update
    suspend fun updateEvidence(evidence: EvidenceItem)
}
