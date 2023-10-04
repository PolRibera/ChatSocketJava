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
 * @author polri
 */
public class baseDades {

    private static Connection cn;
    private static Scanner sc;
    private static String HOST = "localhost";
    private static String DATABASE = "fromjava";
    private static String USER = "root";
    private static String PASSWORD = "1234";

    public static void main(String[] args) {
        if (esCorrectoDriver()) {
                cn = obtenerCon();
                sc = new Scanner(System.in);
                System.out.println("Matricula Alumne:");
                String matriula = sc.next();
                System.out.println("Nom Alumne:");
                sc.nextLine();
                String nom = sc.nextLine();
                alumneInsert(matriula, nom);

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

    private static void alumneInsert(String matricula, String name) {
        try {
            PreparedStatement st = cn.prepareStatement("INSERT INTO alumne(matriula, nom) VALUES (?, ?);");
            st.setString(1, matricula);
            st.setString(2, name);
            st.executeUpdate();

            PreparedStatement st2 = cn.prepareStatement("SELECT matriula,nom FROM alumne;");
            ResultSet rs = st2.executeQuery();
            while (rs.next()) {
                System.out.println( rs.getString("matriula") + " = " + rs.getString("nom"));
            }
        } catch (SQLException e) {
            System.out.println( e.getMessage());
        }
    }
}