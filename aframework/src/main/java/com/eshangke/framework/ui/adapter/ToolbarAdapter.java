package com.eshangke.framework.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eshangke.framework.R;

/**
 * 类的说明：
 * 作者：shims
 * 创建时间：2016/11/7 0007 10:54
 */
public class ToolbarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;

    public ToolbarAdapter(Context context){
        this.context=context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==0){
           return new ItemView(LayoutInflater.from(context).inflate(R.layout.coordinator_layout_head2,null));
        }else{
           return new ItemView(LayoutInflater.from(context).inflate(R.layout.coordinator_layout_item,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return 0;
        }else{
            return 1;
        }
    }

    class ItemView extends RecyclerView.ViewHolder{
        public ItemView(View itemView) {
            super(itemView);
        }
    }

}
