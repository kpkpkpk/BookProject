package com.kp.bookproject.ui.account;

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
import com.kp.bookproject.ui.RecyclerViewBooksAdapter;

import java.util.ArrayList;

public class LikedRecyclerViewAdapter extends RecyclerView.Adapter<LikedRecyclerViewAdapter.ViewHolder> {
    private ArrayList<Book> books;
    private final Context context;
    private LikedRecyclerViewAdapter.ItemClickListener itemClickListener;
    public LikedRecyclerViewAdapter(ArrayList<Book> books,Context context) {
        this.books = books;
        Log.d("checkn",books.isEmpty()+" recycler");
        this.context=context;
    }
    //Создаем наш view
    @NonNull
    @Override
    public LikedRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context)
                .inflate(R.layout.vertical_book_item,parent,false);
        Log.d("adapter","sure cre");
        return new LikedRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LikedRecyclerViewAdapter.ViewHolder holder, int position) {
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
            imageBook=itemView.findViewById(R.id.vertical_book_item_image);
            author=itemView.findViewById(R.id.vertical_book_item_author);
            bookName=itemView.findViewById(R.id.vertical_book_item_bookname);
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
    public void setClickListener(LikedRecyclerViewAdapter.ItemClickListener mItemClickListener) {
        itemClickListener = mItemClickListener;
    }

}
