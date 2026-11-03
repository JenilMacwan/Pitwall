package com.jenil.f1comp.data.local

import android.content.Context
import com.jenil.f1comp.data.local.entity.NewsEntity
import org.json.JSONArray
import org.json.JSONObject

/** Small device-local store for saved articles, independent of the rolling news feed cache. */
class SavedNewsStore(context: Context) {
    private val preferences = context.applicationContext
        .getSharedPreferences("saved_news", Context.MODE_PRIVATE)

    fun load(): List<NewsEntity> = runCatching {
        val array = JSONArray(preferences.getString(KEY_ARTICLES, "[]"))
        buildList {
            for (index in 0 until array.length()) {
                val item = array.optJSONObject(index) ?: continue
                val link = item.optString("link").takeIf(String::isNotBlank) ?: continue
                add(
                    NewsEntity(
                        link = link,
                        title = item.optString("title"),
                        description = item.optString("description"),
                        published = item.optString("published"),
                        image = item.optString("image").takeIf { it.isNotBlank() && it != "null" },
                        source = item.optString("source"),
                        cachedAt = item.optLong("savedAt")
                    )
                )
            }
        }
    }.getOrDefault(emptyList())

    fun toggle(article: NewsEntity): List<NewsEntity> {
        val current = load().toMutableList()
        val existingIndex = current.indexOfFirst { it.link == article.link }
        if (existingIndex >= 0) current.removeAt(existingIndex)
        else current.add(0, article.copy(cachedAt = System.currentTimeMillis()))
        persist(current)
        return current
    }

    private fun persist(articles: List<NewsEntity>) {
        val array = JSONArray()
        articles.forEach { article ->
            array.put(
                JSONObject()
                    .put("link", article.link)
                    .put("title", article.title)
                    .put("description", article.description)
                    .put("published", article.published)
                    .put("image", article.image)
                    .put("source", article.source)
                    .put("savedAt", article.cachedAt)
            )
        }
        preferences.edit().putString(KEY_ARTICLES, array.toString()).apply()
    }

    private companion object {
        const val KEY_ARTICLES = "articles"
    }
}
