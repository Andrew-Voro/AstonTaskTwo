package aston.servlets.dto;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UserOutGoingDto {
    private String login;
    private String name;
    private String email;
    private String birthday;
    private Long id;
    private Set<Long> friends;

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getBirthday() {
        return birthday;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Set<Long> getFriends() {
        return friends;
    }

    public void setFriends(Set<Long> friends) {
        this.friends = friends;
    }



    public UserOutGoingDto() {
    }

    public UserOutGoingDto(String login, String name, String email, String birthday, Long id, Set<Long> friends) {
        this.login = login;
        this.name = name;
        this.email = email;
        this.birthday = birthday;
        this.id = id;
        this.friends = friends;
    }

    public Map<String, Object> toMap() { //new
        Map<String, Object> values = new HashMap<>();
        values.put("EMAIL", email);
        values.put("USER_ID", id);
        values.put("USER_NAME", name);
        values.put("LOGIN", login);
        values.put("BIRTHDAY", birthday);
        return values;
    }
}


