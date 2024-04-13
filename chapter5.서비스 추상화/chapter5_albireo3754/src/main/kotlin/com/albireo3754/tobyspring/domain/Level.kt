package com.albireo3754.domain

enum class Level {
    BASIC,
    SILVER,
    GOLD;

    companion object {
        fun valueOf(value: Int): Level {
            return when (value) {
                0 -> BASIC
                1 -> SILVER
                2 -> GOLD
                else -> throw RuntimeException("Unknown Level: $value")
            }
        }
    }
}