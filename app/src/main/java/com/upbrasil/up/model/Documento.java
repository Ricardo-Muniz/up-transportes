package com.upbrasil.up.model;

import java.io.Serializable;

import javax.annotation.Nullable;

public class Documento implements Serializable {
    private String tipo;
    private String numero;

    @Nullable private String urlFrente;
    @Nullable private String urlVerso;

    public static final String CPF = "CPF";
    public static final String RG = "RG";
    public static final String CARTEIRA_MOTORISTA = "CARTEIRA_MOTORISTA";

    public Documento() {

    }

    public Documento(String tipo, String numero, @Nullable String urlFrente, @Nullable String urlVerso) {
        this.tipo = tipo;
        this.numero = numero;
        this.urlFrente = urlFrente;
        this.urlVerso = urlVerso;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    @Nullable
    public String getUrlFrente() {
        return urlFrente;
    }

    public void setUrlFrente(@Nullable String urlFrente) {
        this.urlFrente = urlFrente;
    }

    @Nullable
    public String getUrlVerso() {
        return urlVerso;
    }

    public void setUrlVerso(@Nullable String urlVerso) {
        this.urlVerso = urlVerso;
    }
}
