package com.absys.test.service;

import com.absys.test.model.Criminal;
import com.absys.test.model.User;
import com.absys.test.model.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    @Autowired
    private SimpMessagingTemplate webSocketTemplate;

    private List<User> memoryDatabase = new LinkedList(){{add(
            new User("SFES45", "DUPONT", "JEAN", new Date(), "FRANCE", "FARMER"));}};
    private List<Criminal> earthCriminalDatabase = Criminal.earthCriminal();
    /**
     * Create an ID and a user then return the ID
     * @param user
     * @return
     */
    public User createUser(User user) {
        if (!user.isValidForCreate()) {
            throw new RuntimeException("All fields must be fill.");
        }
        try {
            // generate key
            do {
                String key = User.generateKey();
                user.setId(key);
            } while (memoryDatabase.stream().anyMatch(userCur -> userCur.getId().equalsIgnoreCase(user.getId())));
            memoryDatabase.add(user);
            // notify
            webSocketTemplate.convertAndSend("/workflow/states", user);
            return user;
        } catch (Exception e) {
            throw new RuntimeException("Error has occured");
        }

    }

    public List<User> findAll() {
        return memoryDatabase;
    }

    /**
     *
     * @param userid
     * @return
     */
    public User workflow(String userid) {
        User user = findUserById(userid).orElseThrow(() -> new RuntimeException("Error : Unable to find user."));
        // fetch user from memory database

        // next step on workflow
        // CREATED -> EARTH_CONTROL -> MARS_CONTROL -> DONE
        // Check criminal list during "EARTH_CONTROL" state, if the user is in the list, set state to REFUSED
        if (UserState.CREATED.equals(user.getState())) {
            user.setState(UserState.EARTH_CONTROL);
        } else if (UserState.EARTH_CONTROL.equals(user.getState())) {
            if (isUserIsCriminal(user)) {
                user.setState(UserState.REFUSED);
            } else {
                user.setState(UserState.MARS_CONTROL);
            }
        } else if (UserState.MARS_CONTROL.equals(user.getState())) {
            if (isDuplicateUser(user)) {
                user.setState(UserState.REFUSED);
            } else {
                user.setState(UserState.DONE);
            }
        }
        // don't forget to use earthCriminalDatabase and UserState

        // send update to all users
        webSocketTemplate.convertAndSend("/workflow/states", user);
        return user;
    }


    /**
     * Return all user group by its job then its country
     * @return
     */
    public Object findByJobThenCountry() {
        // TODO : Return an Object containing user sort by Job then Country (you are not allowed to just return List<User> sorted)
        return new ArrayList<>(0);
    }

    /**
     * Find the user in the memory database by its ID
     * @param userid
     * @return
     */
    public User login(String userid) {
        return findUserById(userid).orElse(null);
    }

    private Optional<User> findUserById(String userid) {
        return memoryDatabase.stream().filter(user -> user.getId().equals(userid)).findFirst();
    }

    /**
     * A user is a criminal whether he exists on the earthCriminalDatabase
     * with same firstname + lastname and not allowed on mars
     * @param user
     * @return
     */
    private boolean isUserIsCriminal(User user) {
        return earthCriminalDatabase.stream().anyMatch(criminal -> {
            return criminal.getFirstname().equalsIgnoreCase(user.getFirstname())
                    && criminal.getLastname().equalsIgnoreCase(user.getLastname())
                    && criminal.isNotAllowedToMars();
        });
    }

    /**
     * check if the user already exists in DB with same firstname + lastname + birthday.
     * And already DONE, otherwise, both users will be refused.
     * @param user
     * @return
     */
    private boolean isDuplicateUser(User user) {
        return memoryDatabase.stream().anyMatch(userCur -> {
            return userCur.getState().equals(UserState.DONE)
                    && userCur.getFirstname().equalsIgnoreCase(user.getFirstname())
                    && userCur.getLastname().equalsIgnoreCase(user.getLastname())
                    && userCur.getBirthday().compareTo(user.getBirthday()) == 0
                    && !userCur.getId().equals(user.getId());
        });
    }
}
