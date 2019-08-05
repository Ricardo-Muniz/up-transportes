package com.upbrasil.up.model;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.upbrasil.up.activity.CadastroActivity;
import com.upbrasil.up.activity.ConfirmarPerfilActivity;
import com.upbrasil.up.activity.SidebarMenuActivity;
import com.upbrasil.up.config.ConfiguracaoFirebase;
import com.upbrasil.up.helper.Alerts;
import com.upbrasil.up.helper.Loading;
import com.upbrasil.up.helper.UsuarioFirebase;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Nullable;

/**
 * Created by jamiltondamasceno
 */

public class Usuario implements Serializable {
    private String id;
    private String nomeCompleto;
    private String email;
    @Nullable private String senha;
    private String tipo;
    private int nivelUsuario;
    private Date criadoEm;
    private Date atualizadoEm;
    private Boolean aprovado;
    private Boolean multiConta;

    @Nullable private Localizacao localizacaoAtual;
    @Nullable private Carro carro;
    @Nullable private Documento documento;
    @Nullable private String telefone;
    @Nullable private Boolean ativarCorridas;
    @Nullable private String fotoPerfil;
    @Nullable private String formaPagamento;
    @Nullable private FormasPagamento formasPagamento;

    @Nullable private Bitmap bmPerfil;
    @Nullable private Bitmap bmCarroFrente;
    @Nullable private Bitmap bmCarroDianteira;
    @Nullable private Bitmap bmCarroDentro;
    @Nullable private Bitmap bmDocFrente;
    @Nullable private Bitmap bmDocVerso;

    public static final String DINHEIRO = "DINHEIRO";
    public static final String PAYPAL = "PAYPAL";
    public static final String CREDITO = "CREDITO";
    public static final String DEBITO = "DEBITO";

    private static Usuario usuario;
    private Intent intent;
    private int i;

    public Usuario() {
        usuario = this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(@Nullable String senha) {
        this.senha = senha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Localizacao getLocalizacaoAtual() {
        return localizacaoAtual;
    }

    public void setLocalizacaoAtual(Localizacao localizacaoAtual) {
        this.localizacaoAtual = localizacaoAtual;
    }

    public int getNivelUsuario() {
        return nivelUsuario;
    }

    public void setNivelUsuario(int nivelUsuario) {
        this.nivelUsuario = nivelUsuario;
    }

    public Date getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Date criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Date getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(Date atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public Boolean getAprovado() {
        return aprovado;
    }

    public void setAprovado(Boolean aprovado) {
        this.aprovado = aprovado;
    }

    public Boolean getMultiConta() {
        return multiConta;
    }

    public void setMultiConta(Boolean multiConta) {
        this.multiConta = multiConta;
    }

    @Nullable
    public Carro getCarro() {
        return carro;
    }

    public void setCarro(@Nullable Carro carro) {
        this.carro = carro;
    }

    @Nullable
    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(@Nullable String telefone) {
        this.telefone = telefone;
    }

    @Nullable
    public Boolean getAtivarCorridas() {
        return ativarCorridas;
    }

    public void setAtivarCorridas(@Nullable Boolean ativarCorridas) {
        this.ativarCorridas = ativarCorridas;
    }

    @Nullable
    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(@Nullable String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    @Nullable
    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(@Nullable String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    @Nullable
    public FormasPagamento getFormasPagamento() {
        return formasPagamento;
    }

    public void setFormasPagamento(@Nullable FormasPagamento formasPagamento) {
        this.formasPagamento = formasPagamento;
    }

    @Nullable
    public Documento getDocumento() {
        return documento;
    }

    public void setDocumento(@Nullable Documento documento) {
        this.documento = documento;
    }


    @Nullable
    public Bitmap getBmPerfil() {
        return bmPerfil;
    }

    public void setBmPerfil(@Nullable Bitmap bmPerfil) {
        this.bmPerfil = bmPerfil;
    }

    @Nullable
    public Bitmap getBmCarroFrente() {
        return bmCarroFrente;
    }

    public void setBmCarroFrente(@Nullable Bitmap bmCarroFrente) {
        this.bmCarroFrente = bmCarroFrente;
    }

    @Nullable
    public Bitmap getBmCarroDianteira() {
        return bmCarroDianteira;
    }

    public void setBmCarroDianteira(@Nullable Bitmap bmCarroDianteira) {
        this.bmCarroDianteira = bmCarroDianteira;
    }

    @Nullable
    public Bitmap getBmCarroDentro() {
        return bmCarroDentro;
    }

    public void setBmCarroDentro(@Nullable Bitmap bmCarroDentro) {
        this.bmCarroDentro = bmCarroDentro;
    }

    @Nullable
    public Bitmap getBmDocFrente() {
        return bmDocFrente;
    }

    public void setBmDocFrente(@Nullable Bitmap bmDocFrente) {
        this.bmDocFrente = bmDocFrente;
    }

    @Nullable
    public Bitmap getBmDocVerso() {
        return bmDocVerso;
    }

    public void setBmDocVerso(@Nullable Bitmap bmDocVerso) {
        this.bmDocVerso = bmDocVerso;
    }

    public void recuperarSenha(String email, Activity activity) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    activity.finish();
                                }
                            }, 3000);
                        }
                    }
                });
    }

    public void salvarPagamento() {
        if(this.getTipo().equals("P")) {
            DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase();
            reference.child("usuarios").child(this.getId()).child("formaPagamento").setValue(this.getFormaPagamento());
        } else {
            DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase();

            String uid = reference.push().getKey();

            FormasPagamento formasPagamento = new FormasPagamento();
            formasPagamento.setId(uid);
            formasPagamento.setNome(this.getFormaPagamento());

            reference.child("usuarios").child(this.getId()).child("formasPagamento").child(uid).setValue(formasPagamento);
        }
    }


    public void salvarNovoTelefone(String telefone) {
        DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference referenceTelefones = ConfiguracaoFirebase.getFirebaseDatabase();

        reference.child("telefones").orderByChild("telefone").equalTo(telefone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    referenceTelefones.child("telefones").child(referenceTelefones.push().getKey()).child("telefone").setValue(telefone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void verificarTelefoneExistente(Context context, String telefone) {
        intent = new Intent();

        DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase();
        reference.child("usuarios").orderByChild("telefone").equalTo(telefone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot each : dataSnapshot.getChildren()) {
                        Usuario usuario = each.getValue(Usuario.class);

                        intent = new Intent(context, ConfirmarPerfilActivity.class);
                        intent.putExtra("email", usuario.getEmail());
                    }
                } else {
                    intent = new Intent(context, CadastroActivity.class);
                }

                intent.putExtra("telefone", telefone);
                context.startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void verificaEmailExistente(Activity activity, Loading loading, Bitmap imagemPerfil) {
        DatabaseReference referenciaUsuarios = FirebaseDatabase.getInstance().getReference("usuarios");
        referenciaUsuarios.orderByChild("email").equalTo(this.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    loading.mostrarLoading();
                    Alerts alerts = new Alerts(activity);
                    alerts.mostrarPopupErro("Detectamos que já existe uma conta nesse e-mail, solicite uma nova senha em RECUPERAR SENHA");
                } else {
                    salvaPassageiro(activity, loading, imagemPerfil);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void salvaPassageiro(Activity activity, Loading loading, Bitmap imagemPerfil) {
        usuario.setCriadoEm(pegarHorarioAtual());
        usuario.setAtualizadoEm(pegarHorarioAtual());

        FirebaseAuth firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        firebaseAuth.createUserWithEmailAndPassword(this.getEmail(), this.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    usuario.setId(firebaseAuth.getUid());
                    usuario.setSenha(null);

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    final StorageReference mountainsRef = storageRef.child(usuario.getId()).child("perfil.jpg");

                    ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                    imagemPerfil.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
                    byte[] byteImagemPerfil = bStream.toByteArray();

                    UploadTask uploadTask = mountainsRef.putBytes(byteImagemPerfil);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mountainsRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    usuario.setBmPerfil(null);
                                    usuario.setFotoPerfil(uri.toString());

                                    DatabaseReference referenciaUsuarios = FirebaseDatabase.getInstance().getReference("usuarios");
                                    referenciaUsuarios.child(usuario.getId()).setValue(usuario).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Alerts alerts = new Alerts(activity);
                                            alerts.mostrarPopupSucessoSemRedirect("Sua conta foi criada com sucesso, aguarde até ser redirecionado");

                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    UsuarioFirebase.redirecionaUsuarioLogado(activity);
                                                }
                                            }, 3000);
                                        }
                                    });
                                }
                            });
                        }
                    });
                } else {
                    loading.fecharLoading();

                    Alerts alerts = new Alerts(activity);
                    String excecao;

                    try {
                        throw task.getException();
                    }catch ( FirebaseAuthInvalidUserException e ) {
                        excecao = "Usuário não está cadastrado.";
                    }catch ( FirebaseAuthInvalidCredentialsException e ){
                        excecao = "E-mail e senha não correspondem a um usuário cadastrado";
                    }catch (Exception e){
                        excecao = "Erro ao cadastrar usuário: "  + e.getMessage();
                        e.printStackTrace();
                    }

                    alerts.mostrarPopupErro(excecao);
                }
            }
        });
    }

    public void salvaMotorista(Activity activity, Loading loading, ArrayList<Bitmap> arrayBitmap) {
        usuario.setCriadoEm(pegarHorarioAtual());
        usuario.setAtualizadoEm(pegarHorarioAtual());

        FirebaseAuth firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        firebaseAuth.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    usuario.setId(firebaseAuth.getUid());
                    usuario.setSenha(null);

                    DatabaseReference referenciaUsuarios = FirebaseDatabase.getInstance().getReference("usuarios");
                    referenciaUsuarios.child(usuario.getId()).setValue(usuario).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference();

                            StorageReference perfilRef = storageRef.child(usuario.getId()).child("perfil;jpg");
                            StorageReference carFrenteRef = storageRef.child(usuario.getId()).child("carro").child("frente.jpg");
                            StorageReference carDianteiraRef = storageRef.child(usuario.getId()).child("carro").child("dianteira.jpg");
                            StorageReference carDentroRef = storageRef.child(usuario.getId()).child("carro").child("dentro.jpg");
                            StorageReference docFrenteRef = storageRef.child(usuario.getId()).child("documento").child("frente.jpg");
                            StorageReference docVersoRef = storageRef.child(usuario.getId()).child("documento").child("verso.jpg");

                            ByteArrayOutputStream baosPerfil = new ByteArrayOutputStream();
                            ByteArrayOutputStream baoscarFrente = new ByteArrayOutputStream();
                            ByteArrayOutputStream baoscarDianteira = new ByteArrayOutputStream();
                            ByteArrayOutputStream baoscarDentro = new ByteArrayOutputStream();
                            ByteArrayOutputStream baosdocFrente = new ByteArrayOutputStream();
                            ByteArrayOutputStream baosdocVerso = new ByteArrayOutputStream();

                            arrayBitmap.get(0).compress(Bitmap.CompressFormat.JPEG, 100, baosPerfil);
                            arrayBitmap.get(1).compress(Bitmap.CompressFormat.JPEG, 100, baoscarFrente);
                            arrayBitmap.get(2).compress(Bitmap.CompressFormat.JPEG, 100, baoscarDianteira);
                            arrayBitmap.get(3).compress(Bitmap.CompressFormat.JPEG, 100, baoscarDentro);
                            arrayBitmap.get(4).compress(Bitmap.CompressFormat.JPEG, 100, baosdocFrente);
                            arrayBitmap.get(5).compress(Bitmap.CompressFormat.JPEG, 100, baosdocVerso);

                            byte[] bytePerfil = baosPerfil.toByteArray();
                            byte[] byteCarFrente = baoscarFrente.toByteArray();
                            byte[] byteCarDianteira = baoscarDianteira.toByteArray();
                            byte[] byteCarDentro = baoscarDentro.toByteArray();
                            byte[] byteDocFrente = baosdocFrente.toByteArray();
                            byte[] byteDocVerso = baosdocVerso.toByteArray();

                            UploadTask uploadPerfilTask = perfilRef.putBytes(bytePerfil);
                            UploadTask uploadCarFrenteTask = carFrenteRef.putBytes(byteCarFrente);
                            UploadTask uploadCarDianteiraTask = carDianteiraRef.putBytes(byteCarDianteira);
                            UploadTask uploadCarDentroTask = carDentroRef.putBytes(byteCarDentro);
                            UploadTask uploadDocFrenteTask = docFrenteRef.putBytes(byteDocFrente);
                            UploadTask uploadDocVersoTask = docVersoRef.putBytes(byteDocVerso);

                            uploadPerfilTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    perfilRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            referenciaUsuarios.child(usuario.getId()).child("fotoPerfil").setValue(uri.toString());
                                        }
                                    });
                                }
                            });

                            uploadCarFrenteTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    carFrenteRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            referenciaUsuarios.child(usuario.getId()).child("carro").child("urlFrente").setValue(uri.toString());
                                        }
                                    });
                                }
                            });

                            uploadCarDianteiraTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    carDianteiraRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            referenciaUsuarios.child(usuario.getId()).child("carro").child("urlDianteira").setValue(uri.toString());
                                        }
                                    });
                                }
                            });

                            uploadCarDentroTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    carDentroRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            referenciaUsuarios.child(usuario.getId()).child("carro").child("urlDentro").setValue(uri.toString());
                                        }
                                    });
                                }
                            });

                            uploadDocFrenteTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    docFrenteRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            referenciaUsuarios.child(usuario.getId()).child("documento").child("urlFrente").setValue(uri.toString());
                                        }
                                    });
                                }
                            });

                            uploadDocVersoTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    docVersoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            referenciaUsuarios.child(usuario.getId()).child("documento").child("urlVerso").setValue(uri.toString());

                                            loading.fecharLoading();
                                            Alerts alerts = new Alerts(activity);
                                            alerts.mostrarPopupSucessoSemRedirect("Sua conta foi criada com sucesso e foi enviada para nossa analise. Responderemos o mais rápido possível");

                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    UsuarioFirebase.redirecionaUsuarioLogado(activity);
                                                }
                                            }, 3000);
                                        }
                                    });
                                }
                            });
                        }
                    });

                } else {
                    loading.fecharLoading();

                    Alerts alerts = new Alerts(activity);
                    String excecao;

                    try {
                        throw task.getException();
                    }catch ( FirebaseAuthInvalidUserException e ) {
                        excecao = "Usuário não está cadastrado.";
                    }catch ( FirebaseAuthInvalidCredentialsException e ){
                        excecao = "E-mail e senha não correspondem a um usuário cadastrado";
                    }catch (Exception e){
                        excecao = "Erro ao cadastrar usuário: "  + e.getMessage();
                        e.printStackTrace();
                    }

                    alerts.mostrarPopupErro(excecao);
                }
            }
        });
    }


    public void atualizarFotoPerfil(Activity activity, Loading loading, Bitmap fotoSelecionada) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final StorageReference mountainsRef = storageRef.child(usuario.getId()).child("perfil.jpg");

        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        fotoSelecionada.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
        byte[] byteImagemPerfil = bStream.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(byteImagemPerfil);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mountainsRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DatabaseReference referenciaUsuarios = FirebaseDatabase.getInstance().getReference("usuarios");
                        referenciaUsuarios.child(usuario.getId()).child("fotoPerfil").setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                loading.fecharLoading();
                                Alerts alerts = new Alerts(activity);
                                alerts.mostrarPopupSucessoSemRedirect("Sua foto de perfil foi alterada com sucesso!");
                            }
                        });
                    }
                });
            }
        });

    }


    public void atualizaEmail(FirebaseUser user, Activity activity, Loading loading, String email) {
        Alerts alerts = new Alerts(activity);
        DatabaseReference firebaseDatabase = ConfiguracaoFirebase.getFirebaseDatabase();
        user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    firebaseDatabase.child("usuarios").child(user.getUid()).child("email").setValue(email);
                    alerts.mostrarPopupSucessoSemRedirect("Email alterado com sucesso");
                    loading.fecharLoading();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(activity, SidebarMenuActivity.class);
                            activity.startActivity(intent);
                        }
                    }, 2000);
                } else {
                    loading.fecharLoading();

                    try {
                        throw task.getException();
                    } catch(FirebaseAuthInvalidCredentialsException e) {
                        alerts.mostrarPopupErro("Suas credentiais são inválidas");
                    } catch(FirebaseAuthUserCollisionException e) {
                        alerts.mostrarPopupErro("Houve um conflito entre usuários");
                    } catch (FirebaseAuthRecentLoginRequiredException e) {
                        alerts.mostrarPopupErro("Sua sessão expirou, é necessário efetuar o login novamente.");

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                LoginManager.getInstance().logOut();
                                FirebaseAuth firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
                                firebaseAuth.signOut();
                                Intent intent = new Intent(activity, ConfirmarPerfilActivity.class);
                                activity.startActivity(intent);
                            }
                        }, 2000);
                    } catch(Exception e) {
                        alerts.mostrarPopupErro(e.getMessage());
                    }
                }
            }
        });
    }


    public Date pegarHorarioAtual() {
        Date currentTime = Calendar.getInstance().getTime();
        return currentTime;
    }
}
