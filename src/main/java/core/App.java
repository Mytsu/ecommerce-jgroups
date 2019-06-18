package core;

import view.View;
import controller.Control;
import model.Persistence;

/**
 * Hello world!
 *
 */
public class App {
    
    public static void main(String[] args) {
        try {
        	if(args[0].equals("-v"))
        		new View();
        	else if(args[0].equals("-c"))
        		new Control();
        	else if(args[0].equals("-m"))
                new Persistence();
            else new View();
        }
        catch (Exception e) {
            // do nothing
        	System.out.println("Um print");
        }
    }
}
