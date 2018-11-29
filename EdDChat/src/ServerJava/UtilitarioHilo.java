/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerJava;

import java.util.LinkedList;

/**
 *
 * @author DELL
 */
public interface UtilitarioHilo {
/**
 * 
 * @param lista 
 */
    public void EnviarMsg(LinkedList<String> lista);
/**
 * 
 */
    public void HiloDeEscucha();
/**
 * 
 * @param lista 
 */
    public void EjecucionHilo(LinkedList<String> lista);
/**
 * 
 * @param identificador 
 */
    public void ConfirmarConexion(String identificador);
/**
 * 
 */
    public void ConfirmarDesconexion();
/**
 * 
 */
    public void DesconectarHilo();

}
