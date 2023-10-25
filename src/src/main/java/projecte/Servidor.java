package projecte;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.logging.*;

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
        propertiesClient = new Properties();
        propertiesServer.load(new FileReader("src\\main\\java\\projecte\\configServer.properties"));
        propertiesClient.load(new FileReader("src\\main\\java\\projecte\\configClient.properties"));
        System.out.println("Servidor ences");
        while (true) {
            try {
                Socket sk = skk.accept(); //accepta una conexión
                Server sv = new Server(sk);
                System.out.println("Escoltant el port " + PUERTO);
                System.out.println("connectant amb el client " + sk.getInetAddress().getHostAddress());
                sv.start();
                logs("S’ha conectat aquesta ip al servidor: " + sk.getInetAddress());
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
                                boolean sesion = false;
                                int cont = 0;
                                String idUsuari = "";
                                String contrasenya = "";
                                while (!sesion) {
                                    idUsuari = dis.readUTF();
                                    contrasenya = dis.readUTF();
                                    System.out.println(idUsuari + contrasenya);
                                    sesion = iniciSesio(idUsuari, contrasenya);
                                    if (sesion == false) {
                                        cont++;
                                        dos.writeUTF("false");
                                        if (cont == 3) {
                                            System.out.println("Cliente desconectado por sobrepasar intentos");
                                            sortir = true;
                                            break;
                                        }
                                    } else {
                                        break;
                                    }
                                }
                                if (sesion == true) {
                                    usuariosConectados.put(idUsuari, sk);
                                    boolean sortirSesio = false;
                                    dos.writeUTF("true");
                                    while (!sortirSesio) {
                                        respostaUsuariRebut = dis.readUTF();
                                        switch (Integer.parseInt(respostaUsuariRebut)) {
                                            case 1:
                                                String idgrupo;
                                                boolean grupo = false;
                                                while (!grupo) {
                                                    System.out.println("grupo");
                                                    respostaUsuariRebut = dis.readUTF();
                                                    switch (Integer.parseInt(respostaUsuariRebut)) {
                                                        case 1:
                                                            idgrupo = dis.readUTF();
                                                            crearGrupo(idgrupo, idUsuari, dos);
                                                            afegirUsuari(idgrupo, idUsuari, dos);
                                                            break;
                                                        case 2:
                                                            idgrupo = dis.readUTF();
                                                            if (comprobargrupo(idgrupo, idUsuari, dos)) {
                                                                String usuarinou;
                                                                boolean adgrup = false;
                                                                while (!adgrup) {
                                                                    System.out.println("adgrup");
                                                                    respostaUsuariRebut = dis.readUTF();
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
                                                                            adgrup = true;
                                                                            break;
                                                                    }
                                                                }
                                                            } else {
                                                                break;
                                                            }
                                                            break;
                                                        case 3:
                                                            dos.writeUTF("true");
                                                            grupo = true;
                                                            break;
                                                    }
                                                }
                                                break;
                                            case 2:
                                                boolean opFitxers = false;
                                                while (!opFitxers) {
                                                    respostaUsuariRebut = dis.readUTF();
                                                    switch (Integer.parseInt(respostaUsuariRebut)) {
                                                        case 1: // Enviar archivo al servidor // Nombre del archivo que el cliente desea enviar
                                                            descaregarFitxer(sk, dis, sk.getInputStream()); // Utiliza tu función descaregarFitxer
                                                            // Realiza alguna lógica adicional si es necesario con el archivo recibido
                                                            break;
                                                        case 2:
                                                            break;
                                                        case 3: // Descargar archivo (cliente envía solicitud)
                                                            String directorioDescarga = "..\\fitxersServer";
                                                            dos.writeUTF(directorioDescarga);// Directorio en el servidor donde se encuentran los archivos a descargar
                                                            listarFicheros(directorioDescarga, dos); // Utiliza tu función listarFicheros
                                                            String archivoDescargar = dis.readUTF(); // Nombre del archivo que el cliente desea descargar
                                                            enviaEnBlocs(archivoDescargar, sk, dos, sk.getOutputStream()); // Utiliza tu función enviaEnBlocs
                                                            break;
                                                        case 4:
                                                            dos.writeUTF("true");
                                                            opFitxers = true;
                                                            break;
                                                    }
                                                }
                                                break;
                                            case 3:
                                                boolean xat = false;
                                                String receptor,
                                                 grup,
                                                 missatge;
                                                while (!xat) {
                                                    respostaUsuariRebut = dis.readUTF();
                                                    switch (Integer.parseInt(respostaUsuariRebut)) {
                                                        case 1:
                                                            receptor = dis.readUTF();
                                                            missatge = dis.readUTF();
                                                            enviarMisatge(receptor, missatge, idUsuari, dos);
                                                            break;
                                                        case 2:
                                                            grup = dis.readUTF();
                                                            missatge = dis.readUTF();
                                                            enviarMisatgeGrup(grup, missatge, idUsuari, dos);
                                                            break;
                                                        case 3:
                                                            llegirMisatge(idUsuari, dos);
                                                            break;
                                                        case 4:
                                                            grup = dis.readUTF();
                                                            llegirMisatgeGrup(idUsuari, grup, dos);
                                                            break;
                                                        case 5:
                                                            xat = true;
                                                            break;
                                                    }
                                                }
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
                                                llistarusuaris(dos);
                                                llistarusuarisconectats(dos,usuariosConectados);
                                                break;
                                                
                                            case 7:
                                                dos.writeUTF("true");
                                                sortirSesio = true;
                                                usuariosConectados.remove(idUsuari);
                                                break;
                                        }
                                    }
                                }
                            }
                            break;
                        case 2:
                            if (esCorrectoDriver()) {
                                cn = obtenerCon();
                                String idUsuariC = dis.readUTF();
                                String NomC = dis.readUTF();
                                String CognomC = dis.readUTF();
                                String contraseñaC = dis.readUTF();
                                usuarioInsert(idUsuariC, NomC, CognomC, contraseñaC, dos);
                            }
                            break;
                        case 3:
                            sortir = true;
                            dos.writeUTF("true");
                            break;
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
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
            if (rs.next()) {
                System.out.println("Login correcte");
                logs("El usuari:" + idUsuario + " ha iniciat sesio.");
                return true;
            } else {
                System.out.println("No ha iniciado sesion");
                logs("El usuari " + idUsuario + " no ha pogut iniciar sesió.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    private static void usuarioInsert(String idusuario, String name, String cognoms, String contraseña, DataOutputStream dos) throws IOException {
        try {

            PreparedStatement st = cn.prepareStatement("INSERT INTO usuario(idusuario, nom, cognoms, contraseña) VALUES (?, ?, ?, ?);");
            st.setString(1, idusuario);
            st.setString(2, name);
            st.setString(3, cognoms);
            String hash = cifrar(contraseña);
            st.setString(4, hash);
            PreparedStatement st1 = cn.prepareStatement("SELECT idusuario FROM usuario WHERE idusuario = ?;");
            st1.setString(1, idusuario);
            ResultSet rs1 = st1.executeQuery();
            if (!rs1.next()) {
                st.executeUpdate();
                dos.writeUTF("correcto");
                logs("S’ha registrat un nou usuari: " + idusuario);
            } else {
                dos.writeUTF("usuario");
                logs("No se ha podido registrar el usuario porque coincide con un nombre de la base de datos.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void crearGrupo(String idgrupo, String idusuario, DataOutputStream dos) throws SQLException, IOException {
        try {
            PreparedStatement st1 = cn.prepareStatement("SELECT idgrupo FROM grupo WHERE idgrupo = ?;");
            st1.setString(1, idgrupo);
            ResultSet rs1 = st1.executeQuery();
            if (!rs1.next()) {
                PreparedStatement st2 = cn.prepareStatement("INSERT INTO grupo(idgrupo, idadmin) VALUES (?, ?);");
                st2.setString(1, idgrupo);
                st2.setString(2, idusuario);
                st2.executeUpdate();
                dos.writeUTF("correcte");
                logs("El usuario " + idusuario + " ha creado el grupo " + idgrupo + " correctamente. ");
            } else {
                dos.writeUTF("grupo");
                logs("El usuario " + idusuario + " ha intentado crear  el grupo " + idgrupo + " pero ya hay un gurpo que se llama asi.");
            }
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
                    logs("El usuario " + idusuario + " ha eliminado el grupo " + idgrupo + " correctamente. ");
                } else {
                    dos.writeUTF("admin");
                    logs("El usuario " + idusuario + " ha intentado eliminar el grupo " + idgrupo + " pero no es el administrador ");
                }
            } else {
                dos.writeUTF("grup");
                logs("El grup " + idgrupo + " no existeix.");
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
            logs("Se ha listado los usuarios de este grupo: " + idgrupo);
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
            logs("El usuari " + idusuari + " ha llistat els grups. ");
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
            logs("S’ha xifrat una contrasenya.");
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void enviarMisatge(String receptorUsuario, String mensaje, DataOutputStream dos, String idUsuari, HashMap usuariosConectados) throws IOException {
        Socket receptorSocket = (Socket) usuariosConectados.get(receptorUsuario);
        if (receptorSocket != null) {
            try {
                DataOutputStream dosReceptor = new DataOutputStream(receptorSocket.getOutputStream());
                dosReceptor.writeUTF("Mensaje de " + idUsuari + " : " + mensaje);
                dos.writeUTF("Enviado: " + mensaje);
                System.out.println("Mensaje enviado");
                logs("El usuari " + idUsuari + " ha enviat un misatge al usuari " + receptorUsuario);
            } catch (IOException e) {
                e.printStackTrace();
                dos.writeUTF("Error al enviar el mensaje al usuario receptor");
                logs("El usuari " + idUsuari + " ha enviat un misatge al usuari " + receptorUsuario + " pero no li ha arribat");
            }
        } else {
            dos.writeUTF("El usuario receptor no está conectado");
            logs("El usuari " + idUsuari + " ha intentat enviar un misatge al usuari " + receptorUsuario + " pero no esta conectat.");
        }
    }

    public static void logs(String missatge) {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String logMessage = formato.format(new Date()) + ": " + missatge;
        try ( PrintWriter writer = new PrintWriter(new FileWriter("logs.txt", true))) {
            writer.println(missatge);
        } catch (IOException e) {
            System.out.println("Error al guardar el log.");
        }
    }

    public static void descaregarFitxer(Socket sk, DataInputStream ois, InputStream in) throws IOException {
        int lbloc = 20000; //no cal que sigui el mateix tamany en el emisor i receptor

        FileOutputStream fileOutput;
        BufferedOutputStream bo;
        File fo;

        String nomfich;
        try {
            nomfich = ois.readUTF();

            String s[] = nomfich.split("[\\\\/]"); //per si acàs, treiem la ruta del nom del fitxer, per si s'ha posat
            nomfich = s[s.length - 1];

            String nomfichPrevi = nomfich; //El nom es canvia per saber que el fitxer encara no s'ha baixat del tot
            long lfic = ois.readLong();

            fo = new File("..\\fitxersServer\\" + nomfichPrevi);
            fo.delete(); //Eliminem el fitxer per si ja existia d'abans
            fileOutput = new FileOutputStream(fo);
            bo = new BufferedOutputStream(fileOutput);
            System.out.println("El fitxer ocuparà " + lfic + " bytes");

            byte b[] = new byte[(int) lbloc];

            long lleva = 0;
            while (lleva < lfic) {
                int leido;
                if (lfic - lleva > lbloc) {
                    leido = in.read(b, 0, lbloc); //llegeix com al molt lbloc bytes, però pot ser que sigui altra quantitat menor
                } else {//falten menys bytes que lbloc
                    leido = in.read(b, 0, (int) (lfic - lleva)); //llegeix com a molt tants bytes com falten
                }
                bo.write(b, 0, leido);
                lleva = lleva + leido; //per saber quants es porten llegits
            }
            //reanomena el fitxer
            File nufile = new File("..\\fitxersServer\\" + nomfich); //El fitxer ja està baixat. Se li ha de posar el nom final correcte. No li posem el que s'envia per si s'està provant al mateix ordinador
            nufile.delete();
            fo.renameTo(nufile);
            System.out.println("Rebut");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void listarFicheros(String directorio, DataOutputStream dos) throws IOException {
        File folder = new File(directorio);
        ArrayList<String> llistatFitxers = new ArrayList<>();
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            int contador = 0;
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        contador++;
                        llistatFitxers.add(file.getName());
                    }
                }
                dos.writeUTF("" + contador + "");
                for (int i = 0; i < contador; i++) {
                    dos.writeUTF(llistatFitxers.get(i));
                }
            } else {
                dos.writeUTF("El directorio está vacío.");
            }
        } else {
            dos.writeUTF("El directorio no existe o no es una carpeta.");
        }
    }

    public static void enviaEnBlocs(String nomfich, Socket sk, DataOutputStream oos, OutputStream out) throws IOException {
        int lbloc = 20000; //tamany del bloc --> sol ser múltiple de 256 bytes, però pot ser qualsevol valor
        // Aquest tamany no cal que sigui el mateix en el emisor que en el receptor.

        try {
            File fi = new File(nomfich);
            System.out.println(nomfich);
            FileInputStream fileInput = new FileInputStream(fi);
            BufferedInputStream bi = new BufferedInputStream(fileInput);
            long lfic = fi.length();
            oos.writeUTF(nomfich);
            oos.writeLong(lfic);
            // oos.flush(); //envía el buffer, pero no cal, doncs continuem escrivint en oos

            long veces = lfic / lbloc; //quants blocs s'han d'enviar
            int resto = (int) (lfic % lbloc); //quant querarà al final per enviar

            byte b[] = new byte[lbloc];

            for (long i = 0; i < veces; i++) {
                bi.read(b); //llegeix un tros del fitxer
                out.write(b); // envia el tros del fitxer
            }
            //envia la resta del fitxer
            if (resto > 0) {
                bi.read(b, 0, resto); // llegeix la resta del fitxer en b
                out.write(b, 0, resto); // l'enviem
            }
            //oos.flush(); //no cal, es fa un flush al fer el close de oos
            System.out.println("Enviat");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static void enviarMisatge(String receptorUsuario, String mensaje, String idUsuari, DataOutputStream dos) throws IOException, SQLException {
        try {
            PreparedStatement st1 = cn.prepareStatement("SELECT idusuario FROM usuario WHERE idusuario = ?;");
            st1.setString(1, receptorUsuario);
            ResultSet rs1 = st1.executeQuery();
            if (rs1.next()) {
                PreparedStatement st2 = cn.prepareStatement("INSERT INTO mensajes_usuario(mensaje, idemisor, idremitente) VALUES (?, ?, ?);");
                st2.setString(1, mensaje);
                st2.setString(2, idUsuari);
                st2.setString(3, receptorUsuario);
                st2.executeUpdate();
                dos.writeUTF("correcte");
            } else {
                dos.writeUTF("usuario");
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void enviarMisatgeGrup(String grupo, String mensaje, String idUsuari, DataOutputStream dos) throws IOException, SQLException {
        try {
            PreparedStatement st1 = cn.prepareStatement("SELECT idgrupo FROM grupo WHERE idgrupo = ?;");
            st1.setString(1, grupo);
            ResultSet rs1 = st1.executeQuery();
            if (rs1.next()) {
                PreparedStatement st2 = cn.prepareStatement("SELECT idusuario FROM grupo_usuario WHERE idgrupo = ? AND idusuario = ?;");
                st2.setString(1, grupo);
                st2.setString(2, idUsuari);
                ResultSet rs2 = st2.executeQuery();
                if (rs2.next()) {
                    PreparedStatement st3 = cn.prepareStatement("INSERT INTO mensaje_grupo(mensaje, idemisor, idgrupo) VALUES (?, ?, ?);");
                    st3.setString(1, mensaje);
                    st3.setString(2, idUsuari);
                    st3.setString(3, grupo);
                    st3.executeUpdate();
                    dos.writeUTF("correcte");
                } else {
                    dos.writeUTF("usuario");
                }
            } else {
                dos.writeUTF("grupo");
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void llegirMisatge(String idUsuari, DataOutputStream dos) throws SQLException {
        try {
            ArrayList<String> missatges = new ArrayList<>();
            PreparedStatement st1 = cn.prepareStatement("SELECT * FROM mensajes_usuario WHERE idremitente = ?;");
            st1.setString(1, idUsuari);
            ResultSet rs1 = st1.executeQuery();
            if (rs1.next()) {
                dos.writeUTF("correctomen");
                do {
                    missatges.add("Missatge " + rs1.getString("fecha") + " de " + rs1.getString("idemisor") + ": " + rs1.getString("mensaje"));
                } while (rs1.next());
                dos.writeUTF(Integer.toString(missatges.size()));
                for (int i = 0; i < missatges.size(); i++) {
                    dos.writeUTF(missatges.get(i));
                }
            } else {
                dos.writeUTF("mensajes");
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void llegirMisatgeGrup(String idUsuari, String grup, DataOutputStream dos) throws SQLException {
        try {
            ArrayList<String> missatges = new ArrayList<>();
            PreparedStatement st1 = cn.prepareStatement("SELECT idgrupo FROM grupo WHERE idgrupo = ?;");
            st1.setString(1, grup);
            ResultSet rs1 = st1.executeQuery();
            if (rs1.next()) {
                PreparedStatement st2 = cn.prepareStatement("SELECT idusuario FROM grupo_usuario WHERE idusuario = ? AND idgrupo = ?;");
                st2.setString(1, idUsuari);
                st2.setString(2, grup);
                ResultSet rs2 = st2.executeQuery();
                if (rs2.next()) {
                    PreparedStatement st3 = cn.prepareStatement("SELECT * FROM mensaje_grupo WHERE idgrupo = ?;");
                    st3.setString(1, grup);
                    ResultSet rs3 = st3.executeQuery();
                    if (rs3.next()) {
                        dos.writeUTF("correctomengrup");
                        do {
                            missatges.add("Missatge  " + rs3.getString("fecha") + " de " + rs3.getString("idemisor") + ": " + rs3.getString("mensaje"));
                        } while (rs1.next());
                        dos.writeUTF(Integer.toString(missatges.size()));
                        for (int i = 0; i < missatges.size(); i++) {
                            dos.writeUTF(missatges.get(i));
                        }
                    } else {
                        dos.writeUTF("mensaje");
                    }
                } else {
                    dos.writeUTF("usuario");
                }

            } else {
                dos.writeUTF("grupo");
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void llistarusuaris(DataOutputStream dos) throws SQLException {
        try {
            ArrayList<String> usuaris = new ArrayList<>();
            PreparedStatement st1 = cn.prepareStatement("SELECT idusuario FROM usuario;");
            ResultSet rs1 = st1.executeQuery();
            if (rs1.next()) {
                dos.writeUTF("correcto");
                do {
                    usuaris.add(rs1.getString("idusuario"));
                } while (rs1.next());
                dos.writeUTF(Integer.toString(usuaris.size()));
                for (int i = 0; i < usuaris.size(); i++) {
                    dos.writeUTF(usuaris.get(i));
                }
            } else {
                dos.writeUTF("usuario");
            }

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void llistarusuarisconectats(DataOutputStream dos, HashMap<String, Socket> usuarisconectats) throws SQLException {
        try {
            dos.writeUTF(Integer.toString(usuarisconectats.size()));
            for (String key : usuarisconectats.keySet()) {
                dos.writeUTF(key);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
