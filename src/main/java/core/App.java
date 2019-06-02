package core;

import view.View;

/**
 * Hello world!
 *
 */
public class App {
    
    public static void main(String[] args) {
        try {
            // new Channel().start();
            new View();
        }
        catch (Exception e) {
            // do nothing
        }
    }
}
