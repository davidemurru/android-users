package me.nootify.users.data;

import java.util.Comparator;

/**
 * User Class
 */
public class User {
    private int id;
    private String name;
    private String email;
    private String infos;
    private boolean selected = false;

    public User() {
    }

    public User(int id) {
        this.id = id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setInfos(String infos) {
        this.infos = infos;
    }

    public String getInfos() {
        return infos;
    }

    public boolean isSelected(){
        return this.selected;
    }

    public void setSelected(boolean selected){
        this.selected = selected;
    }

    public UserComp getUserComp(){
        return new UserComp();
    }

    public class UserComp implements Comparator<User> {

        public int compare(User e1, User e2) {
            if(e1.getId() > e2.getId()){
                return 1;
            } else if (e1.getId() < e2.getId()) {
                return -1;
            } else
                return 0;
        }
    }

}
