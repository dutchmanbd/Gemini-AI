package com.ticonsys.geminiai.domain.utils

sealed class ImageSize(val name: String, val size: Int) {
    data object Small : ImageSize("Small", 280)
    data object Medium : ImageSize("Medium", 320)
    data object Large : ImageSize("Large", 512)

    companion object {
        val DEFAULT_SIZE = Medium

        fun imageSize(size: Int): ImageSize {
            return when (size) {
                280 -> Small
                320 -> Medium
                512 -> Large
                else -> DEFAULT_SIZE
            }
        }
    }
}