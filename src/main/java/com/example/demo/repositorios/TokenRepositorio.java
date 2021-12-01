
package com.example.demo.repositorios;

import com.example.demo.entidades.TokenUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepositorio extends JpaRepository<TokenUsuario, String> {
    
}
