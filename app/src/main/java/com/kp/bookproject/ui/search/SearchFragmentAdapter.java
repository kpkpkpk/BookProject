package com.kp.bookproject.ui.search;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kp.bookproject.R;
import com.kp.bookproject.ui.RecyclerViewBooksAdapter;

import java.util.ArrayList;

public class SearchFragmentAdapter extends RecyclerView.Adapter<SearchFragmentAdapter.ViewHolder> {

private ArrayList<String> tagsString;
private Context context;
private SearchItemClickListener itemClickListener;

    public SearchFragmentAdapter(Context context,ArrayList<String> tagsString) {
        this.tagsString = tagsString;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.tags_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nametag.setText(tagsString.get(position));
    }

    @Override
    public int getItemCount() {
        return tagsString.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView nametag;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nametag=itemView.findViewById(R.id.tags_item_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(itemClickListener!=null)
                itemClickListener.onItemClick(tagsString.get(getAdapterPosition()));

        }
    }
    /**
     * Интерфейс создается для взаимодействия между адаптером и активити, в методе мы просто передаем id
     */
    public interface SearchItemClickListener{
        void onItemClick(String tagName);
    }

    /**
     *
     * @param mItemClickListener передается из Activity, в котором мы определяем поведение программы\
     */
    public void setSearchClickListener(SearchItemClickListener mItemClickListener) {
        itemClickListener = mItemClickListener;
    }
}
