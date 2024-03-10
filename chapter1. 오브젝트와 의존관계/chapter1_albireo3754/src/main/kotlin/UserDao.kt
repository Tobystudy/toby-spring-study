package com.albireo3754

import java.sql.DriverManager

class UserDao {
    fun add(user: User) {
        val connection = DriverManager.getConnection("jdbc:mysql://localhost:3308/springbook?createDatabaseIfNotExist=true", "root", null);

        var ps = connection.prepareStatement("insert into users(id, name, password) values(?, ?, ?)")
        ps.setString(1, user.id);
        ps.setString(2, user.name);
        ps.setString(3, user.password);

        ps.executeUpdate()

        ps.close()
        connection.close()
    }

    fun get(id: String): User {
        val connection = DriverManager.getConnection("jdbc:mysql://localhost:3308/springbook?createDatabaseIfNotExist=true", "root", null);

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
}