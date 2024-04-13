package com.albireo3754

import java.sql.Connection
import java.sql.PreparedStatement

class AddStatement(private var user: User): StatementStrategy {
    override fun makePreparedStatement(connection: Connection): PreparedStatement {
        val ps = connection.prepareStatement("insert into users(id, name, password) values(?, ?, ?)")
        ps.setString(1, user.id);
        ps.setString(2, user.name);
        ps.setString(3, user.password);
        return ps
    }
}