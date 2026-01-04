/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chazzboutiquepersistencia.daos;

import com.mycompany.chazzboutiquepersistencia.conexion.IConexionBD;
import com.mycompany.chazzboutiquepersistencia.dominio.Usuario;
import com.mycompany.chazzboutiquepersistencia.excepciones.PersistenciaException;
import com.mycompany.chazzboutiquepersistencia.interfacesDAO.IUsuarioDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;


/**
 *
 * @author carli
 */
public class UsuarioDAO implements IUsuarioDAO {

    IConexionBD conexionBD;

    public UsuarioDAO(IConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }
    
     @Override
    public Usuario iniciarSesion(String nombreUsuario, String contrasena) throws PersistenciaException {
        EntityManager em = conexionBD.getEntityManager();
        try {
            TypedQuery<Usuario> query = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.nombreUsuario = :nombreUsuario AND u.contraseña = :contrasena", 
                Usuario.class
            );
            query.setParameter("nombreUsuario", nombreUsuario);
            query.setParameter("contrasena", contrasena);

            List<Usuario> resultados = query.getResultList();
            return resultados.isEmpty() ? null : resultados.get(0);
        } catch (Exception e) {
            throw new PersistenciaException("Error al iniciar sesión", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Usuario buscarPorId(Long id) throws PersistenciaException {
        EntityManager em = conexionBD.getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } catch (Exception e) {
            throw new PersistenciaException("Error al buscar usuario por ID", e);
        } finally {
            em.close();
        }
    }
}

