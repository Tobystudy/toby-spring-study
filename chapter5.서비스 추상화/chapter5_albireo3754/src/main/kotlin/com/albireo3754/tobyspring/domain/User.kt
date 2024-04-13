package com.albireo3754.domain

class User(
    var id: String,
    var name: String,
    var password: String,
    var level: Level,
    var login: Int,
    var recommend: Int,
) {
    fun upgradeLevel() {
        level = level.nextLevel() ?: throw IllegalArgumentException("Cannot upgrade Level: $level")
    }
}