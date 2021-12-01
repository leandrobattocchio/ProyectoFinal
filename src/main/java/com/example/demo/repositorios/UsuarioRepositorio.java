
package com.example.demo.repositorios;

import com.example.demo.entidades.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, String> {
    
    //Query para buscar usuario por mail
    @Query("SELECT c FROM Usuario c WHERE c.email = :email")
    public Usuario buscarUsuarioPorEmail(@Param("email") String email);
    
    //Query para buscar usuario por username
    @Query("SELECT c FROM Usuario c WHERE c.username = :username")
    public Usuario buscarUsuarioPorUsername(@Param("username") String username);
    
    //Query para traer todos los usuarios registrados
    @Query("SELECT c FROM Usuario c WHERE c.id NOT LIKE :id")
    public List<Usuario> listarUsuariosRegistrados(@Param("id")String id);
    
    
}
