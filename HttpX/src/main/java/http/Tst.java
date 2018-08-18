package http;

import java.io.InputStream;
import java.util.Properties;

import config.ResLibConfig;

public class Tst {
    public static final String A;

    static {
        Properties properties = new Properties();
        try {
            InputStream open = ResLibConfig.CONTEXT.getAssets().open("http");
            // open = Resources.getSystem().getAssets().open("http"); //获取失败
            // InputStream open = ClassLoader.getSystemResourceAsStream("http");
            properties.load(open);//is是通过上面获得的输入流
            System.out.println("======================");
            System.out.println(properties);
            System.out.println("======================");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e + "======================");
        }
        A = properties+"";

    }

}
