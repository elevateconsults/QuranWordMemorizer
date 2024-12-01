package store.jaranation.quranwordmemorizer.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word_bank")
data class Word(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "arabic_word") val arabicWord: String,
    @ColumnInfo(name = "english_meaning") val englishMeaning: String,
    @ColumnInfo(name = "transliteration") val transliteration: String? = null,
    @ColumnInfo(name = "root_word") val rootWord: String? = null,
    @ColumnInfo(name = "context") val context: String? = null,
    @ColumnInfo(name = "source") val source: String = "user"
) 