package com.albireo3754

import java.sql.Connection
import java.sql.DriverManager

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
        val connection = connectionMaker.getConnection();

        val ps = connection.prepareStatement("delete from users")
        ps.executeUpdate()

        ps.close()
        connection.close()
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