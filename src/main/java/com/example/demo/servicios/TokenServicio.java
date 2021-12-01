package com.example.demo.servicios;

import com.example.demo.entidades.TokenUsuario;
import com.example.demo.entidades.Usuario;

import com.example.demo.excepciones.ErrorServicio;
import com.example.demo.repositorios.TokenRepositorio;
import java.util.Date;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TokenServicio {

    @Autowired
    private TokenRepositorio tokenRepositorio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private NotificacionServicio notificacionServicio;

    /////Metodo para enviar Token de recuperación de contraseña al email ingresado.
    @Transactional
    public void enviarTokenPorMail(String email) throws ErrorServicio {

        Usuario usuario = usuarioServicio.buscarUsuarioPorEmail(email);

        if (usuario != null) {

            TokenUsuario token = new TokenUsuario();
            token.setUsuario(usuario);
            token.setCreado(new Date());

            tokenRepositorio.save(token);

            notificacionServicio.enviarHTMLRecuperacion(email, token.getId());
        } else {
            throw new ErrorServicio("El e-mail ingresado no pertenece a ningún usuario en nuestro sistema, intente nuevamente.");
        }
    }

/////Recuperación y cambio de contraseña del usuario    
    @Transactional
    public void restablecerPasswordUsuario(TokenUsuario token, String clave1, String clave2) throws ErrorServicio {

        if (clave1 == null || clave1.isEmpty() || clave1.length() <= 6) {
            throw new ErrorServicio("La clave no puede ser nula y debe tener más de 6 digitos.");
        }

        if (!clave1.equals(clave2)) {
            throw new ErrorServicio("Las claves no coinciden.");
        }

        Usuario usuario = token.getUsuario();

        String claveCifrada = new BCryptPasswordEncoder().encode(clave1);
        usuario.setClave(claveCifrada);
        usuario.setEditado(new Date());

        usuarioServicio.guardarUsuario(usuario);

        token.setBaja(new Date());
        tokenRepositorio.save(token);
    }

/////Buscar token por id    
    @Transactional(readOnly = true)
    public TokenUsuario buscarTokenPorId(String idToken) throws ErrorServicio {

        Optional<TokenUsuario> respuesta = tokenRepositorio.findById(idToken);

        if (respuesta.isPresent()) {

            TokenUsuario token = respuesta.get();

            if (token.getBaja() != null) {
                throw new ErrorServicio("Error al intentar cambiar la contraseña, el token ingresado no es valido.");
            }

            return token;

        } else {
            throw new ErrorServicio("Error al intentar cambiar la contraseña, el token ingresado no es valido.");
        }

    }

}
