package store.jaranation.quranwordmemorizer.util

import store.jaranation.quranwordmemorizer.data.local.Word

// Data classes to match GitHub JSON structure
data class QuranData(
    val id: Int,
    val name: String,
    val transliteration: String,
    val translation: String,
    val type: String,
    val total_verses: Int,
    val verses: List<Verse>
)

data class Verse(
    val id: Int,
    val text: String,
    val translation: String
)

class QuranJsonConverter {
    fun convertToWords(quranData: QuranData): List<Word> {
        val words = mutableListOf<Word>()
        
        quranData.verses.forEach { verse ->
            // Split Arabic text into words
            val arabicWords = verse.text.split(" ")
            // Split English translation into words
            val englishWords = verse.translation.split(" ")
            
            // Create Word entities
            arabicWords.forEachIndexed { index, arabicWord ->
                val englishMeaning = if (index < englishWords.size) englishWords[index] else ""
                words.add(
                    Word(
                        arabicWord = arabicWord,
                        englishMeaning = englishMeaning,
                        transliteration = null, // We'll need a separate transliteration mapping
                        rootWord = null, // Root word would need to be provided separately
                        context = "${quranData.name} ${verse.id}:${index + 1}",
                        source = "quranic"
                    )
                )
            }
        }
        
        return words
    }
} 