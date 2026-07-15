package noirdetective.game.data.repository

import kotlinx.coroutines.flow.Flow
import noirdetective.game.data.local.dao.EvidenceDao
import noirdetective.game.data.local.entity.EvidenceItem

class EvidenceRepository(private val evidenceDao: EvidenceDao) {
    val allEvidence: Flow<List<EvidenceItem>> = evidenceDao.getAllEvidence()

    suspend fun getEvidenceById(id: String): EvidenceItem? = evidenceDao.getEvidenceById(id)

    suspend fun updateEvidence(evidence: EvidenceItem) = evidenceDao.saveEvidence(evidence)
}
