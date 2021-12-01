package com.example.demo.controladores;

import com.example.demo.entidades.Gato;
import com.example.demo.entidades.Localidad;
import com.example.demo.entidades.Usuario;
import com.example.demo.enums.Raza;
import com.example.demo.enums.SexoAnimal;
import com.example.demo.excepciones.ErrorServicio;
import com.example.demo.servicios.GatoServicio;
import com.example.demo.servicios.LocalidadServicio;
import com.example.demo.servicios.UsuarioServicio;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
@RequestMapping("/admin")
public class AdminControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private GatoServicio gatoServicio;

    @Autowired
    private LocalidadServicio localidadServicio;

    //Mostrar todos los usuarios registrados
    @GetMapping("/lista-usuarios")
    public String listarUsuarios(HttpSession session, ModelMap modelo) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuariosession");

        if (usuarioLogueado == null || !usuarioLogueado.getPrivilegios().toString().equals("ADMIN")) {
            return "redirect:/";
        }

        List<Usuario> usuarios = usuarioServicio.listarUsuariosRegistrados(usuarioLogueado.getId());
        modelo.addAttribute("usuarios", usuarios);
        return "listausuarios.html";

    }

    //Mostrar todos los gatitos registrados
    @GetMapping("/lista-gatitos")
    public String listarGatitos(HttpSession session, ModelMap modelo) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuariosession");

        if (usuarioLogueado == null || !usuarioLogueado.getPrivilegios().toString().equals("ADMIN")) {
            return "redirect:/";
        }

        List<Gato> gatos = gatoServicio.listarGatos();
        modelo.addAttribute("gatos", gatos);
        return "listagatitos.html";

    }

    //Deshabilitar usuario
    @GetMapping("/deshabilitar-usuario")
    public String deshabilitar(ModelMap modelo, HttpSession session, @RequestParam String idUsuario) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuariosession");

        if (usuarioLogueado == null || !usuarioLogueado.getPrivilegios().toString().equals("ADMIN")) {
            return "redirect:/";
        }

        try {
            usuarioServicio.deshabilitar(idUsuario);
            return "redirect:/admin/lista-usuarios";

        } catch (ErrorServicio e) {
            modelo.addAttribute("error", e.getMessage());
            return "index.html";
        }

    }

    //Dar admin a otro usuario
    @GetMapping("/dar-admin")
    public String darAdmin(ModelMap modelo, HttpSession session, @RequestParam String idUsuario) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuariosession");

        if (usuarioLogueado == null || !usuarioLogueado.getPrivilegios().toString().equals("ADMIN")) {
            return "redirect:/";
        }

        try {
            usuarioServicio.darAdmin(idUsuario);
            return "redirect:/admin/lista-usuarios";

        } catch (ErrorServicio e) {
            modelo.addAttribute("error", e.getMessage());
            return "index.html";
        }

    }
    
   
    

    //Ver el perfil del usuario para editarlo
    @GetMapping("/editar-perfil-usuario")
    public String editarPerfilUsuario(HttpSession session, ModelMap modelo, @RequestParam String idUsuario) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuariosession");

        if (usuarioLogueado == null || !usuarioLogueado.getPrivilegios().toString().equals("ADMIN")) {
            return "redirect:/";
        }

        try {

            Usuario usuario = usuarioServicio.buscarUsuarioPorId(idUsuario);

            if (usuario.getPrivilegios().toString().equals("ADMIN")) {
                throw new ErrorServicio("No puede modificar el perfil de otro administrador");
            }

            if (usuario.getId().equals(usuarioLogueado.getId())) {
                return "redirect:/";
            }

            List<Localidad> localidades = localidadServicio.listarLocalidades();

            modelo.addAttribute("localidades", localidades);
            modelo.addAttribute("usuario", usuario);
         

            return "editarregistroadmin.html";

        } catch (ErrorServicio e) {

            modelo.addAttribute("error", e.getMessage());
            return "index.html";
        }

    }

    //Confirmar la modificicacion del perfil del usuario
    @PostMapping("/actualizar-usuario")
    public String actualizarUsuario(HttpSession session, ModelMap modelo, @ModelAttribute Usuario usuario, @RequestParam String borrar) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuariosession");

        if (usuarioLogueado == null || !usuarioLogueado.getPrivilegios().toString().equals("ADMIN")){
            return "redirect:/";
        }

        try {
              
            usuarioServicio.modificarAdmin(usuario.getId(), usuario.getNombre(), usuario.getEdad(), usuario.getNumero(), usuario.getApellido(), usuario.getLocalidad().getId(), borrar);
            
            return "redirect:/admin/lista-usuarios";

        } catch (ErrorServicio e) {
            modelo.addAttribute("usuario", usuario);
            modelo.addAttribute("error", e.getMessage());
            return "editarregistroadmin.html";
        }

    }

    //Habilitar usuario
    @GetMapping("/habilitar-usuario")
    public String habilitar(ModelMap modelo, HttpSession session, @RequestParam String idUsuario) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuariosession");

        if (usuarioLogueado == null || !usuarioLogueado.getPrivilegios().toString().equals("ADMIN")) {
            return "redirect:/";
        }

        try {
            usuarioServicio.habilitar(idUsuario);
            return "redirect:/admin/lista-usuarios";

        } catch (ErrorServicio e) {
            modelo.addAttribute("error", e.getMessage());
            return "index.html";
        }

    }

    //Declarar a un gatito como que ya fue adoptado
    @GetMapping("/gatito-adoptado")
    public String gatoAdoptado(ModelMap modelo, HttpSession session, @RequestParam String idGato) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuariosession");

        if (usuarioLogueado == null || !usuarioLogueado.getPrivilegios().toString().equals("ADMIN")) {
            return "redirect:/";
        }

        try {
            Gato gato = gatoServicio.buscarGatoPorId(idGato);
            gatoServicio.gatoAdoptado(idGato, gato.getUsuario().getId());
            return "redirect:/admin/lista-gatitos";

        } catch (ErrorServicio e) {
            modelo.addAttribute("error", e.getMessage());
            return "index.html";
        }

    }

    //Deshabilitar gato
    @GetMapping("/deshabilitar-gato")
    public String deshabilitarGato(ModelMap modelo, HttpSession session, @RequestParam String idGato) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuariosession");

        if (usuarioLogueado == null || !usuarioLogueado.getPrivilegios().toString().equals("ADMIN")) {
            return "redirect:/";
        }

        try {

            Gato gato = gatoServicio.buscarGatoPorId(idGato);
            gatoServicio.deshabilitarGato(gato.getUsuario().getId(), idGato);
            return "redirect:/admin/lista-gatitos";

        } catch (ErrorServicio e) {
            modelo.addAttribute("error", e.getMessage());
            return "index.html";
        }

    }

    //Habilitar gato
    @GetMapping("/habilitar-gato")
    public String habilitarGato(ModelMap modelo, HttpSession session, @RequestParam String idGato) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuariosession");

        if (usuarioLogueado == null || !usuarioLogueado.getPrivilegios().toString().equals("ADMIN")) {
            return "redirect:/";
        }

        try {

            Gato gato = gatoServicio.buscarGatoPorId(idGato);
            gatoServicio.habilitarGato(gato.getUsuario().getId(), idGato);
            return "redirect:/admin/lista-gatitos";

        } catch (ErrorServicio e) {
            modelo.addAttribute("error", e.getMessage());
            return "index.html";
        }

    }

    //Declarar a un gatito adoptado como no adoptado
    @GetMapping("/gatito-desadoptado")
    public String gatoDesadoptado(ModelMap modelo, HttpSession session, @RequestParam String idGato) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuariosession");

        if (usuarioLogueado == null || !usuarioLogueado.getPrivilegios().toString().equals("ADMIN")) {
            return "redirect:/";
        }

        try {
            Gato gato = gatoServicio.buscarGatoPorId(idGato);
            gatoServicio.gatoDesadoptado(idGato, gato.getUsuario().getId());
            return "redirect:/admin/lista-gatitos";

        } catch (ErrorServicio e) {
            modelo.addAttribute("error", e.getMessage());
            return "index.html";
        }

    }

    //Ver el perfil del gato para editarlo
    @GetMapping("/editar-perfil-gato")
    public String editarPerfilGato(HttpSession session, ModelMap modelo, @RequestParam String idGato) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuariosession");

        if (usuarioLogueado == null || !usuarioLogueado.getPrivilegios().toString().equals("ADMIN")) {
            return "redirect:/";
        }

        try {

            Gato gato = gatoServicio.buscarGatoPorId(idGato);

            if (gato.getBaja() != null || gato.getAdoptado() != null) {
                throw new ErrorServicio("No puede modificar al michi porque esta dado de baja o fue adoptado.");
            }

            modelo.addAttribute("razas", Raza.values());
            modelo.addAttribute("sexos", SexoAnimal.values());
            modelo.addAttribute("gato", gato);

            return "editargatoadmin.html";

        } catch (ErrorServicio e) {

            modelo.addAttribute("error", e.getMessage());
            return "index.html";
        }

    }

    //Modificar el perfil del gato 
    @PostMapping("/modificar-perfil-gato")
    public String actualizarGato(HttpSession session, ModelMap modelo, @ModelAttribute Gato gatoNuevo, @RequestParam String borrar, @RequestParam String descripcion) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuariosession");

        if (usuarioLogueado == null || !usuarioLogueado.getPrivilegios().toString().equals("ADMIN") || gatoNuevo.getId() == null) {
            return "redirect:/";
        }

        try {
            Gato gatoViejo = gatoServicio.buscarGatoPorId(gatoNuevo.getId());

            if (borrar.equals("no")) {
                gatoNuevo.setFotoPrincipal(gatoViejo.getFotoPrincipal());
            }
            
            
            gatoNuevo.setUsuario(gatoViejo.getUsuario());
            gatoNuevo.setCreado(gatoViejo.getCreado());
            
            gatoServicio.actualizarGatoAdmin(gatoNuevo, descripcion);
            return "redirect:/admin/lista-gatitos";
            
        } catch (ErrorServicio e) {
            modelo.addAttribute("error", e.getMessage());
            modelo.addAttribute("razas", Raza.values());
            modelo.addAttribute("sexos", SexoAnimal.values());
            modelo.addAttribute("gato", gatoNuevo);
            return "editargatoadmin.html";
        }

    }
}
