/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClienteJava;

import java.util.LinkedList;

/**
 *
 * @author DELL
 */
public interface UtilitiarioCliente {

    public void EnviarMensaje(String cliente_receptor, String mensaje);

    public void Escuchar();

    public void Ejecutar(LinkedList<String> lista);

    public void EnviarSolicitudConexion(String identificador);

    public void ConfirmarDesconexion();

    public void Desconectar();

}
