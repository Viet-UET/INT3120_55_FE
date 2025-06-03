package com.example.quicknews.ui.screens


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.quicknews.data.model.Article
import com.example.quicknews.ui.viewmodel.NewsViewModel
import kotlinx.coroutines.launch
import java.util.Locale




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryNewsScreen(
    category: String,
    newsViewModel: NewsViewModel = viewModel(),
    onArticleClick: (String) -> Unit,
    onNavigateToHome: () -> Unit // Thêm callback để điều hướng về Home từ drawer
) {
    val categoryNews by newsViewModel.categoryNews.collectAsState()
    val isLoading by newsViewModel.isLoading.collectAsState()
    val error by newsViewModel.error.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed) // Khai báo drawerState
    val scope = rememberCoroutineScope() // Khai báo scope

    val categories = listOf(
        "General", "Entertainment","Business" , "Health", "Science", "Sports", "Technology"
    )

    LaunchedEffect(category) {
        newsViewModel.fetchNewsByCategory(category)
    }

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Menu, contentDescription = null) },
                    label = { Text("Home Screen") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToHome()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Menu, contentDescription = null) },
                    label = { Text("Category News Screen") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

            }
        },
        drawerState = drawerState // Gán drawerState cho ModalNavigationDrawer
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("${category.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }} News") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() } // Mở drawer khi nhấn nút menu
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
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

                // Category Filter Chips
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    items(categories) { categoryName ->
                        FilterChip(
                            selected = categoryName.equals(category, ignoreCase = true),
                            onClick = { newsViewModel.fetchNewsByCategory(categoryName.lowercase(Locale.ROOT)) },
                            label = { Text(categoryName) }
                        )
                    }
                }


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
}