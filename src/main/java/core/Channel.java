package core;

import java.util.Scanner;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;

public class Channel extends ReceiverAdapter {
    private JChannel channel;

    private void eventLoop() {
        Scanner keyboard = new Scanner(System.in);
        String line;

        boolean loop = true;
        while(loop) {
            System.out.print("> ");
            System.out.flush();

            try {
                line = keyboard.nextLine().toLowerCase();

                if(line.startsWith("quit") || line.startsWith("exit")) {
                    loop = false;
                }
                else {
                    line = "[" + "username" + "]" + line;
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

    public void start() {
        try {
            // channel = new JChannel("src/main/resources/config.xml");
            channel = new JChannel();
            channel.setReceiver(this);
        } catch (Exception e) {
            // Error 01 - Failed creating Channel
            System.err.println(e.getMessage());
        }
        try {
            channel.connect("ShopMainGroup");
                eventLoop();
            channel.close();
        } catch (Exception e) {
            // Error 02 - Failed stabilizing connection
            System.err.println(e.getMessage());
        }
    }

    
}
