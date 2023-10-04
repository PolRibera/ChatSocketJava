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
public class ConnectInicialClientString {
    
    public static void main(String[] args) throws IOException {
        
        System.out.println("Inicia cliente");
        
        Socket sk = new Socket( "192.168.2.2", 8100 ); //accepta una conexión
        
        DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
        DataInputStream dis = new DataInputStream(sk.getInputStream());
        
        byte pon = 1;

        String s = "nom";
        
        
        
        dos.writeUTF(s);

        System.out.println("Enviado " + s );

        s = dis.readUTF(); //leo del servidor
        
        System.out.println( "Recibido: " + s );
        
        sk.close(); //tanquem la connexió
    }
   
}

