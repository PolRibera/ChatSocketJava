/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projecte;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 *
 * @author Ricard Sierra Carasapiens, Pol Ribera Sam Sulek , Alex Montoya
 * Comepollas
 */
public class Cliente {

    public static Scanner sc = new Scanner(System.in);
    public static Scanner sc1 = new Scanner(System.in);

    public static void main(String[] args) throws IOException, InterruptedException {

        String[] pantallaInici = {
            "",
            "Benvingut a CopernicXat            ",
            "",
            "1.- [Sign in]                      ",
            "2.- [Sign up]                      ",
            "3.- [Exit]                         ",
            "",};

        String[] signIn = {
            "",
            "[Sign in]",
            "",};

        String[] signUp = {
            "",
            "[Sign up]",
            "",};

        String[] exit = {
            "",
            "Fins un altre.",
            "",};

        String[] opGrup = {
            "",
            "Opcions Grup                       ",
            "",
            "1.- [Donar d’alta grup]            ",
            "2.- [Administrar grup]             ",
            "3.- [Menu principal]               ",
            "",};

        String[] adminGrup = {
            "",
            "Administrar grup                   ",
            "",
            "1.- [Afegir usuari]                ",
            "2.- [Esborrar usuari]              ",
            "3.- [Donar de baixa grup]          ",
            "4.- [Llistar membres del grup]     ",
            "5.- [Menu grups]                   ",
            "",};

        String[] opFitxers = {
            "",
            "Opcions fitxers                    ",
            "",
            "1.- [Enviar fitxer al servidor]    ",
            "2.- [Llegir fitxer]                ",
            "3.- [Descarregar fitxer]           ",
            "4.- [Menu principal]               ",
            "",};

        String[] opXat = {
            "",
            "Xat                                ",
            "",
            "1.- [Enviar missatge]              ",
            "2.- [Enviar missatge a grup]       ",
            "3.- [Llegir missatge]              ",
            "4.- [Llegir missatge d'un grup]    ",
            "5.- [Menu principal]               ",
            "",};

        String[] confServidor = {
            "",
            "Configurar servidor                ",
            "",
            "1.- [Mostra configuracio actual]   ",
            "2.- [Tamany maxim fitxer]          ",
            "3.- [Maximas conexions simultaneas]",
            "4.- [Nova contrasenya BDD]         ",
            "5.- [Client administrador]         ",
            "6.- [Cambiar nom del servidor]     ",
            "7.- [Ruta guardar fitxers]         ",
            "8.- [Menu principal]               ",
            "",};

        String[] confClient = {
            "",
            "Configurar servidor                ",
            "",
            "1.- [Mostra configuracio actual]   ",
            "2.- [Cambiar nom client]           ",
            "3.- [Tamany maxim que pot rebre]   ",
            "4.- [Cambiar ip servidor]          ",
            "5.- [Port per defecta servidor]    ",
            "6.- [Menu principal]               ",
            "",};

        System.out.println("Inicia cliente");

        Socket sk = new Socket("localhost", 54322); //accepta una conexión

        DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
        DataInputStream dis = new DataInputStream(sk.getInputStream());
        String RespuestaServidor;
        System.out.println();
        boolean sortir = false;
        while (!sortir) {
            gui(pantallaInici);
            System.out.println("Introdueix una opció: ");
            String s1 = sc.next();
            dos.writeUTF(s1);
            switch (Integer.parseInt(s1)) {
                case 1:
                    int con = 0;
                    String repServidor = "";
                    String idUsuari = "";
                    gui(signIn);
                    while (con <= 3) {
                        System.out.println("Introdueix nom d'usuari:");
                        idUsuari = sc.next();
                        dos.writeUTF(idUsuari);
                        System.out.println("Introdueix contrasenya:");
                        String contrasenya = sc.next();
                        dos.writeUTF(contrasenya);
                        repServidor = dis.readUTF();
                        if (repServidor.equals("false")) {
                            System.out.println("La contrasenya o el usuari son incorrectes.");
                            con++;
                            if (con <= 3) {
                                sortir = true;
                            }
                        } else if (repServidor.equals("true")) {
                            System.out.println("Has iniciat sesió.");

                            break;
                        }

                    }
                    String[] pantallaPrincipal = {
                        "",
                        "Benvingut, " + idUsuari + "        ",
                        "",
                        "1.- [Opcions Grup]                 ",
                        "2.- [Opcions fitxers]              ",
                        "3.- [Opcions xat]                  ",
                        "4.- [Configuració servidor]        ",
                        "5.- [Configuració client]          ",
                        "6.- [Sign out]                     ",
                        "",};
                    if (repServidor.equals("true")) {
                        Thread.sleep(500);
                        boolean SortirMenu = false;
                        while (!SortirMenu){
                            gui(pantallaPrincipal);
                            s1 = sc.next();
                            dos.writeUTF(s1);
                            switch (Integer.parseInt(s1)) {
                                case 1:
                                    String idgrupo;
                                    boolean grupo = false;
                                    while (!grupo) {
                                        gui(opGrup);
                                        s1 = sc.next();
                                        dos.writeUTF(s1);
                                        switch (Integer.parseInt(s1)) {
                                            case 1:
                                                System.out.println("Nombre del grupo: ");
                                                idgrupo = sc.next();
                                                dos.writeUTF(idgrupo);
                                                String respuestaCrear = dis.readUTF();
                                                if (respuestaCrear.equals("correcte")) {
                                                    System.out.println("Grupo creado correctamente");
                                                } else if (respuestaCrear.equals("grupo")) {
                                                    System.out.println("El grupo ya existe");
                                                }
                                                String respuestacrear2 = dis.readUTF();
                                                break;
                                            case 2:
                                                System.out.println("Nombre del grupo: ");
                                                idgrupo = sc.next();
                                                dos.writeUTF(idgrupo);
                                                String resposta = dis.readUTF();
                                                if (resposta.equals("correcte")) {
                                                    boolean adgrup = false;
                                                    while (!adgrup) {
                                                        gui(adminGrup);
                                                        s1 = sc.next();
                                                        dos.writeUTF(s1);
                                                        switch (Integer.parseInt(s1)) {
                                                            case 1:
                                                                System.out.println("Nom d'usuari: ");
                                                                s1 = sc.next();
                                                                dos.writeUTF(s1);
                                                                resposta = dis.readUTF();
                                                                if (resposta.equals("correcte")) {
                                                                    System.out.println("Usuari afegit");
                                                                } else if (resposta.equals("relacio")) {
                                                                    System.out.println("El usuari ya pertany a aquest grup");
                                                                } else if (resposta.equals("usuari")) {
                                                                    System.out.println("El usuari no existeix");
                                                                }
                                                                break;
                                                            case 2:
                                                                System.out.println("Nom d'usuari: ");
                                                                s1 = sc.next();
                                                                dos.writeUTF(s1);
                                                                resposta = dis.readUTF();
                                                                if (resposta.equals("correcte")) {
                                                                    System.out.println("Usuari esborrat");
                                                                } else if (resposta.equals("relacio")) {
                                                                    System.out.println("El usuari no pertany a aquest grup");
                                                                } else if (resposta.equals("usuari")) {
                                                                    System.out.println("El usuari no existeix");
                                                                } else if (resposta.equals("admin")) {
                                                                    System.out.println("No pots eliminar el teu usuari");
                                                                }
                                                                break;
                                                            case 3:
                                                                System.out.println("El grup ha sigut esborrat");
                                                                break;
                                                            case 4:
                                                                int contador = Integer.parseInt(dis.readUTF());
                                                                String usuario;
                                                                System.out.println("Usuaris del grup " + idgrupo + ":\n");
                                                                for (int i = 0; i < contador; i++) {
                                                                    usuario = dis.readUTF();
                                                                    System.out.println(usuario);
                                                                }
                                                                break;
                                                            case 5:
                                                                if (dis.readUTF().equals("true")) {
                                                                    adgrup = true;
                                                                }
                                                                break;
                                                        }
                                                    }
                                                } else if (resposta.equals("admin")) {
                                                    System.out.println("No eres el admin de este grupo");
                                                } else if (resposta.equals("grupo")) {
                                                    System.out.println("El grupo no existe");
                                                }
                                                break;
                                            case 3:
                                                if (dis.readUTF().equals("true")) {
                                                    grupo = true;
                                                }

                                                break;
                                        }
                                    }
                                    break;
                                case 2:
                                    boolean opFitxer = false;
                                    while (!opFitxer) {
                                        gui(opFitxers);
                                        System.out.print("Introdueix una opció: ");
                                        s1 = sc.next();
                                        dos.writeUTF(s1);
                                    switch (Integer.parseInt(s1)) {
                                        case 1:
                                            System.out.println("Introdueix el nom del fitxer a enviar:");
                                            String nomFitxer = sc.next();
                                            enviaEnBlocs(nomFitxer, sk, dos, sk.getOutputStream());
                                            break;
                                        case 2:
                                            System.out.println("Introdueix el nom del fitxer a llegir:");
                                            String nomFitxerLlegir = sc.next();
                                            FileInputStream fis = new FileInputStream(nomFitxerLlegir);
                                            int content;
                                            while ((content = fis.read()) != -1) {
                                                System.out.print((char) content);
                                            }
                                            fis.close();
                                            break;
                                        case 3:
                                            String directorioDescarga = dis.readUTF();
                                            int contador = Integer.parseInt(dis.readUTF());
                                            for (int i = 0; i < contador; i++) {
                                                System.out.println(dis.readUTF());
                                            }
                                            System.out.println("Introdueix el nom del fitxer a descarregar:");
                                            String nomFitxerDescarregar = sc.next();
                                            dos.writeUTF(directorioDescarga+"\\"+nomFitxerDescarregar);
                                            descaregarFitxer(sk, dis, sk.getInputStream());
                                            break;
                                        case 4:
                                            if (dis.readUTF().equals("true")) {
                                                opFitxer = true;
                                            }
                                            break;
                                    }
                                    }
                                    break;
                                case 3:
                                    boolean xat = false;
                                    String resposta;
                                    while (!xat) {
                                        gui(opXat);
                                        System.out.println("Introdueix una opció: ");
                                        s1 = sc.next();
                                        dos.writeUTF(s1);
                                        switch (Integer.parseInt(s1)) {
                                            case 1:
                                                System.out.println("Introdueix receptor");
                                                s1 = sc1.nextLine();
                                                dos.writeUTF(s1);
                                                System.out.println("Introdueix missatge");
                                                s1 = sc1.nextLine();
                                                dos.writeUTF(s1);
                                                resposta = dis.readUTF();
                                                if (resposta.equals("correcte")) {
                                                    System.out.println("Missatge enviat");
                                                } else if (resposta.equals("usuario")) {
                                                    System.out.println("El usuari no existeix");
                                                }
                                                break;
                                            case 2:
                                                System.out.println("Introdueix grup");
                                                s1 = sc1.nextLine();
                                                dos.writeUTF(s1);
                                                System.out.println("Introdueix missatge");
                                                s1 = sc1.nextLine();
                                                dos.writeUTF(s1);
                                                resposta = dis.readUTF();
                                                if (resposta.equals("correcte")) {
                                                    System.out.println("Missatge enviat");
                                                } else if (resposta.equals("grupo")) {
                                                    System.out.println("El grup no existeix");
                                                } else if (resposta.equals("usuario")) {
                                                    System.out.println("El teu usuari no pertany al grup");
                                                }
                                                break;
                                            case 3:
                                                resposta = dis.readUTF();
                                                if (resposta.equals("correctomen")) {
                                                    int contador = Integer.parseInt(dis.readUTF());
                                                    String mensaje;
                                                    for (int i = 0; i < contador; i++) {
                                                        mensaje = dis.readUTF();
                                                        System.out.println(mensaje);
                                                    }
                                                } else if (resposta.equals("mensajes")) {
                                                    System.out.println("No hi han missatges");
                                                }
                                                break;
                                            case 4:
                                                System.out.println("Introdueix grup");
                                                s1 = sc.next();
                                                dos.writeUTF(s1);
                                                resposta = dis.readUTF();
                                                if (resposta.equals("correctomengrup")) {

                                                    int contador = Integer.parseInt(dis.readUTF());
                                                    String mensaje;
                                                    for (int i = 0; i < contador; i++) {
                                                        mensaje = dis.readUTF();
                                                        System.out.println(mensaje);
                                                    }
                                                } else if (resposta.equals("mensajes")) {
                                                    System.out.println("No hi han missatges");
                                                } else if (resposta.equals("grupo")) {
                                                    System.out.println("El grup no existeix");
                                                } else if (resposta.equals("usuario")) {
                                                    System.out.println("No pertanys a aquest grup");
                                                }
                                                break;
                                            case 5:
                                                xat = true;
                                                break;
                                        }
                                    }
                                    break;
                                case 4:
                                    gui(confServidor);
                                    System.out.print("Introdueix una opció: ");
                                    s1 = sc.next();
                                    dos.writeUTF(s1);
                                    boolean sortirConfigServer = false;
                                    while (!sortirConfigServer) {
                                        switch (Integer.parseInt(s1)) {
                                            case 1:
                                                System.out.println(dis.readUTF());
                                                System.out.println(dis.readUTF());
                                                System.out.println(dis.readUTF());
                                                System.out.println(dis.readUTF());
                                                System.out.println(dis.readUTF());
                                                System.out.println(dis.readUTF());
                                                break;
                                            case 2:
                                                System.out.print("Introdueix la mida maxima del fitxer: ");
                                                String mida = sc.next();
                                                dos.writeUTF(mida);
                                                break;
                                            case 3:
                                                System.out.print("Introdueix les conexions maximas al servidor: ");
                                                String maxCon = sc.next();
                                                dos.writeUTF(maxCon);
                                                break;
                                            case 4:
                                                System.out.print("Introdueix la nova contrasenya de la base de dades: ");
                                                String contraBase = sc.next();
                                                dos.writeUTF(contraBase);
                                                break;
                                            case 5:
                                                System.out.print("Introdueix el nom del nou Admin: ");
                                                String clientAdmin = sc.next();
                                                dos.writeUTF(clientAdmin);
                                                break;
                                            case 6:
                                                System.out.print("Introdueix el nou nom del servidor: ");
                                                String nomServer = sc.next();
                                                dos.writeUTF(nomServer);
                                                break;
                                            case 7:
                                                System.out.print("Introdueix la nova ruta on es guarden els fitxers: ");
                                                String rutaFitxers = sc.next();
                                                dos.writeUTF(rutaFitxers);
                                                break;
                                            case 8:
                                                if (dis.readUTF().equals("true")) {
                                                    sortirConfigServer = true;
                                                }
                                                break;

                                        }
                                    }
                                    break;
                                case 5:
                                    gui(confClient);
                                    System.out.print("Introdueix una opció: ");
                                    s1 = sc.next();
                                    dos.writeUTF(s1);
                                    switch (Integer.parseInt(s1)) {
                                        case 1:
                                            System.out.println(dis.readUTF());
                                            System.out.println(dis.readUTF());
                                            System.out.println(dis.readUTF());
                                            System.out.println(dis.readUTF());
                                            break;
                                        case 2:
                                            System.out.print("Introdueix el nou nom del client: ");
                                            String nomClient = sc.next();
                                            dos.writeUTF(nomClient);
                                            break;
                                        case 3:
                                            System.out.print("Introdueix la mida maxima del fitxer per enviar/rebre: ");
                                            String mida = sc.next();
                                            dos.writeUTF(mida);
                                            break;
                                        case 4:
                                            System.out.print("Introdueix la nova ip: ");
                                            String ip = sc.next();
                                            dos.writeUTF(ip);
                                            break;
                                        case 5:
                                            System.out.print("Introdueix el nou port: ");
                                            String port = sc.next();
                                            dos.writeUTF(port);
                                            break;
                                        case 6:
                                            break;
                                    }
                                    break;
                                case 6:
                                    if (dis.readUTF().equals("true")) {
                                        SortirMenu = true;
                                    }
                                    gui(exit);
                                    break;

                            }
                        }
                    }
                    break;
                case 2:
                    gui(signUp);
                    System.out.println("Introdueix id d'usuari:");
                    String idUsuariC = sc.next();
                    dos.writeUTF(idUsuariC);
                    System.out.println("Introdueix nom d'usuari:");
                    String NomC = sc.next();
                    dos.writeUTF(NomC);
                    System.out.println("Introdueix cognom d'usuari:");
                    String CognomC = sc.next();
                    dos.writeUTF(CognomC);
                    System.out.println("Introdueix contrasenya d'usuari:");
                    String contraseñaC = sc.next();
                    dos.writeUTF(contraseñaC);
                    if (dis.readUTF().equals("correcto")) {
                        System.out.println("S'ha creat correctament el usuari");
                    }
                    break;
                case 3:
                    if (dis.readUTF().equals("true")) {
                        sortir = true;
                    }
                    break;

            }
        }
    }

    public static void gui(String[] contingut) {
        int height = contingut.length + 2;
        int width = 40;

        char horizontalChar = '-';
        char verticalChar = '|';

        for (int i = 0; i < width; i++) {
            System.out.print(horizontalChar);
        }
        System.out.println();

        for (int i = 0; i < height - 2; i++) {
            System.out.print(verticalChar);
            if (i < contingut.length) {
                int contentLength = contingut[i].length();
                int padding = (width - 2 - contentLength) / 2;
                for (int j = 0; j < padding; j++) {
                    System.out.print(" ");
                }
                System.out.print(contingut[i]);
                for (int j = padding + contentLength; j < width - 2; j++) {
                    System.out.print(" ");
                }
            }
            System.out.println(verticalChar);
        }

        for (int i = 0; i < width; i++) {
            System.out.print(horizontalChar);
        }
        System.out.println();
    }

    public static void descaregarFitxer(Socket sk, DataInputStream ois, InputStream in) throws IOException {
        int lbloc = 512; //no cal que sigui el mateix tamany en el emisor i receptor

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
            
            fo = new File("..\\fitxersClient\\"+nomfichPrevi);
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
            File nufile = new File("..\\fitxersClient\\"+nomfich); //El fitxer ja està baixat. Se li ha de posar el nom final correcte. No li posem el que s'envia per si s'està provant al mateix ordinador
            nufile.delete();
            fo.renameTo(nufile);
            System.out.println("Rebut");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void enviaEnBlocs(String nomfich, Socket sk, DataOutputStream oos, OutputStream out) throws IOException {
        int lbloc = 1024; //tamany del bloc --> sol ser múltiple de 256 bytes, però pot ser qualsevol valor
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
}
