package org.jeecg.modules.system.util;

import java.util.Random;

public class RandomCodeUtil {


    public final static String[] sum = { "a", "b", "c", "d", "e", "f", "g", "h",
            "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x",
            "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D",
            "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
            "T", "U", "V", "W", "X", "Y", "Z"};
//应用数目13388280
    public static String applicationCode(){
        StringBuffer str = new StringBuffer();
        Random random = new Random();
        for(int i = 0 ; i < 4 ; i++){
            int b = random.nextInt(62);
            str = str.append(sum[b]);
        }
       return str.toString();
    }

}
