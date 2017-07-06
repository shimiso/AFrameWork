
package com.eshangke.framework.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.eshangke.framework.R;
import com.eshangke.framework.bean.Book;
import com.eshangke.framework.util.AndroidUtil;

import java.util.ArrayList;
import java.util.List;

public class BooksRecyclerAdapter extends RecyclerView.Adapter<BooksRecyclerAdapter.ViewHolder> {
    private final int mBackground;
    private List<Book> mBooks = new ArrayList<Book>();
    private final TypedValue mTypedValue = new TypedValue();

    private static final int ANIMATED_ITEMS_COUNT = 4;
    private Context context;
    private boolean animateItems = false;
    private int lastAnimatedPosition = -1;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public BooksRecyclerAdapter(Context context) {
        this.context = context;
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView ivBook;
        public TextView tvTitle;
        public TextView tvDesc;

        public int position;

        public ViewHolder(View v) {
            super(v);
            ivBook = (ImageView) v.findViewById(R.id.ivBook);
            tvTitle = (TextView) v.findViewById(R.id.tvTitle);
            tvDesc = (TextView) v.findViewById(R.id.tvDesc);
        }
    }


    private void runEnterAnimation(View view, int position) {
        if (!animateItems || position >= ANIMATED_ITEMS_COUNT - 1) {
            return;
        }

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            view.setTranslationY(AndroidUtil.getScreenHeight(context));
            view.animate()
                    .translationY(0)
                    .setStartDelay(100 * position)
                    .setInterpolator(new DecelerateInterpolator(3.f))
                    .setDuration(700)
                    .start();
        }
    }
    /**
     * 刷新数据
     *
     * @param books
     */
    public void onRefresh(List<Book> books, boolean animated) {
        if (null != mBooks) {
            mBooks.clear();
        }
        animateItems = animated;
        lastAnimatedPosition = -1;
        mBooks.addAll(books);
        this.notifyDataSetChanged();
    }

    /**
     * 加载更多数据
     *
     * @param books
     */
    public void onLoadMore(List<Book> books, boolean animated) {
        animateItems = animated;
        lastAnimatedPosition = -1;
        mBooks.addAll(books);
        this.notifyDataSetChanged();
    }

    @Override
    public BooksRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_item, parent, false);
        //v.setBackgroundResource(mBackground);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        runEnterAnimation(holder.itemView, position);
        final Book book = mBooks.get(position);
        holder.tvTitle.setText(book.getTitle());
        String desc = "作者: " + book.getAuthor()[0] + "\n副标题: " + book.getSubtitle()
                + "\n出版年: " + book.getPubdate() + "\n页数: " + book.getPages() + "\n定价:" + book.getPrice();
        holder.tvDesc.setText(desc);
        Glide.with(holder.ivBook.getContext())
                .load(book.getImage())
                .fitCenter()
                .into(holder.ivBook);
        holder.itemView.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v) {
               onItemClickListener.onItemClick(v,position);
           }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mBooks.size();
    }


    public Book getBook(int pos) {
        return mBooks.get(pos);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
