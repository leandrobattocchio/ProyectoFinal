package com.example.demo.controladores;

import com.example.demo.entidades.Localidad;
import com.example.demo.entidades.Usuario;
import com.example.demo.enums.SexoHumano;
import com.example.demo.excepciones.ErrorServicio;
import com.example.demo.servicios.LocalidadServicio;
import com.example.demo.servicios.UsuarioServicio;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class PortalControlador {

    @Autowired
    private LocalidadServicio localidadServicio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @GetMapping("/")
    public String index() {
 
        return "index.html";
    }
 
    @GetMapping("/login")
    public String login(HttpSession session, ModelMap modelo, @RequestParam(required = false) String error, @RequestParam(required = false) String logout) {

        if (logout != null) {
            modelo.put("logout", "¡Se ha deslogueado correctamente de nuestro sitio web!");
        }

        if (error != null) {
            modelo.put("error", "¡El usuario o contraseña no coinciden!");
            session.invalidate();
        }

        return "login.html";
    }

    @GetMapping("/registro")
    public String registo(ModelMap modelo) {

        Usuario usuario = new Usuario();
        modelo.addAttribute("perfil", usuario);
        List<Localidad> localidades = localidadServicio.listarLocalidades();
        modelo.addAttribute("localidades", localidades);
        modelo.addAttribute("accion", "Registro");
        modelo.addAttribute("sexos", SexoHumano.values());
        return "registro.html";
    }

    @PostMapping("/registrar")
    public String registrar(ModelMap modelo, @RequestParam String nombre, @RequestParam String apellido,
            @RequestParam String email, @RequestParam String edad, @RequestParam String clave1, @RequestParam String clave2, @RequestParam MultipartFile archivo,
            String idLocalidad, @RequestParam String username, @RequestParam SexoHumano sexo, @RequestParam String numero) {

        try {
   
            usuarioServicio.registrar(archivo, sexo, username, nombre, edad, numero, apellido, email, clave1, clave2, idLocalidad);
            modelo.addAttribute("titulo", "¡Felicidades!");
            modelo.addAttribute("descripcion", "Se ha registrado correctamente en nuestra página");
            return "exito.html";

        } catch (ErrorServicio e) {
            List<Localidad> localidades = localidadServicio.listarLocalidades();
            modelo.addAttribute("localidades", localidades);
            modelo.addAttribute("sexos", SexoHumano.values());
            
            Usuario usuario = new Usuario();
            usuario.setApellido(apellido);
            usuario.setEdad(edad);
            usuario.setEmail(email);
            usuario.setNombre(nombre);
            usuario.setNumero(numero);
            usuario.setUsername(username);
            modelo.addAttribute("perfil", usuario);
            modelo.addAttribute("error", e.getMessage());
            modelo.addAttribute("accion", "Registro");
            
            return "registro.html";
        }

    }

    
    @GetMapping("/contacto")
    public String contacto(){
        return "contacto.html";
    }
}
