package noirdetective.game.data.repository

import noirdetective.game.data.content.ContentLoader
import noirdetective.game.domain.model.Chapter

class StoryRepository(private val contentLoader: ContentLoader) {
    suspend fun getChapter(chapterId: String): Chapter? {
        return contentLoader.loadChapter(chapterId)
    }
}
