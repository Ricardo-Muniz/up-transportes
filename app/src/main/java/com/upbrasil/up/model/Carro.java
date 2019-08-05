package com.upbrasil.up.model;

import java.io.Serializable;

import javax.annotation.Nullable;

public class Carro implements Serializable {
    private String marca;
    private String modelo;
    private String placa;
    private int ano;
    private String cor;

    @Nullable
    private String urlFrente;
    @Nullable
    private String urlDianteira;
    @Nullable
    private String urlDentro;

    public Carro() {

    }

    public Carro(String marca, String modelo, String placa, int ano, String cor, @Nullable String urlFrente, @Nullable String urlDianteira, @Nullable String urlDentro) {
        this.marca = marca;
        this.modelo = modelo;
        this.placa = placa;
        this.ano = ano;
        this.cor = cor;
        this.urlFrente = urlFrente;
        this.urlDianteira = urlDianteira;
        this.urlDentro = urlDentro;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    @Nullable
    public String getUrlFrente() {
        return urlFrente;
    }

    public void setUrlFrente(@Nullable String urlFrente) {
        this.urlFrente = urlFrente;
    }

    @Nullable
    public String getUrlDianteira() {
        return urlDianteira;
    }

    public void setUrlDianteira(@Nullable String urlDianteira) {
        this.urlDianteira = urlDianteira;
    }

    @Nullable
    public String getUrlDentro() {
        return urlDentro;
    }

    public void setUrlDentro(@Nullable String urlDentro) {
        this.urlDentro = urlDentro;
    }
}
