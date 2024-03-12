package org.example.chapter1_tlswltjq.dao;

import org.example.chapter1_tlswltjq.domain.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.SQLException;

public class UserTest {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);

//        UserDao dao = context.getBean("userDao", UserDao.class);

//        User user = new User();
//        user.setId("whiteship");
//        user.setName("백기선");
//        user.setPassword("married");
//
//        dao.add(user);
//        System.out.println(user.getId() + " 등록 성공");
//
//        User user2 = dao.get(user.getId());
//        System.out.println(user2.getName());
//        System.out.println(user2.getPassword());
//        System.out.println(user2.getId() + " 조회 성공");
        UserDao dao3 = context.getBean("userDao", UserDao.class);
        UserDao dao4 = context.getBean("userDao", UserDao.class);
        System.out.println(dao3);
        System.out.println(dao4);
    }
}
