package com.example.quicknews.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quicknews.data.model.Article
import com.example.quicknews.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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
}