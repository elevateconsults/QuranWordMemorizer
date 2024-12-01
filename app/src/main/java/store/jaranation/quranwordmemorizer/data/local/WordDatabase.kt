package store.jaranation.quranwordmemorizer.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import store.jaranation.quranwordmemorizer.util.JsonParser

@Database(entities = [Word::class], version = 2, exportSchema = true)
abstract class WordDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao

    companion object {
        @Volatile
        private var INSTANCE: WordDatabase? = null

        fun getDatabase(context: Context): WordDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WordDatabase::class.java,
                    "word_database"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        val jsonParser = JsonParser(context.applicationContext)
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                preloadDatabase(database.wordDao(), jsonParser)
                            }
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun preloadDatabase(wordDao: WordDao, jsonParser: JsonParser) {
            if (wordDao.getQuranicWordsCount() == 0) {
                val words = jsonParser.loadQuranicWords()
                wordDao.insertAll(words)
            }
        }
    }
} 