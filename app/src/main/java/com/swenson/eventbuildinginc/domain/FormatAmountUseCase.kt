package com.swenson.eventbuildinginc.domain

import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

class FormatAmountUseCase @Inject constructor() {
    fun formatInt(amount: Int): String = try {
        NumberFormat.getNumberInstance().format(amount)
    } catch (e: Exception){
        e.printStackTrace()
        String.format(Locale.ENGLISH, "%.2f", amount)
    }

    fun formatDouble(amount: Double) = try {
        NumberFormat.getNumberInstance().format(amount)
    } catch (e: Exception){
        e.printStackTrace()
        String.format(Locale.ENGLISH, "%.2f", amount)
    }
}