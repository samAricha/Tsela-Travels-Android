package com.teka.tsela.core.navigation

const val ROOT_GRAPH_ROUTE = "root_graph_route"
const val AUTH_GRAPH_ROUTE = "auth_graph_route"
const val MAIN_GRAPH_ROUTE = "main_graph_route"
const val To_MAIN_GRAPH_ROUTE = "to_main_graph_route"


sealed class AppScreens(val route: String, val title: String? = null) {

    //auth screens
    object LoginScreen : AppScreens(route = "login_screen")
    object CollectionsListScreen : AppScreens(route = "collections_list_screen")
    object CollectionsFormScreen : AppScreens(route = "collections_form_screen")

    //main screens
    object HomeScreen : AppScreens(route = "home_screen")
    object DestinationsScreen : AppScreens(route = "destinations_screen")
    object DestinationsDetailsScreen : AppScreens(route = "destinations_details_screen")
    object ExcursionsScreen : AppScreens(route = "excursions_screen")
    object ExcursionsDetailsScreen : AppScreens(route = "excursions_details_screen")
    object HotelsListScreen : AppScreens(route = "hotels_screen")
    object HotelDetailsScreen : AppScreens(route = "hotel_details_screen")
    object ChatScreen : AppScreens(route = "chat_screen")

}