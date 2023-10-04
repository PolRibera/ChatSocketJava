/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package projecte;


/**
 *
 *  @author Ricard Sierra, Pol Ribera, Alex Montoya
 */

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import static projecte.ConecInicialServidorString.n;

/**
 *
 * @author ivan
 */
public class ConServidorFitxer { //ÉS EL SERVIDOR, ENCARA QUE REP ELS FITXERS

    static final int METODE_REBRE = 1; //1: tot de cop; 2: en blocs
    
    Socket sk;
    FileOutputStream fileOutput;
    BufferedOutputStream bo;
    File fo;
    InputStream in;

    String nomfich;

    public static void main(String[] args) { //per provar el receptor de fitxers
        
        int PUERTO = 54322;

        ServerSocket skServidor;
        while( true ){
        try {
            skServidor = new ServerSocket(PUERTO);
            System.out.println("Escoltant el port " + PUERTO);
            Socket sk = skServidor.accept();
            System.out.println("connectant amb el client " + sk.getInetAddress().getHostAddress() );

            ConServidorFitxer rf = new ConServidorFitxer(sk);


        } catch (IOException e) {
            e.printStackTrace();
        }
       }
    }
     public static class Server extends Thread{
        Socket sk;
        
        Server( Socket sk ){
            this.sk = sk;
        }
        
        @Override
        public void run(){
            
            InputStream is;
            OutputStream os;
            
            try {
                is = sk.getInputStream();
                os = sk.getOutputStream();

                DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
                DataInputStream dis = new DataInputStream(sk.getInputStream());
        
                String s = dis.readUTF();
                System.out.println("llegit " + sk.getInetAddress() + ":" + sk.getPort() + " -> " + s );
               
                dos.writeUTF(  "Eres la conexión número " + n );
                
                sk.close(); //tanquem la connexió
                
            } catch (IOException ex) {
                Logger.getLogger(ConecInicialServidorString.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public ConServidorFitxer(Socket sk) {
        this.sk = sk;
        
        switch( METODE_REBRE ){
            case 1:
                reb(); //reb tot en un array de bytes
                break;
            case 2:
                rebFitxerEnBlocs(); //reb en blocs
                break;
        }
    }

    void reb() {
        try {
            in = sk.getInputStream();

            DataInputStream ois = new DataInputStream(in);
            nomfich = ois.readUTF();

            String s[] = nomfich.split("[\\\\/]"); //per si acàs, treiem la ruta del nom del fitxer, per si s'ha posat
            nomfich = s[s.length - 1];

            String nomfichPrevi = "recibiendo_" + nomfich; //El nom es canvia per saber que el fitxer encara no s'ha baixat del tot
            long lfic = ois.readLong();

            fo = new File(nomfichPrevi);
            fo.delete(); //eliminem el fitxer per si ja existia
            fileOutput = new FileOutputStream(fo);
            bo = new BufferedOutputStream(fileOutput);

            byte b[] = new byte[(int) lfic];

            int rebuts = ois.read(b);
            System.out.println("rebuts: " + rebuts + " falten " + (lfic - rebuts) );

            while (rebuts < lfic) {
                int reci = ois.read(b, rebuts, (int) lfic - rebuts);
                rebuts += reci;
                System.out.println("rebuts: " + reci + " falten " + (lfic - rebuts) );
            }

            bo.write(b);
            bo.close();
            //renomena el fitxer
            File nufile = new File("rec_" + nomfich); //El fitxer ja està baixat. Se li ha de posar el nom final correcte. No li posem el que s'envia per si s'està provant al mateix ordinador

            nufile.delete();
            fo.renameTo(nufile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void rebFitxerEnBlocs() {

        int lbloc = 512; //no cal que sigui el mateix tamany en el emisor i receptor

        try {
            in = sk.getInputStream();

            DataInputStream ois = new DataInputStream(in);
            nomfich = ois.readUTF();

            String s[] = nomfich.split("[\\\\/]"); //per si acàs, treiem la ruta del nom del fitxer, per si s'ha posat
            nomfich = s[s.length - 1];

            String nomfichPrevi = "rebrent_" + nomfich; //El nom es canvia per saber que el fitxer encara no s'ha baixat del tot
            long lfic = ois.readLong();

            fo = new File(nomfichPrevi);
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
                System.out.println("Bytes rebuts: " + leido + " portem: " + lleva + " bytes");
            }

            bo.close();
            //reanomena el fitxer
            File nufile = new File("rec_" + nomfich); //El fitxer ja està baixat. Se li ha de posar el nom final correcte. No li posem el que s'envia per si s'està provant al mateix ordinador
            nufile.delete();
            fo.renameTo(nufile);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}