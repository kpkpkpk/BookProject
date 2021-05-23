package com.kp.bookproject.ui.home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kp.bookproject.Entity.Book;
import com.kp.bookproject.Entity.News;
import com.kp.bookproject.R;
import com.kp.bookproject.ui.account.LikedRecyclerViewAdapter;

import java.util.ArrayList;

public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<NewsRecyclerViewAdapter.ViewHolder> {
    private ArrayList<News> news;
    private final Context context;
    private NewsRecyclerViewAdapter.ItemClickListener itemClickListener;
    public NewsRecyclerViewAdapter(ArrayList<News> news,Context context) {
        this.news = news;
        Log.d("checkn",news.isEmpty()+" recycler");
        this.context=context;
    }
    //Создаем наш view
    @NonNull
    @Override
    public NewsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context)
                .inflate(R.layout.news_horizontal_item,parent,false);
        Log.d("adapter","sure cre");
        return new NewsRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.sourceName.setText(news.get(position).getName());
        holder.description.setText(news.get(position).getDescription());
        Log.d("adap","sure cre");
    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    //создаем ViewHolder

    /*
     *
     * Ни в коем случае не делать static, иначе не получим доступ к позиции
     * Почему не используется static? Потому что мы конкретно понимаем, что дальше нашего адаптера никуда не уйдет ViewHolder,
     * нам важно сохранять ссылку на экземпляр внешнего класса, ибо без него холдер - пустышка :))
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView sourceName;
        TextView description;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sourceName=itemView.findViewById(R.id.news_horizontal_item_text_source);
            description=itemView.findViewById(R.id.news_horizontal_item_text_source_description);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            if(itemClickListener!=null) {
                itemClickListener.onItemClick(news.get(getAdapterPosition()).getUrl());
                Log.d("postiton", "onClick: "+getAdapterPosition());
            }
        }
    }

    /**
     * Интерфейс создается для взаимодействия между адаптером и активити, в методе мы просто передаем id
     */
    public interface ItemClickListener{
        void onItemClick(String url);
    }

    /**
     *
     * @param mItemClickListener передается из Activity, в котором мы определяем поведение программы\
     */
    public void setClickListener(NewsRecyclerViewAdapter.ItemClickListener mItemClickListener) {
        itemClickListener = mItemClickListener;
    }
}
