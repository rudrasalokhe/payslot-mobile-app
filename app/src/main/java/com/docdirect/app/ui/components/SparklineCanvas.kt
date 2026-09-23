package com.docdirect.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.docdirect.app.ui.theme.AuraPrimary

@Composable
fun SparklineCanvas(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(28.dp),
    color: Color = AuraPrimary
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val points = listOf(
            0.0f to 0.65f,
            0.16f to 0.58f,
            0.32f to 0.72f,
            0.48f to 0.42f,
            0.64f to 0.52f,
            0.80f to 0.32f,
            1.0f to 0.38f
        )

        val path = Path()
        points.forEachIndexed { index, (nx, ny) ->
            val x = nx * width
            val y = ny * height
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = 2.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        val lastX = points.last().first * width
        val lastY = points.last().second * height
        drawCircle(
            color = color,
            radius = 3.dp.toPx(),
            center = androidx.compose.ui.geometry.Offset(lastX, lastY)
        )
    }
}
