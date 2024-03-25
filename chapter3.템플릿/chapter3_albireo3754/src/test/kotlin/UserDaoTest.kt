import com.albireo3754.*
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.getBean
import org.springframework.boot.runApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.sql.SQLException
import kotlin.test.Test

@SpringBootTest(classes = [Main::class])
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class UserDaoTest(var userDao: UserDao)
{
    private lateinit var user1: User
    private lateinit var user2: User
    private lateinit var user3: User

    @BeforeEach
    fun setUp() {
        user1 = User("whiteship", "백기선", "married")
        user2 = User("gyumee", "백명선", "single")
        user3 = User("toby", "김영한", "single")
        userDao.deleteAll()
    }

    @Test
    fun addAndGet() {
        println(userDao)
        println(this)
        val user = User("whiteship", "백기선", "married")
        userDao.add(user)

        val user2 = userDao.get(user.id)

        assertEquals(user.id, user2.id)
        assertEquals(user.name, user2.name)
        assertEquals(user.password, user2.password)
    }

    @Test
    fun test() {
        print(userDao)
        println(this)

        assertEquals(userDao.getCount(), 0)

        userDao.add(user1)
        assertEquals(userDao.getCount(), 1)

        userDao.add(user2)
        assertEquals(userDao.getCount(), 2)

        userDao.add(user3)
        assertEquals(userDao.getCount(), 3)
    }

    @Test
    fun getUserFailure() {
        print(userDao)
        println(this)

        assertThrows(SQLException::class.java) {
            // MARK: - 주석을 첬을때 테스트가 실패하는지 확인해야한다.
            userDao.get("unknown_id")
        }

    }

//    companion object {
//        @JvmStatic
//        @AfterAll
//        fun tearDown(): Unit {
//            userDao.deleteAll()
//        }
//    }
}