package store.jaranation.quranwordmemorizer.data.local

object QuranicWords {
    val words = listOf(
        Word(
            arabicWord = "كِتَاب",
            englishMeaning = "Book",
            transliteration = "Kitab",
            rootWord = "ك-ت-ب",
            context = "Al-Baqarah 2:2",
            source = "quranic"
        ),
        Word(
            arabicWord = "سَلَام",
            englishMeaning = "Peace",
            transliteration = "Salam",
            rootWord = "س-ل-م",
            context = "Ya-Sin 36:58",
            source = "quranic"
        ),
        Word(
            arabicWord = "رَحْمَة",
            englishMeaning = "Mercy",
            transliteration = "Rahmah",
            rootWord = "ر-ح-م",
            context = "Al-Fatihah 1:1",
            source = "quranic"
        ),
        // Add more words here...
        // You can add hundreds of words following this pattern
    )
} 