/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerJava;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import javax.swing.JOptionPane;

/**
 * @author nelson.zetino
 * @version
 */
public class ServerCore extends Thread {

    /*
     * Lista de todos los hilos de comunicación, para cada cliente se instancia
     * uno de estos hilos ya que cada hilo esta escuchando permanentemente lo
     * que dicho cliente envía al servidor.
     */
    LinkedList<HiloClienteCore> clientes;

    /*
     * Socket servidor que tiene como principal función escuchar cuando los
     * clientes se conectan para incluirlos en el chat.
     */
    private ServerSocket serverSocket;
    /*
     * Variable que almacena la ventana que gestiona la interfaz gráfica del
     * servidor.
     */
    private final ServerFrame ventana;
    /**
     * Variable que almacena el puerto que el servidor usará para escuchar.
     */
    private final String puerto;
    /**
     * Correlativo para diferenciar a los múltiples clientes que se conectan, si
     * se conectaran, por ejemplo, dos usuarios con el mismo nombre, se podrían
     * diferenciar por este correlativo.
     */
    static int correlativo;

    /**
     * Constructor del servidor.
     *
     * @param puerto
     * @param ventana
     */
    public ServerCore(String puerto, ServerFrame ventana) {
        correlativo = 0;
        this.puerto = puerto;
        this.ventana = ventana;
        clientes = new LinkedList<>();
        this.start();
    }

    /**
     * Método sobre el que corre el bucle infinito que tiene como función
     * escuchar permenentemente en espera de conexiones de nuevos clientes.
     */
    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(Integer.valueOf(puerto));
            ventana.addServidorIniciado();
            while (true) {
                HiloClienteCore h;
                Socket socket;
                socket = serverSocket.accept();
                System.out.println("Nueva conexion entrante: " + socket);
                h = new HiloClienteCore(socket, this);
                h.start();
            }
        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(ventana, "El servidor no se ha podido iniciar,\n"
                    + "puede que haya ingresado un puerto incorrecto.\n"
                    + "Esta aplicación se cerrará.");
            System.exit(0);
        }
    }

    /**
     * Ciclo que devuelve una lista con los identificadores de todos los
     * clientes conectados.
     *
     * @return
     */
    LinkedList<String> getUsuariosConectados() {
        LinkedList<String> usuariosConectados = new LinkedList<>();
        clientes.stream().forEach(c -> usuariosConectados.add(c.getIdentificador()));
        return usuariosConectados;
    }

    /**
     * Método que agrega una linea al log de la interfaz gráfica del servidor.
     *
     * @param texto
     */
    void agregarLog(String texto) {
        ventana.agregarLog(texto);
    }

}
