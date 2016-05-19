package server;

import java.io.*;
import java.net.Socket;

/**
 * Created by dfryer on 5/19/16.
 */
public class ChatServerThread extends Thread {
    private Socket sock;
    private ChatServer srv;
    private int ident;
    private BufferedReader streamIn;
    private BufferedWriter streamOut;

    public ChatServerThread(ChatServer srv, Socket sock) {
        super();
        this.srv = srv;
        this.sock = sock;
        this.ident = sock.getPort();
    }

    @Override
    public void run() {
        while(true) {
            try {
                System.out.println(this.streamIn.readLine());
            } catch (IOException ioe) {
                ioe.printStackTrace();
                srv.remove(this.ident);
                break;
            }
        }
    }

    public void open() throws IOException {
        this.streamIn = new BufferedReader(new InputStreamReader(this.sock.getInputStream()));
        this.streamOut = new BufferedWriter(new OutputStreamWriter(this.sock.getOutputStream()));
    }

    public void close() throws IOException {
        if (this.sock != null) {
            this.sock.close();
        }

        if (this.streamIn != null)  {
            this.streamIn.close();
        }

        if (this.streamOut != null) {
            this.streamOut.close();
        }
    }

    public void send(String msg){
        try {
            streamOut.write(msg);
            streamOut.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            srv.remove(this.ident);
            this.stop();
        }
    }

    public int getIdent() {
        return this.ident;
    }
}
