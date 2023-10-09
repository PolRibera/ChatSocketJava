 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package projecte;


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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 *  @author Ricard Sierra, Pol Ribera, Alex Montoya
 */
public class Servidor { //ÉS EL SERVIDOR, ENCARA QUE REP ELS FITXERS
    public static void main(String[] args) throws IOException { //per provar el receptor de fitxers
        int PUERTO = 54322;
        ServerSocket skk = new ServerSocket(PUERTO);
        while( true ){
        try {
            Socket sk = skk.accept(); //accepta una conexión
            Server sv = new Server( sk );
            System.out.println("Escoltant el port " + PUERTO);
            System.out.println("connectant amb el client " + sk.getInetAddress().getHostAddress() );
            sv.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
       }
    }
     public static class Server extends Thread{
        static Socket sk;
        static String respostaUsuariRebut;
        Server( Socket sk ){
            this.sk = sk;
        }
        @Override
        public void run(){
           FileOutputStream fileOutput;
           BufferedOutputStream bo;
           File fo;
            try {
                DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
                DataInputStream dis = new DataInputStream(sk.getInputStream());
                ArrayList<String> missatgesAEnviar = new ArrayList<>();
                System.out.println("llegit " + sk.getInetAddress() + ":" + sk.getPort());
                missatgesAEnviar.add("0: Sing-in (Entrar en compte exsistent)");
                missatgesAEnviar.add("1: Sing-up (Crear compte) ");
                missatgesAEnviar.add("2: Llistar Usuaris");
                missatgesAEnviar.add("3: Probes enviar fitxer o missatge");
                dos.writeUTF(""+missatgesAEnviar.size()+"");
                for (int i = 0; i < missatgesAEnviar.size(); i++) {
                    dos.writeUTF(missatgesAEnviar.get(i));
                }
                respostaUsuariRebut = dis.readUTF();
                switch(Integer.parseInt(respostaUsuariRebut)){
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        dos.writeUTF("Que vols fer? 0: Enviar String 1:Enviar Fitxer");
                        respostaUsuariRebut = dis.readUTF();
                            if (respostaUsuariRebut.equals("1")) {
                                         try {
                                            respostaUsuariRebut = dis.readUTF();
                                            dos.writeUTF("Fitxer rebut: "+respostaUsuariRebut);
                                            String s2[] = respostaUsuariRebut.split("[\\\\/]"); //per si acàs, treiem la ruta del nom del fitxer, per si s'ha posat
                                             int lbloc = 512; //no cal que sigui el mateix tamany en el emisor i receptor
                                            respostaUsuariRebut = s2[s2.length - 1];
                                            String nomfichPrevi = "rebrent_" + respostaUsuariRebut; //El nom es canvia per saber que el fitxer encara no s'ha baixat del tot
                                            long lfic = dis.readLong();
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
                                                    leido = dis.read(b, 0, lbloc); //llegeix com al molt lbloc bytes, però pot ser que sigui altra quantitat menor
                                                } else {//falten menys bytes que lbloc
                                                    leido = dis.read(b, 0, (int) (lfic - lleva)); //llegeix com a molt tants bytes com falten
                                                }
                                                bo.write(b, 0, leido);
                                                lleva = lleva + leido; //per saber quants es porten llegits
                                                System.out.println("Bytes rebuts: " + leido + " portem: " + lleva + " bytes");
                                            }

                                            bo.close();
                                            //reanomena el fitxer
                                            File nufile = new File("rec_" + respostaUsuariRebut); //El fitxer ja està baixat. Se li ha de posar el nom final correcte. No li posem el que s'envia per si s'està provant al mateix ordinador
                                            nufile.delete();
                                            fo.renameTo(nufile);

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                            } else if (respostaUsuariRebut.equals("0")){
                                respostaUsuariRebut = dis.readUTF();
                                System.out.println("rebut missatge:" + respostaUsuariRebut );
                            }

                            sk.close(); //tanquem la connexió
                        break;
                }

                
            } catch (IOException ex) {
                Logger.getLogger(ConecInicialServidorString.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
     
}