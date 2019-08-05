package com.upbrasil.up.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.upbrasil.up.R;
import com.upbrasil.up.config.ConfiguracaoFirebase;
import com.upbrasil.up.fragments.PedidoPassageiroFragment;
import com.upbrasil.up.helper.Alerts;
import com.upbrasil.up.helper.GPSTracker;
import com.upbrasil.up.helper.Loading;
import com.upbrasil.up.helper.Local;
import com.upbrasil.up.helper.UsuarioFirebase;
import com.upbrasil.up.model.Destino;
import com.upbrasil.up.model.Localizacao;
import com.upbrasil.up.model.Requisicao;
import com.upbrasil.up.model.Usuario;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PassageiroActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    /*
    * Lat/lon destino:-23.556407, -46.662365 (Av. Paulista, 2439)
    * Lat/lon passageiro: -23.562791, -46.654668
    * Lat/lon Motorista (a caminho):
    *   longe: -23.571139, -46.660936
    *   inicial: -23.563196, -46.650607
    *   intermediaria: -23.564801, -46.652196
    *   final: -23.562801, -46.654660
    * Encerramento intermediário: -23.557499, -46.661084
    * Encerramento da corrida: -23.556439, -46.662313
    * */

    //Componentes
    private EditText editDestino;
    private LinearLayout linearLayoutDestino;
    public Button buttonChamarUber;

    private GoogleMap mMap;
    private FirebaseAuth autenticacao;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng localPassageiro;
    private boolean cancelarUber = false;
    private DatabaseReference firebaseRef;
    private Requisicao requisicao;
    private Usuario passageiro;
    private String statusRequisicao;
    private Destino destino;
    private Marker marcadorMotorista;
    private Marker marcadorPassageiro;
    private Marker marcadorDestino;
    private Usuario motorista;
    private LatLng localMotorista;
    private ImageButton botaoPerfil;
    GPSTracker gps;
    private ImageButton botaoSidebarMenu;
    LatLng latLngUsuario;
    LatLng latLngUsuarioDestino;
    String enderecoDestino;
    String nomeRua;
    String numeroRua;
    Place destinoUsuario;
    String resultado;
    float valor;
    String enderecoPartida;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String buscarInformacoesUsuario;
    String formaPagamento;
    ImageButton botaoChat;

    private Loading loading;
    Polyline caminho;

    Alerts alerts;

    FragmentTransaction ft;
    FragmentManager fm;
    PedidoPassageiroFragment pedidoPassageiroFragment;

    public static PassageiroActivity passageiroActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passageiro);

        passageiroActivity = this;

        alerts = new Alerts(this);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            alerts.mostraPopUpGPS("Você precisa ativar a localização do seu celular");
        } else {
            inicializarComponentes();

            //Adiciona listener para status da requisição
            verificaStatusRequisicao();

            loading = new Loading(this);

            firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
            user = firebaseAuth.getCurrentUser();

            buscarInformacao();

            botaoChat = findViewById(R.id.botao_chat);
            botaoChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(requisicao != null) {
                        Intent intent = new Intent(PassageiroActivity.this, ChatActivity.class);
                        startActivity(intent);
                    }
                }
            });

            botaoSidebarMenu = findViewById(R.id.botao_sidebarmenu);
            botaoSidebarMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PassageiroActivity.this, SidebarMenuActivity.class);
                    startActivity(intent);
                }
            });

            gps = new GPSTracker(this);
            if(gps.canGetLocation()){
                gps.getLatitude(); // returns latitude
                gps.getLongitude();
            }

            Localizacao localizacao = new Localizacao(gps.getLatitude(), gps.getLongitude());
            firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
            firebaseRef.child("usuarios").child(user.getUid()).child("localizacaoAtual").setValue(localizacao);

            if (!Places.isInitialized()) {
                Places.initialize(getApplicationContext(), "AIzaSyBX5E3JSQqM7I-vYzIPsR4QB4cxzJIxhpc");
            }

            AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                    getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS_COMPONENTS, Place.Field.ADDRESS));
            autocompleteFragment.setCountry("BR");
            autocompleteFragment.setHint("Onde você está?");

            AutocompleteSupportFragment autocompleteFragmentDestino = (AutocompleteSupportFragment)
                    getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment_destino);
            autocompleteFragmentDestino.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS_COMPONENTS, Place.Field.ADDRESS));
            autocompleteFragmentDestino.setCountry("BR");
            autocompleteFragmentDestino.setHint("Para onde vai?");



            autocompleteFragmentDestino.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    latLngUsuarioDestino = place.getLatLng();
                    enderecoDestino = place.getAddress();
                    numeroRua = place.getAddress();
                    destinoUsuario = place;

                    if(place.getLatLng() == null) {
                        Toast.makeText(PassageiroActivity.this, "Selecione um lugar válido", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onError(Status status) {
                    // TODO: Handle the error.
                    Log.i("TAG", "An error occurred: " + status);
                }
            });

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    latLngUsuario = place.getLatLng();
                    enderecoPartida = place.getAddress();

                    if(place.getLatLng() == null) {
                        Toast.makeText(PassageiroActivity.this, "Selecione um lugar válido", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onError(Status status) {
                    // TODO: Handle the error.
                    Log.i("TAG", "An error occurred: " + status);
                }
            });

            autocompleteFragmentDestino.getView().findViewById(R.id.places_autocomplete_search_button).setVisibility(View.GONE);
            ((EditText)autocompleteFragmentDestino.getView().findViewById(R.id.places_autocomplete_search_input)).setTextSize(15.0f);
            ((EditText)autocompleteFragmentDestino.getView().findViewById(R.id.places_autocomplete_search_input)).setTextColor(Color.BLACK);
            ((EditText)autocompleteFragmentDestino.getView().findViewById(R.id.places_autocomplete_search_input)).setTypeface(Typeface.create("open_sans", Typeface.NORMAL));

            autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_button).setVisibility(View.GONE);
            ((EditText)autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_input)).setTextSize(15.0f);
            ((EditText)autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_input)).setTextColor(Color.BLACK);
            ((EditText)autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_input)).setTypeface(Typeface.create("open_sans", Typeface.NORMAL));
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            switch (requestCode) {
                case 1:
                    Intent intent = new Intent(PassageiroActivity.this, RequisicoesActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }

    private void verificaStatusRequisicao(){

        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        DatabaseReference requisicoes = firebaseRef.child("requisicoes");
        Query requisicaoPesquisa = requisicoes.orderByChild("passageiro/id")
                .equalTo( usuarioLogado.getId() );

        requisicaoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<Requisicao> lista = new ArrayList<>();
                for( DataSnapshot ds: dataSnapshot.getChildren() ){
                    lista.add( ds.getValue( Requisicao.class ) );
                }

                Collections.reverse(lista);
                if( lista!= null && lista.size()>0 ){
                    requisicao = lista.get(0);

                    if(requisicao != null){
                        if( !requisicao.getStatus().equals(Requisicao.STATUS_ENCERRADA) ) {
                            passageiro = requisicao.getPassageiro();
                            localPassageiro = latLngUsuario;
                            statusRequisicao = requisicao.getStatus();
                            destino = requisicao.getDestino();
                            if (requisicao.getMotorista() != null) {
                                motorista = requisicao.getMotorista();
                                localMotorista = new LatLng(motorista.getLocalizacaoAtual().getLatitude(), motorista.getLocalizacaoAtual().getLongitude());
                            }
                            alteraInterfaceStatusRequisicao(statusRequisicao);
                        }
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void alteraInterfaceStatusRequisicao(String status){

        if(status != null && !status.isEmpty()) {
            cancelarUber = false;
            switch (status) {
                case Requisicao.STATUS_AGUARDANDO:
                    requisicaoAguardando();
                    break;
                case Requisicao.STATUS_A_CAMINHO:
                    requisicaoACaminho();
                    break;
                case Requisicao.STATUS_VIAGEM:
                    requisicaoViagem();
                    break;
                case Requisicao.STATUS_FINALIZADA:
                    requisicaoFinalizada();
                    break;
                case Requisicao.STATUS_CANCELADA:
                    requisicaoCancelada();
                    break;

            }
        } else {
            adicionaMarcadorPassageiro(localPassageiro, "Seu local");
            centralizarMarcador(localPassageiro);
            botaoChat.setVisibility(View.INVISIBLE);
        }

    }

    private void requisicaoCancelada(){
        linearLayoutDestino.setVisibility( View.VISIBLE );
        buttonChamarUber.setText("Chamar UP");
        botaoChat.setVisibility(View.INVISIBLE);
        cancelarUber = false;

    }

    private void requisicaoAguardando(){
        botaoChat.setVisibility(View.VISIBLE);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("requisicoes");

        linearLayoutDestino.setVisibility( View.GONE );
        buttonChamarUber.setText("Cancelar UP");
        cancelarUber = true;

        if(localPassageiro == null) {
            reference.orderByChild("status").equalTo("aguardando").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        for(DataSnapshot each : dataSnapshot.getChildren()) {
                            Requisicao requisicaoUsuario = each.getValue(Requisicao.class);

                            if(requisicaoUsuario.getPassageiro().getId().equals(passageiro.getId())) {
                                localPassageiro = new LatLng(Double.valueOf(requisicaoUsuario.getPartida().getLatitude()), Double.valueOf(requisicaoUsuario.getPartida().getLongitude()));
                                latLngUsuarioDestino = new LatLng(Double.valueOf(requisicaoUsuario.getDestino().getLatitude()), Double.valueOf(requisicaoUsuario.getDestino().getLongitude()));

                                adicionaMarcadorPassageiro(localPassageiro, passageiro.getNomeCompleto());
                                adicionaMarcadorDestino(latLngUsuarioDestino, passageiro.getNomeCompleto());
                                centralizaDois(localPassageiro, latLngUsuarioDestino);
                            }
                        }
                    } else {
                        if( marcadorPassageiro != null )
                            marcadorPassageiro.remove();

                        if( marcadorDestino != null )
                            marcadorDestino.remove();

                        if( marcadorMotorista != null )
                            marcadorMotorista.remove();

                        requisicao.setStatus(Requisicao.STATUS_CANCELADA);
                        requisicao.setFormaPagamento(formaPagamento);
                        requisicao.atualizarStatus();
                        localPassageiro = new LatLng(gps.getLatitude(), gps.getLongitude());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            adicionaMarcadorPassageiro(localPassageiro, passageiro.getNomeCompleto());
            adicionaMarcadorDestino(latLngUsuarioDestino, passageiro.getNomeCompleto());
            centralizaDois(localPassageiro, latLngUsuarioDestino);
        }
    }

    public void centralizaDois(LatLng latFrom, LatLng latTo) {
        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include(latFrom)
                .include(latTo)
                .build();

        CameraUpdate camUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds, 10);
        mMap.animateCamera(camUpdate);
    }

    public void centralizaDoisPM(Requisicao requisicao) {
        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include(new LatLng(Double.parseDouble(requisicao.getPartida().getLatitude()), Double.parseDouble(requisicao.getPartida().getLongitude())))
                .include(new LatLng(requisicao.getMotorista().getLocalizacaoAtual().getLatitude(), requisicao.getMotorista().getLocalizacaoAtual().getLongitude()))
                .build();

        CameraUpdate camUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds, 10);
        mMap.animateCamera(camUpdate);
    }

    private void requisicaoACaminho(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("requisicoes");
        linearLayoutDestino.setVisibility( View.GONE );
        buttonChamarUber.setText("CANCELAR CORRIDA");
        buttonChamarUber.setEnabled(true);

        cancelarUber = true;

        if(localPassageiro == null) {
            reference.orderByChild("status").equalTo("acaminho").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        for(DataSnapshot each : dataSnapshot.getChildren()) {
                            Requisicao requisicaoUsuario = each.getValue(Requisicao.class);

                            if(requisicaoUsuario.getPassageiro().getId().equals(passageiro.getId())) {
                                localPassageiro = new LatLng(Double.valueOf(requisicaoUsuario.getPartida().getLatitude()), Double.valueOf(requisicaoUsuario.getPartida().getLongitude()));
                                localMotorista = new LatLng(Double.valueOf(requisicaoUsuario.getDestino().getLatitude()), Double.valueOf(requisicaoUsuario.getDestino().getLongitude()));

                                adicionaMarcadorPassageiro(localPassageiro, passageiro.getNomeCompleto());
                                adicionaMarcadorMotorista(localMotorista, motorista.getNomeCompleto());
                                centralizaDois(localPassageiro, localMotorista);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            adicionaMarcadorPassageiro(localPassageiro, passageiro.getNomeCompleto());
            adicionaMarcadorMotorista(localMotorista, motorista.getNomeCompleto());
            centralizaDois(localPassageiro, localMotorista);
        }

    }

    private void requisicaoViagem(){

        linearLayoutDestino.setVisibility( View.GONE );
        buttonChamarUber.setText("A caminho do destino");
        buttonChamarUber.setEnabled(false);

        //Adiciona marcador motorista
        adicionaMarcadorMotorista(localMotorista, motorista.getNomeCompleto());

        //Adiciona marcador de destino
        LatLng localDestino = latLngUsuarioDestino;

        adicionaMarcadorDestino(localDestino, "Destino");

        //Centraliza marcadores motorista / destino
        centralizarDoisMarcadores(marcadorMotorista, marcadorDestino);

    }

    private void requisicaoFinalizada(){

        linearLayoutDestino.setVisibility( View.GONE );
        buttonChamarUber.setEnabled(false);

        //Adiciona marcador de destino
        LatLng localDestino = latLngUsuarioDestino;

        adicionaMarcadorDestino(localDestino, "Destino");
        centralizarMarcador(localDestino);

        buttonChamarUber.setText("Corrida finalizada - R$ " + resultado);


        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Total da viagem")
                .setMessage("Sua viagem ficou: R$ " + resultado)
                .setCancelable(false)
                .setNegativeButton("Encerrar viagem", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        requisicao.setStatus(Requisicao.STATUS_ENCERRADA);
                        requisicao.atualizarStatus();

                        finish();
                        startActivity(new Intent(getIntent()));

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void adicionaMarcadorPassageiro(LatLng localizacao, String titulo){
        if( marcadorPassageiro != null )
            marcadorPassageiro.remove();

        BitmapDrawable  bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.marker_passageiro_true);
        Bitmap marker = bitmapdraw.getBitmap();
        Bitmap markerImage = Bitmap.createScaledBitmap(marker, 80, 80, false);

        marcadorPassageiro = mMap.addMarker(
                new MarkerOptions()
                        .position(localizacao)
                        .title(titulo)
                        .flat(true)
                        .icon(BitmapDescriptorFactory.fromBitmap(markerImage))
        );

    }

    private void adicionaMarcadorMotorista(LatLng localizacao, String titulo){

        if( marcadorMotorista != null )
            marcadorMotorista.remove();

        BitmapDrawable  bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.marker_driver);
        Bitmap marker = bitmapdraw.getBitmap();
        Bitmap markerImage = Bitmap.createScaledBitmap(marker, 80, 80, false);

        marcadorMotorista = mMap.addMarker(
                new MarkerOptions()
                        .position(localizacao)
                        .title(titulo)
                        .flat(true)
                        .icon(BitmapDescriptorFactory.fromBitmap(markerImage))
        );

    }

    private void adicionaMarcadorDestino(LatLng localizacao, String titulo){

        if( marcadorDestino != null )
            marcadorDestino.remove();

        BitmapDrawable  bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.chegada_marker);
        Bitmap marker = bitmapdraw.getBitmap();
        Bitmap markerImage = Bitmap.createScaledBitmap(marker, 80, 80, false);

        marcadorDestino = mMap.addMarker(
                new MarkerOptions()
                        .position(localizacao)
                        .title(titulo)
                        .icon(BitmapDescriptorFactory.fromBitmap(markerImage))
        );

    }

    private void centralizarMarcador(LatLng local){
        mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(local, 19)
        );
    }

    private void centralizarDoisMarcadores(Marker marcador1, Marker marcador2){

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include( marcador1.getPosition() );
        builder.include( marcador2.getPosition() );

        LatLngBounds bounds = builder.build();

        int largura = getResources().getDisplayMetrics().widthPixels;
        int altura = getResources().getDisplayMetrics().heightPixels;
        int espacoInterno = (int) (largura * 0.20);

        mMap.moveCamera(
                CameraUpdateFactory.newLatLngBounds(bounds,largura,altura,espacoInterno)
        );

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.maps));

            if (!success) {
                Log.e("OnMapReady", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("OnMapReady", "Can't find style. Error: ", e);
        }

        recuperarLocalizacaoUsuario();
    }

    public void chamarUber(View view){
        loading.mostrarLoading();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("usuarios");
        reference.orderByChild("id").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot each : dataSnapshot.getChildren()) {
                        Usuario usuario = each.getValue(Usuario.class);

                        if(usuario.getFormaPagamento() == null) {
                            loading.fecharLoading();
                            Intent intent = new Intent(PassageiroActivity.this, PagamentoActivity.class);
                            startActivity(intent);
                        } else {
                            if( cancelarUber ){
                                loading.fecharLoading();
                                if( marcadorPassageiro != null )
                                    marcadorPassageiro.remove();

                                if( marcadorDestino != null )
                                    marcadorDestino.remove();

                                if( marcadorMotorista != null )
                                    marcadorMotorista.remove();

                                requisicao.setStatus(Requisicao.STATUS_CANCELADA);
                                requisicao.setFormaPagamento(formaPagamento);
                                requisicao.atualizarStatus();
                                localPassageiro = new LatLng(gps.getLatitude(), gps.getLongitude());
                                centralizarMarcador(localPassageiro);
                            }else {
                                if( latLngUsuario != null ){

                                    Address addressDestino = recuperarEndereco( enderecoDestino );
                                    Address addressPartida = recuperarEndereco( enderecoPartida );

                                    if( addressDestino != null ){
                                        requisicao = new Requisicao();


                                        //Calcular distancia
                                        final Destino destino = new Destino();
                                        destino.setCidade( addressDestino.getSubAdminArea());
                                        destino.setCep( addressDestino.getPostalCode() );
                                        destino.setBairro( addressDestino.getSubLocality() );
                                        destino.setRua( addressDestino.getThoroughfare() );
                                        destino.setNumero( addressDestino.getFeatureName() );
                                        destino.setLatitude( String.valueOf(addressDestino.getLatitude()) );
                                        destino.setLongitude( String.valueOf(addressDestino.getLongitude()) );

                                        final Destino partida = new Destino();
                                        partida.setCidade( addressPartida.getSubAdminArea() );
                                        partida.setCep( addressPartida.getPostalCode() );
                                        partida.setBairro( addressPartida.getSubLocality() );
                                        partida.setRua( addressPartida.getThoroughfare() );
                                        partida.setNumero( addressPartida.getFeatureName() );
                                        partida.setLatitude( String.valueOf(addressPartida.getLatitude()) );
                                        partida.setLongitude( String.valueOf(addressPartida.getLongitude()) );

                                        float distancia = Local.calcularDistancia(latLngUsuario, new LatLng(addressDestino.getLatitude(), addressDestino.getLongitude()));
                                        Float taxa = (float) 1.75;
                                        Float valorKM = (float) 1.05;
                                        valor = (distancia * valorKM) + taxa;

                                        DecimalFormat decimal = new DecimalFormat("0.00");
                                        resultado = decimal.format(valor);

                                        requisicao.setPartida(partida);
                                        requisicao.setDestino(destino);
                                        requisicao.setFormaPagamento( formaPagamento );
                                        requisicao.setPreco(String.valueOf(resultado));

                                        loading.fecharLoading();

                                        pedidoPassageiroFragment = new PedidoPassageiroFragment();
                                        fm = getSupportFragmentManager();
                                        ft = fm.beginTransaction();

                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("requisicao", requisicao);
                                        pedidoPassageiroFragment.setArguments(bundle);
                                        ft.replace(R.id.fragment_content, pedidoPassageiroFragment);
                                        ft.commit();

                                        buttonChamarUber.setVisibility(View.GONE);

//                                        builder.show();

                                    }

                                }else {
                                    loading.fecharLoading();
                                    CFAlertDialog.Builder builder = new CFAlertDialog.Builder(PassageiroActivity.this)
                                            .setDialogStyle(CFAlertDialog.CFAlertStyle.NOTIFICATION)
                                            .setMessage("Você precisa selecionar um endereço")
                                            .setTextGravity(Gravity.CENTER_HORIZONTAL);
                                    builder.show();
                                }

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

    public void salvarRequisicao(){
        Usuario usuarioPassageiro = UsuarioFirebase.getDadosUsuarioLogado();
        usuarioPassageiro.setLocalizacaoAtual(new Localizacao(latLngUsuario.latitude, latLngUsuario.longitude));

        firebaseRef.child("usuarios").orderByChild("id").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot each : dataSnapshot.getChildren()) {
                        Usuario usuario = each.getValue(Usuario.class);
                        usuarioPassageiro.setNomeCompleto(usuario.getNomeCompleto());
                        usuarioPassageiro.setFotoPerfil(usuario.getFotoPerfil());
                        requisicao.setPassageiro( usuarioPassageiro );
                        requisicao.setStatus( Requisicao.STATUS_AGUARDANDO );
                        requisicao.salvar();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        linearLayoutDestino.setVisibility( View.GONE );
        buttonChamarUber.setText("Cancelar UP");

    }

    private Address recuperarEndereco(String endereco){

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> listaEnderecos = geocoder.getFromLocationName(endereco, 1);
            if( listaEnderecos != null && listaEnderecos.size() > 0 ){
                Address address = listaEnderecos.get(0);

                return address;

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    private void recuperarLocalizacaoUsuario() {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        mMap.setMyLocationEnabled(true);

        final LatLng minhaLocalidade = new LatLng(gps.getLatitude(), gps.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(minhaLocalidade, 19));

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //recuperar latitude e longitude
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();
                localPassageiro = new LatLng(latitude, longitude);

                //Atualizar GeoFire
                UsuarioFirebase.atualizarDadosLocalizacao(latitude, longitude);

                //Altera interface de acordo com o status
                alteraInterfaceStatusRequisicao( statusRequisicao );

                if(statusRequisicao != null && !statusRequisicao.isEmpty()) {
                    if (statusRequisicao.equals(Requisicao.STATUS_VIAGEM)
                            || statusRequisicao.equals(Requisicao.STATUS_FINALIZADA)) {
                        locationManager.removeUpdates(locationListener);
                    }else {
                        //Solicitar atualizações de localização
                        if (ActivityCompat.checkSelfPermission(PassageiroActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    10000,
                                    10,
                                    locationListener
                            );
                        }
                    }
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
                    10000,
                    10,
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
                LoginManager.getInstance().logOut();
                autenticacao.signOut();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void inicializarComponentes(){
        //Inicializar componentes
        linearLayoutDestino = findViewById(R.id.linearLayoutDestino);
        buttonChamarUber = findViewById(R.id.buttonChamarUber);

        //Configurações iniciais
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public void buscarInformacao() {
        DatabaseReference referenciaUsuarios = FirebaseDatabase.getInstance().getReference("usuarios");
        referenciaUsuarios.orderByChild("id").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot each : dataSnapshot.getChildren()) {
                        Usuario usuario = each.getValue(Usuario.class);

                        formaPagamento = usuario.getFormaPagamento();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String capitalize(String capString){
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()){
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }

}
