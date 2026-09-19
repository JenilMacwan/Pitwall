package com.jenil.f1comp.ui.navigation

import androidx.annotation.DrawableRes
import com.jenil.f1comp.R

sealed class BottomNavItem (
    val route: String,
    val title: String,
    @DrawableRes val icon: Int,
    @DrawableRes val icon2: Int
){
    data object Home: BottomNavItem(
        route = "home",
        title = "Home",
        icon = R.drawable.ic_home,
        icon2 = R.drawable.ic_home_fill
    )

    data object Standings: BottomNavItem(
        route = "standings",
        title = "Standings",
        icon = R.drawable.ic_bar,
        icon2 = R.drawable.ic_bar_fill
    )

    data object Schedule: BottomNavItem(
        route = "schedule",
        title = "Schedule",
        icon = R.drawable.ic_cal,
        icon2 = R.drawable.ic_cal_fill
    )

    data object News: BottomNavItem(
        route = "news",
        title = "News",
        icon = R.drawable.ic_news,
        icon2 = R.drawable.ic_news_fill
    )
    companion object{
        val items by lazy {
            listOf(
                Home,
                Standings,
                Schedule,
                News
            )
        }
    }
}