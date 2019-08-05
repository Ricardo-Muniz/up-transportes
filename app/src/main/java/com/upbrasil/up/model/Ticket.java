package com.upbrasil.up.model;

import com.google.firebase.database.DatabaseReference;
import com.upbrasil.up.config.ConfiguracaoFirebase;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class Ticket implements Serializable {
    private String id;
    private Usuario usuario;
    private Date data;
    private String motivo;
    private String mensagem;
    DatabaseReference reference;

    public Ticket() {
        reference = ConfiguracaoFirebase.getFirebaseDatabase();
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

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Date pegarHorarioAtual() {
        Date currentTime = Calendar.getInstance().getTime();
        return currentTime;
    }

    public void salvar() {
        reference.child("tickets").child(id).setValue(this);
    }

    public String gerarId() {
        return reference.push().getKey();
    }
}
