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
 *
 * @author polri
 */
public class Cliente {
        public static Scanner sc = new Scanner(System.in);
        
        
    public static void main(String[] args) throws IOException {
        
        System.out.println("Inicia cliente");
        
        Socket sk = new Socket( "localhost",54322); //accepta una conexión
        
        DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
        DataInputStream dis = new DataInputStream(sk.getInputStream());
        String RespuestaServidor;
        String s = dis.readUTF();
        for (int i = 0; i < Integer.parseInt(s); i++) {
            RespuestaServidor = dis.readUTF();
            System.out.println(RespuestaServidor);
        }
        String s1 = sc.next();
        dos.writeUTF(s1);
        switch (Integer.parseInt(s1)){
            case 0:
                break;
            case 1:
                            
                            System.out.println(dis.readUTF());
                            String idUsuariC = sc.next();
                            dos.writeUTF(idUsuariC);
                            System.out.println(dis.readUTF());
                            String NomC = sc.next();
                            dos.writeUTF(NomC);
                            System.out.println(dis.readUTF());
                            String CognomC = sc.next();
                            dos.writeUTF(CognomC);
                            System.out.println(dis.readUTF());
                            String contraseñaC = sc.next();
                            dos.writeUTF(contraseñaC);
                break;
            case 2:
                String RespuestaServidorLLISTAT;
                String llargadaArrayList = dis.readUTF();
                for (int i = 0; i < Integer.parseInt(llargadaArrayList); i++) {
                    RespuestaServidorLLISTAT = dis.readUTF();
                    System.out.println(RespuestaServidorLLISTAT);
                }
            case 3:
                System.out.println(dis.readUTF());
                s1 = sc.next();
                dos.writeUTF(s1);
                System.out.println("Enviado " + s1 );
                if (s1.equals("1")) {
                   System.out.println("Connectem amb el servidor");
                   System.out.println("Indica la ruta");
                   String ruta = sc.next();
                   ConClientFitxer ef = new ConClientFitxer(sk,ruta); //posar el fitxer a enviar, podem posar-lo amb la ruta al fitxer  
                   System.out.println("fitxer enviat:  " + ef.nomfich);
                   sk.close();
                } else if (s1.equals("0")){
                   System.out.println("Indica el missatge que vols enviar");
                   String s2 = sc.next();
                   dos.writeUTF(s2);
                   System.out.println("Enviado " + s2 );
                }
                break;
        }
        

    }
    
       
    
   
}

