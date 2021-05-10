package com.kp.bookproject;


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

import java.util.ArrayList;

public class RecyclerViewBooksAdapter extends RecyclerView.Adapter<RecyclerViewBooksAdapter.ViewHolder> {
    private ArrayList<Book> books;
    private Context context;
    public RecyclerViewBooksAdapter(ArrayList<Book> books,Context context) {
        this.books = books;
        Log.d("checkn",books.isEmpty()+" ");
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
    public static class ViewHolder extends RecyclerView.ViewHolder {
         ImageView imageBook;
         TextView author;
         TextView bookName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageBook=itemView.findViewById(R.id.image_book_item);
            author=itemView.findViewById(R.id.book_author_name);
            bookName=itemView.findViewById(R.id.book_name_item);
        }

    }

}
