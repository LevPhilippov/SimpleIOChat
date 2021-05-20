package lev.philippov;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.Locale;

public class HashService {
    public static void main(String[] args) {
        System.out.println(DigestUtils.md5Hex("12345"));
    }
}
