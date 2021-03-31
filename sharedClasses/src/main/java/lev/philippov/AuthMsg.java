package lev.philippov;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class AuthMsg implements Serializable {
    private String login;
    private String password;
}
