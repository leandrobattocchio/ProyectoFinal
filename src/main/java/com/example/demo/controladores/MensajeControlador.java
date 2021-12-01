package com.example.demo.controladores;

import com.example.demo.entidades.Mensaje;
import com.example.demo.entidades.Usuario;
import com.example.demo.excepciones.ErrorServicio;
import com.example.demo.servicios.MensajeServicio;
import com.example.demo.servicios.UsuarioServicio;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
@Controller
@RequestMapping("/mensaje")
public class MensajeControlador {

    @Autowired
    private MensajeServicio mensajeServicio;

    @Autowired
    private UsuarioServicio usuarioServicio;

/////Controlador para ir a la vista de creación del mensaje.    
    @GetMapping("/crear")
    public String crearMensaje(HttpSession session, ModelMap modelo, @RequestParam(name = "idusuarioreceptor") String idUsuarioReceptor) {

        try {
            Usuario usuario = (Usuario) session.getAttribute("usuariosession");

           
           Usuario usuarioReceptor = usuarioServicio.buscarUsuarioPorId(idUsuarioReceptor);
 
            if (usuario != null && !usuario.getId().equals(idUsuarioReceptor)) {
                modelo.addAttribute("idusuarioreceptor", idUsuarioReceptor);
                modelo.addAttribute("nombre", usuarioReceptor.getNombre()+ " " + usuarioReceptor.getApellido());
              
                return "enviarmensaje.html";
            } else {
                return "redirect:/";
            }
        } catch (ErrorServicio e) {
            modelo.addAttribute("error", e.getMessage());
            return "index.html";
        }

    }

/////Controlador para enviar el mensaje al usuario en cuestión.    
    @PostMapping("/enviar")
    public String enviarMensaje(ModelMap modelo, HttpSession session, @RequestParam(name = "idusuarioreceptor") String idUsuarioReceptor, @RequestParam String asunto, @RequestParam(name = "cuerpomensaje") String cuerpoMensaje) {

        Usuario usuarioReceptor = null;
        
        try {

            Usuario usuario = (Usuario) session.getAttribute("usuariosession");

            usuarioReceptor = usuarioServicio.buscarUsuarioPorIdParaEmail(idUsuarioReceptor);
            
            if (usuarioReceptor == null) {
                return "bandejamensajes.html";
            }
            
            
            if (usuario != null && !usuario.getId().equals(usuarioReceptor.getId())) {

                mensajeServicio.enviarMensaje(usuario.getId(), idUsuarioReceptor, asunto, cuerpoMensaje);
                modelo.addAttribute("exito", "Su mensaje ha sido enviado exitosamente.");
                modelo.addAttribute("mensajesrecibidos", mensajeServicio.listarMensajesRecibidos(usuario.getId()));
                modelo.addAttribute("mensajesenviados", mensajeServicio.listarMensajesEnviados(usuario.getId()));
                return "bandejamensajes.html";

            } else {
                return "redirect:/";
            }

        } catch (ErrorServicio e) {
            modelo.addAttribute("asunto", asunto);
            modelo.addAttribute("cuerpomensaje", cuerpoMensaje);
            modelo.addAttribute("idusuarioreceptor", idUsuarioReceptor);
            modelo.addAttribute("error", e.getMessage());
            modelo.addAttribute("nombre", usuarioReceptor.getNombre()+ " " + usuarioReceptor.getApellido());
            return "enviarmensaje.html";
        }
    }

/////Controlador para la bandeja de mensajes del usuario.    
    @GetMapping("/bandeja-mensajes")
    public String bandejaDeMensajes(HttpSession session, ModelMap modelo, @RequestParam(name = "idusuario") String idUsuario) {

        try {
            Usuario usuarioLogueado = (Usuario) session.getAttribute("usuariosession");

            if (usuarioLogueado != null && usuarioLogueado.getId().equals(idUsuario)) {
                Usuario usuario = usuarioServicio.buscarUsuarioPorId(idUsuario);
                modelo.addAttribute("mensajesrecibidos", mensajeServicio.listarMensajesRecibidos(usuario.getId()));
                modelo.addAttribute("mensajesenviados", mensajeServicio.listarMensajesEnviados(usuario.getId()));
                return "bandejamensajes.html";
            } else {
                return "redirect:/";
            }

        } catch (Exception e) {

            modelo.addAttribute("error", e.getMessage());
            return "inicio.html";
        }

    }

    @GetMapping("/ver")
    public String verMensaje(ModelMap modelo, HttpSession session, @RequestParam String id, @RequestParam(required = false) String responder) {

        try {
            Usuario usuario = (Usuario) session.getAttribute("usuariosession");

            if (usuario != null) {

                Mensaje mensaje = mensajeServicio.buscarMensajePorId(id);

                if (mensaje.getUsuarioEmisor().getId().equals(usuario.getId()) || mensaje.getUsuarioReceptor().getId().equals(usuario.getId())) {

                    if (mensaje.getUsuarioReceptor().getId().equals(usuario.getId())) {
                        mensajeServicio.vistearMensaje(mensaje.getId());
                    }

                    if (responder != null && responder.equals("si")) {
                        modelo.addAttribute("idusuarioreceptor", mensaje.getUsuarioEmisor().getId());
                        modelo.addAttribute("emisor", mensaje.getUsuarioEmisor().getNombre() + " " + mensaje.getUsuarioEmisor().getApellido());
                    } else {
                        modelo.addAttribute("receptor", mensaje.getUsuarioReceptor().getNombre() + " " + mensaje.getUsuarioReceptor().getApellido());
                    }

                    modelo.addAttribute("asunto", mensaje.getAsunto());
                    modelo.addAttribute("cuerpomensaje", mensaje.getCuerpoMensaje());
                    return "vermensaje.html";

                } else {
                    return "redirect:/";
                }

            } else {
                return "redirect:/";
            }

        } catch (Exception e) {

            modelo.addAttribute("error", e.getMessage());
            return "index.html";
        }

    }
}
