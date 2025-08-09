package store.jaranation.quranwordmemorizer.data.preferences

data class QuizSettings(
    val isNotificationEnabled: Boolean = true,
    val notificationFrequency: Int = 60, // minutes
    val quizSource: String = "both", // "quranic", "user", "both", or "selected"
    val startTime: Int = 8, // 24-hour format
    val endTime: Int = 22, // 24-hour format
    val selectedWordIds: Set<Int> = emptySet(), // For selected words to quiz on
    val quizDuration: Int = 7, // days to quiz on selected words
    val minInterval: Int = 10, // minimum minutes between notifications
    val maxDailyQuizzes: Int = 10, // maximum quizzes per day
    val isTestMode: Boolean = false, // Add this for testing
    val quizDifficulty: String = "BEGINNER"
) 