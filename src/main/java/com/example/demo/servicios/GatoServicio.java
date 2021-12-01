package com.example.demo.servicios;

import com.example.demo.entidades.Foto;
import com.example.demo.entidades.Gato;
import com.example.demo.entidades.Usuario;
import com.example.demo.enums.Raza;
import com.example.demo.enums.SexoAnimal;
import com.example.demo.excepciones.ErrorServicio;
import com.example.demo.repositorios.GatoRepositorio;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class GatoServicio {

    @Autowired
    private GatoRepositorio gatoRepositorio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private FotoServicio fotoServicio;

/////Metodo para crear un gato.   
    @Transactional
    public void crearGato(String idUsuario, String nombre, Raza raza, SexoAnimal sexo, String edad, MultipartFile archivo, String descripcion) throws ErrorServicio {

        validar(nombre, raza, sexo, edad, descripcion);

        Usuario usuario = usuarioServicio.buscarUsuarioPorId(idUsuario);

        Gato gato = new Gato();
        gato.setNombre(nombre);
        gato.setRaza(raza);
        gato.setSexo(sexo);
        gato.setDescripcion(descripcion);
        gato.setUsuario(usuario);
        gato.setEdad(edad);

        Foto foto = fotoServicio.guardar(archivo);
        gato.setFotoPrincipal(foto);

        gato.setCreado(new Date());

        gatoRepositorio.save(gato);

    }
/////Metodo para actualizar un gato.

    @Transactional
    public void actualizarGato(String idGato, String idUsuario, String nombre, Raza raza, SexoAnimal sexo, String edad, MultipartFile archivo, String descripcion) throws ErrorServicio {

        validar(nombre, raza, sexo, edad, descripcion);

        Gato gato = buscarGatoPorId(idGato);

        if (gato != null) {

            Usuario usuario = usuarioServicio.buscarUsuarioPorId(idUsuario);

            if (usuario != null && gato.getUsuario().getId().equals(usuario.getId())) {

                gato.setNombre(nombre);
                gato.setRaza(raza);
                gato.setSexo(sexo);
                gato.setEdad(edad);
                gato.setDescripcion(descripcion);

                String idFoto = null;

                if (gato.getFotoPrincipal() != null) {
                    idFoto = gato.getFotoPrincipal().getId();
                }

                //Si se eligió nueva foto, la actualiza, sino queda la que había.
                if (archivo != null && !archivo.isEmpty()) {
                    Foto foto = fotoServicio.actualizar(idFoto, archivo);
                    gato.setFotoPrincipal(foto);
                }
            
                gato.setEditado(new Date());

                gatoRepositorio.save(gato);

            } else {
                throw new ErrorServicio("Error al intentar actualziar el gato en cuestión(No es dueño de este gato, o no se encontró al usuario de este).");
            }

        } else {
            throw new ErrorServicio("Error al intentar actualziar el gato en cuestión(No encontrado).");
        }
    }

/////Metodo para deshabilitar un gato.   
    @Transactional
    public void deshabilitarGato(String idUsuario, String idGato) throws ErrorServicio {

        Optional<Gato> respuesta = gatoRepositorio.findById(idGato);

        if (respuesta != null) {

            Gato gato = respuesta.get();

            if (gato.getUsuario().getId().equals(idUsuario)) {
                gato.setBaja(new Date());
                gatoRepositorio.save(gato);
            } else {
                throw new ErrorServicio("Usted no tiene suficientes permisos para efectuar esta acción.");
            }

        } else {
            throw new ErrorServicio("Error al buscar al gato en cuestión(No encontrado).");
        }

    }

/////Metodo para habilitar un gato.  
    @Transactional
    public void habilitarGato(String idUsuario, String idGato) throws ErrorServicio {
        Optional<Gato> respuesta = gatoRepositorio.findById(idGato);

        if (respuesta != null) {

            Gato gato = respuesta.get();

            if (gato.getUsuario().getId().equals(idUsuario)) {
                gato.setBaja(null);
                gatoRepositorio.save(gato);
            } else {
                throw new ErrorServicio("Usted no tiene suficientes permisos para efectuar esta acción.");
            }

        } else {
            throw new ErrorServicio("Error al buscar al gato en cuestión(No encontrado).");
        }
    }

/////Metodo para listar todos los gatos activos.  
    @Transactional(readOnly = true)
    public List<Gato> listarGatosActivos() {
        return gatoRepositorio.listarGatosActivos();
    }

/////Metodo para buscar gato por id.
    @Transactional(readOnly = true)
    public Gato buscarGatoPorId(String id) throws ErrorServicio {

        Optional<Gato> respuesta = gatoRepositorio.findById(id);

        if (respuesta != null) {
            Gato gato = respuesta.get();
            return gato;
        } else {
            throw new ErrorServicio("Error al tratar de encontrar al gato correspondiente(No encontrado).");
        }

    }

/////Metodo para validar datos del gato.  
    public void validar(String nombre, Raza raza, SexoAnimal sexo, String edad, String descripcion) throws ErrorServicio {

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ErrorServicio("El campo del nombre no puede estar vacio.");
        }

        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new ErrorServicio("El campo de la descripcion no puede estar vacio.");
        }

        if (raza == null) {
            throw new ErrorServicio("El campo de la raza no puede estar vacio.");
        }

        if (sexo == null) {
            throw new ErrorServicio("El campo del sexo no puede estar vacio.");
        }

        if (edad == null || edad.trim().isEmpty()) {
            throw new ErrorServicio("El campo de la edad no puede estar vacio.");
        }

    }

    /////Metodo para buscar gatos activos por usuario.     
    @Transactional(readOnly = true)
    public List<Gato> listarGatosActivosPorUsuario(String idUsuario) {

        return gatoRepositorio.listarGatosActivosPorUsuario(idUsuario);
    }

    /////Metodo para buscar gatos activos por usuario.     
    @Transactional(readOnly = true)
    public List<Gato> listarGatosAdoptadosDelUsuario(String idUsuario) {

        return gatoRepositorio.listarGatosAdoptadosDelUsuario(idUsuario);
    }

    /////Metodo para listar gatos activos por query.
    @Transactional(readOnly = true)
    public List<Gato> listarGatosActivosPorQuery(String q) {
        return gatoRepositorio.listarGatosActivosPorQuery(q);
    }

/////Metodo para informar que un gato fue adoptado
    @Transactional
    public void gatoAdoptado(String idGato, String idUsuario) throws ErrorServicio {
        Gato gato = buscarGatoPorId(idGato);

        if (gato != null) {

            Usuario usuario = usuarioServicio.buscarUsuarioPorId(idUsuario);

            if (gato.getUsuario().getId().equals(usuario.getId())) {

                gato.setAdoptado(new Date());
                gatoRepositorio.save(gato);
            }
        }
    }
////Metodo para informar que un gato adoptado va a pasar a desadoptado

    @Transactional
    public void gatoDesadoptado(String idGato, String idUsuario) throws ErrorServicio {
        Gato gato = buscarGatoPorId(idGato);

        if (gato != null) {

            Usuario usuario = usuarioServicio.buscarUsuarioPorId(idUsuario);

            if (gato.getUsuario().getId().equals(usuario.getId())) {

                gato.setAdoptado(null);
                gatoRepositorio.save(gato);
            }
        }
    }

/////Metodo para listar todos los gatos activos/inactivos    
    @Transactional(readOnly = true)
    public List<Gato> listarGatos() {
        return gatoRepositorio.listarGatos();
    }

/////Metodo para actualizar gato desde la vista del admin    
    @Transactional
    public void actualizarGatoAdmin(Gato gato, String descripcion) throws ErrorServicio {

        if (gato.getId() == null) {
            throw new ErrorServicio("Error al intentar encontrar el gato a modificar.");
        }
        
        validar(gato.getNombre(), gato.getRaza(), gato.getSexo(), gato.getEdad(), descripcion);
        gato.setDescripcion(descripcion);
        gato.setEditado(new Date());
        gatoRepositorio.save(gato);

    }
}
