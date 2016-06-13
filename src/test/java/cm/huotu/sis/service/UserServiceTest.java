package cm.huotu.sis.service;

import cm.huotu.sis.common.WebTest;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.huobanplus.common.repository.UserRepository;
import com.huotu.sis.service.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by jinzj on 2016/6/7.
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
public class UserServiceTest extends WebTest{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
//    @Ignore
    public void test() throws Exception {
        User user = new User();
        user.setLoginName(UUID.randomUUID().toString());
        userRepository.save(user);
        User userOne = new User();
        userOne.setLoginName(UUID.randomUUID().toString());
        userOne.setBelongOne(user.getId());
        userRepository.save(userOne);
        User userTwo = new User();
        userTwo.setLoginName(UUID.randomUUID().toString());
        userTwo.setBelongOne(userOne.getId());
        userTwo.setBelongTwo(user.getId());
        userRepository.save(userTwo);
        User userThree = new User();
        user.setLoginName(UUID.randomUUID().toString());
        userThree.setBelongOne(userTwo.getId());
        userThree.setBelongTwo(userOne.getId());
        userThree.setBelongThree(user.getId());
        userRepository.save(userThree);
        User userFour = new User();
        userFour.setLoginName(UUID.randomUUID().toString());
        userFour.setBelongOne(userThree.getId());
        userFour.setBelongTwo(userTwo.getId());
        userFour.setBelongThree(userOne.getId());
        userRepository.save(userFour);
        List<User> userList = userService.getAllRelationByUserId(userFour.getId());
        assertThat(userList.size()).as("某用户上级的一条线").isEqualTo(5);
    }
}
