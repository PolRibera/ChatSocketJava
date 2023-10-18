package projecte;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;
import java.util.logging.*;
import static projecte.baseDades.obtenerCon;

/**
 *
 * @author Ricard Sierra, Pol Ribera, Alex Montoya
 */
public class Servidor { //ÉS EL SERVIDOR, ENCARA QUE REP ELS FITXERS

    private static Connection cn;
    private static Scanner sc;
    private static String HOST = "localhost";
    private static String DATABASE = "projectexat";
    private static String USER = "root";
    private static String PASSWORD = "admin";

    public static Properties propertiesServer;
    public static Properties propertiesClient;

    public static void main(String[] args) throws IOException { //per provar el receptor de fitxers
        int PUERTO = 54322;
        ServerSocket skk = new ServerSocket(PUERTO);
        propertiesServer = new Properties();
        propertiesServer.load(new FileReader("configServer.properties"));
        propertiesClient.load(new FileReader("configClient.properties"));

        while (true) {
            try {
                Socket sk = skk.accept(); //accepta una conexión
                Server sv = new Server(sk);
                System.out.println("Escoltant el port " + PUERTO);
                System.out.println("connectant amb el client " + sk.getInetAddress().getHostAddress());
                sv.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Server extends Thread {

        static Socket sk;
        static String respostaUsuariRebut;
        static HashMap<String, Socket> usuariosConectados = new HashMap<>(); // Mapa para almacenar sockets de usuarios

        Server(Socket sk) {
            this.sk = sk;
        }

        @Override
        public void run() {
            try {
                DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
                DataInputStream dis = new DataInputStream(sk.getInputStream());
                boolean sortir = false;
                while (!sortir) {
                    System.out.println("llegit " + sk.getInetAddress() + ":" + sk.getPort());
                    respostaUsuariRebut = dis.readUTF();
                    switch (Integer.parseInt(respostaUsuariRebut)) {
                        case 1:
                            if (esCorrectoDriver()) {
                                cn = obtenerCon();
                                String idUsuari = dis.readUTF();
                                String contrasenya = dis.readUTF();
                                if (iniciSesio(idUsuari, contrasenya)) {
                                    usuariosConectados.put(idUsuari, sk);
                                    boolean sortirSesio = false;
                                    dos.writeUTF("true");
                                    while (!sortirSesio) {
                                        respostaUsuariRebut = dis.readUTF();
                                        switch (Integer.parseInt(respostaUsuariRebut)) {
                                            case 1:
                                                respostaUsuariRebut = dis.readUTF();
                                                String idgrupo;
                                                switch (Integer.parseInt(respostaUsuariRebut)) {
                                                    case 1:
                                                        idgrupo = dis.readUTF();
                                                        crearGrupo(idgrupo, idUsuari);
                                                        afegirUsuari(idgrupo, idUsuari, dos);
                                                        break;
                                                    case 2:
                                                        idgrupo = dis.readUTF();
                                                        if (comprobargrupo(idgrupo, idUsuari, dos)) {
                                                            respostaUsuariRebut = dis.readUTF();
                                                            String usuarinou;
                                                            boolean grup = false;
                                                            while(!grup){
                                                            switch (Integer.parseInt(respostaUsuariRebut)) {
                                                                case 1:
                                                                    usuarinou = dis.readUTF();
                                                                    afegirUsuari(idgrupo, usuarinou, dos);
                                                                    break;
                                                                case 2:
                                                                    usuarinou = dis.readUTF();
                                                                    if (usuarinou.equals(idUsuari)) {
                                                                        dos.writeUTF("admin");
                                                                    } else {
                                                                        esborrarUsuari(idgrupo, idUsuari, dos);
                                                                    }
                                                                    break;
                                                                case 3:
                                                                    borrarGrupo(idgrupo, idUsuari, dos);
                                                                    break;
                                                                case 4:
                                                                    llistarUsuarisGrup(idgrupo, dos);
                                                                    break;
                                                                case 5:
                                                                    dos.writeUTF("true");
                                                                    grup = true;
                                                                    break;
                                                            }
                                                            }
                                                        } else {

                                                        }
                                                        break;
                                                }
                                                break;
                                            case 2:
                                                break;
                                            case 3:
                                                break;
                                            case 4:
                                                boolean sortirConfigServer = false;
                                                while (!sortirConfigServer) {
                                                    respostaUsuariRebut = dis.readUTF();
                                                    switch (Integer.parseInt(respostaUsuariRebut)) {
                                                        case 1:
                                                            dos.writeUTF((String) propertiesServer.get("maxima_mida"));
                                                            dos.writeUTF((String) propertiesServer.get("maxima_conexions"));
                                                            dos.writeUTF((String) propertiesServer.get("contrasenya_base_dades"));
                                                            dos.writeUTF((String) propertiesServer.get("client_admin"));
                                                            dos.writeUTF((String) propertiesServer.get("nom_servidor"));
                                                            dos.writeUTF((String) propertiesServer.get("ruta_desa_fitxers"));
                                                            break;
                                                        case 2:
                                                            propertiesServer.setProperty("maxima_mida", dis.readUTF());
                                                            System.out.println("S’ha cambiat la mida maxima dels fitxers ha enviar.");
                                                            break;
                                                        case 3:
                                                            propertiesServer.setProperty("maxima_conexions", dis.readUTF());
                                                            System.out.println("S’ha cambiat les conexions maximas al servidor.");
                                                            break;
                                                        case 4:
                                                            propertiesServer.setProperty("contrasenya_base_dades", dis.readUTF());
                                                            System.out.println("S’ha cambiat la contrasenya de la base de dades.");
                                                            break;
                                                        case 5:
                                                            propertiesServer.setProperty("client_admin", dis.readUTF());
                                                            System.out.println("S’ha introduit un nou admin.");
                                                            break;
                                                        case 6:
                                                            propertiesServer.setProperty("nom_servidor", dis.readUTF());
                                                            System.out.println("S’ha cambiat el nom del servidor.");
                                                            break;
                                                        case 7:
                                                            propertiesServer.setProperty("ruta_desa_fitxers", dis.readUTF());
                                                            System.out.println("S’ha cambiat la ruta on es guarden el fitxers.");
                                                            break;
                                                        case 8:
                                                            dos.writeUTF("true");
                                                            sortirConfigServer = true;
                                                            break;

                                                    }
                                                }
                                                break;
                                            case 5:
                                                boolean sortirConfigClient = false;
                                                while (!sortirConfigClient) {
                                                    respostaUsuariRebut = dis.readUTF();
                                                    switch (Integer.parseInt(respostaUsuariRebut)) {
                                                        case 1:
                                                            dos.writeUTF((String) propertiesClient.get("nom_client"));
                                                            dos.writeUTF((String) propertiesClient.get("tamay_maxim"));
                                                            dos.writeUTF((String) propertiesClient.get("ip_default"));
                                                            dos.writeUTF((String) propertiesClient.get("port_default"));
                                                            break;
                                                        case 2:
                                                            propertiesClient.setProperty("nom_client", dis.readUTF());
                                                            System.out.println("S’ha cambiat el nom del client.");
                                                            break;
                                                        case 3:
                                                            propertiesClient.setProperty("tamay_maxim", dis.readUTF());
                                                            System.out.println("S’ha cambiat el tamay maxim de rebre/enviar del client.");
                                                            break;
                                                        case 4:
                                                            propertiesClient.setProperty("ip_default", dis.readUTF());
                                                            System.out.println("S’ha cambiat la ip.");
                                                            break;
                                                        case 5:
                                                            propertiesClient.setProperty("port_default", dis.readUTF());
                                                            System.out.println("S’ha cambiat el port.");
                                                            break;
                                                        case 6:
                                                            dos.writeUTF("true");
                                                            sortirConfigServer = true;
                                                            break;

                                                    }
                                                }
                                                break;
                                            case 6:
                                                dos.writeUTF("true");
                                                sortirSesio = true;
                                                break;
                                        }
                                    }
                                } else {
                                    dos.writeUTF("false");

                                }
                            }
                            ;

                            break;
                        case 2:
                            if (esCorrectoDriver()) {
                                cn = obtenerCon();
                                String idUsuariC = dis.readUTF();
                                String NomC = dis.readUTF();
                                String CognomC = dis.readUTF();
                                String contraseñaC = dis.readUTF();
                                usuarioInsert(idUsuariC, NomC, CognomC, contraseñaC);
                            }
                            break;
                        case 3:
                            sortir = true;
                            dos.writeUTF("true");
                            break;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(ConecInicialServidorString.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
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

    private static boolean iniciSesio(String idUsuario, String contrasenya) {
        try {
            PreparedStatement st = cn.prepareStatement("SELECT * FROM usuario WHERE idusuario = ? AND contraseña = ?");
            st.setString(1, idUsuario);
            String hash = cifrar(contrasenya);
            st.setString(2, hash);
            ResultSet rs = st.executeQuery();
            rs.next();
            if (rs.getString("idusuario").equals(idUsuario)) {
                System.out.println("Login correcte");
                return true;
            } else {
                System.out.println("No ha iniciado sesion");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    private static void usuarioInsert(String idusuario, String name, String cognoms, String contraseña) {
        try {
            PreparedStatement st = cn.prepareStatement("INSERT INTO usuario(idusuario, nom, cognoms, contraseña) VALUES (?, ?, ?, ?);");
            st.setString(1, idusuario);
            st.setString(2, name);
            st.setString(3, cognoms);
            String hash = cifrar(contraseña);
            st.setString(4, hash);
            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static ArrayList<String> llistarUsuaris() throws IOException {
        try {
            ArrayList<String> missatgesAEnviar = new ArrayList<>();
            PreparedStatement st2 = cn.prepareStatement("SELECT idusuario, nom, cognoms, contraseña FROM usuario;");
            ResultSet rs = st2.executeQuery();
            while (rs.next()) {
                missatgesAEnviar.add(rs.getString("idusuario") + " = " + rs.getString("nom"));
            }
            return missatgesAEnviar;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private static void crearGrupo(String idgrupo, String idusuario) {
        try {
            PreparedStatement st = cn.prepareStatement("INSERT INTO grupo(idgrupo, idadmin) VALUES (?, ?);");
            st.setString(1, idgrupo);
            st.setString(2, idusuario);
            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void borrarGrupo(String idgrupo, String idusuario, DataOutputStream dos) throws IOException {
        try {
            PreparedStatement st1 = cn.prepareStatement("SELECT idgrupo FROM grupo WHERE idgrupo = ?;");
            st1.setString(1, idgrupo);
            ResultSet rs1 = st1.executeQuery();
            if (rs1.next()) {
                PreparedStatement st2 = cn.prepareStatement("SELECT idadmin FROM grupo WHERE idadmin = ? AND idgrupo = ?;");
                st2.setString(1, idusuario);
                st2.setString(2, idgrupo);
                ResultSet rs2 = st2.executeQuery();
                if (rs2.next()) {
                    PreparedStatement st3 = cn.prepareStatement("DELETE FROM grupo WHERE idgrupo = ?;");
                    st3.setString(1, idgrupo);
                    st3.execute();
                    dos.writeUTF("correcte");
                } else {
                    dos.writeUTF("admin");
                }
            } else {
                dos.writeUTF("grup");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static boolean comprobargrupo(String idgrupo, String idusuario, DataOutputStream dos) throws SQLException, IOException {
        try {
            PreparedStatement st1 = cn.prepareStatement("SELECT idgrupo FROM grupo WHERE idgrupo = ?;");
            st1.setString(1, idgrupo);
            ResultSet rs1 = st1.executeQuery();
            if (rs1.next()) {
                PreparedStatement st2 = cn.prepareStatement("SELECT idadmin FROM grupo WHERE idadmin = ? AND idgrupo = ?;");
                st2.setString(1, idusuario);
                st2.setString(2, idgrupo);
                ResultSet rs2 = st2.executeQuery();
                if (rs2.next()) {
                    dos.writeUTF("correcte");
                    return true;
                } else {
                    dos.writeUTF("admin");
                    return false;
                }
            } else {
                dos.writeUTF("grupo");
                return false;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    private static void afegirUsuari(String idgrupo, String idusuario, DataOutputStream dos) throws IOException {
        try {
            PreparedStatement st1 = cn.prepareStatement("SELECT idusuario FROM usuario WHERE idusuario = ?;");
            st1.setString(1, idusuario);
            ResultSet rs1 = st1.executeQuery();
            if (rs1.next()) {
                PreparedStatement st2 = cn.prepareStatement("SELECT idgrupo FROM grupo_usuario WHERE idusuario = ? AND idgrupo = ?;");
                st2.setString(1, idusuario);
                st2.setString(2, idgrupo);
                ResultSet rs2 = st2.executeQuery();
                if (rs2.next()) {
                    dos.writeUTF("relacio");
                } else {
                    PreparedStatement st3 = cn.prepareStatement("INSERT INTO grupo_usuario(idgrupo, idusuario) VALUES (?, ?);");
                    st3.setString(1, idgrupo);
                    st3.setString(2, idusuario);
                    st3.executeUpdate();
                    dos.writeUTF("correcte");
                }

            } else {
                dos.writeUTF("usuari");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void esborrarUsuari(String idgrupo, String idusuario, DataOutputStream dos) throws IOException {
        try {
            PreparedStatement st1 = cn.prepareStatement("SELECT idusuario FROM usuario WHERE idusuario = ?;");
            st1.setString(1, idusuario);
            ResultSet rs1 = st1.executeQuery();
            if (rs1.next()) {
                PreparedStatement st2 = cn.prepareStatement("SELECT idgrupo FROM grupo_usuario WHERE idusuario = ? AND idgrupo = ?;");
                st2.setString(1, idusuario);
                st2.setString(2, idgrupo);
                ResultSet rs2 = st2.executeQuery();
                System.out.println(st2);
                if (rs2.next()) {
                    PreparedStatement st3 = cn.prepareStatement("DELETE FROM grupo_usuario WHERE idgrupo = ? AND idusuario = ?");
                    st3.setString(1, idgrupo);
                    st3.setString(2, idusuario);
                    st3.executeUpdate();
                    dos.writeUTF("correcte");
                } else {
                    dos.writeUTF("relacio");
                }
            } else {
                dos.writeUTF("usuari");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void llistarUsuarisGrup(String idgrupo, DataOutputStream dos) throws IOException {
        ArrayList<String> membres = new ArrayList<>();
        try {
            PreparedStatement st = cn.prepareStatement("SELECT idusuario FROM grupo_usuario WHERE idgrupo = ?;");
            st.setString(1, idgrupo);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                membres.add(rs.getString("idusuario"));
            }
            dos.writeUTF(Integer.toString(membres.size()));
            for (int i = 0; i < membres.size(); i++) {
                dos.writeUTF(membres.get(i));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static ArrayList<String> llistarGrups(String idusuari) throws IOException {
        try {
            ArrayList<String> grups = new ArrayList<>();
            PreparedStatement st = cn.prepareStatement("SELECT idgrupo FROM grupo WHERE idadmin = ?;");
            st.setString(1, idusuari);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                grups.add(rs.getString("idgrupo"));
            }
            return grups;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }



    public static String cifrar(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void descaregarFitxer(String respostaUsuariRebut, DataOutputStream dos, DataInputStream dis) throws IOException {
        FileOutputStream fileOutput;
        BufferedOutputStream bo;
        File fo;
        String s2[] = respostaUsuariRebut.split("[\\\\/]"); //per si acàs, treiem la ruta del nom del fitxer, per si s'ha posat
        int lbloc = 2048; //no cal que sigui el mateix tamany en el emisor i receptor
        respostaUsuariRebut = s2[s2.length - 1];
        String nomfichPrevi = "rebrent_" + respostaUsuariRebut; //El nom es canvia per saber que el fitxer encara no s'ha baixat del tot
        long lfic = dis.readLong();
        fo = new File(nomfichPrevi);
        fo.delete(); //Eliminem el fitxer per si ja existia d'abans
        fileOutput = new FileOutputStream(fo);
        bo = new BufferedOutputStream(fileOutput);
        byte b[] = new byte[(int) lbloc];
        long lleva = 0;
        while (lleva < lfic) {
            int leido;
            if (lfic - lleva > lbloc) {
                leido = dis.read(b, 0, lbloc); //llegeix com al molt lbloc bytes, però pot ser que sigui altra quantitat menor
            } else {//falten menys bytes que lbloc
                leido = dis.read(b, 0, (int) (lfic - lleva)); //llegeix com a molt tants bytes com falten
            }
            bo.write(b, 0, leido);
            lleva = lleva + leido; //per saber quants es porten llegits
        }
        bo.close();
        //reanomena el fitxer
        File nufile = new File("rec_" + respostaUsuariRebut); //El fitxer ja està baixat. Se li ha de posar el nom final correcte. No li posem el que s'envia per si s'està provant al mateix ordinador
        nufile.delete();
        fo.renameTo(nufile);
        System.out.println("Fitxer descargat: " + respostaUsuariRebut);
    }

}
