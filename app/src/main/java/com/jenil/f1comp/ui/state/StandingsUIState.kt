package com.jenil.f1comp.ui.state

import com.jenil.f1comp.data.local.entity.DriverStandingsEntity
import com.jenil.f1comp.data.local.entity.ConstructorStandingsEntity

data class StandingsUIState(
    val driverStandings: List<DriverStandingsEntity> = emptyList(),
    val constructorStandings: List<ConstructorStandingsEntity> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)