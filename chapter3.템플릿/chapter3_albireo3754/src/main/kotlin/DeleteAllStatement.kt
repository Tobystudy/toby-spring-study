package com.albireo3754

import java.sql.Connection
import java.sql.PreparedStatement

class DeleteAllStatement: StatementStrategy {
    override fun makePreparedStatement(connection: Connection): PreparedStatement {
        return connection.prepareStatement("delete from users")
    }
}