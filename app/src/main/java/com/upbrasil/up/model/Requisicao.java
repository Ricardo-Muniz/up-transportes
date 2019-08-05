package com.upbrasil.up.model;

import com.google.firebase.database.DatabaseReference;
import com.upbrasil.up.config.ConfiguracaoFirebase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Created by jamiltondamasceno
 */

public class Requisicao implements Serializable {

    private String id;
    private String status;
    private Usuario passageiro;
    private Usuario motorista;
    private Destino destino;
    @Nullable private String preco;
    @Nullable private Destino partida;
    @Nullable private String formaPagamento;
    @Nullable private Mensagem mensagem;
    @Nullable private Boolean achouMotorista;

    public static final String STATUS_AGUARDANDO = "aguardando";
    public static final String STATUS_A_CAMINHO = "acaminho";
    public static final String STATUS_VIAGEM = "viagem";
    public static final String STATUS_FINALIZADA = "finalizada";
    public static final String STATUS_ENCERRADA = "encerrada";
    public static final String STATUS_CANCELADA = "cancelada";

    public Requisicao() {
    }

    public void salvar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference requisicoes = firebaseRef.child("requisicoes");

        String idRequisicao = requisicoes.push().getKey();
        setId( idRequisicao );

        requisicoes.child( getId() ).setValue(this);

    }

    public void atualizar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference requisicoes = firebaseRef.child("requisicoes");

        DatabaseReference requisicao = requisicoes.child(getId());

        Map objeto = new HashMap();
        objeto.put("motorista", getMotorista() );
        objeto.put("status", getStatus());

        requisicao.updateChildren( objeto );

    }

    public void atualizarStatus(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference requisicoes = firebaseRef.child("requisicoes");

        DatabaseReference requisicao = requisicoes.child(getId());

        Map objeto = new HashMap();
        objeto.put("status", getStatus());

        requisicao.updateChildren( objeto );

    }

    public void atualizarLocalizacaoMotorista(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference requisicoes = firebaseRef
                .child("requisicoes");

        DatabaseReference requisicao = requisicoes
                .child(getId())
                .child("motorista");

        Map objeto = new HashMap();
        objeto.put("latitude", getMotorista().getLocalizacaoAtual().latitude );
        objeto.put("longitude", getMotorista().getLocalizacaoAtual().longitude);

        requisicao.updateChildren( objeto );

    }

    @Nullable
    public Mensagem getMensagem() {
        return mensagem;
    }

    public void setMensagem(@Nullable Mensagem mensagem) {
        this.mensagem = mensagem;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Usuario getPassageiro() {
        return passageiro;
    }

    public void setPassageiro(Usuario passageiro) {
        this.passageiro = passageiro;
    }

    public Usuario getMotorista() {
        return motorista;
    }

    public void setMotorista(Usuario motorista) {
        this.motorista = motorista;
    }

    public Destino getDestino() {
        return destino;
    }

    public void setDestino(Destino destino) {
        this.destino = destino;
    }

    @Nullable
    public String getPreco() {
        return preco;
    }

    public void setPreco(@Nullable String preco) {
        this.preco = preco;
    }

    @Nullable
    public Destino getPartida() {
        return partida;
    }

    public void setPartida(@Nullable Destino partida) {
        this.partida = partida;
    }

    @Nullable
    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(@Nullable String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    @Nullable
    public Boolean getAchouMotorista() {
        return achouMotorista;
    }

    public void setAchouMotorista(@Nullable Boolean achouMotorista) {
        this.achouMotorista = achouMotorista;
    }
}
