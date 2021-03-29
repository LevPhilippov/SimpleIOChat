package lev.philippov;

import lombok.Data;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

@Data
public class ChatClient implements Serializable {

    private Long id;

    private String name;

    private String message;

}
