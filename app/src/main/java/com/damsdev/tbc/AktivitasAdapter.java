//package com.damsdev.tbc;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.damsdev.tbc.databinding.ItemAktivitasBinding;
//import com.damsdev.tbc.model.AktivitasModel;
//
//import java.util.List;
//
//public class AktivitasAdapter extends RecyclerView.Adapter<AktivitasAdapter.Holder> {
//    int lastPos = -1;
//    private List<AktivitasModel> mListData;
//    private Context context;
//    private IAktivitasClick iAktivitasClick;
//
//    public AktivitasAdapter(List<AktivitasModel> mListData, Context context, IAktivitasClick iAktivitasClick) {
//        this.context = context;
//        this.mListData = mListData;
//        this.iAktivitasClick = iAktivitasClick;
//    }
//
//    @Override
//    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
//        return new Holder(ItemAktivitasBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
//    }
//
//    @SuppressLint("SetTextI18n")
//    @Override
//    public void onBindViewHolder(Holder holder, int position) {
//        holder.setIsRecyclable(false);
//        final AktivitasModel model = mListData.get(position);
//        holder.binding.tvTgl.setText(model.getTglMulai() + " s/d " + model.getTglSelesai());
//        setAnimation(holder.itemView, position);
//        holder.binding.getRoot().setOnClickListener(view -> {
//            iAktivitasClick.onItemAktivitasClick(position);
//        });
//    }
//
//    private void setAnimation(View itemView, int position) {
//        if (position > lastPos) {
//            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_left);
//            itemView.setAnimation(animation);
//            lastPos = position;
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return mListData.size();
//    }
//
//    public interface IAktivitasClick {
//        void onItemAktivitasClick(int position);
//    }
//
//    public static class Holder extends RecyclerView.ViewHolder {
//        private final ItemAktivitasBinding binding;
//
//        public Holder(ItemAktivitasBinding binding) {
//            super(binding.getRoot());
//            this.binding = binding;
//        }
//    }
//}
