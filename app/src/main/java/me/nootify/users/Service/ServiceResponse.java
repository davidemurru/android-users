package me.nootify.users.Service;

import java.util.List;

import me.nootify.users.Data.User;

/**
 * Service class for API response.
 */
public class ServiceResponse {
    private List<User> users;

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<User> getUsers() {
        return users;
    }
}
