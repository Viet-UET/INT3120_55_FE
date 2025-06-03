package com.example.quicknews.ui.screens


import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.quicknews.data.model.Article
import com.example.quicknews.ui.viewmodel.NewsViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryNewsScreen(
    category: String,
    newsViewModel: NewsViewModel = viewModel(),
    onArticleClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val categoryNews by newsViewModel.categoryNews.collectAsState()
    val isLoading by newsViewModel.isLoading.collectAsState()
    val error by newsViewModel.error.collectAsState()

    LaunchedEffect(category) {
        newsViewModel.fetchNewsByCategory(category)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${category.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }} News") },
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
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${error}", color = MaterialTheme.colorScheme.error)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categoryNews) { article ->
                        ArticleCard(article = article, onClick = {
                            article.url?.let { onArticleClick(it) }
                        })
                    }
                }
            }
        }
    }
}