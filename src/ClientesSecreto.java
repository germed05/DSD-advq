
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class ClientesSecreto extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Clientes.class.getName());
    private String[] personajes;
    private String nombrePersonaje;

    String usuarioActivo;
    // Variables de red
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ClientesSecreto(String nombreUsuario, Socket s, PrintWriter pw, BufferedReader br, String personaje) {
        this.usuarioActivo = nombreUsuario;
        this.socket = s;
        this.out = pw;
        this.in = br;
        this.nombrePersonaje = personaje;

        initComponents(); // Dibuja la ventana
        this.setLocationRelativeTo(null);
        this.setTitle("Adivina Quién (Chat Global) - Usuario: " + usuarioActivo);

        Conexion cnx = new Conexion();
        String victorias = cnx.obtenerVictorias(usuarioActivo);
        LVeces_Ganadas.setText("Has ganado " + victorias + " veces");
        
        TAMensajes.setEditable(false);
        TAUsuarios.setEditable(false);
        personajes = new String[]{
            "Manuel", "Jorge", "María", "Pablo", "Paco", "Pedro",
            "Pepe", "Ricardo", "Tomás", "Roberto", "Samuel", "Susana"
        };

        cargarPersonaje();

        txtMensaje.addActionListener(evt -> btnEnviarActionPerformed(evt));

        iniciarEscuchaDelChat();
    }

    private void iniciarEscuchaDelChat() {

        new Thread(() -> {
            try {
                String linea;
                while ((linea = in.readLine()) != null) {
                    procesarMensajeDelServidor(linea);
                }
            } catch (Exception e) {
                appendMensaje(">> Conexión con el servidor cerrada.\n");
            }
        }).start();

        if (out != null) {
            out.println("UPDATE_USERS");
            out.println("MSG|TODOS|¡He entrado a la partida!");
        }
    }

    private void procesarMensajeDelServidor(String linea) {
        SwingUtilities.invokeLater(() -> {
            // 1. CHAT: Actualización de conectados
            if (linea.startsWith("USERS|")) {
                String[] usuarios = linea.substring(6).split(",");
                TAUsuarios.setText("CONECTADOS:\n-----------\n");
                for (String u : usuarios) {
                    String trimmed = u.trim();
                    if (!trimmed.isEmpty()) {
                        TAUsuarios.append("• " + trimmed + "\n");
                    }
                }
            } // 2. CHAT: Mensajes recibidos
            else if (linea.startsWith("MSG|")) {
                String[] partes = linea.split("\\|", 3);
                if (partes.length == 3) {
                    try {
                        String remitente = partes[1];
                        String texto = partes[2];
                        String descifrado = AES.descifrar(texto);

                        if (remitente.equals(usuarioActivo)) {
                            appendMensaje("[Tú]: " + descifrado + "\n");
                        } else {
                            appendMensaje("[" + remitente + "]: " + descifrado + "\n");
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } // 3. CHAT: Alertas del sistema
            else if (linea.startsWith("SYSTEM|")) {
                appendMensaje(">> " + linea.substring(7) + "\n");
            } else if (linea.startsWith("GAME_WIN|")) {

                String ganador = linea.substring(9);
                JOptionPane.showMessageDialog(this, "¡" + ganador + " adivinó el personaje!");

                try {
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                this.dispose();
                new JFLobby(usuarioActivo).setVisible(true);
            } else if (linea.startsWith("TURN|")) {
                String nombreTurno = linea.substring(5);
                appendMensaje(">> Turno de: " + nombreTurno + "\n");
            } else {
                appendMensaje(linea + "\n");
            }
        });
    }

    // Utilidad para agregar texto al chat y bajar el scroll automáticamente
    private void appendMensaje(String texto) {
        TAMensajes.append(texto);
        TAMensajes.setCaretPosition(TAMensajes.getDocument().getLength());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        TAMensajes = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        TAUsuarios = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        txtMensaje = new javax.swing.JTextField();
        btnEnviar = new javax.swing.JButton();
        lblPersonaje = new javax.swing.JLabel();
        LPersonaje = new javax.swing.JLabel();
        LVeces_Ganadas = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        TAMensajes.setColumns(20);
        TAMensajes.setRows(5);
        jScrollPane1.setViewportView(TAMensajes);

        TAUsuarios.setColumns(20);
        TAUsuarios.setLineWrap(true);
        TAUsuarios.setRows(5);
        TAUsuarios.setMaximumSize(new java.awt.Dimension(30, 84));
        TAUsuarios.setMinimumSize(new java.awt.Dimension(30, 84));
        TAUsuarios.setPreferredSize(new java.awt.Dimension(30, 84));
        jScrollPane2.setViewportView(TAUsuarios);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtMensaje))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(txtMensaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        btnEnviar.setText("Enviar");
        btnEnviar.addActionListener(this::btnEnviarActionPerformed);

        lblPersonaje.setBackground(new java.awt.Color(51, 255, 102));
        lblPersonaje.setOpaque(true);

        LPersonaje.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LPersonaje.setText("Personaje");

        LVeces_Ganadas.setFont(new java.awt.Font("Comic Sans MS", 0, 14)); // NOI18N
        LVeces_Ganadas.setText("Has ganado x veces");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEnviar)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 153, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblPersonaje, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
                            .addComponent(LPersonaje, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(147, 147, 147))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(LVeces_Ganadas, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                    .addComponent(jScrollPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(LVeces_Ganadas)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(LPersonaje, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblPersonaje, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEnviar, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnEnviarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnviarActionPerformed
        try {
            String texto = txtMensaje.getText().trim();

            // Si el campo está vacío, no hacemos nada
            if (texto.isEmpty()) {
                return;
            }
            // En lugar de enviar un destinatario, le decimos al servidor que es para "TODOS"
            String cifrado = AES.cifrar(texto);
            if (out != null) {
                out.println("MSG|TODOS|" + cifrado);
            }
            // Limpiamos la caja de texto para seguir escribiendo
            txtMensaje.setText("");
            txtMensaje.requestFocus();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_btnEnviarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LPersonaje;
    private javax.swing.JLabel LVeces_Ganadas;
    private javax.swing.JTextArea TAMensajes;
    private javax.swing.JTextArea TAUsuarios;
    private javax.swing.JButton btnEnviar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblPersonaje;
    private javax.swing.JTextField txtMensaje;
    // End of variables declaration//GEN-END:variables

    private void cargarPersonaje() {
        LPersonaje.setText(nombrePersonaje);

        switch (nombrePersonaje) {

            case "Manuel":
                lblPersonaje.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/manuel.jpg")));
                break;

            case "Jorge":
                lblPersonaje.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/jorge.jpg")));
                break;

            case "María":
                lblPersonaje.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/maria.jpg")));
                break;

            case "Pablo":
                lblPersonaje.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/pablo.jpg")));
                break;

            case "Paco":
                lblPersonaje.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/paco.jpg")));
                break;

            case "Pedro":
                lblPersonaje.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/pedro.jpg")));
                break;

            case "Pepe":
                lblPersonaje.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/pepe.jpg")));
                break;

            case "Ricardo":
                lblPersonaje.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/ricardo.jpg")));
                break;

            case "Tomás":
                lblPersonaje.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/tomas.jpg")));
                break;

            case "Roberto":
                lblPersonaje.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/roberto.jpg")));
                break;

            case "Samuel":
                lblPersonaje.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/samuel.jpg")));
                break;

            case "Susana":
                lblPersonaje.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/susana.jpg")));
                break;
        }
        if (out != null) {
            out.println("SECRET|" + nombrePersonaje);
        }
    }
}
