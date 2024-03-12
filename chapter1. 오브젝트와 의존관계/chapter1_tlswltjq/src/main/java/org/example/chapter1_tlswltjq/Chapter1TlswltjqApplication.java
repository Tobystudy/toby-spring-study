package org.example.chapter1_tlswltjq;

import org.example.chapter1_tlswltjq.dao.ConnectionMaker;
import org.example.chapter1_tlswltjq.dao.DConnectionMaker;
import org.example.chapter1_tlswltjq.dao.DaoFactory;
import org.example.chapter1_tlswltjq.dao.UserDao;
import org.example.chapter1_tlswltjq.domain.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;

//@SpringBootApplication
public class Chapter1TlswltjqApplication {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
//        SpringApplication.run(Chapter1TlswltjqApplication.class, args);
        UserDao dao = new DaoFactory().userDao();

        User user = new User();
        user.setId("whiteship");
        user.setName("백기선");
        user.setPassword("married");

        dao.add(user);
        System.out.println(user.getId() + " 등록 성공");

        User user2 = dao.get(user.getId());
        System.out.println(user2.getName());
        System.out.println(user2.getPassword());
        System.out.println(user2.getId() + " 조회 성공");
    }
}
