package com.pelle.videoreducer.data

import android.graphics.Bitmap
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import java.io.Serializable
import java.math.BigDecimal
import java.math.RoundingMode

data class VideoModel(
    val fileName: String,
    val absolutePath: String,
    val thumbnail: Bitmap?,
    val fileSize: Double, // Size in MB
): Serializable {

    fun getEstimatedSize(selectedQuality: VideoQuality) : Double = BigDecimal(
        fileSize * when (selectedQuality) {
            VideoQuality.VERY_LOW -> 0.1
            VideoQuality.LOW -> 0.2
            VideoQuality.MEDIUM -> 0.3
            VideoQuality.HIGH -> 0.4
            VideoQuality.VERY_HIGH -> 0.6
        }
    ).setScale(2, RoundingMode.FLOOR).toDouble()

}