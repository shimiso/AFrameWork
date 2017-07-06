package com.eshangke.framework.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eshangke.framework.R;
import com.eshangke.framework.bean.Book;

import java.util.ArrayList;
import java.util.List;

/**
 * 存储示例的数据列表适配器
 * Created by g on 2016/1/26.
 */
public class SaveRecyclerAdapter extends RecyclerView.Adapter<SaveRecyclerAdapter.ViewHolder> {

    private Context context;
    private OnSetBookInfoListener onSetBookInfoListener;
    private boolean isShowId = true;
    private List<Book> data = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public void setData(List<Book> data) {
        this.data = (List<Book>) ((ArrayList<Book>) data).clone();
    }

    public void setShowId(boolean isShowId) {
        this.isShowId = isShowId;
    }

    public void setOnSetBookInfoListener(OnSetBookInfoListener onSetBookInfoListener) {
        this.onSetBookInfoListener = onSetBookInfoListener;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Book book = (Book) v.getTag();
            if (onSetBookInfoListener != null && book != null) {
                switch (v.getId()) {
                    case R.id.act_save_list_item_update_tv:
                        onSetBookInfoListener.onSetBookInfoListener(OnSetBookInfoListener.OPTION_TYPE_UPDATE, book);
                        break;
                    case R.id.act_save_list_item_delete_tv:
                        onSetBookInfoListener.onSetBookInfoListener(OnSetBookInfoListener.OPTION_TYPE_DELETE, book);
                        break;
                }
            } else {
                Log.e(SaveRecyclerAdapter.class.getSimpleName(), "onSetBookInfoListener error");
            }
        }
    };

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null)
            context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.sqlite_sample_list_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Book book = data.get(position);
        holder.updateButton.setTag(book);
        holder.deleteButton.setTag(book);
        String id = context.getString(R.string.book_id) + book.getId();
        String title = context.getString(R.string.book_title) + book.getTitle();
        String price = context.getString(R.string.book_price, book.getPrice());
        holder.id.setText(id);
        holder.title.setText(title);
        holder.price.setText(price);
        holder.id.setVisibility(isShowId ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView id;
        public TextView title;
        public TextView price;
        public TextView updateButton;
        public TextView deleteButton;

        public ViewHolder(View view) {
            super(view);
            id = (TextView) view.findViewById(R.id.act_save_list_item_id_tv);
            title = (TextView) view.findViewById(R.id.act_save_list_item_title_tv);
            price = (TextView) view.findViewById(R.id.act_save_list_item_price_tv);
            updateButton = (TextView) view.findViewById(R.id.act_save_list_item_update_tv);
            deleteButton = (TextView) view.findViewById(R.id.act_save_list_item_delete_tv);
            updateButton.setOnClickListener(onClickListener);
            deleteButton.setOnClickListener(onClickListener);
        }
    }

    public interface OnSetBookInfoListener {
        public static final int OPTION_TYPE_UPDATE = 1;
        public static final int OPTION_TYPE_DELETE = 2;

        public void onSetBookInfoListener(int optionType, Book book);
    }
}
