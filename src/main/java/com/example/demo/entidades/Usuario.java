package com.example.demo.entidades;

import com.example.demo.enums.PrivilegiosUsuario;
import com.example.demo.enums.SexoHumano;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Column(unique = true)
    private String username;
    
    private String nombre;

    private String apellido;

    private String edad;

    private String numero;

    @Enumerated(EnumType.STRING)
    private SexoHumano sexo;

    @Column(unique = true)
    private String email;

    private String clave;

    @Enumerated(EnumType.STRING)
    private PrivilegiosUsuario privilegios;

    @ManyToOne
    private Localidad localidad;

    @OneToOne
    private Foto foto;

    @Temporal(TemporalType.TIMESTAMP)
    private Date creado;

    @Temporal(TemporalType.TIMESTAMP)
    private Date editado;

    @Temporal(TemporalType.TIMESTAMP)
    private Date baja;

    public Usuario() {
    }

   
    public String getUsername() {
        return username;
    }

    public Usuario(String id, String username, String nombre, String apellido, String edad, String numero, SexoHumano sexo, String email, String clave, PrivilegiosUsuario privilegios, Localidad localidad, Foto foto, Date creado, Date editado, Date baja) {
        this.id = id;
        this.username = username;
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.numero = numero;
        this.sexo = sexo;
        this.email = email;
        this.clave = clave;
        this.privilegios = privilegios;
        this.localidad = localidad;
        this.foto = foto;
        this.creado = creado;
        this.editado = editado;
        this.baja = baja;
    }

    public void setUsername(String username) {
        this.username = username;
    }

   
    public Date getBaja() {
        return baja;
    }

    public void setBaja(Date baja) {
        this.baja = baja;
    }


    public SexoHumano getSexo() {
        return sexo;
    }

    public void setSexo(SexoHumano sexo) {
        this.sexo = sexo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public PrivilegiosUsuario getPrivilegios() {
        return privilegios;
    }

    public void setPrivilegios(PrivilegiosUsuario privilegios) {
        this.privilegios = privilegios;
    }

    public Localidad getLocalidad() {
        return localidad;
    }

    public void setLocalidad(Localidad localidad) {
        this.localidad = localidad;
    }

    public Foto getFoto() {
        return foto;
    }

    public void setFoto(Foto foto) {
        this.foto = foto;
    }

    public Date getCreado() {
        return creado;
    }

    public void setCreado(Date creado) {
        this.creado = creado;
    }

    public Date getEditado() {
        return editado;
    }

    public void setEditado(Date editado) {
        this.editado = editado;
    }

 

}
