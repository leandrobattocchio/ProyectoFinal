package com.example.demo.servicios;

import com.example.demo.entidades.Mensaje;
import com.example.demo.entidades.Usuario;
import com.example.demo.excepciones.ErrorServicio;
import com.example.demo.repositorios.MensajeRepositorio;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MensajeServicio {

    @Autowired
    private MensajeRepositorio mensajeRepositorio;

    @Autowired
    private UsuarioServicio usuarioServicio;

/////Metodo para enviar mensaje    
    @Transactional
    public void enviarMensaje(String idUsuarioEmisor, String idUsuarioReceptor, String asunto, String cuerpoMensaje) throws ErrorServicio {

        if (asunto == null || asunto.trim().isEmpty()) {
            throw new ErrorServicio("No ha selecionado un asunto para el mensaje.");
        }

        if (cuerpoMensaje == null || cuerpoMensaje.trim().isEmpty()) {
            throw new ErrorServicio("No ha selecionado un cuerpo para el mensaje.");
        }

        Usuario usuarioEmisor = usuarioServicio.buscarUsuarioPorId(idUsuarioEmisor);

        if (usuarioEmisor != null) {

            Usuario usuarioReceptor = usuarioServicio.buscarUsuarioPorId(idUsuarioReceptor);

            if (usuarioReceptor != null) {

                Mensaje mensaje = new Mensaje();

                mensaje.setAsunto(asunto);
                mensaje.setCuerpoMensaje(cuerpoMensaje);
                mensaje.setUsuarioEmisor(usuarioEmisor);
                mensaje.setUsuarioReceptor(usuarioReceptor);
                mensaje.setCreado(new Date());

                mensajeRepositorio.save(mensaje);

            } else {
                throw new ErrorServicio("Error al intentar encontrar al usuario receptor del mensaje.");
            }

        } else {

            throw new ErrorServicio("Error al intentar encontrar al usuario emisor del mensaje.");
        }

    }

/////Metodo para traer mensajes recibidos.
    @Transactional(readOnly = true)
    public List<Mensaje> listarMensajesRecibidos(String idUsuario) {

        return mensajeRepositorio.listarMensajesRecibidos(idUsuario);
    }

/////Metodo para traer mensajes enviados.  
    @Transactional(readOnly = true)
    public List<Mensaje> listarMensajesEnviados(String idUsuario) {

        return mensajeRepositorio.listarMensajesEnviados(idUsuario);
    }

/////Marcar como leido un mensaje    
    @Transactional
    public void vistearMensaje(String idMensaje) throws ErrorServicio {

        Optional<Mensaje> respuesta = mensajeRepositorio.findById(idMensaje);

        if (respuesta.isPresent()) {

            Mensaje mensaje = respuesta.get();
            mensaje.setVisto(true);
            mensajeRepositorio.save(mensaje);

        } else {

            throw new ErrorServicio("Error, el mensaje no se ha podido encontrar");
        }
    }

/////Traer mensaje por id de mensaje    
    @Transactional(readOnly = true)
    public Mensaje buscarMensajePorId(String idMensaje) throws ErrorServicio {

        Optional<Mensaje> respuesta = mensajeRepositorio.findById(idMensaje);

        if (respuesta.isPresent()) {

            Mensaje mensaje = respuesta.get();
            return mensaje;

        } else {
            throw new ErrorServicio("Error al intentar encontrar el mensaje.");
        }

    }

}
