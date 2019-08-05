package com.upbrasil.up.model;

import java.io.Serializable;

import javax.annotation.Nullable;

public class Mensagem implements Serializable {
    private String id;
    private String mensagem;
    private String nomePassageiro;
    @Nullable private String nomeMotorista;
    private String idPassageiro;
    @Nullable private String idMotorista;

    public Mensagem(String id, String mensagem, String nomePassageiro, @Nullable String nomeMotorista, String idPassageiro, @Nullable String idMotorista) {
        this.id = id;
        this.mensagem = mensagem;
        this.nomePassageiro = nomePassageiro;
        this.nomeMotorista = nomeMotorista;
        this.idPassageiro = idPassageiro;
        this.idMotorista = idMotorista;
    }

    public Mensagem() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getNomePassageiro() {
        return nomePassageiro;
    }

    public void setNomePassageiro(String nomePassageiro) {
        this.nomePassageiro = nomePassageiro;
    }

    public String getNomeMotorista() {
        return nomeMotorista;
    }

    public void setNomeMotorista(String nomeMotorista) {
        this.nomeMotorista = nomeMotorista;
    }

    public String getIdPassageiro() {
        return idPassageiro;
    }

    public void setIdPassageiro(String idPassageiro) {
        this.idPassageiro = idPassageiro;
    }

    public String getIdMotorista() {
        return idMotorista;
    }

    public void setIdMotorista(String idMotorista) {
        this.idMotorista = idMotorista;
    }
}
