package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private int generator = 0;
    private Map<Integer, User> allUsers = new HashMap<>();

    @Override
    public User get(int userId) {
        return allUsers.get(userId);
    }

    @Override
    public List<User> getCommonFriends(int id, int friendId) {
        List<User> userList = getOneUserFriendsList(id);
        Set<Integer> firstUserFriends = new HashSet<>();
        for (User friend : userList) {
            firstUserFriends.add(friend.getId());
        }
        List<User> secondUserList = getOneUserFriendsList(friendId);
        Set<Integer> secondUserFriends = new HashSet<>();
        for (User friend : userList) {
            secondUserList.add(secondUserList.get(friend.getId()));
        }
        firstUserFriends.retainAll(secondUserFriends);
        List<User> otherFriends = new ArrayList<>();
        Iterator<Integer> friendIdIterator = firstUserFriends.iterator();
        while (friendIdIterator.hasNext()) {
            otherFriends.add(get(friendIdIterator.next()));
        }
        return otherFriends;
    }

    @Override
    public List<User> getAllUsersList() {
        return new ArrayList<>(allUsers.values());
    }

    @Override
    public User save(User user) {
        if (!allUsers.containsKey(user.getId())) {
            update(user);
        }
        allUsers.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUserById(int id) {
        allUsers.remove(id);
    }

    @Override
    public void addFriend(User user, User friend) {
        user.getFriendsId().add(friend.getId());
    }

    @Override
    public void deleteFriends(User user, User friend) {
        user.getFriendsId().remove(friend.getId());
    }

    @Override
    public User update(User user) {
        user.setId(++generator);
        return user;
    }

    @Override
    public List<User> getOneUserFriendsList(int userId) {
        List<User> friends = new ArrayList<>();
        for (Integer id : new ArrayList<>(get(userId).getFriendsId())) {
            friends.add(get(id));
        }
        return friends;
    }
}
