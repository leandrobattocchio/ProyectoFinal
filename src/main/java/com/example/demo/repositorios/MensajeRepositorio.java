
package com.example.demo.repositorios;

import com.example.demo.entidades.Mensaje;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MensajeRepositorio extends JpaRepository<Mensaje, String>{
    
/////Query para traer todos los mensajes recibidos de un usuario.  
    @Query("SELECT c FROM Mensaje c WHERE c.usuarioReceptor.id = :id")
    List<Mensaje> listarMensajesRecibidos(@Param("id") String id);
        
    
/////Query para traer todos los mensajes enviados de un usuario.    
    @Query("SELECT c FROM Mensaje c WHERE c.usuarioEmisor.id = :id")
    List<Mensaje> listarMensajesEnviados(@Param("id") String id);
}
