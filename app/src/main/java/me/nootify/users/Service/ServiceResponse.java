package me.nootify.users.service;

import java.util.List;

import me.nootify.users.data.User;

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
