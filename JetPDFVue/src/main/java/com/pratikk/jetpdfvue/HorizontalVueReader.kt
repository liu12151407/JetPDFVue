package com.pratikk.jetpdfvue

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.pratikk.jetpdfvue.state.HorizontalVueReaderState
import com.pratikk.jetpdfvue.state.VuePageState
import com.pratikk.jetpdfvue.util.pinchToZoomAndDrag

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalVueReader(
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    horizontalVueReaderState: HorizontalVueReaderState,
) {
    val density = LocalDensity.current
    var boxWidth by remember {
        mutableStateOf(0.dp)
    }
    val holderSize by remember {
        mutableStateOf(IntSize(0, 0))
    }
    val vueRenderer = horizontalVueReaderState.vueRenderer
    if (vueRenderer != null)
        HorizontalPager(
            modifier = modifier
                .onSizeChanged {
                    boxWidth = with(density) { it.width.toDp() }
                },
            state = horizontalVueReaderState.pagerState
        ) { idx ->
            val pageContent by vueRenderer.pageLists[idx].stateFlow.collectAsState()
            DisposableEffect(key1 = Unit) {
                vueRenderer.pageLists[idx].load()
                onDispose {
                    vueRenderer.pageLists[idx].recycle()
                }
            }
            when (pageContent) {
                is VuePageState.BlankState -> {
                    BlankPage(modifier = contentModifier,width = holderSize.width, height = holderSize.height)
                }

                is VuePageState.LoadedState -> {
                    Image(
                        modifier = contentModifier
                            .clipToBounds()
                            .pinchToZoomAndDrag(),
                        bitmap = (pageContent as VuePageState.LoadedState).content.asImageBitmap(),
                        contentDescription = ""
                    )
                }

            }
        }
}