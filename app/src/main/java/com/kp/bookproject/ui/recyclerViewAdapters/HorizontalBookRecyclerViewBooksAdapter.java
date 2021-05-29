package com.kp.bookproject.ui.recyclerViewAdapters;


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
import com.kp.bookproject.R;

import java.util.ArrayList;

// Вынес класс из одного ui, ибо понял, что он будет использоваться и на новой активити при выборе тега на вкладке Search

public class HorizontalBookRecyclerViewBooksAdapter extends RecyclerView.Adapter<HorizontalBookRecyclerViewBooksAdapter.ViewHolder> {
    private ArrayList<Book> books;
    private final Context context;
    private  ItemClickListener itemClickListener;
    public HorizontalBookRecyclerViewBooksAdapter(ArrayList<Book> books, Context context) {
        this.books = books;
        Log.d("checkn",books.isEmpty()+" recycler");
        this.context=context;
    }
//Создаем наш view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(context)
               .inflate(R.layout.horizontal_book_item,parent,false);
       Log.d("adapter","sure cre");
       return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bookName.setText(books.get(position).getBook_name());
        holder.author.setText(books.get(position).getAuthorName());
        Log.d("adap","sure cre");
        Glide.with(context).load(books.get(position).getImage_url()).into(holder.imageBook);
    }
//к-во элементов на recyclerview(примерно 10)
    @Override
    public int getItemCount() {
      return books.size();
    }

    //создаем ViewHolder

    /*
     *
     * Ни в коем случае не делать static, иначе не получим доступ к позиции
     * Почему не используется static? Потому что мы конкретно понимаем, что дальше нашего адаптера никуда не уйдет ViewHolder,
     * нам важно сохранять ссылку на экземпляр внешнего класса, ибо без него холдер - пустышка :))
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
         ImageView imageBook;
         TextView author;
         TextView bookName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageBook=itemView.findViewById(R.id.image_book_item);
            author=itemView.findViewById(R.id.book_author_name);
            bookName=itemView.findViewById(R.id.book_name_item);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            if(itemClickListener!=null) {
                itemClickListener.onItemClick(books.get(getAdapterPosition()).getId());
                Log.d("postiton", "onClick: "+getAdapterPosition());
            }
        }
    }

    /**
     * Интерфейс создается для взаимодействия между адаптером и активити, в методе мы просто передаем id
     */
    public interface ItemClickListener{
        void onItemClick(int id);
    }

    /**
     *
     * @param mItemClickListener передается из Activity, в котором мы определяем поведение программы\
     */
    public void setClickListener(ItemClickListener mItemClickListener) {
        itemClickListener = mItemClickListener;
    }

}
