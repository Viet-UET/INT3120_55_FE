package com.example.quicknews.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quicknews.ui.viewmodel.NewsViewModel
import com.example.quicknews.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    newsViewModel: NewsViewModel = viewModel(),
    authViewModel: AuthViewModel,
    onArticleClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val favoriteArticles by newsViewModel.favoriteArticles.collectAsState()
    val isLoading by newsViewModel.isLoading.collectAsState()
    val error by newsViewModel.error.collectAsState()

    val userAccount by authViewModel.userAccount.collectAsState()
    val jwtToken = userAccount?.jwtToken

    // Khi Screen mở → tự động fetch Favorites nếu chưa có
    LaunchedEffect(jwtToken) {
        if (jwtToken != null) {
            newsViewModel.fetchFavorites(jwtToken)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorites") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: ${error}", color = MaterialTheme.colorScheme.error)
                    }
                }
                favoriteArticles.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No favorite articles yet.")
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(favoriteArticles) { article ->
                            ArticleCard(
                                article = article,
                                onClick = { article.url?.let { onArticleClick(it) } },
                                onToggleFavorite = {
                                    if (jwtToken != null) {
                                        newsViewModel.removeFavorite(article, jwtToken)
                                    }
                                },
                                isFavorite = true // Favorites Screen → tất cả đều là Favorite
                            )
                        }
                    }
                }
            }
        }
    }
}
