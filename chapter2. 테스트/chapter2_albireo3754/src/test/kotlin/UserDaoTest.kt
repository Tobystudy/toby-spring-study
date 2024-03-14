import com.albireo3754.DaoFactory
import com.albireo3754.Main
import com.albireo3754.User
import com.albireo3754.UserDao
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.getBean
import org.springframework.boot.runApplication
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test

@SpringBootTest(classes = [Main::class])
class UserDaoTest(@Autowired var userDao: UserDao)
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
}