
package projecte;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ivan
 */
public class ConecInicialServidorString {
    
    static int n = 0; //contador de conexiones
    
    public static void main(String[] args) throws IOException {
        
        System.out.println("Inicia servidor");
        
        ServerSocket ssk = new ServerSocket(8100); //escoltarà en el port 8100
        
        while( true ){
            Socket sk = ssk.accept(); //accepta una conexión
            Server sv = new Server( sk );
            sv.start();
            n++;
            System.out.println("CONECTADOS " +  n );
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
}


