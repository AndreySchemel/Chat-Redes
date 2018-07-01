import java.net.ServerSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.*;

public class Server extends Thread {
    
    private static Map<String, PrintStream> CLIENTS_MAP;
    private static List<String> LIST_NAMES = new ArrayList<String>();
    private Socket connection;
    private String nameClient;

    public Server(Socket socket) {
        this.connection = socket;
    }

    public boolean armazena(String newName) {
        for (int i = 0; i < LIST_NAMES.size(); i++) {
            if (LIST_NAMES.get(i).equals(newName))
                return true;
        }
        LIST_NAMES.add(newName);
        return false;
    }

    public void remove(String oldName) {
        for (int i = 0; i < LIST_NAMES.size(); i++) {
            if (LIST_NAMES.get(i).equals(oldName))
                LIST_NAMES.remove(oldName);
        }
    }

    public static void main(String args[]) {
        CLIENTS_MAP = new HashMap<String, PrintStream>();
        try {
            ServerSocket server = new ServerSocket(8000);
            System.out.println("ServidorSocket na porta 8000");
            while (true) {
                Socket connection = server.accept();
                Thread t = new Server(connection);
                t.start();
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }
    }

    public void run() {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(this.connection.getInputStream()));
            PrintStream output = new PrintStream(this.connection.getOutputStream());
            this.nameClient = input.readLine();
            if (armazena(this.nameClient)) {
                output.println("Usuário já existe.");
                this.connection.close();
                return;
            } else {
                
                System.out.println(this.nameClient + " : Conectado!");

                String s = "";
                for (String aux : LIST_NAMES) {
                    if (!aux.equalsIgnoreCase(this.nameClient)) {
                        s = s + aux + " ";
                    }
                }
              
                output.println("Conectados: " + s);

               
                sendListToAll(this.nameClient);
            }

            if (this.nameClient == null) {
                return;
            }
        
            CLIENTS_MAP.put(this.nameClient, output);

            String[] message = input.readLine().split(":");
            while (message != null && !(message[0].trim().equals(""))) {
                send(output, " disse: ", message);
                message = input.readLine().split(":");
            }
            System.out.println(this.nameClient + " saiu da sala!");
            String[] out = {" da sala!"};
            send(output, " saiu", out);
            remove(this.nameClient);

            CLIENTS_MAP.remove(this.nameClient);

            this.connection.close();
        } catch (IOException e) {
            System.out.println("Houve falha na conexão .. ." + " IOException: " + e);
        }
    }

    
    public void send(PrintStream output, String action, String[] message) {
        out:
        for (Map.Entry<String, PrintStream> cliente : CLIENTS_MAP.entrySet()) {
            PrintStream chat = cliente.getValue();
            if (chat != output) {
                if (message.length == 1) {
                    chat.println(this.nameClient + action + message[0]);
                } else {
                    if (message[1].equalsIgnoreCase(cliente.getKey())) {
                        chat.println(this.nameClient + action + message[0]);
                        break out;
                    }
                }
            }
        }
    }

    public void sendListToAll(String name) {
        for (Map.Entry<String, PrintStream> client : CLIENTS_MAP.entrySet()) {
            if (!client.getKey().equalsIgnoreCase(name)) {
                String aux = "";
                for (String s : LIST_NAMES) {
                    if (!s.equalsIgnoreCase(client.getKey())) {
                        aux = aux + s + " ";
                    }
                }
                PrintStream chat = client.getValue();
                chat.println("[" + aux + "]");
                chat.flush();
            }
        }
    }
}