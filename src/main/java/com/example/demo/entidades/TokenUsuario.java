package com.example.demo.entidades;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class TokenUsuario {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    
    @ManyToOne
    private Usuario usuario;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date creado;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date baja;

    public TokenUsuario() {
    }

    public TokenUsuario(String id, Usuario usuario, Date creado, Date baja) {
        this.id = id;
        this.usuario = usuario;
        this.creado = creado;
        this.baja = baja;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Date getCreado() {
        return creado;
    }

    public void setCreado(Date creado) {
        this.creado = creado;
    }

    public Date getBaja() {
        return baja;
    }

    public void setBaja(Date baja) {
        this.baja = baja;
    }

}
