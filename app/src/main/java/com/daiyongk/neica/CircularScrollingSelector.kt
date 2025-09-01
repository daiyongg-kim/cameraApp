package com.daiyongk.neica

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import kotlinx.coroutines.flow.distinctUntilChanged
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CircularFilmEffectSelector(
    effects: List<FilmEffect>,
    selectedEffect: FilmEffect,
    onEffectSelected: (FilmEffect) -> Unit,
    modifier: Modifier = Modifier,
    visibleItemsCount: Int = 5
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    // Snap behavior with optimized settings for smoothness
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    
    // Create infinite list by repeating effects
    val infiniteEffects = remember(effects) {
        if (effects.isEmpty()) return@remember emptyList()
        val repeatCount = 1001 // Odd number to avoid centering issues
        buildList {
            repeat(repeatCount) { index ->
                add(effects[index % effects.size])
            }
        }
    }
    
    // Calculate initial position - center of the list
    val initialPosition = remember {
        if (infiniteEffects.isEmpty()) 0
        else {
            val center = infiniteEffects.size / 2
            val selectedIndex = effects.indexOf(selectedEffect)
            if (selectedIndex >= 0) center - (center % effects.size) + selectedIndex else center
        }
    }
    
    // Simple center calculation based on first visible item
    val centerIndex = remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            if (layoutInfo.visibleItemsInfo.isEmpty()) {
                initialPosition
            } else {
                // Get the middle visible item as center
                val visibleItems = layoutInfo.visibleItemsInfo
                if (visibleItems.size >= 3) {
                    visibleItems[visibleItems.size / 2].index
                } else {
                    visibleItems.firstOrNull()?.index ?: initialPosition
                }
            }
        }
    }
    
    // Update selection when center changes
    LaunchedEffect(listState) {
        snapshotFlow { centerIndex.value }
            .distinctUntilChanged()
            .collect { index ->
                if (infiniteEffects.isNotEmpty() && index < infiniteEffects.size) {
                    val centerEffect = infiniteEffects[index]
                    if (centerEffect.shortName != selectedEffect.shortName) {
                        onEffectSelected(centerEffect)
                    }
                }
            }
    }
    
    // Set initial scroll position
    LaunchedEffect(Unit) {
        if (infiniteEffects.isNotEmpty()) {
            listState.scrollToItem(initialPosition)
        }
    }
    
    Box(modifier = modifier) {
        LazyRow(
            state = listState,
            flingBehavior = snapBehavior,
            horizontalArrangement = Arrangement.spacedBy(20.dp), // Slightly more spacing for smoother movement
            contentPadding = PaddingValues(horizontal = 140.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(infiniteEffects.size) { index ->
                val effect = infiniteEffects[index]
                val distanceFromCenter = kotlin.math.abs(index - centerIndex.value)
                val isCenter = distanceFromCenter == 0
                // Smoother alpha transition
                val alpha = when (distanceFromCenter) {
                    0 -> 1.0f
                    1 -> 0.8f
                    2 -> 0.5f
                    3 -> 0.3f
                    else -> 0.15f
                }
                
                FilmEffectItem(
                    effect = effect,
                    isCenter = isCenter,
                    alpha = alpha,
                    onClick = { 
                        onEffectSelected(effect)
                        // Smooth scroll to clicked item
                        coroutineScope.launch {
                            listState.animateScrollToItem(index)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun FilmEffectItem(
    effect: FilmEffect,
    isCenter: Boolean,
    alpha: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .alpha(alpha)
            .width(72.dp) // Fixed width for consistent snapping
    ) {
        Text(
            text = effect.shortName,
            color = if (isCenter) effect.primaryColor else Color.White,
            fontSize = if (isCenter) 16.sp else 12.sp,
            fontWeight = if (isCenter) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}