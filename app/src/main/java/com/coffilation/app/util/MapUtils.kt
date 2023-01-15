package com.coffilation.app.util

import com.coffilation.app.data.PointData
import com.yandex.mapkit.ScreenPoint
import com.yandex.mapkit.ScreenRect
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.mapview.MapView

/**
 * @author pvl-zolotov on 29.11.2022
 */
fun List<Point>.getBoundingBox(): BoundingBox {
    return BoundingBox(
        Point(minBy { it.latitude }.latitude, minBy { it.longitude }.longitude),
        Point(maxBy { it.latitude }.latitude, maxBy { it.longitude }.longitude),
    )
}

fun PointData.toMapPoint(): Point {
    return Point(latitude, longitude)
}

fun MapView.setBottomPadding(width: Float, height: Float, bottomPadding: Float) {
    try {
        focusRect = ScreenRect(ScreenPoint(100f, 100f), ScreenPoint(width - 100, height - bottomPadding - 150))
    } catch (e: RuntimeException) {
        e.printStackTrace()
    }
}
