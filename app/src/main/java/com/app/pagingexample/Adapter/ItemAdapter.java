package com.app.pagingexample.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pagingexample.R;
import com.app.pagingexample.Model.StackApiResponse;
import com.bumptech.glide.Glide;

public class ItemAdapter extends PagedListAdapter<StackApiResponse.Item,ItemAdapter.ItemViewHolder> {

    private Context mContext;
    public ItemAdapter(Context mContext) {
        super(diffCallback);
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ItemAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.rc_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ItemViewHolder holder, int position) {
        StackApiResponse.Item item = getItem(position);
        if (item != null) {
            holder.textView.setText(item.owner.display_name);
            Glide.with(mContext)
                    .load(item.owner.profile_image)
                    .into(holder.imageView);
        }else{
            Toast.makeText(mContext, "Item is null", Toast.LENGTH_LONG).show();
        }
    }

    private static  DiffUtil.ItemCallback<StackApiResponse.Item> diffCallback = new DiffUtil.ItemCallback<StackApiResponse.Item>() {
        @Override
        public boolean areItemsTheSame(@NonNull StackApiResponse.Item oldItem, @NonNull StackApiResponse.Item newItem) {
            return oldItem.answer_id == newItem.answer_id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull StackApiResponse.Item oldItem, @NonNull StackApiResponse.Item newItem) {
            return oldItem.equals(newItem);
        }
    };

    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView imageView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewName);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
