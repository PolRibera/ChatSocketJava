package projecte;

import java.io.IOException;
import java.Util.Scanner;

public class testGui {

    static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) {

        String[] pantallaInici = {
                "",
                "Benvingut a CopernicXat",
                "",
                "1.- [Sign in]",
                "2.- [Sign up]",
                "3.- [Exit]   ",
                "",
        };

        String[] signIn = {
                "",
                "[Sign in]",
                "",
        };

        String[] signUp = {
                "",
                "[Sign up]",
                "",
        };

        String[] exit ={
                "",
                "Fins un altre.",
                "",
        };

        String[] pantallaPrincipal = {
                "",
                "Benvingut a [nom usuari]",
                "",
                "1.- [Opcions Grup]       ",
                "2.- [Opcions fitxers]    ",
                "3.- [Xat]                ",
                "4.- [Configuració client]",
                "5.- [Configuració client]",
                "",

        };

        gui(pantallaInici, 9);
        gui(signIn,5);
        gui(signUp, 5);
        gui(exit, 5);
        gui(pantallaPrincipal,11 );

        int op = sc.nextInt();



    }

    public static void gui(String[] contingut,int height){
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


    static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) {

        String[] pantallaInici = {
                "",
                "Benvingut a CopernicXat",
                "",
                "1.- [Sign in]",
                "2.- [Sign up]",
                "3.- [Exit]   ",
                "",
        };

        String[] signIn = {
                "",
                "[Sign in]",
                "",
        };

        String[] signUp = {
                "",
                "[Sign up]",
                "",
        };

        String[] exit ={
                "",
                "Fins un altre.",
                "",
        };

        String[] pantallaPrincipal = {
                "",
                "Benvingut a [nom usuari]",
                "",
                "1.- [Opcions Grup]       ",
                "2.- [Opcions fitxers]    ",
                "3.- [Xat]                ",
                "4.- [Configuració client]",
                "5.- [Configuració client]",
                "",

        };

        gui(pantallaInici, 9);
        gui(signIn,5);
        gui(signUp, 5);
        gui(exit, 5);
        gui(pantallaPrincipal,11 );

        int op = sc.nextInt();



    }

    public static void gui(String[] contingut,int height){
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

