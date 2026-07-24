package com.ironledger.app.core.navigation

object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val ACTIVITY = "activity"
    const val ANALYTICS = "analytics"
    const val VAULT = "vault"
    const val AI = "ai"
    const val VEHICLES = "vehicles"
    const val ADD_VEHICLE = "add_vehicle"
    const val TRIPS = "trips"
    const val ADD_TRIP = "add_trip"
    const val TRIP_DETAIL = "trip_detail/{tripId}"
    const val BUDGETS = "budgets"
    const val SETTINGS = "settings"
    const val SCAN_RECEIPT = "scan_receipt"
    const val SUBSCRIPTIONS = "subscriptions"
    const val EMIS = "emis"
    const val SHARED_WALLETS = "shared_wallets"
    const val REMINDERS = "reminders"
    const val ADD_ACCOUNT = "add_account"
    const val ADD_TRANSACTION = "add_transaction/{type}"
    const val PLACEHOLDER = "placeholder/{title}"

    fun addTransaction(type: String) = "add_transaction/$type"
    fun tripDetail(tripId: String) = "trip_detail/$tripId"
    fun placeholder(title: String) = "placeholder/${title.replace(" ", "_")}"
}
