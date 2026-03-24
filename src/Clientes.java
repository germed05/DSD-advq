import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.SwingUtilities;

public class Clientes extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Clientes.class.getName());

    String usuarioActivo;
    // Variables de red
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;


    public Clientes(String nombreUsuario, Socket s, PrintWriter pw, BufferedReader br) {
        this.usuarioActivo = nombreUsuario; 
        this.socket = s;
        this.out = pw;
        this.in = br;
        
        initComponents(); // Dibuja la ventana
        this.setLocationRelativeTo(null); 
        this.setTitle("Adivina Quién (Chat Global) - Usuario: " + usuarioActivo);
        
        TAMensajes.setEditable(false);
        TAUsuarios.setEditable(false);
        
        txtMensaje.addActionListener(evt -> btnEnviarActionPerformed(evt));
        
        iniciarEscuchaDelChat();
    }

    private void iniciarEscuchaDelChat() {
        new Thread(() -> {
            try {
                String linea;
                // Leemos los mensajes de la misma tubería que dejó abierta el Lobby
                while ((linea = in.readLine()) != null) {
                    procesarMensajeDelServidor(linea);
                }
            } catch (Exception e) {
                appendMensaje(">> Conexión con el servidor cerrada.\n");
            }
        }).start();
        
        // Avisamos a todos que ya cargó nuestra ventana de chat
        if (out != null) {
            out.println("MSG|TODOS|¡He entrado a la partida!");
        }
    }

 

    private void procesarMensajeDelServidor(String linea) {
        SwingUtilities.invokeLater(() -> {
            //Actualización de conectados
            if (linea.startsWith("USERS|")) {
                String[] usuarios = linea.substring(6).split(",");
                TAUsuarios.setText("CONECTADOS:\n-----------\n");
                for (String u : usuarios) {
                    String trimmed = u.trim();
                    if (!trimmed.isEmpty()) {
                        TAUsuarios.append("• " + trimmed + "\n");
                    }
                }
            } 
            //Mensajes recibidos
            else if (linea.startsWith("MSG|")) {
                String[] partes = linea.split("\\|", 3);
                if (partes.length == 3) {
                    String remitente = partes[1];
                    String texto = partes[2];
                    if (remitente.equals(usuarioActivo)) {
                        appendMensaje("[Tú]: " + texto + "\n");
                    } else {
                        appendMensaje("[" + remitente + "]: " + texto + "\n");
                    }
                }
            } 
            //Alertas del sistema
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
        btnEnviar = new javax.swing.JButton();
        txtMensaje = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();

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

        btnEnviar.setText("Enviar");
        btnEnviar.addActionListener(this::btnEnviarActionPerformed);

        jLabel8.setBackground(new java.awt.Color(51, 255, 102));
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/manuel.jpg"))); // NOI18N
        jLabel8.setText("jLabel1");
        jLabel8.setOpaque(true);

        jLabel9.setBackground(new java.awt.Color(51, 255, 102));
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/jorge.jpg"))); // NOI18N
        jLabel9.setText("jLabel1");
        jLabel9.setOpaque(true);

        jLabel10.setBackground(new java.awt.Color(51, 255, 102));
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/maria.jpg"))); // NOI18N
        jLabel10.setText("jLabel1");
        jLabel10.setOpaque(true);

        jLabel11.setBackground(new java.awt.Color(51, 255, 102));
        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/pablo.jpg"))); // NOI18N
        jLabel11.setText("jLabel1");
        jLabel11.setOpaque(true);

        jLabel12.setBackground(new java.awt.Color(51, 255, 102));
        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/paco.jpg"))); // NOI18N
        jLabel12.setText("jLabel1");
        jLabel12.setOpaque(true);

        jLabel13.setBackground(new java.awt.Color(51, 255, 102));
        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/pedro.jpg"))); // NOI18N
        jLabel13.setText("jLabel1");
        jLabel13.setOpaque(true);

        jLabel14.setBackground(new java.awt.Color(51, 255, 102));
        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/pepe.jpg"))); // NOI18N
        jLabel14.setText("jLabel1");
        jLabel14.setOpaque(true);

        jLabel15.setBackground(new java.awt.Color(51, 255, 102));
        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/ricardo.jpg"))); // NOI18N
        jLabel15.setText("jLabel1");
        jLabel15.setOpaque(true);

        jLabel16.setBackground(new java.awt.Color(51, 255, 102));
        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/tomas.jpg"))); // NOI18N
        jLabel16.setText("jLabel1");
        jLabel16.setOpaque(true);

        jLabel17.setBackground(new java.awt.Color(51, 255, 102));
        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/roberto.jpg"))); // NOI18N
        jLabel17.setText("jLabel1");
        jLabel17.setOpaque(true);

        jLabel18.setBackground(new java.awt.Color(51, 255, 102));
        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/samuel.jpg"))); // NOI18N
        jLabel18.setText("jLabel1");
        jLabel18.setOpaque(true);

        jLabel19.setBackground(new java.awt.Color(51, 255, 102));
        jLabel19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/susana.jpg"))); // NOI18N
        jLabel19.setText("jLabel1");
        jLabel19.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtMensaje, javax.swing.GroupLayout.PREFERRED_SIZE, 823, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnEnviar, javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 555, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 555, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEnviar, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMensaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
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
        txtMensaje.requestFocus();
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
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField txtMensaje;
    // End of variables declaration//GEN-END:variables
}
