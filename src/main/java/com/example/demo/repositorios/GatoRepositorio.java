package com.example.demo.repositorios;

import com.example.demo.entidades.Gato;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GatoRepositorio extends JpaRepository<Gato, String> {

/////Query para traer a todos los gatos activos de la página.  
    @Query("SELECT c FROM Gato c WHERE c.baja IS NULL")
    public List<Gato> listarGatosActivos();

/////Query para traer a todos los gatos del usuario.    
    @Query("SELECT c FROM Gato c WHERE (c.usuario.id = :id AND c.baja IS NULL) AND c.adoptado IS NULL")
    public List<Gato> listarGatosActivosPorUsuario(@Param("id") String idUsuario);

/////Query para traer a todos los gatos determinados por un filtro especifico.    
    @Query("SELECT c FROM Gato c WHERE ((c.nombre LIKE :q or c.sexo LIKE :q or c.raza LIKE :q or c.usuario.localidad.nombre LIKE :q) AND c.baja IS NULL) AND c.adoptado IS NULL")
    List<Gato> listarGatosActivosPorQuery(@Param("q") String q);

    /////Query para traer a todos los gatos que fueron adoptados del usuario.    
    @Query("SELECT c FROM Gato c WHERE (c.usuario.id = :id AND c.baja IS NULL) AND c.adoptado IS NOT NULL")
    public List<Gato> listarGatosAdoptadosDelUsuario(@Param("id") String idUsuario);
    
    //Query para traer todos los gatos activos/inactivos
    @Query("SELECT c FROM Gato c ORDER BY c.creado desc")
    public List<Gato>listarGatos();
}
