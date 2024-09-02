//package com.damsdev.tbc;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.damsdev.tbc.databinding.ItemPasienBinding;
//import com.damsdev.tbc.model.PasienModel;
//import com.damsdev.tbc.model.RequestModel;
//
//import java.util.List;
//
//public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.Holder> {
//    private List<RequestModel> mListData;
//    private Context context;
//    private IPasienClick iPasienClick;
//    int lastPos = -1;
//
//    public RequestAdapter(List<RequestModel> mListData, Context context, IPasienClick iPasienClick) {
//        this.context = context;
//        this.mListData = mListData;
//        this.iPasienClick = iPasienClick;
//    }
//
//    @Override
//    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
//        return new Holder(ItemPasienBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
//    }
//
//    @Override
//    public void onBindViewHolder(Holder holder, int position) {
//        holder.setIsRecyclable(false);
//        final RequestModel model = mListData.get(position);
//        holder.binding.tvNama.setText(model.getNmPasien());
//        holder.binding.tvAlamat.setText(model.getAlamatPasien());
//        setAnimation(holder.itemView, position);
//        holder.binding.getRoot().setOnClickListener(view -> {
//            iPasienClick.onItemPasienClick(position);
//        });
//    }
//
//    private void setAnimation(View itemView, int position) {
//        if (position > lastPos) {
//            Animation animation= AnimationUtils.loadAnimation(context, R.anim.slide_in_left);
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
//    public static class Holder extends RecyclerView.ViewHolder {
//        private final ItemPasienBinding binding;
//
//        public Holder(ItemPasienBinding binding) {
//            super(binding.getRoot());
//            this.binding = binding;
//        }
//    }
//
//    public interface IPasienClick {
//        void onItemPasienClick(int position);
//    }
//}
