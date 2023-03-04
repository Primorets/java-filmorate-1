package ru.yandex.practicum.filmorate.storage;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.DbUserStorage;

import java.time.LocalDate;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode=DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DbUserStorageTest {

    private final DbUserStorage userStorage;

    private User user1;
    private User user2;

    private User user3;
    @Autowired
    DbUserStorageTest(@Qualifier("dbUserStorage") DbUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @BeforeEach
    void initialization(){
        user1 = new User();
        user1.setName("name1");
        user1.setBirthday(LocalDate.of(1999, 2, 22));
        user1.setEmail("email1@email.com");
        user1.setLogin("login1");


        user2 = new User();
        user2.setName("name2");
        user2.setBirthday(LocalDate.of(1998, 2, 22));
        user2.setEmail("email2@email.com");
        user2.setLogin("login2");


        user3 = new User();
        user3.setName("name3");
        user3.setBirthday(LocalDate.of(1999, 2, 22));
        user3.setEmail("email3@email.com");
        user3.setLogin("login3");
        userStorage.save(user1);
        userStorage.save(user2);
    }



    @Test
    void shouldGetUserLists(){
        assertEquals(2,userStorage.getAllUsersList().size());
    }

    @Test
    void shouldGetUserById1(){
        Optional<User> userOptional = Optional
                .ofNullable(userStorage.get(1));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(
                user -> assertThat(user).hasFieldOrPropertyWithValue("id",1));
    }

    @Test
    void shouldCreateUser(){
        userStorage.save(user3);
        assertEquals(3,userStorage.getAllUsersList().size());
    }

    @Test
    void shouldUpdateUser(){
        User userUpdate = new User();
        userUpdate.setId(1);
        userUpdate.setName("updateName");
        userUpdate.setBirthday(LocalDate.of(1989, 5, 24));
        userUpdate.setEmail("updateEmail6@email.com");
        userUpdate.setLogin("login1");
        userStorage.update(userUpdate);
        assertEquals("updateName",userStorage.get(1).getName());
        assertEquals("updateEmail6@email.com",userStorage.get(userUpdate.getId()).getEmail());

    }


    @Test
    void shouldAddFriend(){
        userStorage.save(user1);
        userStorage.save(user2);
        userStorage.save(user3);
        userStorage.addFriend(userStorage.get(user1.getId()),userStorage.get(user2.getId()));
        userStorage.addFriend(userStorage.get(user1.getId()),userStorage.get(user3.getId()));
        assertEquals(2,userStorage.getOneUserFriendsList(user1.getId()).size());
    }

    @Test
    void shouldDeleteFriends(){
        userStorage.save(user1);
        userStorage.save(user2);
        userStorage.save(user3);
        userStorage.addFriend(userStorage.get(user1.getId()),userStorage.get(user2.getId()));
        userStorage.addFriend(userStorage.get(user1.getId()),userStorage.get(user3.getId()));
        userStorage.deleteFriends(userStorage.get(user1.getId()),userStorage.get(user2.getId()));
        assertEquals(1,userStorage.getOneUserFriendsList(user1.getId()).size());
    }

    @Test
    void shouldGetCommonFriendsList(){
        userStorage.save(user3);
        userStorage.addFriend(userStorage.get(1),userStorage.get(2));
        userStorage.addFriend(userStorage.get(1),userStorage.get(3));
        userStorage.addFriend(userStorage.get(2),userStorage.get(3));
        assertEquals(1,userStorage.getCommonFriends(userStorage.get(user1.getId()).getId(),
                userStorage.get(user2.getId()).getId()).size());
    }

    @AfterEach
    void closeDb(){
        for(User user: userStorage.getAllUsersList()){
            userStorage.deleteUserById(user.getId());
        }
    }
}