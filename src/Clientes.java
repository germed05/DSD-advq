
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Clientes extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Clientes.class.getName());
    private boolean esMiTurno = false;
    private String nombreTurno = null;

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
        
        Conexion cnx = new Conexion();
        String victorias = cnx.obtenerVictorias(usuarioActivo);
        LVeces_Ganadas.setText("Has ganado " + victorias + " veces");
        
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
        if (out != null) {
            out.println("UPDATE_USERS"); // <--- NUEVO: Pedimos la lista al servidor
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
            } //Mensajes recibidos
            else if (linea.startsWith("MSG|")) {
                String[] partes = linea.split("\\|", 3);
                if (partes.length == 3) {
                    try {
                        String remitente = partes[1];
                        String texto = partes[2];
                        String decifrado = AES.descifrar(texto);
                        if (remitente.equals(usuarioActivo)) {
                            appendMensaje("[Tú]: " + decifrado + "\n");
                        } else {
                            appendMensaje("[" + remitente + "]: " + decifrado + "\n");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } //Alertas del sistema
            else if (linea.startsWith("SYSTEM|")) {
                appendMensaje(">> " + linea.substring(7) + "\n");
            } else if (linea.startsWith("GAME_WIN|")) {
                String ganador = linea.substring(9);
                JOptionPane.showMessageDialog(this, "¡" + ganador + " adivinó el personaje!");

                try {
                    socket.close();   // cerrar conexión con el servidor
                } catch (Exception e) {
                    e.printStackTrace();
                }

                this.dispose();
                new JFLobby(usuarioActivo).setVisible(true);

            } else if (linea.startsWith("TURN|")) {
                nombreTurno = linea.substring(5);
                esMiTurno = nombreTurno.trim().equals(usuarioActivo.trim());

                appendMensaje(">> Turno de: " + nombreTurno + "\n");
                appendMensaje(">> Mi usuario: " + usuarioActivo + "\n");

                habilitarTablero(esMiTurno);
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
        btnEnviar = new javax.swing.JButton();
        txtMensaje = new javax.swing.JTextField();
        lblManuel = new javax.swing.JLabel();
        lblJorge = new javax.swing.JLabel();
        lblMaria = new javax.swing.JLabel();
        lblPablo = new javax.swing.JLabel();
        lblPaco = new javax.swing.JLabel();
        lblPedro = new javax.swing.JLabel();
        lblPepe = new javax.swing.JLabel();
        lblRicardo = new javax.swing.JLabel();
        lblTomas = new javax.swing.JLabel();
        lblRoberto = new javax.swing.JLabel();
        lblSamuel = new javax.swing.JLabel();
        lblSusana = new javax.swing.JLabel();
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

        btnEnviar.setText("Enviar");
        btnEnviar.addActionListener(this::btnEnviarActionPerformed);

        lblManuel.setBackground(new java.awt.Color(51, 255, 102));
        lblManuel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/manuel.jpg"))); // NOI18N
        lblManuel.setText("jLabel1");
        lblManuel.setOpaque(true);
        lblManuel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblManuelMouseClicked(evt);
            }
        });

        lblJorge.setBackground(new java.awt.Color(51, 255, 102));
        lblJorge.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/jorge.jpg"))); // NOI18N
        lblJorge.setText("jLabel1");
        lblJorge.setOpaque(true);
        lblJorge.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblJorgeMouseClicked(evt);
            }
        });

        lblMaria.setBackground(new java.awt.Color(51, 255, 102));
        lblMaria.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/maria.jpg"))); // NOI18N
        lblMaria.setText("jLabel1");
        lblMaria.setOpaque(true);
        lblMaria.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblMariaMouseClicked(evt);
            }
        });

        lblPablo.setBackground(new java.awt.Color(51, 255, 102));
        lblPablo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/pablo.jpg"))); // NOI18N
        lblPablo.setText("jLabel1");
        lblPablo.setOpaque(true);
        lblPablo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblPabloMouseClicked(evt);
            }
        });

        lblPaco.setBackground(new java.awt.Color(51, 255, 102));
        lblPaco.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/paco.jpg"))); // NOI18N
        lblPaco.setText("jLabel1");
        lblPaco.setOpaque(true);
        lblPaco.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblPacoMouseClicked(evt);
            }
        });

        lblPedro.setBackground(new java.awt.Color(51, 255, 102));
        lblPedro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/pedro.jpg"))); // NOI18N
        lblPedro.setText("jLabel1");
        lblPedro.setOpaque(true);
        lblPedro.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblPedroMouseClicked(evt);
            }
        });

        lblPepe.setBackground(new java.awt.Color(51, 255, 102));
        lblPepe.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/pepe.jpg"))); // NOI18N
        lblPepe.setText("jLabel1");
        lblPepe.setOpaque(true);
        lblPepe.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblPepeMouseClicked(evt);
            }
        });

        lblRicardo.setBackground(new java.awt.Color(51, 255, 102));
        lblRicardo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/ricardo.jpg"))); // NOI18N
        lblRicardo.setText("jLabel1");
        lblRicardo.setOpaque(true);
        lblRicardo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblRicardoMouseClicked(evt);
            }
        });

        lblTomas.setBackground(new java.awt.Color(51, 255, 102));
        lblTomas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/tomas.jpg"))); // NOI18N
        lblTomas.setText("jLabel1");
        lblTomas.setOpaque(true);
        lblTomas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblTomasMouseClicked(evt);
            }
        });

        lblRoberto.setBackground(new java.awt.Color(51, 255, 102));
        lblRoberto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/roberto.jpg"))); // NOI18N
        lblRoberto.setText("jLabel1");
        lblRoberto.setOpaque(true);
        lblRoberto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblRobertoMouseClicked(evt);
            }
        });

        lblSamuel.setBackground(new java.awt.Color(51, 255, 102));
        lblSamuel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/samuel.jpg"))); // NOI18N
        lblSamuel.setText("jLabel1");
        lblSamuel.setOpaque(true);
        lblSamuel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblSamuelMouseClicked(evt);
            }
        });

        lblSusana.setBackground(new java.awt.Color(51, 255, 102));
        lblSusana.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/susana.jpg"))); // NOI18N
        lblSusana.setText("jLabel1");
        lblSusana.setOpaque(true);
        lblSusana.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblSusanaMouseClicked(evt);
            }
        });

        LVeces_Ganadas.setFont(new java.awt.Font("Comic Sans MS", 0, 14)); // NOI18N
        LVeces_Ganadas.setText("Has ganado x veces");

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
                        .addComponent(btnEnviar, javax.swing.GroupLayout.PREFERRED_SIZE, 64, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblRoberto, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblPaco, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblJorge, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblSamuel, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblPedro, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblManuel, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblSusana, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblPepe, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblMaria, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTomas, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblRicardo, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblPablo, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(LVeces_Ganadas, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(LVeces_Ganadas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 555, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 555, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblJorge, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblManuel, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblMaria, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblPablo, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblPaco, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblPedro, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblPepe, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblRicardo, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblRoberto, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblSamuel, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblSusana, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblTomas, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEnviar, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMensaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnEnviarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnviarActionPerformed
        try {
            String texto = txtMensaje.getText().trim();

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

    private void lblJorgeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblJorgeMouseClicked
        verificarPersonaje("Jorge");
    }//GEN-LAST:event_lblJorgeMouseClicked

    private void lblManuelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblManuelMouseClicked
        verificarPersonaje("Manuel");
    }//GEN-LAST:event_lblManuelMouseClicked

    private void lblMariaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblMariaMouseClicked
        verificarPersonaje("María");
    }//GEN-LAST:event_lblMariaMouseClicked

    private void lblPabloMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblPabloMouseClicked
        verificarPersonaje("Pablo");
    }//GEN-LAST:event_lblPabloMouseClicked

    private void lblPacoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblPacoMouseClicked
        verificarPersonaje("Paco");
    }//GEN-LAST:event_lblPacoMouseClicked

    private void lblPedroMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblPedroMouseClicked
        verificarPersonaje("Pedro");
    }//GEN-LAST:event_lblPedroMouseClicked

    private void lblPepeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblPepeMouseClicked
        verificarPersonaje("Pepe");
    }//GEN-LAST:event_lblPepeMouseClicked

    private void lblRicardoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblRicardoMouseClicked
        verificarPersonaje("Ricardo");
    }//GEN-LAST:event_lblRicardoMouseClicked

    private void lblRobertoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblRobertoMouseClicked
        verificarPersonaje("Roberto");
    }//GEN-LAST:event_lblRobertoMouseClicked

    private void lblSamuelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSamuelMouseClicked
        verificarPersonaje("Samuel");
    }//GEN-LAST:event_lblSamuelMouseClicked

    private void lblSusanaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSusanaMouseClicked
        verificarPersonaje("Susana");
    }//GEN-LAST:event_lblSusanaMouseClicked

    private void lblTomasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTomasMouseClicked
        verificarPersonaje("Tomás");
    }//GEN-LAST:event_lblTomasMouseClicked

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
    private javax.swing.JLabel LVeces_Ganadas;
    private javax.swing.JTextArea TAMensajes;
    private javax.swing.JTextArea TAUsuarios;
    private javax.swing.JButton btnEnviar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblJorge;
    private javax.swing.JLabel lblManuel;
    private javax.swing.JLabel lblMaria;
    private javax.swing.JLabel lblPablo;
    private javax.swing.JLabel lblPaco;
    private javax.swing.JLabel lblPedro;
    private javax.swing.JLabel lblPepe;
    private javax.swing.JLabel lblRicardo;
    private javax.swing.JLabel lblRoberto;
    private javax.swing.JLabel lblSamuel;
    private javax.swing.JLabel lblSusana;
    private javax.swing.JLabel lblTomas;
    private javax.swing.JTextField txtMensaje;
    // End of variables declaration//GEN-END:variables

    private void verificarPersonaje(String personajeSeleccionado) {
        if (!esMiTurno) {
            JOptionPane.showMessageDialog(this, "Espera tu turno.");
            return;
        }

        esMiTurno = false;
        habilitarTablero(false);

        if (out != null) {
            out.println("GUESS|" + personajeSeleccionado);
        }
    }

    private void habilitarTablero(boolean habilitar) {
        lblManuel.setEnabled(habilitar);
        lblJorge.setEnabled(habilitar);
        lblMaria.setEnabled(habilitar);
        lblPablo.setEnabled(habilitar);
        lblPaco.setEnabled(habilitar);
        lblPedro.setEnabled(habilitar);
        lblPepe.setEnabled(habilitar);
        lblRicardo.setEnabled(habilitar);
        lblTomas.setEnabled(habilitar);
        lblRoberto.setEnabled(habilitar);
        lblSamuel.setEnabled(habilitar);
        lblSusana.setEnabled(habilitar);
    }
}
