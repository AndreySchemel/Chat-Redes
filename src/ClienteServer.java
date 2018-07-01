import java.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class ClienteServer extends Thread {
    // Controlador de mensagens
    private Socket connection;
  
    public ClienteServer(Socket socket) {
        this.connection = socket;
    }
    
    public static void main(String args[])
    {
        try {
            // Conexão Socket com IP e porta.
            Socket socket = new Socket("127.0.0.1", 8000);
            
            // Controla o fluxo de comunicação
            PrintStream out = new PrintStream(socket.getOutputStream());
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            
            // Envio do nome para o server
            System.out.print("Nome de usuário: ");
            String name = input.readLine();
            out.println(name.toUpperCase());
            
            // Instancia a thread para o IP e porta
            Thread thread = new ClienteServer(socket);
            thread.start();
            
            // Cria mensagem para o servidor
            String message;
            while (true)
            {
                // Digitação da mensagem
                System.out.print(" Message: ");
                message = input.readLine();
                // Envio da mensagem para o servidor
                out.println(message);
            }
        } catch (IOException e) {
            System.out.println("Houve falha na conexão .. ." + " IOException: " + e);
        }
    }
    
    public void run()
    {
        try {
            // Recebimento de mensagens de outros usuários
            BufferedReader input = new BufferedReader(new InputStreamReader(this.connection.getInputStream()));
            // Cria variavel de mensagem
            String message;
            while (true)
            {
                // Recebe a mensagem
                message = input.readLine();
                // Caso haja dados, entra no IF se não encerra a conexão.
                if (message == null) {
                    System.out.println(" Fim da conexão! ");
                    System.exit(0);
                }
                System.out.println();
                // Imprime a mensagem
                System.out.println(message);
                // Cria linha para resposta
                System.out.print(" - Resposta : ");
            }
        } catch (IOException e) {
            // Caso haja erros.
            System.out.println("Houve falha na conexão .. ." + " IOException: " + e);
        }
    }
}
