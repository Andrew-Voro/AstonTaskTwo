package aston.servlets.dto;


public class UserIncomingDto {


    private String login;
    private String name;
    private String email;
    private String birthday;

    public UserIncomingDto() {
    }

    public UserIncomingDto(String login, String name, String email, String birthday) {
        this.login = login;
        this.name = name;
        this.email = email;
        this.birthday = birthday;
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
}

