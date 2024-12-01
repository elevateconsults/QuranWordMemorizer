package store.jaranation.quranwordmemorizer.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import store.jaranation.quranwordmemorizer.data.local.Word

class JsonParser(private val context: Context) {
    fun loadQuranicWords(): List<Word> {
        val jsonString = context.assets.open("quranic_words.json").bufferedReader().use { it.readText() }
        val type = object : TypeToken<Map<String, List<Word>>>() {}.type
        val data: Map<String, List<Word>> = Gson().fromJson(jsonString, type)
        return data["words"] ?: emptyList()
    }
} 