/*
 * ©Informática Atlántida 2019.
 * Derechos Reservados.
 * 
 * Este software es propiedad intelectual de Informática Atlántida (Infatlan). La información contenida
 * es de carácter confidencial y no deberá revelarla. Solamente podrá utilizarlo de conformidad con los
 * términos del contrato suscrito con Informática Atlántida S.A.
 */
package hn.bancatlan.main;

import hn.bancatlan.util.Util;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generación de reportes de cierre caja empresarial.
 *
 * @author Erick Fabricio Martínez Castellanos
 * (<a href='mailto:efmartinez@bancatlan.hn'>efmartinez@bancatlan.hn</a>)
 * @version 1.0.0 12-abr-2019
 */
public class Main {

    /**
     * Inicio de aplicación.
     * @param args
     */
    public static void main(String[] args) {
        //1- Configuracion
        Util.configurar();
        //2- Procesar
        procesar();
    }

    /**
     * Genera el cierre de reportes.
     */
    public static void procesar() {

        String fecha = Util.getFechaHoraActual("yyyyMMdd");
        String hora = Util.getFechaHoraActual("HH:mm:ss");
        String canal = "V";
        int correlativo = 0;

        try {

            PreparedStatement ps1 = Util.CONEXION.prepareStatement(Util.QUERY_SELECT_EMPRESAS);
            ResultSet rs1 = ps1.executeQuery();

            while (rs1.next()) {//Empresa

                String empresa = rs1.getString(1);
                //System.out.println(empresa);

                //Cantidad de servicios
                PreparedStatement ps2 = Util.CONEXION.prepareStatement(Util.QUERY_SELECT_COUNT_SERVICIOS);
                ps2.setString(1, empresa);
                ResultSet rs2 = ps2.executeQuery();

                int servicios = 0;
                if (rs2.next()) {
                    servicios = rs2.getInt(1); //Cantidad de servicios
                }

                //Servicios
                if (servicios > 0) {
                    correlativo++;

                    //Escribe en CAECEA
                    PreparedStatement ps3 = Util.CONEXION.prepareStatement(Util.QUERY_INSERT_CAECEA);
                    ps3.setString(1, fecha);//CEAFEC
                    ps3.setString(2, hora);//CEAHOR
                    ps3.setString(3, canal);//CEACAN
                    ps3.setInt(4, correlativo);//CEACOR
                    ps3.setString(5, "H");//CEATIP
                    ps3.setString(6, "I");//CEASTA
                    ps3.setString(7, "PAGOS");//CEATIF
                    ps3.setString(8, fecha);//CEAFEI
                    ps3.setString(9, fecha);//CEAFEF
                    ps3.setString(10, empresa);//CEAEMP
                    ps3.setString(11, "S");//CEATCO
                    ps3.setString(12, "");//CEACOE
                    ps3.setString(13, "C");//CEATGE
                    ps3.executeUpdate();
                    ps3.close();

                    PreparedStatement ps4 = Util.CONEXION.prepareStatement(Util.QUERY_SELECT_SERVICIOS);
                    ps4.setString(1, empresa);
                    ResultSet rs4 = ps4.executeQuery();

                    while (rs4.next()) {
                        //Escribe en CAECEAS
                        String servicio = rs4.getString(1);
                        //System.out.println(empresa + " -> " + servicio);

                        //Escribe en CAECEAS
                        PreparedStatement ps5 = Util.CONEXION.prepareStatement(Util.QUERY_INSERT_CAECEAS);
                        ps5.setString(1, fecha);//CEAFEC
                        ps5.setString(2, hora);//CEAHOR
                        ps5.setString(3, canal);//CEACAN
                        ps5.setInt(4, correlativo);//CEACOR
                        ps5.setString(5, servicio);//CEASER
                        ps5.setString(6, "I");//CEASERE
                        ps5.executeUpdate();
                        ps5.close();

                    }

                    rs4.close();
                    ps4.close();

                }

                rs2.close();
                ps2.close();

            }

            rs1.close();
            ps1.close();

        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        Util.print("Cierre generado --> fecha: " + fecha + " hora: " + hora + " canal: " + canal + " correlativo: " + correlativo);
    }

}
