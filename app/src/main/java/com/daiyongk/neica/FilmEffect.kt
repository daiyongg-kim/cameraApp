package com.daiyongk.neica

import androidx.compose.ui.graphics.Color

data class FilmEffect(
    val name: String,
    val shortName: String,
    val description: String,
    val primaryColor: Color = Color.White,
    val isActive: Boolean = false
)

object FilmEffects {
    val VIV = FilmEffect(
        name = "NEICA LOOK VIVID",
        shortName = "VIV",
        description = "Vivid colors with enhanced contrast",
        primaryColor = Color(0xFF00BCD4)
    )
    
    val NAT = FilmEffect(
        name = "NEICA LOOK NATURAL",
        shortName = "NAT", 
        description = "Natural color reproduction",
        primaryColor = Color(0xFF4CAF50)
    )
    
    val CHR = FilmEffect(
        name = "NEICA LOOK CHROME",
        shortName = "CHR",
        description = "Classic chrome film look",
        primaryColor = Color(0xFFFF5722)
    )
    
    val CLS = FilmEffect(
        name = "NEICA LOOK CLASSIC",
        shortName = "CLS",
        description = "Timeless classic film aesthetic",
        primaryColor = Color(0xFF9C27B0)
    )
    
    val CNT = FilmEffect(
        name = "NEICA LOOK CONTEMPORARY",
        shortName = "CNT",
        description = "Modern contemporary style",
        primaryColor = Color(0xFFFF9800)
    )
    
    fun getAllEffects() = listOf(VIV, NAT, CHR, CLS, CNT)
}