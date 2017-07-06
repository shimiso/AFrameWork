package com.eshangke.framework.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.eshangke.framework.R;
import com.eshangke.framework.bean.Book;
import com.eshangke.framework.ui.activities.BookDetailActivity;

import java.util.List;

/**
 *Book适配器
 */
public class BookListAdapter extends BaseAdapter {

    private Context mContext;
    private List<Book> caseList;
    private LayoutInflater mLayoutInflater;

    public BookListAdapter(Context context) {
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    private   View.OnClickListener onItemClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Book book = (Book) v.findViewById(R.id.tvTitle).getTag();
            Intent intent = new Intent();
            intent.setClass(mContext, BookDetailActivity.class);
            intent.putExtra("book", book);
            mContext.startActivity(intent);
        }
    };

    /**
     * 刷新数据
     *
     * @param list
     */
    public void onRefresh(List<Book> list) {
        if (null != this.caseList) {
            this.caseList.clear();
        }
        this.caseList = list;
        this.notifyDataSetChanged();
    }

    /**
     * 加载更多数据
     *
     * @param list
     */
    public void onLoadMore(List<Book> list) {
        caseList.addAll(list);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return caseList == null ? 0 : caseList.size();
    }

    @Override
    public Object getItem(int position) {
        return caseList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Book book = caseList.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.book_item, null);
            holder.ivBook = (ImageView) convertView.findViewById(R.id.ivBook);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            holder.tvDesc = (TextView) convertView.findViewById(R.id.tvDesc);
            convertView.setTag(holder);
            convertView.setOnClickListener(onItemClickListener);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvTitle.setText(book.getTitle());
        holder.tvTitle.setTag(book);
        String desc = "作者: " + book.getAuthor()[0] + "\n副标题: " + book.getSubtitle()
                + "\n出版年: " + book.getPubdate() + "\n页数: " + book.getPages() + "\n定价:" + book.getPrice();
        holder.tvDesc.setText(desc);
        Glide.with(holder.ivBook.getContext())
                .load(book.getImage())
                .fitCenter()
                .into(holder.ivBook);

        return convertView;
    }

    public final class ViewHolder {
        //书缩略图
        public ImageView ivBook;
        //标题
        public TextView tvTitle;
        //描述
        public TextView tvDesc;
    }

}
