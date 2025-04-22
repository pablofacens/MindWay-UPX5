package com.mindway.app.ui.mapa;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mindway.app.R;
import com.mindway.app.data.model.Parada;

import java.util.Collections;
import java.util.List;

public class ParadaAdapter extends RecyclerView.Adapter<ParadaAdapter.ViewHolder> {

    private List<Parada> paradas = Collections.emptyList();

    void setParadas(List<Parada> lista) {
        this.paradas = lista != null ? lista : Collections.emptyList();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_parada, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Parada p = paradas.get(position);
        Context ctx = h.itemView.getContext();

        h.nomeParada.setText(p.getNome() != null ? p.getNome() : "");
        h.linhaParada.setText(p.getLinha() != null ? p.getLinha() : "");

        // Ícone e cor da tag pelo tipo de transporte
        String tipo = p.getTipo() != null ? p.getTipo().toLowerCase() : "";
        int tagColorRes;
        if (tipo.contains("metro") || tipo.contains("metrô")) {
            h.icTransporte.setImageResource(android.R.drawable.ic_menu_compass);
            h.icTransporte.setBackgroundColor(ContextCompat.getColor(ctx, R.color.cor_secundaria));
            h.tagTipo.setText(R.string.tipo_metro);
            tagColorRes = R.color.cor_secundaria;
        } else if (tipo.contains("bike") || tipo.contains("bici")) {
            h.icTransporte.setImageResource(android.R.drawable.ic_menu_mylocation);
            h.icTransporte.setBackgroundColor(ContextCompat.getColor(ctx, R.color.cor_verde));
            h.tagTipo.setText(R.string.tipo_bike);
            tagColorRes = R.color.cor_verde;
        } else {
            h.icTransporte.setImageResource(android.R.drawable.ic_menu_directions);
            h.icTransporte.setBackgroundColor(ContextCompat.getColor(ctx, R.color.cor_destaque));
            h.tagTipo.setText(R.string.tipo_onibus);
            tagColorRes = R.color.cor_destaque;
        }
        // Reset background drawable each bind to preserve rounded corners, then recolor
        h.tagTipo.setBackgroundResource(R.drawable.handle_shape);
        ((GradientDrawable) h.tagTipo.getBackground().mutate())
                .setColor(ContextCompat.getColor(ctx, tagColorRes));
    }

    @Override
    public int getItemCount() {
        return paradas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView icTransporte;
        final TextView  nomeParada;
        final TextView  linhaParada;
        final TextView  tagTipo;

        ViewHolder(View v) {
            super(v);
            icTransporte = v.findViewById(R.id.ic_transporte);
            nomeParada   = v.findViewById(R.id.nome_parada);
            linhaParada  = v.findViewById(R.id.linha_parada);
            tagTipo      = v.findViewById(R.id.tag_tipo);
        }
    }
}
