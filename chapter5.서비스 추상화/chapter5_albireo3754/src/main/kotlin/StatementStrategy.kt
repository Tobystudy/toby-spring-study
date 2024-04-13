package com.albireo3754

import java.sql.Connection
import java.sql.PreparedStatement

fun interface StatementStrategy {
    fun makePreparedStatement(connection: Connection): PreparedStatement
}