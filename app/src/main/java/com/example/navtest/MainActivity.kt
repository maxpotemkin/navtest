package com.example.navtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.navtest.ui.theme.NavTestTheme

enum class RootStack {
    RootTabs, MapScreen, ModalScreen, AuthStack
}

enum class AuthStack {
    PhoneEnterScreen, SmsEnterScreen, PhoneConfirmationScreen, SmsConfirmationScreen
}

enum class Tabs {
    HomeStack, CatalogStack, ProfileStack
}

enum class HomeStack {
    HomeScreen, StoresScreen, HomeProductScreen, EmptyScreen,
}

enum class CatalogStack {
    CatalogScreen, TaxonScreen, ProductsScreen, ProductScreen
}

enum class ProfileStack {
    ProfileScreen, HistoryScreen, OrderScreen, BonusScreen, OrderHistoryStack
}

enum class OrderHistoryStack {
    OrdersListScreen, OrderDetailsScreen
}


fun NavGraphBuilder.homeStack(navController: NavController, openModalStack: () -> Unit) {
    navigation(startDestination = HomeStack.StoresScreen.name, route = Tabs.HomeStack.name) {
        composable(HomeStack.StoresScreen.name) {
            Screen(screenName = HomeStack.StoresScreen.name, onPress = {
                navController.navigate(HomeStack.HomeScreen.name)
            }, onPress2Text = "nav with reset", onPress2 = {
                navController.navigate(HomeStack.HomeScreen.name) {
                    popUpTo(HomeStack.StoresScreen.name) {
                        inclusive = true
                    }
                }
            })
        }
        composable(HomeStack.HomeScreen.name) {
            Screen(screenName = HomeStack.HomeScreen.name, onPress = {
                navController.navigate(HomeStack.HomeProductScreen.name)
            }, onPress2Text = "open modal", onPress2 = openModalStack)
        }
        composable(HomeStack.HomeProductScreen.name) {
            Screen(screenName = HomeStack.HomeProductScreen.name, onPress = { })
        }
    }
}

fun NavGraphBuilder.catalogStack(navController: NavController) {
    navigation(startDestination = CatalogStack.CatalogScreen.name, route = Tabs.CatalogStack.name) {
        composable(CatalogStack.CatalogScreen.name) {
            Screen(screenName = CatalogStack.CatalogScreen.name, onPress = {
                navController.navigate(CatalogStack.TaxonScreen.name)
            })
        }
        composable(CatalogStack.TaxonScreen.name) {
            Screen(screenName = CatalogStack.TaxonScreen.name, onPress = {
                navController.navigate(CatalogStack.ProductsScreen.name)
            })
        }
        composable(CatalogStack.ProductsScreen.name) {
            Screen(screenName = CatalogStack.ProductsScreen.name, onPress = {
                navController.navigate(CatalogStack.ProductScreen.name)
            })
        }
        composable(CatalogStack.ProductScreen.name) {
            Screen(screenName = CatalogStack.ProductScreen.name, onPress = { })
        }
    }
}

fun NavGraphBuilder.orderHistoryStack(navController: NavController, close: () -> Unit) {
    navigation(
        startDestination = OrderHistoryStack.OrdersListScreen.name,
        route = ProfileStack.OrderHistoryStack.name
    ) {
        composable(OrderHistoryStack.OrdersListScreen.name) {
            Screen(screenName = OrderHistoryStack.OrdersListScreen.name, onPress = {
                navController.navigate(OrderHistoryStack.OrderDetailsScreen.name)
            })
        }
        composable(OrderHistoryStack.OrderDetailsScreen.name) {
            Screen(screenName = OrderHistoryStack.OrderDetailsScreen.name, onPress = {
            }, onPress2Text = "close stack", onPress2 = close)
        }

    }
}

fun NavGraphBuilder.profileStack(navController: NavController) {
    navigation(startDestination = ProfileStack.ProfileScreen.name, route = Tabs.ProfileStack.name) {
        composable(ProfileStack.ProfileScreen.name) {
            Screen(screenName = ProfileStack.ProfileScreen.name, onPress = {
                navController.navigate(ProfileStack.HistoryScreen.name)
            })
        }
        composable(ProfileStack.HistoryScreen.name) {
            Screen(screenName = ProfileStack.HistoryScreen.name, onPress = {
                navController.navigate(ProfileStack.OrderScreen.name)
            }, onPress2Text = "open orderHisstory stack", onPress2 = {
                navController.navigate(ProfileStack.OrderHistoryStack.name)
            })
        }
        composable(ProfileStack.OrderScreen.name) {
            Screen(screenName = ProfileStack.OrderScreen.name, onPress = { })
        }
        orderHistoryStack(navController, close = {
            navController.popBackStack(
                route = ProfileStack.OrderHistoryStack.name,
                inclusive = true
            )
//            navController.popBackStack(route = ProfileStack.HistoryScreen.name, inclusive = true)
        })
    }
}

fun NavGraphBuilder.rootTabs(navController: NavController, openModalStack: () -> Unit) {
    navigation(startDestination = Tabs.HomeStack.name, route = RootStack.RootTabs.name) {
        homeStack(navController, openModalStack = {navController.navigate(RootStack.AuthStack.name)})
        catalogStack(navController)
        profileStack(navController)
        authStack(navController, close = {navController.popBackStack(route = RootStack.AuthStack.name, inclusive = false)})

    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun NavGraphBuilder.authStack(navController: NavController, close: () -> Unit) {
    navigation(
        startDestination = AuthStack.PhoneEnterScreen.name,
        route = RootStack.AuthStack.name
    ) {
        dialog(AuthStack.PhoneEnterScreen.name,
            dialogProperties = DialogProperties(usePlatformDefaultWidth = false)) {
            Screen(screenName = AuthStack.PhoneEnterScreen.name, onPress = {
                navController.navigate(AuthStack.PhoneConfirmationScreen.name)
            })
        }
        dialog(AuthStack.PhoneConfirmationScreen.name, dialogProperties = DialogProperties(usePlatformDefaultWidth = false)) {
            Screen(screenName = AuthStack.PhoneConfirmationScreen.name, onPress = { },
                onPress2 = close, onPress2Text = "close auth modal"
            )
        }

    }
}

fun NavGraphBuilder.rootStack(navController: NavController) {
    navigation(startDestination = RootStack.MapScreen.name, route = "RootStack") {
        composable(RootStack.MapScreen.name) {
            Screen(screenName = RootStack.MapScreen.name, onPress = {
                navController.navigate(RootStack.RootTabs.name) {
                    popUpTo(RootStack.MapScreen.name) {
                        inclusive = true
                    }
                }
            }, onPress2 = {
                navController.navigate(RootStack.ModalScreen.name)
            }, onPress2Text = "open modal")
        }
        composable(RootStack.ModalScreen.name) {
            Screen(screenName = RootStack.ModalScreen.name, onPress = {
            })
        }
        composable(RootStack.RootTabs.name) {
            RootTabsScreen(
                openModalStack = { navController.navigate(RootStack.AuthStack.name) },
                navController = navController,
                navGraphBuilder = this
            )
        }
        authStack(navController, close = {
            navController.popBackStack(route = RootStack.AuthStack.name, inclusive = false)
        })
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavTestTheme {
                NavHost(
                    navController = navController,
                    startDestination = "RootStack"
                ) {
                    rootStack(navController)
                }
            }
        }
    }
}


@Composable
fun RootTabsScreen(openModalStack: () -> Unit, navController: NavController, navGraphBuilder: NavGraphBuilder) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            val stacks = Tabs.values().toList()
            BottomAppBar(backgroundColor = Color.Red) {

                val navBackStackEntry = navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry.value?.destination

                stacks.forEach { screen ->
                    BottomNavigationItem(
                        selected = currentDestination?.hierarchy?.any { it.route == screen.name } == true,
                        onClick = {
                            navController.navigate(screen.name) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {},
                        label = { Text(text = screen.toString()) })
                }
            }
        }
    ) {
//        navGraphBuilder.rootTabs(navController, openModalStack = openModalStack)
        NavHost(
            navController = navController,
            startDestination = RootStack.RootTabs.name
        ) {
            rootTabs(navController, openModalStack = openModalStack)
        }
    }
}

@Composable
fun Screen(
    screenName: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Cyan,
    onPress: () -> Unit,
    onPress2Text: String = "",
    onPress2: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .background(color)
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = screenName)
        Button(onClick = onPress) {
            Text(text = "Press Me pls")
        }
        Button(onClick = onPress2) {
            Text(text = onPress2Text)
        }
    }
}

