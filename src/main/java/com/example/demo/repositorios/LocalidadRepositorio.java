
package com.example.demo.repositorios;

import com.example.demo.entidades.Localidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalidadRepositorio extends JpaRepository<Localidad, String> {
    
}
