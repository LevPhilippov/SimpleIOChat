package lev.philippov;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ChatMsg implements Serializable {

    private Long id;

    private String name;

    private String message;

}
