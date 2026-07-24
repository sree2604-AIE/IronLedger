package com.ironledger.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ironledger.app.core.design.IronColors
import com.ironledger.app.core.design.IronExpandableFab
import com.ironledger.app.core.design.IronLedgerTheme
import com.ironledger.app.core.design.PremiumBackground
import com.ironledger.app.core.navigation.Routes
import com.ironledger.app.feature.activity.ActivityScreen
import com.ironledger.app.feature.ai.AiScreen
import com.ironledger.app.feature.analytics.AnalyticsScreen
import com.ironledger.app.feature.budget.BudgetsScreen
import com.ironledger.app.feature.emi.EmiScreen
import com.ironledger.app.feature.home.HomeScreen
import com.ironledger.app.feature.placeholder.PlaceholderScreen
import com.ironledger.app.feature.receipt.ScanReceiptScreen
import com.ironledger.app.feature.reminder.ReminderScreen
import com.ironledger.app.feature.settings.SettingsScreen
import com.ironledger.app.feature.splash.SplashScreen
import com.ironledger.app.feature.subscription.SubscriptionScreen
import com.ironledger.app.feature.transaction.AddTransactionScreen
import com.ironledger.app.feature.trip.AddTripScreen
import com.ironledger.app.feature.trip.TripDetailScreen
import com.ironledger.app.feature.trip.TripListScreen
import com.ironledger.app.feature.vault.AddAccountScreen
import com.ironledger.app.feature.vault.VaultScreen
import com.ironledger.app.feature.vehicle.AddVehicleScreen
import com.ironledger.app.feature.vehicle.VehicleScreen
import com.ironledger.app.feature.wallet.SharedWalletScreen

@Composable
fun IronLedgerRoot(appViewModel: AppViewModel = hiltViewModel()) {
    val preferences by appViewModel.preferences.collectAsStateWithLifecycle()
    IronLedgerTheme(
        themeMode = preferences.themeMode,
        accentColor = preferences.accentColor
    ) {
        IronLedgerApp(
            hideBalances = preferences.hideBalances,
            onToggleHidden = appViewModel::setHideBalances,
            userName = preferences.userName
        )
    }
}

@Composable
private fun IronLedgerApp(
    hideBalances: Boolean,
    onToggleHidden: (Boolean) -> Unit,
    userName: String = ""
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route
    val mainRoutes = bottomItems.map { it.route }.toSet()
    val showChrome = currentRoute in mainRoutes

    PremiumBackground {
        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            bottomBar = {
                AnimatedVisibility(showChrome) {
                    IronBottomBar(navController = navController)
                }
            },
            floatingActionButton = {
                AnimatedVisibility(showChrome) {
                    IronExpandableFab(
                        onAddExpense = { navController.navigate(Routes.addTransaction("expense")) },
                        onAddIncome = { navController.navigate(Routes.addTransaction("income")) },
                        onVoiceEntry = { navController.navigate(Routes.placeholder("Voice Entry")) },
                        onReminder = { navController.navigate(Routes.REMINDERS) },
                        onTripExpense = { navController.navigate(Routes.TRIPS) }
                    )
                }
            }
        ) { padding ->
            AppNavHost(
                navController = navController,
                padding = if (showChrome) padding else PaddingValues(),
                hideBalances = hideBalances,
                onToggleHidden = onToggleHidden,
                userName = userName
            )
        }
    }
}

@Composable
private fun AppNavHost(
    navController: NavHostController,
    padding: PaddingValues,
    hideBalances: Boolean,
    onToggleHidden: (Boolean) -> Unit,
    userName: String = ""
) {
    Box(Modifier.fillMaxSize().padding(padding)) {
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Routes.SPLASH) {
                SplashScreen(onFinished = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                })
            }
            composable(Routes.HOME) {
                HomeScreen(
                    hideBalances = hideBalances,
                    onToggleHidden = onToggleHidden,
                    onSeeActivity = { navController.navigateMain(Routes.ACTIVITY) },
                    onSeeAnalytics = { navController.navigateMain(Routes.ANALYTICS) },
                    onAddExpense = { navController.navigate(Routes.addTransaction("expense")) },
                    onAddIncome = { navController.navigate(Routes.addTransaction("income")) },
                    onVoiceEntry = { navController.navigate(Routes.placeholder("Voice Entry")) },
                    onScanReceipt = { navController.navigate(Routes.SCAN_RECEIPT) },
                    userName = userName
                )
            }
            composable(Routes.ACTIVITY) { ActivityScreen(hideBalances = hideBalances) }
            composable(Routes.ANALYTICS) { AnalyticsScreen(hideBalances = hideBalances, onBack = { navController.popBackStack() }) }
            composable(Routes.VAULT) {
                VaultScreen(
                    hideBalances = hideBalances,
                    onNavigate = { route -> navController.navigate(route) }
                )
            }
            composable(Routes.AI) { AiScreen(hideBalances = hideBalances) }
            composable(Routes.VEHICLES) {
                VehicleScreen(
                    hideBalances = hideBalances,
                    onBack = { navController.popBackStack() },
                    onAddVehicle = { navController.navigate(Routes.ADD_VEHICLE) }
                )
            }
            composable(Routes.ADD_VEHICLE) {
                AddVehicleScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }
            composable(Routes.TRIPS) {
                TripListScreen(
                    hideBalances = hideBalances,
                    onBack = { navController.popBackStack() },
                    onAddTrip = { navController.navigate(Routes.ADD_TRIP) },
                    onTripDetail = { tripId -> navController.navigate(Routes.tripDetail(tripId)) }
                )
            }
            composable(
                route = Routes.TRIP_DETAIL,
                arguments = listOf(navArgument("tripId") { type = NavType.StringType })
            ) { entry ->
                TripDetailScreen(
                    tripId = entry.arguments?.getString("tripId") ?: "",
                    hideBalances = hideBalances,
                    onBack = { navController.popBackStack() },
                    onAddExpense = { navController.navigate(Routes.addTransaction("expense")) },
                    onSettleUp = { navController.navigate(Routes.placeholder("Settle Up")) }
                )
            }
            composable(Routes.ADD_TRIP) {
                AddTripScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }
            composable(Routes.ADD_ACCOUNT) {
                AddAccountScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }
            composable(Routes.SUBSCRIPTIONS) {
                SubscriptionScreen(
                    hideBalances = hideBalances,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.EMIS) {
                EmiScreen(
                    hideBalances = hideBalances,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.BUDGETS) {
                BudgetsScreen(
                    hideBalances = hideBalances,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    hideBalances = hideBalances,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.SCAN_RECEIPT) {
                ScanReceiptScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.SHARED_WALLETS) {
                SharedWalletScreen(
                    hideBalances = hideBalances,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.REMINDERS) {
                ReminderScreen(
                    hideBalances = hideBalances,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Routes.ADD_TRANSACTION,
                arguments = listOf(navArgument("type") { type = NavType.StringType })
            ) {
                AddTransactionScreen(
                    onClose = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }
            composable(
                route = Routes.PLACEHOLDER,
                arguments = listOf(navArgument("title") { type = NavType.StringType })
            ) { entry ->
                PlaceholderScreen(
                    title = entry.arguments?.getString("title")?.replace("_", " ") ?: "Module",
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun IronBottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    NavigationBar(
        containerColor = IronColors.Black.copy(alpha = 0.96f),
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        bottomItems.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
            NavigationBarItem(
                selected = selected,
                onClick = { navController.navigateMain(item.route) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

private fun NavHostController.navigateMain(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

private data class BottomItem(val route: String, val label: String, val icon: ImageVector)

private val bottomItems = listOf(
    BottomItem(Routes.HOME, "Home", Icons.Rounded.Home),
    BottomItem(Routes.ACTIVITY, "Transactions", Icons.Rounded.ReceiptLong),
    BottomItem(Routes.ANALYTICS, "Analytics", Icons.Rounded.Analytics),
    BottomItem(Routes.VAULT, "Vault", Icons.Rounded.AccountBalanceWallet),
    BottomItem(Routes.AI, "AI", Icons.Rounded.AutoAwesome)
)
