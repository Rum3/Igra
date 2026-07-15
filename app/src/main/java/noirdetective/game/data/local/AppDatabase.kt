package noirdetective.game.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import noirdetective.game.data.local.dao.*
import noirdetective.game.data.local.entity.*

@Database(
    entities = [
        NotebookEntry::class,
        EvidenceItem::class,
        GameProgress::class,
        DecisionLog::class,
        ChapterVisit::class
    ],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notebookDao(): NotebookDao
    abstract fun evidenceDao(): EvidenceDao
    abstract fun gameProgressDao(): GameProgressDao
    abstract fun chapterVisitDao(): ChapterVisitDao
}
