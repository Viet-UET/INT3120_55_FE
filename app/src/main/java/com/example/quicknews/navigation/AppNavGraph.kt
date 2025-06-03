package com.example.quicknews.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quicknews.ui.screens.ArticleDetailScreen
import com.example.quicknews.ui.screens.CategoryNewsScreen
import com.example.quicknews.ui.screens.HomeScreen
import com.example.quicknews.ui.screens.SearchScreen

object Routes {
    const val HOME = "home"
    const val CATEGORY_NEWS = "category_news/{category}"
    const val SEARCH = "search"
    const val ARTICLE_DETAIL = "article_detail/{url}"

    fun createCategoryRoute(category: String) = "category_news/$category"
    fun createArticleDetailRoute(url: String) = "article_detail/${url.encodeUrl()}" // Encode URL
}

// Extension function to encode URL for navigation
fun String.encodeUrl(): String {
    return java.net.URLEncoder.encode(this, "UTF-8")
}

fun String.decodeUrl(): String {
    return java.net.URLDecoder.decode(this, "UTF-8")
}

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onArticleClick = { url ->
                    navController.navigate(Routes.createArticleDetailRoute(url))
                },
                onSearchClick = {
                    navController.navigate(Routes.SEARCH)
                },
                onCategoryClick = { category -> // Đã thêm lại tham số này
                    navController.navigate(Routes.createCategoryRoute(category))
                },
            )
        }
        composable(Routes.CATEGORY_NEWS) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category")
            if (category != null) {
                CategoryNewsScreen(
                    category = category,
                    onArticleClick = { url ->
                        navController.navigate(Routes.createArticleDetailRoute(url))
                    },
                    onNavigateToHome = {
                        navController.popBackStack(Routes.HOME, inclusive = false)
                    }
                )
            }
        }
        composable(Routes.SEARCH) {
            SearchScreen(
                onArticleClick = { url ->
                    navController.navigate(Routes.createArticleDetailRoute(url))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.ARTICLE_DETAIL) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("url")?.decodeUrl()
            if (url != null) {
                ArticleDetailScreen(
                    articleUrl = url,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}