package com.jenil.f1comp.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.jenil.f1comp.R

sealed class BottomNavItem (
    val route: String,
    @StringRes val titleRes: Int,
    @DrawableRes val icon: Int,
    @DrawableRes val icon2: Int
){
    data object Home: BottomNavItem(
        route = "home",
        titleRes = R.string.title_home,
        icon = R.drawable.ic_home,
        icon2 = R.drawable.ic_home_fill
    )

    data object Standings: BottomNavItem(
        route = "standings",
        titleRes = R.string.title_standings,
        icon = R.drawable.ic_bar,
        icon2 = R.drawable.ic_bar_fill
    )

    data object Schedule: BottomNavItem(
        route = "schedule",
        titleRes = R.string.title_schedule,
        icon = R.drawable.ic_cal,
        icon2 = R.drawable.ic_cal_fill
    )

    data object News: BottomNavItem(
        route = "news",
        titleRes = R.string.title_news,
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
