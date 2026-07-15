package tracker.entity.login;


import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginRequest {
    private String user;
    private String password;
}
