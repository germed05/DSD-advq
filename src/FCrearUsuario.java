import org.mindrot.jbcrypt.BCrypt; //libreria de bycript
import java.sql.*;
import javax.swing.JOptionPane;

public class FCrearUsuario extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FCrearUsuario.class.getName());

    public FCrearUsuario() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.getRootPane().setDefaultButton(BCrear);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        TFUsuario = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        BCrear = new javax.swing.JButton();
        JFPassword = new javax.swing.JPasswordField();
        BSesion = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(358, 459));
        setSize(new java.awt.Dimension(358, 459));

        TFUsuario.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel2.setFont(new java.awt.Font("Comic Sans MS", 0, 18)); // NOI18N
        jLabel2.setText("Usuario");

        jLabel3.setFont(new java.awt.Font("Comic Sans MS", 0, 18)); // NOI18N
        jLabel3.setText("Contraseña");

        BCrear.setText("Crear");
        BCrear.addActionListener(this::BCrearActionPerformed);

        BSesion.setFont(new java.awt.Font("Calibri Light", 0, 12)); // NOI18N
        BSesion.setText("<html><u>Iniciar Sesion</u></html>");
        BSesion.setContentAreaFilled(false);
        BSesion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                BSesionMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                BSesionMouseExited(evt);
            }
        });
        BSesion.addActionListener(this::BSesionActionPerformed);

        jLabel4.setFont(new java.awt.Font("Leelawadee UI", 1, 36)); // NOI18N
        jLabel4.setText("Crear Usuario");
        jLabel4.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(BCrear, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 57, Short.MAX_VALUE)
                .addComponent(BSesion, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(TFUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(JFPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(jLabel4)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(52, 52, 52)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(TFUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(JFPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(BSesion)
                    .addComponent(BCrear, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BCrearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BCrearActionPerformed
        Conexion cnx = new Conexion();
        String usuario = TFUsuario.getText().trim();
        String passwordPlana = new String(JFPassword.getPassword());

        if (usuario.isEmpty() || passwordPlana.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, llena todos los campos");
            return;
        }

        String passwordEncriptada = BCrypt.hashpw(passwordPlana, BCrypt.gensalt());//uso de la libreria bycrypt para encriptar contraseña
        String[] datos = {usuario, passwordEncriptada, "0"};

        int insertado = cnx.insertar("advq", datos);//funciones del crud agregadas en Conexion

        if (insertado == 1) {
            JOptionPane.showMessageDialog(this, "¡Usuario '" + usuario + "' creado con éxito!");
        } else {
            JOptionPane.showMessageDialog(this, "El nombre de usuario ya existe");
        }

    }//GEN-LAST:event_BCrearActionPerformed

    private void BSesionMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BSesionMouseEntered
        BSesion.setText("<html><div style='text-align: center; color: blue;'><u>Iniciar Sesion</u></div></html>");
    }//GEN-LAST:event_BSesionMouseEntered

    private void BSesionMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BSesionMouseExited
        BSesion.setText("<html><u>Iniciar Sesion</u></html>");
    }//GEN-LAST:event_BSesionMouseExited

    private void BSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BSesionActionPerformed
        new FSesion().setVisible(true);
        dispose();
        
    }//GEN-LAST:event_BSesionActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new FCrearUsuario().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BCrear;
    private javax.swing.JButton BSesion;
    private javax.swing.JPasswordField JFPassword;
    private javax.swing.JTextField TFUsuario;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    // End of variables declaration//GEN-END:variables
}
