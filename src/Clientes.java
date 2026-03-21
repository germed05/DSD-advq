
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.SwingUtilities;

public class Clientes extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Clientes.class.getName());

    String usuarioActivo;
    private PrintWriter out;
    private BufferedReader in;

    public Clientes(String nombreUsuario) {
        // Guardamos el dato que llego desde el inicio de sesion
        this.usuarioActivo = nombreUsuario;
        initComponents();
        this.setLocationRelativeTo(null); // Centra la ventana
        // Mostramos quién inició sesión en el título de la ventana
        this.setTitle("Usuario: " + usuarioActivo);
        TAMensajes.setEditable(false);
        TAUsuarios.setEditable(false);
        txtMensaje.addActionListener(evt -> btnEnviarActionPerformed(evt));
        conectarAlServidor();
    }

    // Método para conectar con tu Servidor de Sockets
    private void conectarAlServidor() {
        try {
            // Conecta al puerto 6000 (asegúrate de que tu servidor esté corriendo)
            Socket socket = new Socket("localhost", 6000);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Al conectar, le decimos al servidor quiénes somos
            out.println("LOGIN|" + usuarioActivo);

            // Hilo para escuchar todo lo que manda el servidor sin congelar la ventana
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

        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "No se pudo conectar al servidor de chat. ¿Está encendido?",
                    "Error de Conexión",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método que procesa lo que llega del Servidor
    private void procesarMensajeDelServidor(String linea) {
        SwingUtilities.invokeLater(() -> {
            
            if (linea.startsWith("USERS|")) {
                // ... (Este se queda igual que como lo tenías) ...
                String[] usuarios = linea.substring(6).split(",");
                TAUsuarios.setText("USUARIOS EN LÍNEA:\n------------------\n");
                for (String u : usuarios) {
                    String trimmed = u.trim();
                    if (!trimmed.isEmpty()) {
                        TAUsuarios.append("• " + trimmed + "\n"); 
                    }
                }
            } 
            // --- NUEVO: Modificamos cómo se dibujan los mensajes ---
            else if (linea.startsWith("MSG|")) {
                String[] partes = linea.split("\\|", 3);
                if (partes.length == 3) {
                    String remitente = partes[1];
                    String texto = partes[2];
                    
                    // Comparamos el remitente con tu usuario activo
                    if (remitente.equals(usuarioActivo)) {
                        appendMensaje("[Tú]: " + texto + "\n");
                    } else {
                        appendMensaje("[" + remitente + "]: " + texto + "\n");
                    }
                }
            } 
            else if (linea.startsWith("SYSTEM|")) {
                appendMensaje(">> " + linea.substring(7) + "\n");
            } 
            else {
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEnviar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEnviar, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnEnviarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnviarActionPerformed
        String texto = txtMensaje.getText().trim();

        // Si el campo está vacío, no hacemos nada
        if (texto.isEmpty()) {
            return;
        }
        // En lugar de enviar un destinatario, le decimos al servidor que es para "TODOS"
        if (out != null) {
            out.println("MSG|TODOS|" + texto);
        }
        // Limpiamos la caja de texto para seguir escribiendo
        txtMensaje.setText("");
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
    private javax.swing.JTextArea TAMensajes;
    private javax.swing.JTextArea TAUsuarios;
    private javax.swing.JButton btnEnviar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField txtMensaje;
    // End of variables declaration//GEN-END:variables
}
