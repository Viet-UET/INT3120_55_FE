package com.example.quicknews.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quicknews.ui.screens.AccountScreen
import com.example.quicknews.ui.screens.ArticleDetailScreen
import com.example.quicknews.ui.screens.CategoryNewsScreen
import com.example.quicknews.ui.screens.FavoritesScreen
import com.example.quicknews.ui.screens.HomeScreen
import com.example.quicknews.ui.screens.SearchScreen
import com.example.quicknews.ui.viewmodel.AuthViewModel

object Routes {
    const val FAVORITES = "favorites"
    const val HOME = "home"
    const val CATEGORY_NEWS = "category_news/{category}"
    const val SEARCH = "search"
    const val ARTICLE_DETAIL = "article_detail/{url}"
    const val ACCOUNT = "account"

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
    val authViewModel: AuthViewModel = viewModel() // tạo 1 lần duy nhất

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.ACCOUNT) {
            AccountScreen(
                authViewModel = authViewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.FAVORITES) {
            FavoritesScreen(
                authViewModel = authViewModel,
                onArticleClick = { url -> navController.navigate(Routes.createArticleDetailRoute(url)) },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Routes.HOME) {
            HomeScreen(
                authViewModel = authViewModel,
                onArticleClick = { url ->
                    navController.navigate(Routes.createArticleDetailRoute(url))
                },
                onSearchClick = {
                    navController.navigate(Routes.SEARCH)
                },
                onCategoryClick = { category ->
                    navController.navigate(Routes.createCategoryRoute(category))
                },
                onAccountClick = {
                    navController.navigate(Routes.ACCOUNT)
                },
                onFavoritesClick = {
                    navController.navigate(Routes.FAVORITES)
                }
            )
        }
        composable(Routes.CATEGORY_NEWS) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category")
            if (category != null) {
                CategoryNewsScreen(
                    authViewModel = authViewModel,
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
                authViewModel = authViewModel,
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
