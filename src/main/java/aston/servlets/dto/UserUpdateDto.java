package aston.servlets.dto;

public class UserUpdateDto {
    private String login;
    private String name;
    private String email;
    private String birthday;
    private Long id;

    public UserUpdateDto() {
    }

    public UserUpdateDto(String login, String name, String email, String birthday, Long id) {
        this.login = login;
        this.name = name;
        this.email = email;
        this.birthday = birthday;
        this.id = id;
    }

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

}
