package com.upbrasil.up.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import com.upbrasil.up.R;
import com.upbrasil.up.activity.RequisicoesActivity;
import com.upbrasil.up.helper.GPSTracker;
import com.upbrasil.up.model.Requisicao;

public class CorridaFragment extends Fragment {

    private GPSTracker gps;
    private GoogleMap mMap;
    private Context context;
    private Marker marcadorMotorista;
    private Marker marcadorPassageiro;
    private Requisicao requisicao;

    public CorridaFragment(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_corrida,
                container, false);

        requisicao = (Requisicao) getArguments().getSerializable("requisicao");

        TextView tvEnderecoPartida = view.findViewById(R.id.rua_partida);
        TextView tvEnderecoDestino = view.findViewById(R.id.rua_destino);
        TextView tvNomePassageiro = view.findViewById(R.id.nome_passageiro);
        Button botaoConfirmarCorrida = view.findViewById(R.id.botao_confirmar_corrida);

        botaoConfirmarCorrida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btnIniciar = (Button) RequisicoesActivity.requisicoesActivity.findViewById(R.id.botao_iniciar);
                RequisicoesActivity.requisicoesActivity.abrirTelaCorrida(requisicao.getId(), true);
                btnIniciar.setVisibility(View.VISIBLE);
                getActivity().getSupportFragmentManager().beginTransaction().remove(CorridaFragment.this).commit();
            }
        });

        tvEnderecoPartida.setText(enderecoPartida());
        tvEnderecoDestino.setText(enderecoDestino());
        tvNomePassageiro.setText(requisicao.getPassageiro().getNomeCompleto());

        TextView tvNotaPassageiro = view.findViewById(R.id.rua_destino);

        ImageView fotoPassageiro = view.findViewById(R.id.foto_passageiro);

        Glide.with(context).load(requisicao.getPassageiro().getFotoPerfil()).apply(RequestOptions.circleCropTransform()).into(fotoPassageiro);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_corrida);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                mMap.clear(); //clear old markers

                try {
                    // Customise the styling of the base map using a JSON object defined
                    // in a raw resource file.
                    boolean success = mMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    context, R.raw.maps));

                    if (!success) {
                        Log.e("OnMapReady", "Style parsing failed.");
                    }
                } catch (Resources.NotFoundException e) {
                    Log.e("OnMapReady", "Can't find style. Error: ", e);
                }


                gps = new GPSTracker(context);
                if(gps.canGetLocation()){
                    gps.getLatitude(); // returns latitude
                    gps.getLongitude();
                }

                LatLng latLng = new LatLng(gps.getLatitude(), gps.getLongitude());
                LatLng latlngPassageiro = new LatLng(Double.parseDouble(requisicao.getPartida().getLatitude()), Double.parseDouble(requisicao.getPartida().getLongitude()));

                BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.marker_passageiro_true);
                Bitmap marker = bitmapdraw.getBitmap();
                Bitmap markerImage = Bitmap.createScaledBitmap(marker, 80, 80, false);

                mMap.addMarker(new MarkerOptions()
                        .position(latlngPassageiro)
                        .flat(true)
                        .icon(BitmapDescriptorFactory.fromBitmap(markerImage)));

                BitmapDrawable bitmapdrawMotorista = (BitmapDrawable)getResources().getDrawable(R.drawable.marker_driver);
                Bitmap markerMotorista = bitmapdrawMotorista.getBitmap();
                Bitmap markerImageMotorista = Bitmap.createScaledBitmap(markerMotorista, 80, 80, false);

                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .flat(true)
                        .icon(BitmapDescriptorFactory.fromBitmap(markerImageMotorista)));

                LatLngBounds latLngBounds = new LatLngBounds.Builder()
                        .include(latLng)
                        .include(latlngPassageiro)
                        .build();

                CameraUpdate camUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds, 10);
                mMap.animateCamera(camUpdate);
            }
        });

        ImageButton btnFechar = view.findViewById(R.id.botao_fechar);

        btnFechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btnIniciar = (Button) RequisicoesActivity.requisicoesActivity.findViewById(R.id.botao_iniciar);
                btnIniciar.setVisibility(View.VISIBLE);
                getActivity().getSupportFragmentManager().beginTransaction().remove(CorridaFragment.this).commit();
            }
        });

        return view;

    }

    private String valorCorrida() {
        String valor = new String();
        valor = requisicao.getPreco().replace(".", ",");
        return "R$ "  + valor;
    }

    private StringBuilder enderecoPartida() {
        StringBuilder enderecoPartida = new StringBuilder();

        if(requisicao.getPartida().getRua() != null) {
            enderecoPartida.append(requisicao.getPartida().getRua());
        }

        if(!requisicao.getPartida().getRua().equals(requisicao.getPartida().getNumero())) {
            if(requisicao.getPartida().getNumero() != null) {
                enderecoPartida.append(", " + requisicao.getPartida().getNumero());
            }
        }

        if(requisicao.getPartida().getBairro() != null) {
            enderecoPartida.append(", " + requisicao.getPartida().getBairro());
        }

        if(requisicao.getPartida().getCidade() != null) {
            enderecoPartida.append(", " + requisicao.getPartida().getCidade());
        }

        if(requisicao.getPartida().getCep() != null) {
            enderecoPartida.append(" - " + requisicao.getPartida().getCep());
        }

        return enderecoPartida;
    }

    private StringBuilder enderecoDestino() {
        StringBuilder enderecoDestino = new StringBuilder();

        if(requisicao.getDestino().getRua() != null) {
            enderecoDestino.append(requisicao.getDestino().getRua());
        }

        if(requisicao.getDestino().getNumero() != null) {
            enderecoDestino.append(", " + requisicao.getDestino().getNumero());
        }

        if(requisicao.getDestino().getBairro() != null) {
            enderecoDestino.append(", " + requisicao.getDestino().getBairro());
        }

        if(requisicao.getDestino().getCidade() != null) {
            enderecoDestino.append(", " + requisicao.getDestino().getCidade());
        }

        if(requisicao.getDestino().getCep() != null) {
            enderecoDestino.append(" - " + requisicao.getDestino().getCep());
        }

        return enderecoDestino;
    }

}
