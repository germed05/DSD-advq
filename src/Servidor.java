
import javax.swing.*;
import java.awt.BorderLayout;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Servidor extends JFrame {

    private JTextArea areaLogs;
    private static volatile boolean running = true;
    private static ServerSocket serverSocket;
    private static String personajeActual = null;
    private static List<ManejadorCliente> ordenTurnos = new ArrayList<>();
    private static int turnoActual = 0;
    private static ManejadorCliente jugadorTurnoActual = null;

    private static Map<String, ManejadorCliente> clientes = new ConcurrentHashMap<>();
    private static boolean juegoEnCurso = false;
    private static final String[] personajes = {
        "Manuel", "Jorge", "María", "Pablo", "Paco", "Pedro",
        "Pepe", "Ricardo", "Tomás", "Roberto", "Samuel", "Susana"
    };

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

            //Crear una lista de clientes
            List<ManejadorCliente> listaClientes = new ArrayList<>(clientes.values());
            Random random = new Random();

            int indiceSecreto = random.nextInt(listaClientes.size());//Generar un indice aleatorio en base al tamaño de la lista
            ManejadorCliente jugadorSecreto = listaClientes.get(indiceSecreto);//DEfinir el jugador con el perdonaje secreto

            log("Jugador secreto: " + jugadorSecreto.nombre);

            personajeActual = personajes[random.nextInt(personajes.length)];
            log("Personaje secreto elegido: " + personajeActual);

            ordenTurnos = new ArrayList<>(listaClientes);
            ordenTurnos.remove(jugadorSecreto); // el secreto no adivina
            Collections.shuffle(ordenTurnos, random);
            turnoActual = 0;

            for (ManejadorCliente c : listaClientes) {

                c.isReady = false; // reiniciar ready

                if (c == jugadorSecreto) {
                    c.enviar("ROLE|SECRET");
                    c.enviar("SECRET_CHARACTER|" + personajeActual);

                } else {
                    c.enviar("ROLE|NORMAL");
                }

                c.enviar("START_GAME");
            }

            if (!ordenTurnos.isEmpty()) {
                jugadorTurnoActual = ordenTurnos.get(turnoActual);
                anunciarTurno();
            }
        }
    }

    public static void main(String[] args) {
        Servidor server = new Servidor();
        server.iniciar(6000);
    }

    private synchronized void anunciarTurno() {
        if (jugadorTurnoActual == null) {
            return;
        }

        String mensajeTurno = "TURN|" + jugadorTurnoActual.nombre;

        for (ManejadorCliente c : clientes.values()) {
            c.enviar(mensajeTurno);
        }

        log("Turno de: " + jugadorTurnoActual.nombre);
    }

    private synchronized void avanzarTurno() {
        if (ordenTurnos.isEmpty()) {
            return;
        }

        turnoActual = (turnoActual + 1) % ordenTurnos.size();
        jugadorTurnoActual = ordenTurnos.get(turnoActual);
        anunciarTurno();
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
                    } else if (linea.equals("UPDATE_USERS")) {
                        broadcastListaUsuarios();
                    } else if (linea.startsWith("GUESS|")) {
                        String intento = linea.substring(6);

                        if (jugadorTurnoActual == null || !nombre.equals(jugadorTurnoActual.nombre)) {
                            this.enviar("SYSTEM|No es tu turno.");
                            continue;
                        }

                        log(nombre + " intentó adivinar: " + intento);

                        if (intento.equals(personajeActual)) {
                            
                            Conexion cnx = new Conexion();
                            cnx.sumarVictoria(nombre);
                            
                            for (ManejadorCliente c : clientes.values()) {
                                c.enviar("GAME_WIN|" + nombre);
                                c.isReady = false;
                            }

                            juegoEnCurso = false;
                            personajeActual = null;
                            ordenTurnos.clear();
                            jugadorTurnoActual = null;
                            turnoActual = 0;

                            log("La partida terminó. Reiniciando lobby...");
                            verificarLobby();

                        } else {
                            this.enviar("SYSTEM|Personaje incorrecto.");
                            avanzarTurno();
                        }
                    }
                }

            } catch (IOException e) {
            } finally {
                if (nombre != null) {
                    clientes.remove(nombre);
                    ordenTurnos.removeIf(c -> nombre.equals(c.nombre));
                    if (jugadorTurnoActual != null && nombre.equals(jugadorTurnoActual.nombre)) {
                        avanzarTurno();
                    }
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
