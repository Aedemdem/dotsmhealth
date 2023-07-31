package com.damsdev.tbc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.recyclerview.widget.RecyclerView;

import com.damsdev.tbc.databinding.ItemNakesBinding;
import com.damsdev.tbc.model.NakesModel;

import java.util.List;

public class NakesAdapter extends RecyclerView.Adapter<NakesAdapter.Holder> {
    private List<NakesModel> mListData;
    private Context context;
    private INakesClick iNakesClick;
    int lastPos = -1;

    public NakesAdapter(List<NakesModel> mListData, Context context, INakesClick iNakesClick) {
        this.context = context;
        this.mListData = mListData;
        this.iNakesClick = iNakesClick;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(ItemNakesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.setIsRecyclable(false);
        final NakesModel model = mListData.get(position);
        holder.binding.tvNama.setText(model.getNama());
        holder.binding.tvAlamat.setText(model.getAlamat());
        setAnimation(holder.itemView, position);
        holder.binding.btnTambahkan.setOnClickListener(view -> {
            iNakesClick.onItemNakesClick(position);
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
        private final ItemNakesBinding binding;

        public Holder(ItemNakesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface INakesClick {
        void onItemNakesClick(int position);
    }
}
