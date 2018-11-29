/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerJava;

import ServerJava.UtilitarioHilo;
import ServerJava.ServerCore;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;

/**
 * @author nelson.zetino
 * @version
 */
public class HiloClienteCore extends Thread implements UtilitarioHilo {

    /**
     * Socket que se utiliza para comunicarse con el cliente.
     */
    private final Socket socket;
    /**
     * Stream con el que se envían objetos al servidor.
     */
    private ObjectOutputStream objectOutputStream;
    /**
     * Stream con el que se reciben objetos del servidor.
     */
    private ObjectInputStream objectInputStream;
    /**
     * Servidor al que pertenece este hilo.
     */
    private final ServerCore server;
    /**
     * Identificador único del cliente con el que este hilo se comunica.
     */
    private String identificador;
    /**
     * Variable booleana que almacena verdadero cuando este hilo esta escuchando
     * lo que el cliente que atiende esta diciendo.
     */
    private boolean escuchando;

    /**
     * Método constructor de la clase hilo cliente.
     *
     * @param socket
     * @param server
     */
    public HiloClienteCore(Socket socket, ServerCore server) {
        this.server = server;
        this.socket = socket;
        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            System.err.println("Error en la inicialización del ObjectOutputStream y el ObjectInputStream");
        }
    }

    @Override
    public void run() {
        try {
            HiloDeEscucha();
        } catch (Exception ex) {
            System.err.println("Error al llamar al método readLine del hilo del cliente.");
        }
        DesconectarHilo();
    }

    @Override
    public void EnviarMsg(LinkedList<String> lista) {
        try {
            objectOutputStream.writeObject(lista);
        } catch (IOException e) {
            System.err.println("Error al enviar el objeto al cliente.");
        }

    }

    @Override
    public void HiloDeEscucha() {
        escuchando = true;
        while (escuchando) {
            try {
                Object aux = objectInputStream.readObject();
                if (aux instanceof LinkedList) {
                    EjecucionHilo((LinkedList<String>) aux);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error al leer lo enviado por el cliente.");
            }
        }
    }

    @Override
    public void EjecucionHilo(LinkedList<String> lista) {
// 0 - El primer elemento de la lista es siempre el tipo
        String tipo = lista.get(0);
        switch (tipo) {
            case "SOLICITUD_CONEXION":
                // 1 - Identificador propio del nuevo usuario
                ConfirmarConexion(lista.get(1));
                break;
            case "SOLICITUD_DESCONEXION":
                // 1 - Identificador propio del nuevo usuario
                ConfirmarDesconexion();
                break;
            case "MENSAJE":
                // 1      - Cliente emisor
                // 2      - Cliente receptor
                // 3      - Mensaje
                String destinatario = lista.get(2);
                server.clientes
                        .stream()
                        .filter(h -> (destinatario.equals(h.getIdentificador())))
                        .forEach((h) -> h.EnviarMsg(lista));
                break;
            default:
                break;
        }
    }

    @Override
    public void ConfirmarConexion(String identificador) {
        ServerCore.correlativo++;
        this.identificador = ServerCore.correlativo + " - " + identificador;
        LinkedList<String> lista = new LinkedList<>();
        lista.add("CONEXION_ACEPTADA");
        lista.add(this.identificador);
        lista.addAll(server.getUsuariosConectados());
        EnviarMsg(lista);
        server.agregarLog("\nNuevo cliente: " + this.identificador);
        //enviar a todos los clientes el nombre del nuevo usuario conectado excepto a él mismo
        LinkedList<String> auxLista = new LinkedList<>();
        auxLista.add("NUEVO_USUARIO_CONECTADO");
        auxLista.add(this.identificador);
        server.clientes
                .stream()
                .forEach(cliente -> cliente.EnviarMsg(auxLista));
        server.clientes.add(this);

    }

    public String getIdentificador() {
        return identificador;
    }

    @Override
    public void ConfirmarDesconexion() {
        LinkedList<String> auxLista = new LinkedList<>();
        auxLista.add("USUARIO_DESCONECTADO");
        auxLista.add(this.identificador);
        server.agregarLog("\nEl cliente \"" + this.identificador + "\" se ha desconectado.");
        this.DesconectarHilo();
        for (int i = 0; i < server.clientes.size(); i++) {
            if (server.clientes.get(i).equals(this)) {
                server.clientes.remove(i);
                break;
            }
        }
        server.clientes
                .stream()
                .forEach(h -> h.EnviarMsg(auxLista));
    }

    @Override
    public void DesconectarHilo() {
        try {
            socket.close();
            escuchando = false;
        } catch (IOException ex) {
            System.err.println("Error al cerrar el socket de comunicación con el cliente.");
        }
    }

}
