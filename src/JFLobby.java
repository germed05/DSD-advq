import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class JFLobby extends javax.swing.JFrame {

    private String usuarioActivo;

    // Objetos de la conexión que le pasaremos al Chat después
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean escuchandoLobby = true;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(JFLobby.class.getName());

    public JFLobby(String nombreUsuario) {
        this.usuarioActivo = nombreUsuario;
        initComponents();
        setTitle("Lobby - Adivina Quién (" + usuarioActivo + ")");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        lblContador.setFont(new java.awt.Font("Segoe UI", 1, 60));
        lblContador.setForeground(new java.awt.Color(200, 50, 50));
        lblContador.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblContador.setText(" ");
        conectarAlServidor();

    }

    private void conectarAlServidor() {
        try {
            socket = new Socket("localhost", 6000);//cambiar de localhost a direccion ipconfig/hacer una ventana de entrada de ip
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Avisamos quién entró
            out.println("LOGIN|" + usuarioActivo);

            // Hilo que escucha solo los mensajes del Lobby
            new Thread(() -> {
                try {
                    String linea;
                    // El ciclo se rompe cuando escuchandoLobby sea false
                    while (escuchandoLobby && (linea = in.readLine()) != null) {
                        procesarMensaje(linea);
                    }
                } catch (Exception e) {
                    System.out.println("Lobby desconectado.");
                }
            }).start();

        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error: El servidor no está encendido.");
            System.exit(0);
        }
    }

    private void procesarMensaje(String linea) {
        SwingUtilities.invokeLater(() -> {

            if (linea.equals("ERROR|DUPLICATE_USER")) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Esta cuenta ya tiene una sesión iniciada actualmente.",
                        "Acceso Denegado",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                escuchandoLobby = false;
                this.dispose();
                // Asumiendo que tu ventana de inicio se llama FSesion:
                // new FSesion().setVisible(true); 
                return;
            } // --- NUEVO: Atrapamos el mensaje de que la partida ya arrancó ---
            else if (linea.equals("GAME_IN_PROGRESS")) {
                lblEstado.setText("Partida en curso...");
                btnListo.setEnabled(false);
                btnListo.setText("En espera");
            } // ---------------------------------------------------------------
            else if (linea.startsWith("LOBBY_UPDATE|")) {
                String[] partes = linea.split("\\|");
                int listos = Integer.parseInt(partes[1]);
                int total = Integer.parseInt(partes[2]);

                if (total < 2) {
                    lblEstado.setText("Esperando a más jugadores... (" + total + " en sala)");
                    btnListo.setEnabled(false);
                } else {
                    lblEstado.setText("Jugadores listos: " + listos + " / " + total);
                    if (!btnListo.getText().equals("Esperando a los demás...")
                            && !btnListo.getText().equals("En espera")) { // Aseguramos que no habilite el botón si estaba en espera
                        btnListo.setEnabled(true);
                    }
                }
            } else if (linea.equals("START_GAME")) {
                escuchandoLobby = false;
                iniciarCuentaRegresivaYPasarAlChat();
            }
        });
    }

    private void iniciarCuentaRegresivaYPasarAlChat() {
        btnListo.setVisible(false);
        lblEstado.setText("¡PREPÁRATE!");

        Timer timer = new Timer(1000, new ActionListener() {
            int conteo = 3;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (conteo > 0) {
                    lblContador.setText(String.valueOf(conteo));
                } else if (conteo == 0) {
                    lblContador.setText("¡YA!");
                } else {
                    ((Timer) e.getSource()).stop();

                    //Pasamos los objetos vivos a la ventana de Clientes
                    Clientes chatFrame = new Clientes(usuarioActivo, socket, out, in);
                    chatFrame.setVisible(true);

                    // Cerramos el Lobby
                    dispose();
                }
                conteo--;
            }
        });
        timer.setInitialDelay(0);
        timer.start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnListo = new javax.swing.JButton();
        lblEstado = new javax.swing.JLabel();
        lblContador = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(462, 346));
        setMinimumSize(new java.awt.Dimension(462, 346));
        setResizable(false);

        btnListo.setText("Listo");
        btnListo.addActionListener(this::btnListoActionPerformed);

        lblEstado.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblEstado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        lblContador.setFont(new java.awt.Font("Segoe UI", 1, 60)); // NOI18N
        lblContador.setForeground(new java.awt.Color(200, 50, 50));
        lblContador.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(65, 65, 65)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblContador, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(165, 165, 165)
                        .addComponent(btnListo, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(67, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addComponent(lblEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblContador, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnListo, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(89, 89, 89))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnListoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnListoActionPerformed
        if (out != null) {
            out.println("READY");
            btnListo.setEnabled(false);
            btnListo.setText("Esperando a los demás...");
            btnListo.setBackground(Color.GRAY);
        }
    }//GEN-LAST:event_btnListoActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new JFLobby("JugadorPrueba").setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnListo;
    private javax.swing.JLabel lblContador;
    private javax.swing.JLabel lblEstado;
    // End of variables declaration//GEN-END:variables
}
