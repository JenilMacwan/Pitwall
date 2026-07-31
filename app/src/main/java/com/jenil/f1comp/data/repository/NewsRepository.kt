package com.jenil.f1comp.data.repository

import com.jenil.f1comp.data.local.dao.NewsDao
import com.jenil.f1comp.data.local.entity.NewsEntity
import com.jenil.f1comp.data.remote.F1ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class NewsRepository @Inject constructor(
    private val apiService: F1ApiService,
    private val newsDao: NewsDao
) {

    fun getCachedNews(): Flow<List<NewsEntity>> {
        return newsDao.getCachedNews()
    }

    suspend fun refreshNews() {
        val response = apiService.getF1News()
        val newsEntities = response.articles.map { networkModel ->
            NewsEntity(
                link = networkModel.link,
                title =networkModel.title,
                description = networkModel.description,
                published = networkModel.published,
                image = networkModel.image,
                source = networkModel.source,
                cachedAt = System.currentTimeMillis(),
            )
        }
        newsDao.refreshNews(newsEntities)
    }

    suspend fun insertNews(news: List<NewsEntity>) {
        newsDao.insertNews(news)
    }

    suspend fun deleteOldNews(threshold: Long) {
        newsDao.deleteOldNews(threshold)
    }

    suspend fun deleteAllNews() {
        newsDao.deleteAllNews()
    }
}