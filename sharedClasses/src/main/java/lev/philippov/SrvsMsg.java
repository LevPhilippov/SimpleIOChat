package lev.philippov;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
public class SrvsMsg implements Serializable {

    private static String AUTH_COMPLEET = "Auth compleet";
    private String field_1;
    private String field_2;
    private String field_3;
    private int type;

}
