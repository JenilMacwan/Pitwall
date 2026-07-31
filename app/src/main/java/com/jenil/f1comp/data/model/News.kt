package com.jenil.f1comp.data.model


data class NewsResponse(
    val articles: List<News>
)
data class News(
    val title: String,
    val description: String,
    val link: String,
    val published: String,
    val image: String?,
    val source: String
)