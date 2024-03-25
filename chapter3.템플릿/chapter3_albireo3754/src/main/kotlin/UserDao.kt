package com.albireo3754

import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement

class UserDao(private val connectionMaker: ConnectionMaker) {
    fun add(user: User) {
        val connection = connectionMaker.getConnection();

        var ps = connection.prepareStatement("insert into users(id, name, password) values(?, ?, ?)")
        ps.setString(1, user.id);
        ps.setString(2, user.name);
        ps.setString(3, user.password);

        ps.executeUpdate()

        ps.close()
        connection.close()
    }

    fun get(id: String): User {
        val connection = connectionMaker.getConnection();

        var ps = connection.prepareStatement("select * from users where id = ?")
        ps.setString(1, id);

        val rs = ps.executeQuery()
        rs.next()

        val user = User(rs.getString("id"), rs.getString("name"), rs.getString("password"))

        rs.close()
        ps.close()
        connection.close()

        return user
    }

    fun deleteAll() {
        val connection: Connection = connectionMaker.getConnection();
        var ps: PreparedStatement? = null

        try {
            ps = connection.prepareStatement("delete from users")
            ps?.executeUpdate()
        } catch (e: Exception) {
            throw e
        } finally {
            ps?.let {
                try { it.close() } catch (_: Exception) { }
            }

            connection.let {
                try { it.close() } catch (_: Exception) { }
            }
        }
    }

    fun getCount(): Int {
        val connection = connectionMaker.getConnection()

        val ps = connection.prepareStatement("select count(*) from users")
        val rs = ps.executeQuery()
        rs.next()
        val count = rs.getInt(1);

        rs.close()
        ps.close()
        connection.close()

        return count
    }

}