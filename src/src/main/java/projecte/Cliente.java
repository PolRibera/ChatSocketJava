/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projecte;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * a
 *
 * @author Ricard Sierra, Pol Ribera, Alex Montoya
 */
public class Cliente {

    public static Scanner sc = new Scanner(System.in);

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
            "2.- [Llegir missatge]              ",
            "3.- [Llistar usuaris]              ",
            "4.- [Menu principal]               ",
            "",};

        String[] confServidor = {
            "",
            "Configurar servidor                ",
            "",
            "1.- [Tamany maxim fitxer]          ",
            "2.- [Maximas conexions simultaneas]",
            "3.- [Nova contrasenya BDD]         ",
            "4.- [Client administrador]         ",
            "5.- [Cambiar nom del servidor]     ",
            "6.- [Ruta guardar fitxers]         ",
            "7.- [Menu principal]               ",
            "",};

        String[] confClient = {
            "",
            "Configurar servidor                ",
            "",
            "1.- [Cambiar nom client]           ",
            "2.- [Tamany maxim que pot rebre]   ",
            "3.- [Cambiar ip servidor]          ",
            "4.- [Port per defecta servidor]    ",
            "7.- [Menu principal]               ",
            "",};

        System.out.println("Inicia cliente");

        Socket sk = new Socket("localhost", 54322); //accepta una conexión

        DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
        DataInputStream dis = new DataInputStream(sk.getInputStream());
        String RespuestaServidor;
        System.out.println();
        gui(pantallaInici);
        System.out.print("Introdueix una opció: ");
        String s1 = sc.next();
        boolean sortir = false;
        dos.writeUTF(s1);
        while (!sortir) {
            switch (Integer.parseInt(s1)) {
                case 1:
                    int con = 0;
                    String repServidor = "";
                    String idUsuari = "";
                    gui(signIn);
                    while (con <= 3) {
                        System.out.println("Introdueix nom d'usuari:");
                         idUsuari = sc.next();
                        System.out.println("Introdueix contrasenya:");
                        String contrasenya = sc.next();
                        dos.writeUTF(idUsuari);
                        dos.writeUTF(contrasenya);
                        repServidor = dis.readUTF();
                        if (repServidor.equals("false")) {
                            System.out.println("La contrasenya o el usuari son incorrectes.");
                            con++;
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
                                "3.- [Xat]                          ",
                                "4.- [Configuració servidor]        ",
                                "5.- [Configuració client]          ",
                                "6.- [Sign out]                     ",
                                "",};
                    if (repServidor.equals("true")) {
                        Thread.sleep(2000);
                        gui(pantallaPrincipal);
                        s1 = sc.next();
                        switch (Integer.parseInt(s1)) {
                            case 1:
                                gui(opGrup);
                                s1 = sc.next();
                                switch (Integer.parseInt(s1)) {
                                    case 1:
                                        break;
                                    case 2:
                                        gui(adminGrup);
                                        System.out.print("Introdueix una opció: ");
                                        s1 = sc.next();
                                        switch (Integer.parseInt(s1)){
                                            case 1:
                                                break;
                                            case 2:
                                                break;
                                            case 3:
                                                break;    
                                            case 4:
                                                break;
                                        }
                                        break;
                                    case 3:
                                        break;
                                }
                                break;
                            case 2:
                                gui(opFitxers);
                                System.out.print("Introdueix una opció: ");
                                s1 = sc.next();
                                switch (Integer.parseInt(s1)) {
                                    case 1:
                                        break;
                                    case 2:
                                        break;
                                    case 3:
                                        break;
                                    case 4:
                                        break;
                                }
                                break;
                            case 3:
                                gui(opXat);
                                System.out.print("Introdueix una opció: ");
                                s1 = sc.next();
                                switch (Integer.parseInt(s1)) {
                                    case 1:
                                        break;
                                    case 2:
                                        break;
                                    case 3:
                                        break;
                                    case 4:
                                        break;
                                }
                                break;
                            case 4:
                                gui(confServidor);
                                System.out.print("Introdueix una opció: ");
                                s1 = sc.next();
                                switch (Integer.parseInt(s1)) {
                                    case 1:
                                        break;
                                    case 2:
                                        break;
                                    case 3:
                                        break;
                                    case 4:
                                        break;
                                    case 5:
                                        break;
                                    case 6:
                                        break;
                                    case 7:
                                        break;
                                }
                                break;
                            case 5:
                                gui(confClient);
                                System.out.print("Introdueix una opció: ");
                                s1 = sc.next();
                                switch (Integer.parseInt(s1)) {
                                    case 1:
                                        break;
                                    case 2:
                                        break;
                                    case 3:
                                        break;
                                    case 4:
                                        break;
                                    case 5:
                                        break;
                                    case 6:
                                        break;
                                    case 7:
                                        break;    
                                }
                                break;
                            case 6:
                                
                                break;

                        }
                        break;
                    }
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
                    break;
                case 3:
                    if (dis.readUTF().equals("true")) {
                        sortir = true;
                    }
                    gui(exit);

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
}
