package core;

/**
 * Hello world!
 *
 */
public class App {
    
    public static void main(String[] args) {
        try {
            new Channel().start();
        }
        catch (Exception e) {
            // do nothing
        }
    }
}
