package com.upbrasil.up.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.upbrasil.up.R;
import com.upbrasil.up.helper.Alerts;
import com.upbrasil.up.helper.GPSTracker;
import com.upbrasil.up.helper.Loading;
import com.upbrasil.up.model.Carro;
import com.upbrasil.up.model.Documento;
import com.upbrasil.up.model.Localizacao;
import com.upbrasil.up.model.Usuario;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CadastroActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha, campoConfirmaSenha;

    private Button botaoCadastrar;
    String telefone;

    private Button btnPassageiro;
    private Button btnMotorista;


    private String txtSenha;
    private String txtConfirmaSenha;
    private String txtEmail;
    private String txtNome;

    private Alerts alerts;

    private Usuario usuario;

    private GPSTracker gps;

    private Boolean isPassageiro = true;

    private Loading loading;

    private Bitmap imagemPerfil;

    private RelativeLayout rlMotorista;

    private AutoCompleteTextView campoMarca;
    private EditText campoModelo;
    private AutoCompleteTextView campoAno;
    private EditText campoPlaca;
    private AutoCompleteTextView campoCor;

    private ImageView fotoPerfil;

    private ImageView fotoCarFrente;
    private ImageView fotoCarDianteira;
    private ImageView fotoCarDentro;

    private ImageView fotoDocFrente;
    private ImageView fotoDocVerso;

    private Bitmap bmCarFrente;
    private Bitmap bmCarDianteira;
    private Bitmap bmCarDentro;

    private Bitmap bmDocFrente;
    private Bitmap bmDocVerso;

    Bitmap bmPerfil;


    private String txtMarca;
    private String txtModelo;
    private String txtAno;
    private String txtPlaca;
    private String txtCor;

    private Button btnEnviar;

    private Button btnCadastrarMotorista;

    private String txtTipoIdentidade;
    private String txtIdentidade;

    private int minCaracteres = 9;

    private MaterialSpinner spinner;

    private EditText campoIdentidade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        alerts = new Alerts(this);
        loading = new Loading(this);

        loadComponentes();
        loadCarComponentes();
        loadDocComponentes();

        gps = new GPSTracker(this);
        if(gps.canGetLocation()){
            gps.getLatitude(); // returns latitude
            gps.getLongitude();
        }
    }

    public void loadComponentes() {
        rlMotorista = findViewById(R.id.box_motorista);

        campoNome  = findViewById(R.id.campo_nome);
        campoNome.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

        campoEmail = findViewById(R.id.campo_email);
        campoEmail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

        campoSenha = findViewById(R.id.campo_senha);
        campoConfirmaSenha = findViewById(R.id.campo_confirmar_senha);

        btnPassageiro = findViewById(R.id.btn_passageiro);
        btnMotorista = findViewById(R.id.btn_motorista);

        fotoPerfil = findViewById(R.id.imagem_perfil);

        Intent intent = getIntent();
        telefone = intent.getStringExtra("telefone");

        botaoCadastrar = findViewById(R.id.botao_cadastrar);
        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading.mostrarLoading();

                if(isPassageiro) {
                    if(camposPreenchidosPassageiro()) {
                        usuario = new Usuario();
                        usuario.setNomeCompleto(txtNome);
                        usuario.setEmail(txtEmail);
                        usuario.setTipo("P");
                        usuario.setNivelUsuario(0);
                        usuario.setAprovado(true);
                        usuario.setTelefone(telefone);
                        usuario.setSenha(txtSenha);
                        usuario.setMultiConta(false);
                        usuario.setLocalizacaoAtual(new Localizacao(gps.getLatitude(), gps.getLongitude()));
                        usuario.verificaEmailExistente(CadastroActivity.this, loading, bmPerfil);
                    } else {
                        loading.fecharLoading();
                    }
                }
            }
        });

        btnCadastrarMotorista = findViewById(R.id.botao_cadastrar_motorista);
        btnCadastrarMotorista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isPassageiro) {
                    if(camposPreenchidosMotorista()) {
                        ArrayList<Bitmap> arrayBitmap = new ArrayList<>();
                        arrayBitmap.add(bmPerfil);
                        arrayBitmap.add(bmCarFrente);
                        arrayBitmap.add(bmCarDianteira);
                        arrayBitmap.add(bmCarDentro);
                        arrayBitmap.add(bmDocFrente);
                        arrayBitmap.add(bmDocVerso);

                        usuario = new Usuario();
                        usuario.setNomeCompleto(txtNome);
                        usuario.setEmail(txtEmail);
                        usuario.setTipo("M");
                        usuario.setNivelUsuario(0);
                        usuario.setAprovado(false);
                        usuario.setMultiConta(false);
                        usuario.setTelefone(telefone);
                        usuario.setSenha(txtSenha);
                        usuario.setBmPerfil(imagemPerfil);
                        usuario.setLocalizacaoAtual(new Localizacao(gps.getLatitude(), gps.getLongitude()));

                        usuario.setCarro(new Carro(txtMarca, txtModelo, txtPlaca, Integer.parseInt(txtAno),
                                txtCor, null, null, null));
                        usuario.setDocumento(new Documento(txtTipoIdentidade, txtIdentidade, null, null));
                        usuario.salvaMotorista(CadastroActivity.this, loading, arrayBitmap);

                        loading.mostrarLoading();
                    } else {
                        loading.fecharLoading();
                    }
                }
            }
        });

        btnPassageiro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adicionaBackgroundBotao(btnPassageiro);
                isPassageiro = true;
                rlMotorista.setVisibility(View.INVISIBLE);
                botaoCadastrar.setVisibility(View.VISIBLE);
            }
        });

        btnMotorista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adicionaBackgroundBotao(btnMotorista);
                isPassageiro = false;
                rlMotorista.setVisibility(View.VISIBLE);
                botaoCadastrar.setVisibility(View.INVISIBLE);
            }
        });

        fotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecionarImagem(1);
            }
        });
    }

    public void loadCarComponentes() {
        fotoCarFrente = findViewById(R.id.foto_frente);
        fotoCarDianteira = findViewById(R.id.foto_dianteira);
        fotoCarDentro = findViewById(R.id.foto_dentro);

        String[] marcas = getResources().getStringArray(R.array.marcas_carro);
        campoMarca = findViewById(R.id.campo_marca);
        campoMarca.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, marcas);
        campoMarca.setAdapter(adapter);

        campoModelo = findViewById(R.id.campo_modelo);
        campoModelo.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

        String[] ano = getResources().getStringArray(R.array.ano_carro);
        campoAno = findViewById(R.id.campo_ano);
        campoAno.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        ArrayAdapter<String> adapterAno =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ano);
        campoAno.setAdapter(adapterAno);

        campoPlaca = findViewById(R.id.campo_placa);
        campoPlaca.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

        String[] cores = getResources().getStringArray(R.array.cores_carro);
        campoCor = findViewById(R.id.campo_cor);
        campoCor.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        ArrayAdapter<String> adapterCores =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cores);
        campoCor.setAdapter(adapterCores);

        fotoCarFrente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecionarImagem(2);
            }
        });

        fotoCarDianteira.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecionarImagem(3);
            }
        });

        fotoCarDentro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecionarImagem(4);
            }
        });
    }

    public void loadDocComponentes() {
        campoIdentidade = findViewById(R.id.campo_num_identidade);
        fotoDocFrente = findViewById(R.id.foto_doc_frente);
        fotoDocVerso = findViewById(R.id.foto_doc_verso);

        btnCadastrarMotorista = findViewById(R.id.botao_cadastrar_motorista);

        campoIdentidade.setFilters(new InputFilter[] {new InputFilter.LengthFilter(minCaracteres + 1)});

        spinner = (MaterialSpinner) findViewById(R.id.spinner);
        spinner.setItems("RG", "CPF", "CNH");
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                txtTipoIdentidade = item;

                if(item.equals("RG")) {
                    minCaracteres = 9;
                    campoIdentidade.setFilters(new InputFilter[] {new InputFilter.LengthFilter(minCaracteres + 1)});
                    campoIdentidade.setText("");
                } else if(item.equals("CPF")) {
                    minCaracteres = 10;
                    campoIdentidade.setFilters(new InputFilter[] {new InputFilter.LengthFilter(minCaracteres + 1)});
                    campoIdentidade.setText("");
                } else if(item.equals("CNH")) {
                    minCaracteres = 8;
                    campoIdentidade.setFilters(new InputFilter[] {new InputFilter.LengthFilter(minCaracteres + 1)});
                    campoIdentidade.setText("");
                }
            }
        });

        // Doc
        fotoDocFrente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecionarImagem(5);
            }
        });

        fotoDocVerso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecionarImagem(6);
            }
        });
    }

    public void adicionaBackgroundBotao(Button btn) {
        btnPassageiro.setBackgroundColor(Color.TRANSPARENT);
        btnPassageiro.setTextColor(Color.BLACK);

        btnMotorista.setBackgroundColor(Color.TRANSPARENT);
        btnMotorista.setTextColor(Color.BLACK);

        btn.setBackgroundResource(R.drawable.degrade_color);
        btn.setTextColor(Color.WHITE);
    }

    private void selecionarImagem(int code) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("outputX", 256);
        intent.putExtra("outputY", 256);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == 1) {
            final Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap fotoSelecionada = extras.getParcelable("data");
                bmPerfil = fotoSelecionada;
                fotoPerfil.setImageBitmap(fotoSelecionada);
            }
        }

        if (requestCode == 2) {
            final Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap fotoSelecionada = extras.getParcelable("data");
                bmCarFrente = fotoSelecionada;
                fotoCarFrente.setImageBitmap(fotoSelecionada);
            }
        }

        if (requestCode == 3) {
            final Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap fotoSelecionada = extras.getParcelable("data");
                bmCarDianteira = fotoSelecionada;
                fotoCarDianteira.setImageBitmap(fotoSelecionada);
            }
        }

        if (requestCode == 4) {
            final Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap fotoSelecionada = extras.getParcelable("data");
                bmCarDentro = fotoSelecionada;
                fotoCarDentro.setImageBitmap(fotoSelecionada);
            }
        }

        if (requestCode == 5) {
            final Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap fotoSelecionada = extras.getParcelable("data");
                bmDocFrente = fotoSelecionada;
                fotoDocFrente.setImageBitmap(fotoSelecionada);
            }
        }

        if (requestCode == 6) {
            final Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap fotoSelecionada = extras.getParcelable("data");
                bmDocVerso = fotoSelecionada;
                fotoDocVerso.setImageBitmap(fotoSelecionada);
            }
        }
    }


    public boolean camposPreenchidosPassageiro() {
        txtSenha = campoSenha.getText().toString();
        txtConfirmaSenha = campoConfirmaSenha.getText().toString();
        txtEmail = campoEmail.getText().toString();
        txtNome = campoNome.getText().toString();


        if(txtNome.isEmpty()) {
            alerts.mostrarPopupErro("Precisamos saber qual o seu nome");
            return false;
        }

        if(txtEmail.isEmpty()) {
            alerts.mostrarPopupErro("É necessário inserir um email");
            return false;
        }

        if(!isValidEmail(txtEmail)) {
            alerts.mostrarPopupErro("É necessário inserir um email válido");
            return false;
        }

        if(txtSenha.isEmpty()) {
            alerts.mostrarPopupErro("É necessário inserir uma senha");
            return false;
        }

        if(txtConfirmaSenha.isEmpty()) {
            alerts.mostrarPopupErro("É necessário confirmar sua senha");
            return false;
        }

        if(!isValidPassword(txtSenha)) {
            alerts.mostrarPopupErro("Sua senha precisa ter no minimo 8 caracteres, 1 letra, 1 número e um caractere especial");
            return false;
        }

        if(!txtSenha.equals(txtConfirmaSenha)) {
            alerts.mostrarPopupErro("As senhas precisam ser iguais");
            return false;
        }

        if(bmPerfil == null) {
            alerts.mostrarPopupErro("Selecione uma foto de perfil");
            return false;
        }

        return true;
    }

    public boolean camposPreenchidosMotorista() {
        txtSenha = campoSenha.getText().toString();
        txtConfirmaSenha = campoConfirmaSenha.getText().toString();
        txtEmail = campoEmail.getText().toString();
        txtNome = campoNome.getText().toString();
        txtIdentidade = campoIdentidade.getText().toString();
        txtMarca = campoMarca.getText().toString();
        txtModelo = campoModelo.getText().toString();
        txtAno = campoAno.getText().toString();
        txtPlaca = campoPlaca.getText().toString();
        txtCor = campoCor.getText().toString();

        if(txtNome.isEmpty()) {
            alerts.mostrarPopupErro("Precisamos saber qual o seu nome");
            return false;
        }

        if(txtEmail.isEmpty()) {
            alerts.mostrarPopupErro("É necessário inserir um email");
            return false;
        }

        if(!isValidEmail(txtEmail)) {
            alerts.mostrarPopupErro("É necessário inserir um email válido");
            return false;
        }

        if(txtSenha.isEmpty()) {
            alerts.mostrarPopupErro("É necessário inserir uma senha");
            return false;
        }

        if(txtConfirmaSenha.isEmpty()) {
            alerts.mostrarPopupErro("É necessário confirmar sua senha");
            return false;
        }

        if(!isValidPassword(txtSenha)) {
            alerts.mostrarPopupErro("Sua senha precisa ter no minimo 8 caracteres, 1 letra, 1 número e um caractere especial");
            return false;
        }

        if(!txtSenha.equals(txtConfirmaSenha)) {
            alerts.mostrarPopupErro("As senhas precisam ser iguais");
            return false;
        }

        if(bmPerfil == null) {
            alerts.mostrarPopupErro("Selecione uma foto de perfil");
            return false;
        }

        if(txtMarca.isEmpty()) {
            alerts.mostrarPopupErro("Selecione a marca do veículo");
            return false;
        }

        if(txtModelo.isEmpty()) {
            alerts.mostrarPopupErro("Selecione o modelo do veículo");
            return false;
        }

        if(txtAno.isEmpty()) {
            alerts.mostrarPopupErro("Selecione o ano do veículo");
            return false;
        }

        if(txtPlaca.isEmpty()) {
            alerts.mostrarPopupErro("Digite a placa do veículo");
            return false;
        }

        if(txtPlaca.length() <= 4) {
            alerts.mostrarPopupErro("Digite uma placa válida");
            return false;
        }

        if(Integer.valueOf(txtAno) <= 1999) {
            alerts.mostrarPopupErro("O ano do seu carro precisa ser maior que 2000");
            return false;
        }

        if(txtCor.isEmpty()) {
            alerts.mostrarPopupErro("Selecione a cor do veículo");
            return false;
        }

        if(bmCarFrente == null || bmCarDianteira == null || bmCarDentro == null) {
            alerts.mostrarPopupErro("Todas as imagens precisam ser enviadas");
            return false;
        }

        if(txtIdentidade.isEmpty()) {
            alerts.mostrarPopupErro("Precisamos saber qual o número de sua identidade");
            return false;
        }

        if(txtIdentidade.length() < minCaracteres) {
            alerts.mostrarPopupErro("Insira um número de identidade válido");
            return false;
        }

        if(bmDocFrente == null || bmDocVerso == null) {
            alerts.mostrarPopupErro("Todas as imagens precisam ser enviadas");
            return false;
        }

        return true;
    }

    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    public void onBackPressed() {

    }

    public static int pxToDp(int px)
    {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }
}
