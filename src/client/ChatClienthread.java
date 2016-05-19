package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by dfryer on 5/19/16.
 */
public class ChatClienthread extends Thread {
    private Socket sock;
    private ChatClient client;
    private BufferedReader streamIn;

    public ChatClienthread(ChatClient client, Socket sock) {
        this.client = client;
        this.sock = sock;
        this.open();
        this.start();
    }

    public void open() {
        try {
            this.streamIn = new BufferedReader(new InputStreamReader(this.sock.getInputStream()));
        } catch (IOException ioe) {
            ioe.printStackTrace();
            this.client.stop();
        }
    }

    public void close() {
        try {
            if (this.streamIn != null) {
                this.streamIn.close();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                this.client.handle(streamIn.readLine());
            } catch (IOException ioe) {
                ioe.printStackTrace();
                this.client.stop();
            }
        }
    }
}
