package noirdetective.game.data.content

import android.content.Context
import kotlinx.serialization.json.Json
import noirdetective.game.domain.model.Chapter
import java.io.IOException

class ContentLoader(private val context: Context) {
    private val json = Json { 
        ignoreUnknownKeys = true 
        coerceInputValues = true
    }

    fun loadChapter(chapterId: String): Chapter? {
        val fileName = when {
            chapterId.startsWith("chapter_") -> {
                // Extract chapter folder (e.g., chapter_01_1 -> chapter_01)
                val parts = chapterId.split("_")
                if (parts.size >= 2) {
                    val folder = "${parts[0]}_${parts[1]}"
                    "chapters/main_story/$folder/$chapterId.json"
                } else {
                    "chapters/main_story/$chapterId.json"
                }
            }
            chapterId.startsWith("side_") -> "chapters/side_stories/$chapterId.json"
            else -> return null
        }

        return try {
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            json.decodeFromString<Chapter>(jsonString)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
