package com.albireo3754

import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.PreparedStatement
import javax.sql.DataSource

@Component
class JdbcContext(private val dataSource: DataSource) {
    fun workWithStatementStrategy(strategy: StatementStrategy) {
        var connection: Connection? = null
        var preparedStatement: PreparedStatement? = null
        try {
            connection = dataSource.connection
            preparedStatement = strategy.makePreparedStatement(connection)
            preparedStatement.executeUpdate()
        } catch (e: Exception) {
            throw e
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close()
                } catch (e: Exception) {
                }
            }
            if (connection != null) {
                try {
                    connection.close()
                } catch (e: Exception) {
                }
            }
        }
    }
}