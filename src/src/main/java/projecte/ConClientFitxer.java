
package projecte;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;


/**
 *
 * @author Ricard Sierra, Pol Ribera, Alex Montoya
 */


public class ConClientFitxer { //És el client, ha d'estar posat en marxa el servidor

    static final int METODE_ENVIO = 2; //1: Se envia tot en un array de bytes; 2: Se envía en blocs d'arrays de bytes
    
    Socket sk;
    FileInputStream fileInput;
    BufferedInputStream bi;
    File fi;
    OutputStream out;

    String nomfich;
    
   public ConClientFitxer(Socket sk, String nomfich) {
        this.sk = sk;
        this.nomfich = nomfich;
        
        switch( METODE_ENVIO ){
            case 1:
                envia(); //envía de cop un el fitxer en un array de bytes
                break;
            case 2:
                enviaEnBlocs(); //envía en bloques
                break;
        }
    }

    void envia() { //utilitzem per enviar el DataOutputStream --> recibirem amb el DataInputStream

        try {
            fi = new File(nomfich);

            fileInput = new FileInputStream(fi);
            bi = new BufferedInputStream(fileInput); // es podria posar: bi = new BufferedInputStream(new FileInputStream(fi)) i no faria falta la línia anterior
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            out = sk.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        long lfic = fi.length();
        System.out.println("longitud del fitxer a enviar: " + lfic);
        DataOutputStream oos;
        try {
            oos = new DataOutputStream(out);
            oos.writeUTF(nomfich);
            oos.writeLong(lfic);
            
            byte b[] = new byte[(int) lfic];
            bi.read(b); // legeix el fitxer sencer en b
            oos.write(b); // enviem tot el fitxer. ATENCIÓ, EL SISTEMA OPERATIU DIVIDIRÀ LES DADES EN PAQUETS 
                            //MÉS PETITS PER ENVIAR-LO ==> LA RECEPCIÓ NO SERÁ UNICAMENT UN PAQUET, 
                            //S'HAURA D'ANAR LLEGINT FINS QUE ESTIGUI TOTA LA LONGITUT DE BYTES, PER AIXÒ S'ENVIA 
                            //ABANS AQUESTA QUANTITAT
            //oos.flush(); //vacia el buffer de oos, però no cal, doncs, amb el oos.close ja es buida tot
            oos.close();
            sk.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    void enviaEnBlocs() { 
        
        int lbloc = 1024; //tamany del bloc --> sol ser múltiple de 256 bytes, però pot ser qualsevol valor
        // Aquest tamany no cal que sigui el mateix en el emisor que en el receptor.

        try {
            fi = new File(nomfich);
            fileInput = new FileInputStream(fi);
            bi = new BufferedInputStream(fileInput);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            out = sk.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        long lfic = fi.length();
        DataOutputStream oos;
        try {
            sk.setSoLinger( true, 60);
        //espera 60 segons al tancar el socket en espera que l'altre extrem rebi la solicitut de finalitzar la sesió
        // si no es posa, pot pasar que es tanqui la sesió i en l'altra extrem hi hagi un error per falta de conexió. No seria greu si està gestionat
            oos = new DataOutputStream(out);
            oos.writeUTF(nomfich);
            oos.writeLong(lfic);
            // oos.flush(); //envía el buffer, pero no cal, doncs continuem escrivint en oos

            long veces = lfic / lbloc; //quants blocs s'han d'enviar
            int resto = (int) (lfic % lbloc); //quant querarà al final per enviar

            byte b[] = new byte[lbloc];

            for (long i = 0; i < veces; i++) {
                bi.read(b); //llegeix un tros del fitxer
                out.write(b); // envia el tros del fitxer
                System.out.println("enviat el tros " + i + " portem enviats " + (i + 1) * lbloc + " bytes");
            }
            //envia la resta del fitxer
            if (resto > 0) {
                bi.read(b, 0, resto); // llegeix la resta del fitxer en b
                out.write(b, 0, resto); // l'enviem
                System.out.println("Enviem els " + resto + " bytes restants");
            }
            //oos.flush(); //no cal, es fa un flush al fer el close de oos
            System.out.println("Enviat");
            //oos.close();
            //sk.close();
            System.out.println("Socket tancat");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}



