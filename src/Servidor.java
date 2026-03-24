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

    private static Map<String, ManejadorCliente> clientes = new ConcurrentHashMap<>();
    private static boolean juegoEnCurso = false;

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
                if (running) {
                    log("Error servidor: " + e.getMessage());
                }
            }
        }).start();
    }

    public void broadcastListaUsuarios() {
        String lista = String.join(",", clientes.keySet());
        String mensaje = "USERS|" + lista;
        for (ManejadorCliente cliente : clientes.values()) {
            cliente.enviar(mensaje);
        }
    }

    public void broadcastMensaje(String remitente, String texto) {
        String mensajeFormateado = "MSG|" + remitente + "|" + texto;
        for (ManejadorCliente cliente : clientes.values()) {
            cliente.enviar(mensajeFormateado);
        }
    }
    
    //cambios realizados al servidor..
    //funcion verificarLobby
    //funcion run
    public synchronized void verificarLobby() {
        int total = clientes.size();
        int listos = 0;

        for (ManejadorCliente c : clientes.values()) {
            if (c.isReady) {
                listos++;
            }
        }

        for (ManejadorCliente c : clientes.values()) {
            c.enviar("LOBBY_UPDATE|" + listos + "|" + total);
        }

        // Iniciar la partida si hay 2 o más y todos están listos
        if (total >= 2 && listos == total && !juegoEnCurso) {
            juegoEnCurso = true;
            log("¡Todos listos! Iniciando el juego...");
            for (ManejadorCliente c : clientes.values()) {
                c.enviar("START_GAME");
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
        public boolean isReady = false;

        public ManejadorCliente(Socket s) {
            this.socket = s;
        }

        public void enviar(String msg) {
            if (out != null) {
                out.println(msg);
            }
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String linea = in.readLine();
                if (linea == null || !linea.startsWith("LOGIN|")) {
                    return;
                }

                nombre = linea.substring(6).trim();

                if (clientes.containsKey(nombre)) {
                    out.println("ERROR|DUPLICATE_USER");
                    return;
                }

                clientes.put(nombre, this);
                log("[+] " + nombre + " se unió a la sala.");

                broadcastListaUsuarios();
                out.println("SYSTEM|¡Bienvenido al servidor, " + nombre + "!");

                //Control de jugadores en espera
                if (juegoEnCurso) {
                    // Si el juego ya empezó, lo mandamos a la sala de espera
                    out.println("GAME_IN_PROGRESS");
                } else {
                    verificarLobby();
                }

                while ((linea = in.readLine()) != null) {
                    if (linea.startsWith("MSG|")) {
                        String[] partes = linea.split("\\|", 3);
                        if (partes.length == 3) {
                            String texto = partes[2];
                            log(nombre + " a TODOS: " + texto);
                            broadcastMensaje(nombre, texto);
                        }
                    } else if (linea.equals("READY")) {
                        this.isReady = true;
                        verificarLobby();
                    }else if (linea.equals("UPDATE_USERS")) {
                        broadcastListaUsuarios();
                    }
                }

            } catch (IOException e) {
            } finally {
                if (nombre != null) {
                    clientes.remove(nombre);
                    log("[-] " + nombre + " salió de la sala.");

                    //Liberar la sala si se quedan solos ---
                    if (clientes.size() < 2) {
                        juegoEnCurso = false; // Se cancela la partida activa
                    }

                    broadcastListaUsuarios();
                    verificarLobby(); // Actualiza a los que estaban en espera para que puedan jugar
                }
                try {
                    socket.close();
                } catch (Exception ignored) {
                }
            }
        }
    }
}
