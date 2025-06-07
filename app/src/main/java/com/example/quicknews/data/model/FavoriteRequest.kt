package com.example.quicknews.data.model

import com.example.quicknews.data.model.Article

data class FavoriteRequest(
    val articleUrl: String,
    val articleData: Article
)
