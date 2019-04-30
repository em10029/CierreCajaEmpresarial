/*
 * ©Informática Atlántida 2019.
 * Derechos Reservados.
 * 
 * Este software es propiedad intelectual de Informática Atlántida (Infatlan). La información contenida
 * es de carácter confidencial y no deberá revelarla. Solamente podrá utilizarlo de conformidad con los
 * términos del contrato suscrito con Informática Atlántida S.A.
 */

package hn.bancatlan.util;

import hn.bancatlan.main.Main;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Erick Fabricio Martínez Castellanos
 * (<a href='mailto:efmartinez@bancatlan.hn'>efmartinez@bancatlan.hn</a>)
 * @version 1.0.0 12-abr-2019
 */
public class Util {
   
    /**
     * Query para la busqueda de empresas activas.
     */
    public static final String QUERY_SELECT_EMPRESAS = "SELECT EMPNUM FROM CAEDTA.CAEEMP WHERE EMPEST = 'A' ORDER BY EMPNUM ASC";
    
    /**
     * Query para la busqueda de los servicio de la empresa.
     */
    public static final String QUERY_SELECT_SERVICIOS = "SELECT SERNUM FROM CAEDTA.CAESER WHERE SEREST = 'A' AND EMPNUM = ? ORDER BY SERNUM ASC";
    
    /**
     * Query para la cantidad de servicios de la empresa.
     */
    public static final String QUERY_SELECT_COUNT_SERVICIOS = "SELECT COUNT(*) AS TOTAL FROM CAEDTA.CAESER WHERE SEREST = 'A' AND EMPNUM = ? ";
    
    /**
     * Query cierre CAECEA.
     */
    public static final String QUERY_INSERT_CAECEA = "INSERT INTO CAEDTA.CAECEA (CEAFEC,CEAHOR,CEACAN,CEACOR,CEATIP,CEASTA,CEATIF,CEAFEI,CEAFEF,CEAEMP,CEATCO,CEACOE,CEATGE) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
    
    /**
     * Query cierre CAECEAS.
     */
    public static final String QUERY_INSERT_CAECEAS = "INSERT INTO CAEDTA.CAECEAS(CEAFEC,CEAHOR,CEACAN,CEACOR,CEASER,CEASERE)VALUES(?,?,?,?,?,?)";
    
    /**
     * Query validacion de cierre CAECIE
     */
    public static final String QUERY_SELECT_CIERRE = "SELECT CIEPOC FROM CAEDTA.CAECIE WHERE CIEFEC = ?";
    
    /**
     * Conexion
     */
    public static Connection CONEXION;
    
    /**
     * Tiempo de espera
     */
    public static long TIEMPO;
    
    /**
     * Carga el archivo <b>config.properties</b> y obtiene las variables de
     * propiedades.
     *
     * @return boolean True si todas la variables han sido cargadas
     * exitosamente, False en caso contrario.
     */
    public static boolean configurar() {

        boolean config = false; //determinara si la configuracion del programa es correcta
        Properties propiedades = new Properties(); //prodiedades del programa
        InputStream configuracion = null; //archivo de propiedades

        try {
            //Obteniento la ruta absoluta del archivo de propiedades
            String ruta = System.getProperty("user.dir");
            String rutaAbsoluta = ruta + "/configs/config.properties";

            //Cargando el archivo de configuracion
            configuracion = new FileInputStream(rutaAbsoluta);
            propiedades.load(configuracion);

            //------------OBTENIENDO LAS VARIABLES DE CONFIGURACION------------//            
            String url = propiedades.getProperty("conexion.url").trim();            
            String user = propiedades.getProperty("conexion.user").trim();
            String password = propiedades.getProperty("conexion.password").trim();
            TIEMPO = Long.parseLong(propiedades.getProperty("control.time").trim());
            
            try {
                Class.forName("com.ibm.as400.access.AS400JDBCDriver");
                CONEXION = DriverManager.getConnection(url, user, password);                
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Util.class.getName()).log(Level.SEVERE, "ERROR ClassNotFoundException", ex);
            } catch (SQLException ex) {
                Logger.getLogger(Util.class.getName()).log(Level.SEVERE, "ERROR SQLException", ex);
            }
            
            configuracion.close();
            config = true;

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "ERROR con el archivo de congiguración config.properties no se encuentra.", ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "ERROR con el archivo de congiguración config.properties.", ex);
        } catch (NullPointerException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "ERROR con el archivo de congiguración config.properties las propiedades han sido renombradas.", ex);
        }

        return config;
    }
    
    /**
     * Escribe en el archivo .log del programa. Se utiliza para registrar los
     * procesos realizados.
     *
     * @param proceso
     */
    public static void log(String proceso) {

        FileWriter lector = null;
        PrintWriter escritor = null;

        try {
            //Obteniento la ruta absoluta del archivo App.log
            String ruta = System.getProperty("user.dir");
            String rutaAbsoluta = ruta + "/logs/App.log";

            //#1 Abrir
            lector = new FileWriter(rutaAbsoluta, true);
            escritor = new PrintWriter(lector);

            //#2 Escribir
            escritor.write(proceso + "\r\n");

            //#3 Cerrar
            escritor.close();
            lector.close();

        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Registra el proceso en el log de la aplicación y lo muestra en la
     * consola.
     *
     * @param string
     */
    public static void print(String string) {
        System.out.println(string);
        log(string);
    }
    
    /**
     * Obtiene la fecha y la hora según formato requerido.
     *
     * @param formato: Formato de fecha que se desea. Ejemplo: DD/MM/YYYY ->
     * 10/11/1994
     * @return Fecha del sistema.
     */
    public static String getFechaHoraActual(String formato) {
        SimpleDateFormat fecha = new SimpleDateFormat(formato);
        Date fechaActual = new Date(System.currentTimeMillis());
        return fecha.format(fechaActual);
    }
      
}
