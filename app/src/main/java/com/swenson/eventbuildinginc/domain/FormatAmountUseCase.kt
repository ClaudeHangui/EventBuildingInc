package com.swenson.eventbuildinginc.domain

import java.text.NumberFormat
import javax.inject.Inject

class FormatAmountUseCase @Inject constructor() {
    fun formatInt(amount: Int): String = NumberFormat.getNumberInstance().format(amount)
}