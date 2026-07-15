package noirdetective.game.data.repository

import kotlinx.coroutines.flow.Flow
import noirdetective.game.data.local.dao.NotebookDao
import noirdetective.game.data.local.entity.NotebookEntry

class NotebookRepository(private val notebookDao: NotebookDao) {
    fun getNotesForStory(storyId: String): Flow<List<NotebookEntry>> = 
        notebookDao.getNotesForStory(storyId)

    suspend fun insertNote(note: NotebookEntry) = notebookDao.insertNote(note)
    
    suspend fun deleteNote(note: NotebookEntry) = notebookDao.deleteNote(note)
}
