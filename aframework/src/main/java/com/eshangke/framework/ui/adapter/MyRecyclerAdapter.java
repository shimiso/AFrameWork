package com.eshangke.framework.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.eshangke.framework.R;
import com.eshangke.framework.bean.Hobby;
import com.eshangke.framework.bean.User;
import com.eshangke.framework.gddb.DbHelper;
import com.eshangke.framework.gen.UserDao;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by huochangsheng on 2018/6/20.
 */

public class MyRecyclerAdapter extends  RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {

    //定义一个接口点击事件
    public interface OnRecyclerViewItemClickLintemet{
        void onItemClick(int position);
    }
    //定义接口对象         单击事件
    private OnRecyclerViewItemClickLintemet listener;
    //定义接口方法         单击事件
    public void setOnRecyclerViewItemClickLintemet(OnRecyclerViewItemClickLintemet listener){

        this.listener=listener;
    };
    //================================================================================================
    //定义一个接口的长按事件
    public interface OnRecyclerViewLongClickLintement{
        void onLongClick(int position);
    }
    //定义接口方法  长按事件
    private OnRecyclerViewLongClickLintement longistener;

    //定义接口方法    长按事件
    public void setOnRecyclerViewLongClickLintement(OnRecyclerViewLongClickLintement longistener){
        this.longistener=longistener;
    }

    private List<User> user = new ArrayList<>();
    private Context context;

    public MyRecyclerAdapter(Context context) {
        this.context = context;
    }
    public void refreshRecyclerAdapter(List<User> users) {
        if(this.user != null){
            //清理集合的时候如果当前集合与传入的集合在栈中地址一样会将其清空导致展示数据的时候没有可展示的数据
            this.user.clear();
        }
        this.user = users;
        notifyDataSetChanged();
    }
    public List<User> getList() {
        return this.user;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //上面的方式会导致宽高设置无效
       // View v = View.inflate(context, R.layout.item_news, null);
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        //条目单击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(position);
            }
        });
        //条目长按事件
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                longistener.onLongClick(position);
                return true;
            }
        });
        Long id = user.get(position).getId();
        String name = user.get(position).getName();
        boolean check = user.get(position).getCheck();
        Hobby hobby = user.get(position).getHobby();


        holder.id_tv.setText("ID:"+user.get(position).getId());
        holder.name_tv.setText("姓名："+user.get(position).getName());
        holder.age_tv.setText("年龄："+user.get(position).getAge());
        holder.sex_tv.setText("性别："+user.get(position).getSex());
        if(hobby != null){
            holder.hobby_tv.setText("爱好："+hobby.getAihao());
        }else{
            holder.hobby_tv.setText("爱好：无");
        }

        holder.check.setChecked(user.get(position).getCheck());

        holder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //拿到要操作的对象
                UserDao dao = DbHelper.getInstance(context).getUserDao();
                List<User> users = dao.queryRaw("where _id=?", user.get(position).getId() + "");
                if(users != null && users.size() != 0){
                    User user = users.get(0);
                    user.setCheck(b);
                    dao.update(user);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (user == null) {
            return 0;
        }
        return user.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView id_tv;
        public TextView name_tv;
        public TextView age_tv;
        public TextView sex_tv;
        public TextView hobby_tv;
        public CheckBox check;
        public ViewHolder(View itemView) {
            super(itemView);
            id_tv = (TextView) itemView.findViewById(R.id.id_tv);
            name_tv = (TextView) itemView.findViewById(R.id.name_tv);
            age_tv = (TextView) itemView.findViewById(R.id.age_tv);
            sex_tv = (TextView) itemView.findViewById(R.id.sex_tv);
            check = (CheckBox) itemView.findViewById(R.id.check);
            hobby_tv = (TextView) itemView.findViewById(R.id.hobby_tv);

        }
    }
    @Override
    public int getItemViewType(int position) {
        //重写此方法可以防止数据展示错乱问题
        return position;
    }
}
