package core;

import java.util.Scanner;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;

/**
 * Hello world!
 *
 */
public class App extends ReceiverAdapter {

    private JChannel channel;
    private String username = "Jonathan";

    private void start() throws Exception {
        channel = new JChannel();
        channel.setReceiver(this);
        channel.connect("ChatGroup");
            eventLoop();
        channel.close();
    }

    private void eventLoop() {
        Scanner keyboard = new Scanner(System.in);
        String line;

        boolean loop = true;
        while( loop ) {
            System.out.print("> ");
            System.out.flush();

            try {
                line = keyboard.nextLine().toLowerCase();

                if(line.startsWith("quit") || line.startsWith("exit")) {
                    loop = false;
                }
                else {
                    line = "[" + username + "]" + line;
                    Message msg = new Message(null, line);
                    channel.send(msg);
                }
            }
            catch(Exception e) {
                // TODO tratar erros na mensagem
            }
        }

        keyboard.close();
    }

    public static void main(String[] args) {
        try {
            new App().start();
        }
        catch (Exception e) {
            // do nothing
        }
    }
}
