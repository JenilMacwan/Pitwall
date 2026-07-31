
package com.jenil.f1comp.ui.state

import com.jenil.f1comp.data.local.entity.NextRaceEntity

data class NextRaceUiState(
    val isLoading: Boolean = false,
    val nextRace: NextRaceEntity? = null,
    val error: String? = null
)