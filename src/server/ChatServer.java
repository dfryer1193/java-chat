package server;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by dfryer on 5/19/16.
 */
public class ChatServer implements Runnable {

    private ServerSocket ssock;
    private Thread thread;
    private HashMap<Integer, ChatServerThread> clients;

    public ChatServer(int port) {
        try {
            this.ssock = new ServerSocket(port);
            this.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.stop();
    }

    public void start() {
        if (this.thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        if (this.thread != null) {
            this.thread.stop(); // TODO: Do this better. (Create a client object that extends Thread?)
            this.thread = null;
        }
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        while (this.thread != null) {
            try {
                this.addThread(this.ssock.accept());
            } catch (IOException ioe) {
                ioe.printStackTrace();
                this.stop();
            }
        }
    }

    public synchronized void handle(int id, String input) {
        if (input.equals("/part")) {
            clients.remove(id);
        } else {
            clients.forEach((key,client) -> client.send(key+"|"+input));
        }
    }

    public synchronized void remove(int id) {
        ChatServerThread killMe = this.clients.get(id);
        try {
            killMe.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            killMe.stop(); // TODO: Do this better.
        }
        // TODO: Inform other clients that this client has left.
    }

    public void addThread(Socket sock) {
        ChatServerThread iAmNew = new ChatServerThread(this, sock);
        clients.put(iAmNew.getIdent(), iAmNew);
        try {
            iAmNew.open();
            iAmNew.start();
            // TODO: Inform everyone that this client has joined.
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = 5555;

        if (1 == args.length) {
            port = Integer.parseInt(args[0]);
        }

        ChatServer srv = new ChatServer(port);

    }
}
