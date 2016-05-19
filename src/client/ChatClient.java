package client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by dfryer on 5/19/16.
 */
public class ChatClient implements Runnable{

    private Socket sock;
    private Thread thread;
    private BufferedReader console;
    private BufferedWriter streamOut;
    private ChatClienthread client;

    public ChatClient(String serverName, int serverPort) {
        try {
            this.sock = new Socket(serverName, serverPort);
            this.start();
        } catch (UnknownHostException uhe) {
            uhe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
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
        while (thread != null) {
            try {
                streamOut.write(console.readLine());
                streamOut.flush();
            } catch (IOException ioe) {
                ioe.printStackTrace();
                this.stop();
            }
        }
    }

    public void handle(String msg) {
        if (msg.equals("/part")) {
            stop();
        } else {
            System.out.println(msg);
        }
    }

    public void start() throws IOException {
        this.console = new BufferedReader(new InputStreamReader(System.in));
        this.streamOut = new BufferedWriter(new OutputStreamWriter(this.sock.getOutputStream()));
        if (thread == null) {
            this.client = new ChatClienthread(this, this.sock);
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        if (this.thread != null) {
            thread.stop(); // TODO: Do this differently
            this.thread = null;
        }
        try {
            if (this.console != null) {
                this.console.close();
            }

            if (this.streamOut != null) {
                this.streamOut.close();
            }

            if (this.sock != null) {
                this.sock.close();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            this.client.close();
            this.client.stop();
        }
    }

    public static void main(String[] args) {
        String srv = "127.0.0.1";
        int port = 5555;

        switch (args.length) {
            case 2:
                srv = args[0];
                port = Integer.parseInt(args[1]);
                break;
            case 1:
                srv = args[0];
                break;
            case 0:
                break;
            default:
                System.out.println("Usage: java ChatClient [host] [port]");
                break;
        }

        ChatClient client = new ChatClient(srv, port);
    }
}
