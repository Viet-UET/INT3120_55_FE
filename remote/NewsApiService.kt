package com.example.quicknews.data.remote


import com.example.quicknews.data.model.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NewsApiService {
    @GET("api/news/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = "us",
        @Query("pageSize") pageSize: Int = 10,
        @Query("page") page: Int = 1
    ): NewsResponse

    @GET("api/news/category/{category}")
    suspend fun getNewsByCategory(
        @Path("category") category: String,
        @Query("country") country: String = "us",
        @Query("pageSize") pageSize: Int = 10,
        @Query("page") page: Int = 1
    ): NewsResponse

    @GET("api/news/search")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("pageSize") pageSize: Int = 10,
        @Query("page") page: Int = 1,
        @Query("sortBy") sortBy: String = "publishedAt"
    ): NewsResponse
}