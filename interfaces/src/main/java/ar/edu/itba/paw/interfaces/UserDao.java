package ar.edu.itba.paw.interfaces;

import ar.edu.itba.paw.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    Optional<User> getUserById(long id);

    User getUserByValidationKey(String key);

    User getUserByMail(String mail);

    Optional<User> createUser(String userName, String password, String mail, boolean isAdmin);

    boolean checkIfValidationKeyExists(String key);

    void setValidationKey(long userId, String key);

    int banUser(long userId);

    byte[] getUserAvatar(long userId);
    void setUserAvatar(long userId, byte[] byteArray);
    int unbanUser(long userId);

    boolean userExists(long userId);

    List<User> getAllUsers();

}
