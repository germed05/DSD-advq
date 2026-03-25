
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author drlias
 */
public class Conexion {

    String url = "https://xglizaola.fun/mysql.php";

    public Conexion() {
        this.url = url;
    }

    //--------------------------------------------------------------------------
    //-------- Probar si hay conexión exitosa con el servidor
    //--------------------------------------------------------------------------
    public boolean probarConexion() {
        String consulta = "SELECT 1";

        try {
            String resultado = peticionHttpPost(url, consulta);

            if (resultado != null && !resultado.trim().isEmpty()) {
                if (resultado.contains("ERROR") || resultado.contains("Bad Request")) {
                    System.out.println("Error detectado en la conexión: " + resultado);
                    return false;
                }
                return true;
            }
        } catch (Exception e) {
            System.out.println("Excepción en Java: " + e.getMessage());
        }

        return false;
    }

    //=========================================================================
    // <editor-fold defaultstate="collapsed" desc="Funciones Solicitudes a BD">   
    //--------------------------------------------------------------------------
    //-------- Muestra el resultado de una consulta en una JTable
    //--------------------------------------------------------------------------
    public int entablar(String consulta, JTable malla) {
        int correcta = 0;
        String resultado = peticionHttpPost(url, consulta);

        if (resultado != null) {
            String[] lineas = resultado.split("->");
            DefaultTableModel modelo = (DefaultTableModel) malla.getModel();

            modelo.setColumnCount(0);
            modelo.setRowCount(0);

            String cols[] = lineas[0].split(",");
            for (String col : cols) {
                modelo.addColumn(col.toUpperCase());
            }

            for (int k = 1; k < lineas.length; k++) {
                String ren[] = lineas[k].split(",");
                modelo.addRow(ren);
            }
            correcta = 1;
        }
        return correcta;
    }

    //--------------------------------------------------------------------------
    //-------- muestra los datos de un campo en un combobox
    //--------------------------------------------------------------------------
    public int seleccionar(String consulta, JComboBox box) {
        int correcta = 1;
        ArrayList<String> datos = new ArrayList();
        String[] lineas = null;

        try {
            String resultado = peticionHttpPost(url, consulta);
            if (resultado != null) {
                lineas = resultado.split("->");
                for (int k = 1; k < lineas.length; k++) {
                    datos.add(lineas[k]);
                }
                DefaultComboBoxModel cbm = new DefaultComboBoxModel(datos.toArray());
                box.setModel(cbm);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            correcta = 0;
        }
        return correcta;
    }

    //--------------------------------------------------------------------------
    //-------- regresa el valor de una consulta de un solo dato
    //--------------------------------------------------------------------------
    public String obtenerDato(String consulta) {
        String dato = "";
        try {
            String resultado = peticionHttpPost(url, consulta);
            if (resultado != null) {
                String[] lineas = resultado.split("->");
                if (lineas.length > 1) {
                    dato = lineas[1];
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return dato;
    }

    //--------------------------------------------------------------------------
    //-------- regresa el valor de un campo que coincida con un dato dado
    //--------------------------------------------------------------------------
    public String buscarDato(String tabla, String columnaDeseada, String campoBuscado, String valorBuscado) {
        String consulta = "SELECT " + columnaDeseada + " FROM " + tabla
                + " WHERE " + campoBuscado + " = '" + valorBuscado + "'";
        String dato = "";

        try {
            String resultado = peticionHttpPost(url, consulta);
            if (resultado != null) {
                String[] lineas = resultado.split("->");
                if (lineas.length > 1) {
                    dato = lineas[1];
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return dato;
    }

    //--------------------------------------------------------------------------
    //-------- regresa una lista con los registros del resultado de la consulta
    //--------------------------------------------------------------------------
    public ArrayList<ArrayList<String>> consultar(String consulta) {
        ArrayList<ArrayList<String>> datos = new ArrayList();
        String resultado = peticionHttpPost(url, consulta);

        if (resultado != null) {
            String[] lineas = resultado.split("->");
            for (int idx = 1; idx < lineas.length; idx++) {
                String linea = lineas[idx];
                ArrayList<String> renglon = new ArrayList();
                String[] valores = linea.split(",");
                for (Object elem : valores) {
                    renglon.add(elem.toString());
                }
                datos.add(renglon);
            }
        }
        return datos;
    }
    // </editor-fold>

    //=========================================================================
    // <editor-fold defaultstate="collapsed" desc="Funciones de fechas">   
    //========================================================================
    public String obtenerFechaHora() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        int mSecond = c.get(Calendar.SECOND);
        int mMili = c.get(Calendar.MILLISECOND);

        return year + "-" + month + "-" + day + " " + mHour + ":" + mMinute + ":" + mSecond + "." + mMili;
    }

    public String obtenerFechaHoraE() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        int mSecond = c.get(Calendar.SECOND);
        int mMili = c.get(Calendar.MILLISECOND);

        return day + "/" + month + "/" + year + " " + mHour + ":" + mMinute + ":" + mSecond;
    }

    public String toSQL(java.util.Date fecha) {
        if (fecha == null) {
            return "";
        }
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(fecha);
    }

    public java.util.Date toDate(String fecha) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(fecha);
        } catch (java.text.ParseException e) {
            System.out.println("Error al convertir la fecha: " + e.getMessage());
            return null;
        }
    }

    public String obtenerFecha() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        return year + "-" + month + "-" + day;
    }

    public String obtenerHora() {
        Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        int mSecond = c.get(Calendar.SECOND);
        int mMili = c.get(Calendar.MILLISECOND);

        return mHour + ":" + mMinute + ":" + mSecond + "." + mMili;
    }
    // </editor-fold> 

    //=========================================================================
    // <editor-fold defaultstate="collapsed" desc="Funciones para peticiones">   
    public String peticionHttpPost(String url_visitar, String query) {
        HttpURLConnection con = null; // Definimos fuera para poder cerrar en finally
        try {
            URL urlv = new URL(url_visitar);
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("key", "secret");
            params.put("query", query);

            StringBuilder postdata = new StringBuilder();
            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (postdata.length() != 0) {
                    postdata.append("&");
                }
                postdata.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postdata.append("=");
                postdata.append(URLEncoder.encode(param.getValue().toString(), "UTF-8"));
            }

            byte[] postbytes = postdata.toString().getBytes("UTF-8");

            con = (HttpURLConnection) urlv.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("Content-Length", String.valueOf(postbytes.length));
            con.setDoOutput(true);
            con.setConnectTimeout(5000); // Evita que el programa se congele si el server cae
            con.setReadTimeout(5000);

            con.getOutputStream().write(postbytes);

            // Uso de Try-with-resources para el BufferedReader (se cierra solo)
            try (BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String linea;
                StringBuilder resultado = new StringBuilder();
                while ((linea = rd.readLine()) != null) {
                    if (resultado.length() != 0) {
                        resultado.append("->");
                    }
                    resultado.append(linea);
                }
                return resultado.toString();
            }

        } catch (IOException e) {
            System.err.println("Error de Red: " + e.getMessage());
            return null;
        } finally {
            if (con != null) {
                con.disconnect(); // Cerramos la conexión física al servidor
            }
        }
    }

    public static String peticionHttpGet(String url) throws Exception {
        StringBuilder resultado = new StringBuilder();
        URL urlv = new URL(url);
        HttpURLConnection conexion = (HttpURLConnection) urlv.openConnection();
        conexion.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
        String linea;
        while ((linea = rd.readLine()) != null) {
            resultado.append(linea);
        }
        rd.close();
        return resultado.toString();
    }
    // </editor-fold> 
    
    
    public String obtenerHash(String usuario) { 
        String consulta = "SELECT psw FROM advq WHERE usr = '" + usuario + "'";
        return obtenerDato(consulta);
    }

    //=========================================================================
    //-------------------------------------------------------
    //---Obtiene los nombres de las columnas de una tabla
    //-------------------------------------------------------
    public String[] columnas(String tabla) {
        String consulta = "SELECT * FROM " + tabla;
        String[] columnas = null;
        try {
            String resultado = peticionHttpPost(url, consulta);
            if (resultado != null) {
                String[] lineas = resultado.split("->");
                columnas = lineas[0].split(",");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return columnas;
    }

    //-------------------------------------------------------
    //---Insertar un registro en una tabla
    //-------------------------------------------------------
    public int insertar(String tabla, String[] valores) {
        int correcta = 0;
        String sql = "INSERT INTO " + tabla + " VALUES(";
        for (int k = 0; k < valores.length; k++) {
            if (valores[k].equals("")) {
                sql += "''";
            } else {
                sql += "'" + valores[k] + "'";
            }
            if (k < valores.length - 1) {
                sql += ",";
            }
        }
        sql += ")";

        System.out.println("INSERTAR: " + sql);

        try {
            String resultado = peticionHttpPost(url, sql);
            if (resultado.contains("AFFECTED ROWS")) {
                correcta = 1;
            }
            System.out.println("Resultado: " + resultado);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return correcta;
    }

    //-------------------------------------------------------
    //---Actualizar un registro en una tabla
    //-------------------------------------------------------
    public int actualizar(String tabla, String[] valores) {
        int correcta = 0;
        String[] cols = columnas(tabla);
        String sql = "UPDATE " + tabla + " SET ";

        for (int k = 1; k < valores.length; k++) {
            sql += cols[k] + " = '" + valores[k] + "'";
            if (k < valores.length - 1) {
                sql += ",";
            }
        }
        sql += " WHERE " + cols[0] + " = '" + valores[0] + "'";

        System.out.println("ACTUALIZAR: " + sql);

        try {
            String resultado = peticionHttpPost(url, sql);
            if (resultado.contains("AFFECTED ROWS")) {
                correcta = 1;
            }
            System.out.println("Resultado: " + resultado);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return correcta;
    }

    //-------------------------------------------------------
    //---Borra el registro en una tabla
    //-------------------------------------------------------
    public int borrar(String tabla, String[] valores) {
        int correcta = 0;
        String[] cols = columnas(tabla);
        String sql = "DELETE FROM " + tabla + " WHERE " + cols[0] + " = '" + valores[0] + "'";

        System.out.println("BORRAR: " + sql);

        try {
            String resultado = peticionHttpPost(url, sql);
            if (resultado.contains("AFFECTED ROWS")) {
                correcta = 1;
            }
            System.out.println("Resultado: " + resultado);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return correcta;
    }

    //-------------------------------------------------------
    //--- BORRAR CON 2 CONDICIONES 
    //-------------------------------------------------------
    public int borrarDoble(String tabla, String col1, String val1, String col2, String val2) {
        int correcta = 0;
        String sql = "DELETE FROM " + tabla + " WHERE " + col1 + " = '" + val1 + "' AND " + col2 + " = '" + val2 + "'";

        System.out.println("BORRAR DOBLE: " + sql);

        try {
            String resultado = peticionHttpPost(url, sql);
            if (resultado != null && resultado.contains("AFFECTED ROWS")) {
                correcta = 1;
            }
            System.out.println("Resultado: " + resultado);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return correcta;
    }

    //-------------------------------------------------------
    //--- ACTUALIZAR CON 2 CONDICIONES
    //-------------------------------------------------------
    public int actualizarDoble(String tabla, String[] valores, String col1, String val1, String col2, String val2) {
        int correcta = 0;
        String[] cols = columnas(tabla);
        String sql = "UPDATE " + tabla + " SET ";

        for (int k = 0; k < valores.length; k++) {
            if (k < cols.length) {
                sql += cols[k] + " = '" + valores[k] + "'";
                if (k < valores.length - 1) {
                    sql += ",";
                }
            }
        }
        sql += " WHERE " + col1 + " = '" + val1 + "' AND " + col2 + " = '" + val2 + "'";

        System.out.println("ACTUALIZAR DOBLE: " + sql);

        try {
            String resultado = peticionHttpPost(url, sql);
            if (resultado != null && resultado.contains("AFFECTED ROWS")) {
                correcta = 1;
            }
            System.out.println("Resultado: " + resultado);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return correcta;
    }
    //-------------------------------------------------------
    //--- Obtener cantidad de victorias
    //-------------------------------------------------------
    public String obtenerVictorias(String usuario) {
        String consulta = "SELECT win FROM advq WHERE usr = '" + usuario + "'";
        return obtenerDato(consulta);
    }

    //-------------------------------------------------------
    //--- Sumar 1 victoria al jugador
    //-------------------------------------------------------
        //-------------------------------------------------------
    //--- Sumar 1 victoria al jugador (Versión Segura)
    //-------------------------------------------------------
    public void sumarVictoria(String usuario) {
        //Obtenemos las victorias actuales del servidor
        String victoriasStr = obtenerVictorias(usuario);
        int victoriasActuales = 0;
        try {
            if (victoriasStr != null && !victoriasStr.trim().isEmpty()) {
                victoriasActuales = Integer.parseInt(victoriasStr.trim());
            }
        } catch (NumberFormatException e) {
            System.out.println("Error al parsear victorias: " + e.getMessage());
        }
        //Hacemos la suma localmente en Java
        victoriasActuales++;

        //Enviamos el valor absoluto, idéntico a como actualizas otras tablas
        String consulta = "UPDATE advq SET win = '" + victoriasActuales + "' WHERE usr = '" + usuario + "'";
        
        System.out.println("--- ACTUALIZACIÓN ---");
        System.out.println("Enviando: " + consulta);
        String respuesta = peticionHttpPost(url, consulta);
        System.out.println("Respuesta MySQL/PHP: " + respuesta);
    }
}
