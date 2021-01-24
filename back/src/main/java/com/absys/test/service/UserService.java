package com.absys.test.service;

import com.absys.test.model.Criminal;
import com.absys.test.model.User;
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
        User user = null;
        // fetch user from memory database

        // next step on workflow
        // CREATED -> EARTH_CONTROL -> MARS_CONTROL -> DONE
        // Check criminal list during "EARTH_CONTROL" state, if the user is in the list, set state to REFUSED
        // TODO
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
        return memoryDatabase.stream().filter(user -> user.getId().equals(userid)).findFirst().orElse(null);
    }
}
