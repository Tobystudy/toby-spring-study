package com.albireo3754.tobyspring.dao

import com.albireo3754.domain.User
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import java.sql.ResultSet

@Component
class UserDao(private var jdbcTemplate: JdbcTemplate) {
    fun add(user: User) {
        jdbcTemplate.update("insert into users(id, name, password) values(?, ?, ?)", user.id, user.name, user.password)
    }

    fun get(id: String): User {
        return jdbcTemplate.queryForObject("select * from users where id = ?", userMapper(), id)!!
    }

    private fun userMapper() = { rs: ResultSet, _: Int ->
        User(rs.getString("id"), rs.getString("name"), rs.getString("password"))
    }

    fun deleteAll() {
        jdbcTemplate.update("delete from users")
    }

    fun getCount(): Int {
        return jdbcTemplate.queryForObject("select count(*) from users", Int::class.java)!!
    }
}