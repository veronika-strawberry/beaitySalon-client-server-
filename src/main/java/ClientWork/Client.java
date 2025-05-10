/*
package ClientWork;


import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    private Socket clientSocket;
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;
    private String message;

    public Client(String ipAddress, String port) {
        try {
            this.clientSocket = new Socket(ipAddress, Integer.parseInt(port));
            this.inStream = new ObjectInputStream(this.clientSocket.getInputStream());
            this.outStream = new ObjectOutputStream(this.clientSocket.getOutputStream());

        } catch (IOException var4) {
            System.out.println("Server not found: " + var4.getMessage());
            System.exit(0);
        }

    }


    public void sendMessage(String message){
        try {
            outStream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


  public void sendObject(Object object) {
        try {
            this.outStream.writeObject(object);
        } catch (IOException var3) {
            var3.printStackTrace();
        }

    }

    public String readMessage() throws IOException {
        try {
            this.message = (String)this.inStream.readObject();
        } catch (IOException | ClassNotFoundException var2) {
            var2.printStackTrace();
        }

        return this.message;
    }

    public Object readObject() {
        Object object = new Object();

        try {
            object = this.inStream.readObject();
        } catch (IOException | ClassNotFoundException var3) {
            var3.printStackTrace();
        }

        return object;
    }

    public void close() {
        try {
            clientSocket.close();
            //outStream.flush();
            inStream.close();
            outStream.close();
        } catch (EOFException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}*/

package ClientWork;


import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

    public class Client {
        private Socket clientSocket;
        private ObjectOutputStream outStream;
        private ObjectInputStream inStream;
        private String message;

        public Client(String ipAddress, String port) {
            try {
                this.clientSocket = new Socket(ipAddress, Integer.parseInt(port));
                this.outStream = new ObjectOutputStream(this.clientSocket.getOutputStream());
                this.inStream = new ObjectInputStream(this.clientSocket.getInputStream());
            } catch (IOException var4) {
                System.out.println("Server not found: " + var4.getMessage());
                System.exit(0);
            }

        }


        public void sendMessage(String message){
            try {
                outStream.writeObject(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public void sendObject(Object object) {
            try {
                this.outStream.writeObject(object);
            } catch (IOException var3) {
                var3.printStackTrace();
            }

        }
        public String readMessage() throws IOException {
            try {
                this.message = (String)this.inStream.readObject();
            } catch (IOException | ClassNotFoundException var2) {
                var2.printStackTrace();
            }

            return this.message;
        }

        public Object readObject() {
            Object object = new Object();

            try {
                object = this.inStream.readObject();
            } catch (IOException | ClassNotFoundException var3) {
                var3.printStackTrace();
            }

            return object;
        }

        public void close() {
            try {
                this.clientSocket.close();
                this.inStream.close();
                this.outStream.close();
            } catch (EOFException var2) {
                var2.printStackTrace();
            } catch (IOException var3) {
                var3.printStackTrace();
            }

        }
    }
