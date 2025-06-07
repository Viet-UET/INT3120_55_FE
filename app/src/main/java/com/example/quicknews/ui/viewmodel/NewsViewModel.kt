package com.example.quicknews.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quicknews.data.model.Article
import com.example.quicknews.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.quicknews.ui.viewmodel.AuthViewModel
import com.example.quicknews.data.model.FavoriteRequest
import java.net.URLEncoder

class NewsViewModel : ViewModel() {

    private val _topHeadlines = MutableStateFlow<List<Article>>(emptyList())
    val topHeadlines: StateFlow<List<Article>> = _topHeadlines

    private val _categoryNews = MutableStateFlow<List<Article>>(emptyList())
    val categoryNews: StateFlow<List<Article>> = _categoryNews

    private val _searchResults = MutableStateFlow<List<Article>>(emptyList())
    val searchResults: StateFlow<List<Article>> = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // ---- Thêm các biến state Favorites ----
    private val _favoriteArticles = MutableStateFlow<List<Article>>(emptyList())
    val favoriteArticles: StateFlow<List<Article>> = _favoriteArticles

    init {
        fetchTopHeadlines()
    }

    fun fetchTopHeadlines(country: String = "us", pageSize: Int = 10, page: Int = 1) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = RetrofitClient.newsApiService.getTopHeadlines(country, pageSize, page)
                _topHeadlines.value = response.articles
            } catch (e: Exception) {
                _error.value = "Failed to fetch top headlines: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchNewsByCategory(category: String, country: String = "us", pageSize: Int = 10, page: Int = 1) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = RetrofitClient.newsApiService.getNewsByCategory(category, country, pageSize, page)
                _categoryNews.value = response.articles
            } catch (e: Exception) {
                _error.value = "Failed to fetch news for category '$category': ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchNews(query: String, pageSize: Int = 10, page: Int = 1, sortBy: String = "publishedAt") {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = RetrofitClient.newsApiService.searchNews(query, pageSize, page, sortBy)
                _searchResults.value = response.articles
            } catch (e: Exception) {
                _error.value = "Failed to search news for '$query': ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }


    // Lấy danh sách Favorites từ Server
    fun fetchFavorites(jwtToken: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val favorites = RetrofitClient.newsApiService.getFavorites("Bearer $jwtToken")
                _favoriteArticles.value = favorites
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addFavorite(article: Article, jwtToken: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.newsApiService.addFavorite(
                    "Bearer $jwtToken",
                    FavoriteRequest(article.url ?: return@launch, article)
                )
                println("Add Favorite response code: ${response.code()}")
                fetchFavorites(jwtToken)
            } catch (e: Exception) {
                println("Error adding favorite: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun removeFavorite(article: Article, jwtToken: String) {
        viewModelScope.launch {
            try {
                val encodedUrl = URLEncoder.encode(article.url ?: return@launch, "UTF-8")
                val response = RetrofitClient.newsApiService.removeFavorite("Bearer $jwtToken", encodedUrl)
                println("Remove Favorite response code: ${response.code()}")
                fetchFavorites(jwtToken)
            } catch (e: Exception) {
                println("Error removing favorite: ${e.message}")
                e.printStackTrace()
            }
        }
    }


    // Kiểm tra 1 bài có đang là Favorite không
    fun isFavorite(article: Article): Boolean {
        return _favoriteArticles.value.any { it.url == article.url }
    }
}