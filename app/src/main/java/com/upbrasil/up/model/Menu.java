package com.upbrasil.up.model;

import android.media.Image;

public class Menu {
    String titulo;
    Integer icone;

    public Menu(String titulo, Integer icone) {
        this.titulo = titulo;
        this.icone = icone;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getIcone() {
        return icone;
    }

    public void setIcone(Integer icone) {
        this.icone = icone;
    }
}
