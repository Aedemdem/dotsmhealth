package com.damsdev.tbc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.recyclerview.widget.RecyclerView;

import com.damsdev.tbc.databinding.ItemAktivitasBinding;
import com.damsdev.tbc.databinding.ItemDetailAktivitasBinding;
import com.damsdev.tbc.model.AktivitasDetailModel;
import com.damsdev.tbc.model.AktivitasModel;

import java.util.List;

public class AktivitasDetailAdapter extends RecyclerView.Adapter<AktivitasDetailAdapter.Holder> {
    private List<AktivitasDetailModel> mListData;
    private Context context;
    private IAktivitasDetailClick iAktivitasClick;
    int lastPos = -1;

    public AktivitasDetailAdapter(List<AktivitasDetailModel> mListData, Context context, IAktivitasDetailClick iAktivitasClick) {
        this.context = context;
        this.mListData = mListData;
        this.iAktivitasClick = iAktivitasClick;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(ItemDetailAktivitasBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.setIsRecyclable(false);
        final AktivitasDetailModel model = mListData.get(position);
        holder.binding.tvTgl.setText(model.getTgl());
        setAnimation(holder.itemView, position);
        holder.binding.getRoot().setOnClickListener(view -> {
            iAktivitasClick.onItemDetailAktivitasClick(position);
        });
    }

    private void setAnimation(View itemView, int position) {
        if (position > lastPos) {
            Animation animation= AnimationUtils.loadAnimation(context, R.anim.slide_in_left);
            itemView.setAnimation(animation);
            lastPos = position;
        }
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        private final ItemDetailAktivitasBinding binding;

        public Holder(ItemDetailAktivitasBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface IAktivitasDetailClick {
        void onItemDetailAktivitasClick(int position);
    }
}
