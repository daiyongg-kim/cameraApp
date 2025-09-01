package com.daiyongk.neica

import androidx.compose.ui.graphics.Color
import org.junit.Assert.*
import org.junit.Test

class FilmEffectTest {

    @Test
    fun `test FilmEffect data class properties`() {
        val effect = FilmEffect(
            name = "Test Effect",
            shortName = "TEST",
            description = "Test description",
            primaryColor = Color.Red,
            isActive = true
        )

        assertEquals("Test Effect", effect.name)
        assertEquals("TEST", effect.shortName)
        assertEquals("Test description", effect.description)
        assertEquals(Color.Red, effect.primaryColor)
        assertTrue(effect.isActive)
    }

    @Test
    fun `test FilmEffect default values`() {
        val effect = FilmEffect(
            name = "Test",
            shortName = "T",
            description = "Desc"
        )

        assertEquals(Color.White, effect.primaryColor)
        assertFalse(effect.isActive)
    }

    @Test
    fun `test FilmEffects VIV properties`() {
        val viv = FilmEffects.VIV
        
        assertEquals("NEICA LOOK VIVID", viv.name)
        assertEquals("VIV", viv.shortName)
        assertEquals("Vivid colors with enhanced contrast", viv.description)
        assertEquals(Color(0xFF00BCD4), viv.primaryColor)
        assertFalse(viv.isActive)
    }

    @Test
    fun `test FilmEffects NAT properties`() {
        val nat = FilmEffects.NAT
        
        assertEquals("NEICA LOOK NATURAL", nat.name)
        assertEquals("NAT", nat.shortName)
        assertEquals("Natural color reproduction", nat.description)
        assertEquals(Color(0xFF4CAF50), nat.primaryColor)
        assertFalse(nat.isActive)
    }

    @Test
    fun `test FilmEffects CHR properties`() {
        val chr = FilmEffects.CHR
        
        assertEquals("NEICA LOOK CHROME", chr.name)
        assertEquals("CHR", chr.shortName)
        assertEquals("Classic chrome film look", chr.description)
        assertEquals(Color(0xFFFF5722), chr.primaryColor)
        assertFalse(chr.isActive)
    }

    @Test
    fun `test FilmEffects CLS properties`() {
        val cls = FilmEffects.CLS
        
        assertEquals("NEICA LOOK CLASSIC", cls.name)
        assertEquals("CLS", cls.shortName)
        assertEquals("Timeless classic film aesthetic", cls.description)
        assertEquals(Color(0xFF9C27B0), cls.primaryColor)
        assertFalse(cls.isActive)
    }

    @Test
    fun `test FilmEffects CNT properties`() {
        val cnt = FilmEffects.CNT
        
        assertEquals("NEICA LOOK CONTEMPORARY", cnt.name)
        assertEquals("CNT", cnt.shortName)
        assertEquals("Modern contemporary style", cnt.description)
        assertEquals(Color(0xFFFF9800), cnt.primaryColor)
        assertFalse(cnt.isActive)
    }

    @Test
    fun `test getAllEffects returns all film effects`() {
        val effects = FilmEffects.getAllEffects()
        
        assertEquals(5, effects.size)
        assertTrue(effects.contains(FilmEffects.VIV))
        assertTrue(effects.contains(FilmEffects.NAT))
        assertTrue(effects.contains(FilmEffects.CHR))
        assertTrue(effects.contains(FilmEffects.CLS))
        assertTrue(effects.contains(FilmEffects.CNT))
    }

    @Test
    fun `test getAllEffects returns consistent order`() {
        val effects1 = FilmEffects.getAllEffects()
        val effects2 = FilmEffects.getAllEffects()
        
        assertEquals(effects1, effects2)
        assertEquals(effects1[0], FilmEffects.VIV)
        assertEquals(effects1[1], FilmEffects.NAT)
        assertEquals(effects1[2], FilmEffects.CHR)
        assertEquals(effects1[3], FilmEffects.CLS)
        assertEquals(effects1[4], FilmEffects.CNT)
    }
}