package core;

import vision.Vision;

/**
 * Hello world!
 *
 */
public class App {
    
    public static void main(String[] args) {
        try {
            // new Channel().start();
            new Vision();
        }
        catch (Exception e) {
            // do nothing
        }
    }
}
