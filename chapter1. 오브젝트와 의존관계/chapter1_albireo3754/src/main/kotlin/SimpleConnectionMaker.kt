package com.albireo3754

import java.sql.Connection
import java.sql.DriverManager

class SimpleConnectionMaker {
    fun getConnection(): Connection =
        DriverManager.getConnection(
            "jdbc:mysql://localhost:3308/springbook?createDatabaseIfNotExist=true",
            "root",
            null
        )
}