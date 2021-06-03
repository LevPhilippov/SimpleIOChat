package lev.philippov;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.util.Locale;

public class HashService {
    public static void main(String[] args) {
        System.out.println(DigestUtils.md5Hex("12345"));

//        try {
//            new File("sharedClasses/src/main/resources/../demo.txt").createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream("sharedClasses/src/main/resources/1.txt"));
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("sharedClasses/src/main/resources/2.txt", true))) {
            int i;
            while((i=bis.read())!=-1) {
                bos.write(i);
            }
            BufferedInputStream bis2 = new BufferedInputStream(new FileInputStream("sharedClasses/src/main/resources/2.txt"));
            FileReader fr = new FileReader("sharedClasses/src/main/resources/2.txt");
            BufferedReader bf  = new BufferedReader(fr);
            while (bf.ready()){
                System.out.print(bf.readLine());
            }

            while ((i=bis2.read())!=-1) {
                char a = (char)i;
                System.out.print(a);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
