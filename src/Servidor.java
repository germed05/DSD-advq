import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Servidor extends JFrame {
    private JTextArea areaLogs;
    private static volatile boolean running = true;
    private static ServerSocket serverSocket;
    
    // Estructura clave: nombre → manejador del cliente
    private static Map<String, ManejadorCliente> clientes = new ConcurrentHashMap<>();
    
    public Servidor() {
        setTitle("Servidor Chat Grupal - Adivina Quién");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        areaLogs = new JTextArea();
        areaLogs.setEditable(false);
        add(new JScrollPane(areaLogs), BorderLayout.CENTER);
        
        setVisible(true);
    }

    public void log(String msg) {
        SwingUtilities.invokeLater(() -> areaLogs.append(msg + "\n"));
    }

    public void iniciar(int puerto) {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(puerto);
                log("[SERVIDOR] Escuchando en puerto " + puerto);
                
                while (running) {
                    Socket socket = serverSocket.accept();
                    new Thread(new ManejadorCliente(socket)).start();
                }
            } catch (IOException e) {
                if (running) log("Error servidor: " + e.getMessage());
            }
        }).start();
    }

    // Enviar lista de usuarios conectados a TODOS los clientes
    public void broadcastListaUsuarios() {
        String lista = String.join(",", clientes.keySet());
        String mensaje = "USERS|" + lista;
        
        for (ManejadorCliente cliente : clientes.values()) {
            cliente.enviar(mensaje);
        }
    }

    // NUEVO: Enviar el mensaje a TODOS los clientes (Chat Grupal)
    public void broadcastMensaje(String remitente, String texto) {
        String mensajeFormateado = "MSG|" + remitente + "|" + texto;
        
        for (ManejadorCliente cliente : clientes.values()) {
            cliente.enviar(mensajeFormateado);
        }
    }

    public static void main(String[] args) {
        Servidor server = new Servidor();
        server.iniciar(6000); // El puerto debe coincidir con el del cliente
    }

    // ────────────────────────────────────────────────
    class ManejadorCliente implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String nombre = null;

        public ManejadorCliente(Socket s) {
            this.socket = s;
        }

        public void enviar(String msg) {
            if (out != null) out.println(msg);
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // 1. El cliente envía su nombre
                String linea = in.readLine();
                if (linea == null || !linea.startsWith("LOGIN|")) {
                    return;
                }
                nombre = linea.substring(6).trim();

                // Evitar nombres duplicados (Si alguien entra de 2 PCs con el mismo usuario)
                if (clientes.containsKey(nombre)) {
                    out.println("SYSTEM|El usuario ya está conectado en otra sesión.");
                    return;
                }

                // Registrar al nuevo cliente
                clientes.put(nombre, this);
                log("[+] " + nombre + " (" + socket.getInetAddress() + ") se unió a la sala.");

                // Avisar a todos la nueva lista de conectados
                broadcastListaUsuarios();
                
                // Darle la bienvenida al usuario
                out.println("SYSTEM|¡Bienvenido al chat global, " + nombre + "!");
                
                // Procesar mensajes entrantes
                while ((linea = in.readLine()) != null) {
                    if (linea.startsWith("MSG|")) {
                        String[] partes = linea.split("\\|", 3);
                        if (partes.length == 3) {
                            // En el cliente lo enviamos como MSG|TODOS|Texto
                            String texto = partes[2];
                            
                            // Imprimimos en la consola del servidor para llevar registro
                            log(nombre + " a TODOS: " + texto);
                            
                            // Disparamos el mensaje a todos los conectados
                            broadcastMensaje(nombre, texto);
                        }
                    }
                }

            } catch (IOException e) {
                // Si ocurre un error, asumimos que se desconectó
            } finally {
                // Limpieza al desconectarse
                if (nombre != null) {
                    clientes.remove(nombre);
                    log("[-] " + nombre + " salió de la sala.");
                    broadcastListaUsuarios(); // Actualizamos la lista de todos
                }
                try { socket.close(); } catch (Exception ignored) {}
            }
        }
    }
}