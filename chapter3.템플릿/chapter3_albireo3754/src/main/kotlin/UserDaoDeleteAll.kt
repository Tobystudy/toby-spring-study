package com.albireo3754

import java.sql.Connection
import java.sql.PreparedStatement

class UserDaoDeleteAll(connectionMaker: ConnectionMaker) : UserDao(connectionMaker) {
    fun makeStatement(connection: Connection): PreparedStatement {
        val ps = connection.prepareStatement("delete from users")
        return ps
    }
}