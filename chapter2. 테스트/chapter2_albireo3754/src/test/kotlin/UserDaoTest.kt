import com.albireo3754.DaoFactory
import com.albireo3754.Main
import com.albireo3754.User
import com.albireo3754.UserDao
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.getBean
import org.springframework.boot.runApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.test.Test

@SpringBootTest(classes = [Main::class])
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class UserDaoTest(var userDao: UserDao)
{
    @Test
    fun addAndGet() {
        val user = User("whiteship", "백기선", "married")
        userDao.add(user)

        val user2 = userDao.get(user.id)

        assertEquals(user.id, user2.id)
        assertEquals(user.name, user2.name)
        assertEquals(user.password, user2.password)
    }

    @Test
    fun test() {
        val user1 = User("whiteship", "백기선", "married")
        val user2 = User("gyumee", "백명선", "single")
        val user3 = User("toby", "김영한", "single")

        userDao.deleteAll()
        assertEquals(userDao.getCount(), 0)

        userDao.add(user1)
        assertEquals(userDao.getCount(), 1)

        userDao.add(user2)
        assertEquals(userDao.getCount(), 2)

        userDao.add(user3)
        assertEquals(userDao.getCount(), 3)
    }

//    companion object {
//        @JvmStatic
//        @AfterAll
//        fun tearDown(): Unit {
//            userDao.deleteAll()
//        }
//    }
}