//-----------------------//
//@Author Ricard Sierra--//
//-------DAM2T-----------//
//-----------------------//



import java.util.Scanner;

public class testGui {


    static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) {

        String[] pantallaInici = {
                "",
                "Benvingut a CopernicXat            ",
                "",
                "1.- [Sign in]                      ",
                "2.- [Sign up]                      ",
                "3.- [Exit]                         ",
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
                "Benvingut a [nom usuari]           ",
                "",
                "1.- [Opcions Grup]                 ",
                "2.- [Opcions fitxers]              ",
                "3.- [Xat]                          ",
                "4.- [Configuració servidor]        ",
                "5.- [Configuració client]          ",
                "6.- [Sign out]                     ",
                "",
        };

        String[] opGrup = {
                "",
                "Opcions Grup                       ",
                "",
                "1.- [Donar de alta client]         ",
                "2.- [Donar de baixa client]        ",
                "3.- [Administrar grup]             ",
                "4.- [Menu principal]               ",
                "",
        };

        String[] adminGrup = {
                "",
                "Administrar grup                   ",
                "",
                "1.- [Afegir usuari]                ",
                "2.- [Esborrar usuari]              ",
                "3.- [Llistar membres del grup]     ",
                "",
        };

        String[] opFitxers = {
                "",
                "Opcions fitxers                    ",
                "",
                "1.- [Enviar fitxer al servidor]    ",
                "2.- [Llegir fitxer]                ",
                "3.- [Descarregar fitxer]           ",
                "4.- [Menu principal]               ",
                "",
        };

        String[]  opXat = {
                "",
                "Xat                                ",
                "",
                "1.- [Enviar missatge]              ",
                "2.- [Llegir missatge]              ",
                "3.- [Menu principal]               ",
                "",
        };

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
                "",
        };

        String[] confClient = {
                "",
                "Configurar servidor                ",
                "",
                "1.- [Cambiar nom client]           ",
                "2.- [Tamany maxim que pot rebre]   ",
                "3.- [Cambiar ip servidor]          ",
                "4.- [Port per defecta servidor]    ",
                "7.- [Menu principal]               ",
                "",
        };



        gui(pantallaInici, 9);
        gui(signIn,5);
        gui(signUp, 5);
        gui(exit, 5);
        gui(pantallaPrincipal,12 );
        gui(opGrup, 10);
        gui(adminGrup, 9);
        gui(opFitxers, 10);
        gui(opXat , 9);
        gui(confServidor, 13);
        gui(confClient, 11);

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

