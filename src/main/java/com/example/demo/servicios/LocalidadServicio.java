package com.example.demo.servicios;

import com.example.demo.entidades.Localidad;
import com.example.demo.excepciones.ErrorServicio;
import com.example.demo.repositorios.LocalidadRepositorio;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LocalidadServicio {

    @Autowired
    private LocalidadRepositorio localidadRepositorio;

    
/////Metodo para buscar todas las localidades
    @Transactional(readOnly = true)
    public List<Localidad> listarLocalidades(){
      return localidadRepositorio.findAll();
    }
    
/////Metodo para buscar localidad por id    
    @Transactional(readOnly = true)
    public Localidad listarLocalidadPorId(String idLocalidad) throws ErrorServicio{

        Optional<Localidad> respuesta = localidadRepositorio.findById(idLocalidad);
        
        if (respuesta != null) {
            Localidad localidad = respuesta.get();
            return localidad;
        }else{
           throw new ErrorServicio("Error al intentar encontrar la localidad en cuestión(No se encontró)."); 
        }
  
    }
}
