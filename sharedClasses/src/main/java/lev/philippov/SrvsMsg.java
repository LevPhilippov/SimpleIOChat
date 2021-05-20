package lev.philippov;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class SrvsMsg implements Serializable {

    private Map<String, String> params;

    public SrvsMsg() {
        this.params = new HashMap<>();
    }

    private static String AUTH_COMPLEET = "Auth compleet";
    private String field_1;
    private String field_2;
    private String field_3;
    private int type;

}
