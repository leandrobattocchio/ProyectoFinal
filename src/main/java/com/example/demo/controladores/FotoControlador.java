
package com.example.demo.controladores;

import com.example.demo.entidades.Gato;
import com.example.demo.entidades.Usuario;
import com.example.demo.excepciones.ErrorServicio;
import com.example.demo.servicios.GatoServicio;
import com.example.demo.servicios.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
@RequestMapping("/foto")
public class FotoControlador {
    
    
    @Autowired
    private UsuarioServicio usuarioServicio;
    
    @Autowired
    private GatoServicio gatoServicio;
    
    
/////Controlador para mostrar la foto del usuario.    
    @GetMapping("/usuario/{id}")
    public ResponseEntity<byte[]>fotoUsuario(@PathVariable String id) {
             
        try {
            Usuario usuario = usuarioServicio.buscarUsuarioPorId(id);
            
            if (usuario.getFoto() == null) {
                throw new ErrorServicio("No se encontro una foto para este usuario.");
            }
   
            byte[] foto = usuario.getFoto().getContenido();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            
            
            return new ResponseEntity<>(foto, headers, HttpStatus.OK);
            
        } catch (ErrorServicio ex) {
           
            return new ResponseEntity(HttpStatus.NOT_FOUND);
            
        }
        
        
        
        
    }
    
/////Controlador para mostrar la foto del gato.     
    @GetMapping("/gato/{id}")
    public ResponseEntity<byte[]>fotoMascota(@PathVariable String id) {
             
        try {
            
            Gato gato = gatoServicio.buscarGatoPorId(id);
            
            if (gato.getFotoPrincipal() == null) {
                throw new ErrorServicio("No se encontro una foto para esta mascota.");
            }
   
            byte[] foto = gato.getFotoPrincipal().getContenido();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            
            
            return new ResponseEntity<>(foto, headers, HttpStatus.OK);
            
        } catch (ErrorServicio ex) {
           
            return new ResponseEntity(HttpStatus.NOT_FOUND);
            
        }
        
        
        
        
    }
}
