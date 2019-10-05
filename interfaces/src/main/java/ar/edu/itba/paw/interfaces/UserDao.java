package ar.edu.itba.paw.interfaces;

import ar.edu.itba.paw.model.User;

public interface UserDao {

    User getUserById(long id);

    User getUserByMail(String mail);

    User createUser(String userName, String password, String mail,boolean isAdmin);

    boolean checkIfValidationKeyExists(String key);

    void setValidationKey(long userId, String key);

    int banUser(long userId);

    int unbanUser(long userId);
}
