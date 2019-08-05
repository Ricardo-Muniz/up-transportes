package com.upbrasil.up.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.facebook.login.LoginManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.upbrasil.up.R;
import com.upbrasil.up.adapter.RequisicoesAdapter;
import com.upbrasil.up.config.ConfiguracaoFirebase;
import com.upbrasil.up.fragments.CorridaFragment;
import com.upbrasil.up.helper.Alerts;
import com.upbrasil.up.helper.GPSTracker;
import com.upbrasil.up.helper.UsuarioFirebase;
import com.upbrasil.up.model.FormasPagamento;
import com.upbrasil.up.model.Localizacao;
import com.upbrasil.up.model.Requisicao;
import com.upbrasil.up.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class RequisicoesActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FirebaseAuth autenticacao;
    private DatabaseReference firebaseRef;
    private List<Requisicao> listaRequisicoes = new ArrayList<>();
    private RequisicoesAdapter adapter;
    private Usuario motorista;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private Button btnSair;
    GPSTracker gps;
    private GoogleMap mMap;
    private ImageButton botaoSidebarMenu;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    DatabaseReference firebaseDatabase;

    Usuario usuario;

    ArrayList<FormasPagamento> formasPagamentoArrayList = new ArrayList<>();

    private Alerts alerts;

    public static RequisicoesActivity requisicoesActivity;

    private Button btnIniciar;
    private Boolean isLigado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requisicoes);

        requisicoesActivity = this;

        alerts = new Alerts(this);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            alerts.mostraPopUpGPS("Você precisa ativar a localização do seu celular");
        } else {
            firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
            user = firebaseAuth.getCurrentUser();

            gps = new GPSTracker(this);
            if(gps.canGetLocation()){
                gps.getLatitude(); // returns latitude
                gps.getLongitude();
            }

            Localizacao localizacao = new Localizacao(gps.getLatitude(), gps.getLongitude());
            firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
            firebaseRef.child("usuarios").child(user.getUid()).child("localizacaoAtual").setValue(localizacao);

            buscarUsuario();

            botaoSidebarMenu = findViewById(R.id.botao_sidebarmenu);
            botaoSidebarMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RequisicoesActivity.this, SidebarMenuActivity.class);
                    startActivity(intent);
                }
            });



            btnIniciar = findViewById(R.id.botao_iniciar);
            btnIniciar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ligarCorrida();
                }
            });

            inicializarComponentes();
        }
    }

    public void ligarCorrida() {
        firebaseDatabase = FirebaseDatabase.getInstance().getReference("usuarios");
        firebaseDatabase.orderByChild("id").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot each : dataSnapshot.getChildren()) {
                        usuario = each.getValue(Usuario.class);

                        if(usuario.getFormasPagamento() != null) {
                            if(usuario.getAtivarCorridas()) {
                                btnIniciar.setBackgroundResource(R.drawable.button_degrade_confirm_color);
                                btnIniciar.setText("Ligar");
                                firebaseRef.child("usuarios").child(user.getUid()).child("ativarCorridas").setValue(false);
                            } else {
                                btnIniciar.setBackgroundResource(R.drawable.button_degrade_error_color);
                                btnIniciar.setText("Desligar");
                                firebaseRef.child("usuarios").child(user.getUid()).child("ativarCorridas").setValue(true);
                            }
                        } else {
                            Intent intent = new Intent(RequisicoesActivity.this, PagamentoActivity.class);
                            startActivity(intent);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            switch (requestCode) {
                case 1:
                    Intent intent = new Intent(RequisicoesActivity.this, RequisicoesActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        verificaStatusRequisicao();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(gps.getLatitude(), gps.getLongitude());

        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(19));

        recuperarLocalizacaoUsuario();
    }

    public void logout() {
        LoginManager.getInstance().logOut();
        autenticacao.signOut();
        Intent intent = new Intent(RequisicoesActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void verificaStatusRequisicao(){

        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference requisicoes = firebaseRef.child("requisicoes");

        Query requisicoesPesquisa = requisicoes.orderByChild("motorista/id")
                .equalTo( usuarioLogado.getId() );

        requisicoesPesquisa.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for( DataSnapshot ds: dataSnapshot.getChildren() ){

                    Requisicao requisicao = ds.getValue( Requisicao.class );

                    if( requisicao.getStatus().equals(Requisicao.STATUS_A_CAMINHO)
                            || requisicao.getStatus().equals(Requisicao.STATUS_VIAGEM)
                            || requisicao.getStatus().equals(Requisicao.STATUS_FINALIZADA)){
                        motorista = requisicao.getMotorista();
                        abrirTelaCorrida(requisicao.getId(), true);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void recuperarLocalizacaoUsuario() {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        final LatLng minhaLocalidade = new LatLng(gps.getLatitude(), gps.getLongitude());

        mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(minhaLocalidade, 19)
        );

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //recuperar latitude e longitude
                String latitude = String.valueOf(location.getLatitude());
                String longitude = String.valueOf(location.getLongitude());

                //Atualizar GeoFire
                UsuarioFirebase.atualizarDadosLocalizacao(
                        location.getLatitude(),
                        location.getLongitude()
                );

                if( !latitude.isEmpty() && !longitude.isEmpty() ){
                    motorista.setLocalizacaoAtual(new Localizacao(location.getLatitude(), location.getLongitude()));

                    locationManager.removeUpdates(locationListener);
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        //Solicitar atualizações de localização
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0,
                    locationListener
            );
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuSair :
                autenticacao.signOut();
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void abrirTelaCorrida(String idRequisicao, boolean requisicaoAtiva){
        Intent i = new Intent(RequisicoesActivity.this, CorridaActivity.class );
        i.putExtra("idRequisicao", idRequisicao );
        i.putExtra("motorista", motorista );
        i.putExtra("requisicaoAtiva", requisicaoAtiva );
        startActivity( i );
    }

    private void inicializarComponentes(){
        //Configurações iniciais
        motorista = UsuarioFirebase.getDadosUsuarioLogado();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        recuperarRequisicoes();

    }

    private void recuperarRequisicoes(){

        DatabaseReference requisicoes = firebaseRef.child("requisicoes");

        Query requisicaoPesquisa = requisicoes.orderByChild("status")
                .equalTo(Requisicao.STATUS_AGUARDANDO);

        requisicaoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                listaRequisicoes.clear();
                for ( DataSnapshot ds: dataSnapshot.getChildren() ){
                    Requisicao requisicao = ds.getValue( Requisicao.class );
                    listaRequisicoes.add( requisicao );
                }

                if( listaRequisicoes.size() > 0 ) {
                    for(Requisicao requisicao : listaRequisicoes) {
                        mostrarRequisicao(requisicao);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void buscarUsuario() {
        firebaseDatabase = FirebaseDatabase.getInstance().getReference("usuarios");
        firebaseDatabase.orderByChild("id").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    formasPagamentoArrayList.clear();

                    for(DataSnapshot each : dataSnapshot.getChildren()) {
                        usuario = each.getValue(Usuario.class);
                        btnIniciar.setVisibility(View.VISIBLE);

                        if(usuario.getAtivarCorridas() != null) {
                            if(usuario.getAtivarCorridas()) {
                                isLigado = true;

                                btnIniciar.setBackgroundResource(R.drawable.button_degrade_error_color);
                                btnIniciar.setText("Desligar");
                            } else {
                                isLigado = false;
                                btnIniciar.setBackgroundResource(R.drawable.button_degrade_confirm_color);
                                btnIniciar.setText("Ligar");
                            }
                        } else {
                            isLigado = false;
                            btnIniciar.setBackgroundResource(R.drawable.button_degrade_confirm_color);
                            btnIniciar.setText("Ligar");
                        }

                        if(usuario.getFormasPagamento() != null) {
                            for(DataSnapshot forma : each.child("formasPagamento").getChildren()) {
                                FormasPagamento formasPagamento = forma.getValue(FormasPagamento.class);
                                formasPagamentoArrayList.add(formasPagamento);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void mostrarRequisicao(Requisicao requisicao) {
        if(usuario.getFormasPagamento() != null) {
            for(FormasPagamento forma : formasPagamentoArrayList) {
                if(requisicao.getFormaPagamento().equals(forma.getNome())) {
                    CorridaFragment corridaFragment = new CorridaFragment(this);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("requisicao", requisicao);
                    corridaFragment.setArguments(bundle);
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.fragment_content, corridaFragment);
                    ft.commit();
                    btnIniciar.setVisibility(View.GONE);
                }
            }
        }
    }

}
