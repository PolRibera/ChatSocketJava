/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package projecte;




import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;


/**
 *
 * @author Ricard Sierra, Pol Ribera, Alex Montoya
 */
public class baseDades {

    private static Connection cn;
    private static Scanner sc;
    private static String HOST = "localhost";
    private static String DATABASE = "projectexat";
    private static String USER = "root";
    private static String PASSWORD = "admin";

    public static void main(String[] args) {
        if (esCorrectoDriver()) {
                cn = obtenerCon();
                sc = new Scanner(System.in);
                System.out.println("Id usuario:");
                int idusuario = sc.nextInt();
                System.out.println("Nom usuario:");
                sc.nextLine();
                String nom = sc.nextLine();
                System.out.println("Cognoms usuario:");
                String cognoms = sc.nextLine();
                System.out.println("Contraseña usuario:");
                String contraseña = sc.nextLine();
                usuarioInsert(idusuario, nom, cognoms, contraseña);

        }
    }

    public static Connection obtenerCon() {
        if (cn == null) {
            try {
                cn = DriverManager.getConnection("jdbc:mysql://" + HOST + ":3306/" + DATABASE + "?autoReconnect=true", USER, PASSWORD);
                System.out.println("S'ha conectat correctament a la base de dades!!");
            } catch (SQLException ex) {
                System.out.println("La conexio a la base de dades ha fallat!");
            }
        }
        return cn;
    }

    private static boolean esCorrectoDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return true;
        } catch (ClassNotFoundException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

    private static void usuarioInsert(int idusuario, String name, String cognoms, String contraseña) {
        try {
            PreparedStatement st = cn.prepareStatement("INSERT INTO usuario(idusuario, nom, cognoms, contraseña) VALUES (?, ?, ?, ?);");
            st.setInt(1, idusuario);
            st.setString(2, name);
            st.setString(3, cognoms);
            st.setString(4, contraseña);
            st.executeUpdate();

            PreparedStatement st2 = cn.prepareStatement("SELECT idusuario, nom, cognoms, contraseña FROM usuario;");
            ResultSet rs = st2.executeQuery();
            while (rs.next()) {
                System.out.println( rs.getString("idusuario") + " = " + rs.getString("nom"));
            }
        } catch (SQLException e) {
            System.out.println( e.getMessage());
        }
    }
    
    
}