package com.albireo3754

class DaoFactory {
    fun getUserDao(): UserDao {
        val connectionMaker = SimpleConnectionMaker()
        return UserDao(connectionMaker)
    }
}