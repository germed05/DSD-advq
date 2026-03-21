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
        setTitle("Servidor Chat Privado");
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

    // Enviar mensaje solo al destinatario
    public void enviarMensajePrivado(String remitente, String destinatario, String texto) {
        ManejadorCliente receptor = clientes.get(destinatario);
        if (receptor != null) {
            receptor.enviar("MSG|" + remitente + "|" + texto);
            // Opcional: eco al remitente (para que vea su propio mensaje)
            ManejadorCliente emisor = clientes.get(remitente);
            if (emisor != null) {
                emisor.enviar("MSG|" + remitente + "|" + texto);
            }
        } else {
            // Opcional: notificar que el usuario no está conectado
            ManejadorCliente emisor = clientes.get(remitente);
            if (emisor != null) {
                emisor.enviar("SYSTEM|El usuario " + destinatario + " no está conectado.");
            }
        }
    }

    public static void main(String[] args) {
        Servidor server = new Servidor();
        server.iniciar(6000);
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

                // Evitar nombres duplicados (simple)
                if (clientes.containsKey(nombre)) {
                    out.println("SYSTEM|Nombre ya en uso. Conéctate con otro.");
                    return;
                }

                clientes.put(nombre, this);
                log("[+] " + nombre + " (" + socket.getInetAddress() + ") conectado");

                // Avisar a todos (incluido él) la nueva lista
                broadcastListaUsuarios();
                out.println("SYSTEM|Bienvenido " + nombre + "!");
                
                // Enviar lista inicial de usuarios a este cliente
                String lista = String.join(",", clientes.keySet());
                out.println("USERS|" + lista);

                // Procesar mensajes entrantes
                while ((linea = in.readLine()) != null) {
                    if (linea.startsWith("MSG|")) {
                        String[] partes = linea.split("\\|", 3);
                        if (partes.length == 3) {
                            String destinatario = partes[1];
                            String texto = partes[2];
                            log(nombre + " → " + destinatario + ": " + texto);
                            enviarMensajePrivado(nombre, destinatario, texto);
                        }
                    }
                }

            } catch (IOException e) {
                // desconexión
            } finally {
                if (nombre != null) {
                    clientes.remove(nombre);
                    log("[-] " + nombre + " desconectado");
                    broadcastListaUsuarios();
                    out.println("SYSTEM|Desconexión confirmada.");
                }
                try { socket.close(); } catch (Exception ignored) {}
            }
        }
    }
}