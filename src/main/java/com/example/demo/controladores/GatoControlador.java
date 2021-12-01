package com.example.demo.controladores;

import com.example.demo.entidades.Gato;
import com.example.demo.entidades.Usuario;
import com.example.demo.enums.Raza;
import com.example.demo.enums.SexoAnimal;
import com.example.demo.excepciones.ErrorServicio;
import com.example.demo.servicios.GatoServicio;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
@Controller
@RequestMapping("/gato")
public class GatoControlador {

    @Autowired
    private GatoServicio gatoServicio;

    // Envía una lista con los todos los Gatos. Puede recibir filtros.
    @GetMapping("/lista-gatos")
    public String listaGatos(HttpSession session, ModelMap model, @RequestParam(required = false) String q) {

        //Si no hay usuario logueado, lo saca.
        Usuario usuariologged = (Usuario) session.getAttribute("usuariosession");
        if (usuariologged == null) {
            return "redirect:/login";
        }

        //Envía List de Gatos según sea el filtro que se aplicó.
        List<Gato> gatos;
        if (q != null && !q.trim().isEmpty()) {
            gatos = gatoServicio.listarGatosActivosPorQuery(q);
        } else {
            gatos = gatoServicio.listarGatosActivos();
        }

        model.put("gatos", gatos);

        return "adoptargatitos.html";
    }

    //Envía el perfil de un Gato para ver sus detalles.
    @GetMapping("/perfil")
    public String perfilGato(HttpSession session, ModelMap model, @RequestParam String id) {

        //Si no hay usuario logueado, lo saca.
        Usuario usuariologged = (Usuario) session.getAttribute("usuariosession");
        if (usuariologged == null) {
            return "redirect:/login";
        }

        try {
            Gato gato = gatoServicio.buscarGatoPorId(id);
            model.put("gato", gato);
            return "perfilgato.html";
        } catch (ErrorServicio ex) {
            model.addAttribute("error", ex.getMessage());
            return "index.html";
        }

    }

    //Envía una lista de los gatos del Usuario logeado.
    @GetMapping("/mis-gatos")
    public String misGatos(HttpSession session, ModelMap model) {

        //Si no hay usuario logueado, lo saca.
        Usuario usuariologged = (Usuario) session.getAttribute("usuariosession");
        if (usuariologged == null) {
            return "redirect:/login";
        }

        List<Gato> gatos = gatoServicio.listarGatosActivosPorUsuario(usuariologged.getId());
        model.put("gatos", gatos);

        List<Gato> gatosAdoptados = gatoServicio.listarGatosAdoptadosDelUsuario(usuariologged.getId());
        model.put("gatosAdoptados", gatosAdoptados);

        return "misgatitos.html";
    }

    //Maneja las opciones de crear, editar o eliminar gato y devuelve el formulario para hacerlo.
    @GetMapping("/editar-gato")
    public String editarGato(HttpSession session, ModelMap model,
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String accion) {

        //Si no hay usuario logueado, lo saca.
        Usuario usuariologged = (Usuario) session.getAttribute("usuariosession");
        if (usuariologged == null) {
            return "redirect:/login";
        }

        /*Para crear un gato nuevo viene accion=null, para modificar o eliminar 
        viene accion=Actualizar o accion=Eliminar. */
        if (accion == null) {
            accion = "Crear";
        }

        Gato gato = new Gato();
        if (id != null && !id.isEmpty()) {

            try {
                gato = gatoServicio.buscarGatoPorId(id);
                if (!gato.getUsuario().getId().equals(usuariologged.getId())) {
                    return "redirect:/";
                }
            } catch (ErrorServicio ex) {
                model.addAttribute("error", ex.getMessage());
                return "index.html";
            }
        }
        model.put("gato", gato);
        model.put("accion", accion);
        model.put("razas", Raza.values());
        model.put("sexos", SexoAnimal.values());

        return "formulariogato.html";
    }

    //Recibe los datos del formulario para crear o actualizar un Gato.
    @PostMapping("/actualizar-gato")
    public String actualizarGato(HttpSession session, ModelMap model,
            MultipartFile archivo,
            @RequestParam String idGato,
            @RequestParam String nombre,
            @RequestParam Raza raza,
            @RequestParam String edad,
            @RequestParam SexoAnimal sexo,
            @RequestParam String descripcion) {

        //Si no hay usuario logueado, lo saca.
        Usuario usuariologged = (Usuario) session.getAttribute("usuariosession");

        if (usuariologged == null) {
            return "redirect:/login";
        }

        try {
            //Hay un método para crear gato y otro para actualizarlo.
            if (idGato == null || idGato.isEmpty()) {
                gatoServicio.crearGato(usuariologged.getId(), nombre, raza, sexo, edad, archivo, descripcion);
            } else {
                gatoServicio.actualizarGato(idGato, usuariologged.getId(), nombre, raza, sexo, edad, archivo, descripcion);
            }

            return "redirect:/gato/mis-gatos";

        } catch (ErrorServicio e) {
            model.put("error", e.getMessage());

            //Si falla, vuelvo a mostrar en el formulario los datos que había cargdo el usuario.
            Gato gato = new Gato();
            gato.setNombre(nombre);
            gato.setRaza(raza);
            gato.setEdad(edad);
            gato.setSexo(sexo);
            gato.setDescripcion(descripcion);

            model.put("gato", gato);
            model.put("razas", Raza.values());
            model.put("sexos", SexoAnimal.values());

            return "formulariogato.html";
        }
    }

    //Recibe los datos del formulario para eliminar un Gato.
    @PostMapping("/eliminar-gato")
    public String eliminarGato(HttpSession session, @RequestParam String idGato) {

        //Si no hay usuario logueado, lo saca.
        Usuario usuariologged = (Usuario) session.getAttribute("usuariosession");
        if (usuariologged == null) {
            return "redirect:/login";
        }

        try {
            gatoServicio.deshabilitarGato(usuariologged.getId(), idGato);
        } catch (ErrorServicio ex) {
            Logger.getLogger(GatoControlador.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "redirect:/gato/mis-gatos";
    }

    
    //Controlador para declarar a un gato como adoptado.
    @GetMapping("/adoptado")
    public String adoptado(ModelMap modelo, HttpSession session, @RequestParam String idGato) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuariosession");

        if (usuarioLogueado == null) {
            return "redirect:/";
        }

        try {

            gatoServicio.gatoAdoptado(idGato, usuarioLogueado.getId());

            return "redirect:/gato/mis-gatos";

        } catch (ErrorServicio e) {
            modelo.addAttribute("error", e.getMessage());
            return "index.html";
        }

    }
    
    //Para restaurar un gato marcado como adoptado en la lista de mis gatos.
    @GetMapping("/restaurar")
    public String restaurar(ModelMap modelo, HttpSession session, @RequestParam String idGato) {
        
        Usuario usuariologged = (Usuario) session.getAttribute("usuariosession");
        if (usuariologged == null) {
            return "redirect:/login";
        }
        
        try {
            gatoServicio.gatoDesadoptado(idGato, usuariologged.getId());
            return "redirect:/gato/mis-gatos";
        } catch (ErrorServicio e) {
            modelo.addAttribute("error", e.getMessage());
            return "index.html";
        }
    }

}
