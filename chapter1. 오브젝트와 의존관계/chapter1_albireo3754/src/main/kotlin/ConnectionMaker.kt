package com.albireo3754

import java.sql.Connection

interface ConnectionMaker {
    fun getConnection(): Connection
}