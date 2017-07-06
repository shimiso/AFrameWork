package com.eshangke.framework.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eshangke.framework.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 类的说明:xlistview示例界面的顶部分类筛选适配器
 * 作者: caoyulong
 * 创建时间: 2016/2/19 9:36
 */
public class BookListClassifyAdapter extends BaseAdapter {

    public final static int TYPE_PERIOD = 1;
    public final static int TYPE_GRADE = 2;

    private Context context;
    private List<String> list = new ArrayList<>();
    private int checkItemPosition = -1;
    private int type = TYPE_PERIOD;

    public BookListClassifyAdapter(int type) {
        this.type = type;
    }

    public void setData(List<String> data) {
        list.clear();
        list.addAll(data);
    }

    public void setCheckItem(int position) {
        checkItemPosition = position;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            if (context == null)
                context = parent.getContext();
            convertView = LayoutInflater.from(context).inflate(R.layout.book_list_item_list_drop_down, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        fillValue(position, viewHolder);
        return convertView;
    }

    private void fillValue(int position, ViewHolder viewHolder) {
        viewHolder.mText.setText(list.get(position));
        if (checkItemPosition != -1) {
            if (checkItemPosition == position) {
                viewHolder.mText.setTextColor(context.getResources().getColor(R.color.drop_down_selected));
                if (position == 0 || type == TYPE_GRADE)
                    viewHolder.mText.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.drop_down_checked), null);
            } else {
                viewHolder.mText.setTextColor(context.getResources().getColor(R.color.drop_down_unselected));
                if (position == 0 || type == TYPE_GRADE)
                    viewHolder.mText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            }
        }else{
            viewHolder.mText.setTextColor(context.getResources().getColor(R.color.drop_down_unselected));
            viewHolder.mText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
    }

    static class ViewHolder {
        TextView mText;

        ViewHolder(View view) {
            mText = (TextView) view.findViewById(R.id.text);
        }
    }
}
