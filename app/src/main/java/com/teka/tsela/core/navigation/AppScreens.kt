package com.teka.tsela.core.navigation

const val ROOT_GRAPH_ROUTE = "root_graph_route"
const val AUTH_GRAPH_ROUTE = "auth_graph_route"
const val MAIN_GRAPH_ROUTE = "main_graph_route"
const val To_MAIN_GRAPH_ROUTE = "to_main_graph_route"


sealed class AppScreens(val route: String, val title: String? = null) {
    object CollectionsListScreen : AppScreens(route = "collections_list_screen")
    object CollectionsFormScreen : AppScreens(route = "collections_form_screen")

    //auth screens
    object LoginScreen : AppScreens(route = "login_screen")

}