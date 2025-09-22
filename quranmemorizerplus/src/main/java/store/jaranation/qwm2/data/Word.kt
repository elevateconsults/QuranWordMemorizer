package store.jaranation.qwm2.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class WordSource { QURANIC, USER }

@Entity(tableName = "words")
data class Word(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val arabic: String,
    val transliteration: String,
    val meaning: String,
    val source: WordSource = WordSource.USER,
    val createdAt: Long = System.currentTimeMillis()
)
