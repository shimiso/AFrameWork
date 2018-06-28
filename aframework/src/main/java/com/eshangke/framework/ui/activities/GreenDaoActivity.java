package com.eshangke.framework.ui.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.eshangke.framework.R;
import com.eshangke.framework.bean.Hobby;
import com.eshangke.framework.bean.User;
import com.eshangke.framework.gddb.CommonDialog;
import com.eshangke.framework.gddb.DbHelper;
import com.eshangke.framework.gddb.MySQLiteOpenHelper;
import com.eshangke.framework.gddb.RecycleViewDivider;
import com.eshangke.framework.gen.DaoMaster;
import com.eshangke.framework.gen.HobbyDao;
import com.eshangke.framework.gen.UserDao;
import com.eshangke.framework.ui.adapter.MyRecyclerAdapter;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GreenDaoActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    @BindView(R.id.xrecyclerview)
    XRecyclerView mRecyclerView;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_age)
    EditText etAge;
    @BindView(R.id.et_sex)
    EditText etSex;
    private UserDao dao;
    private HobbyDao ahdao;
    private MyRecyclerAdapter mAdapter;
    private List<User> list = new ArrayList<User>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_green_dao);
        //MigrationHelper.DEBUG = true; //如果你想查看日志信息，请将DEBUG设置为true
        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(this, "user",
                null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());

        ButterKnife.bind(this);
        //拿到要操作的对象
        dao = DbHelper.getInstance(this).getUserDao();
        ahdao = DbHelper.getInstance(this).getHobbyDao();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setLoadingMoreEnabled(false);
        mRecyclerView.setPullRefreshEnabled(false);
        mRecyclerView.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL));

        mAdapter = new MyRecyclerAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        //单击事件
        mAdapter.setOnRecyclerViewItemClickLintemet(new MyRecyclerAdapter.OnRecyclerViewItemClickLintemet() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(GreenDaoActivity.this, position + "单击", Toast.LENGTH_SHORT).show();
            }
        });
        //长按事件
        mAdapter.setOnRecyclerViewLongClickLintement(new MyRecyclerAdapter.OnRecyclerViewLongClickLintement() {
            @Override
            public void onLongClick(final int position) {
                final CommonDialog confirmDialog = new CommonDialog(GreenDaoActivity.this);
                confirmDialog.show();
                confirmDialog.setClicklistener(new CommonDialog.ClickListenerInterface() {
                    @Override
                    public void doConfirm() {
                        List<User> list = mAdapter.getList();//获取当前列表展示的数据
                        User p1 = new User();
                        p1.setId(list.get(position).getId());
                        dao.delete(p1);
                        List<User> lisi2 = dao.loadAll(); //查询全部数据
                        mAdapter.refreshRecyclerAdapter(lisi2);
                    }

                    @Override
                    public void doCancel() {
                    }
                });
            }
        });



    }

    @OnClick({R.id.button_add, R.id.button_del, R.id.button_que, R.id.button_upd, R.id.editor, R.id.batch_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_add:
                User user = new User();
                user.setName(etName.getText().toString().trim());
                user.setAge(etAge.getText().toString().trim());
                user.setSex(etSex.getText().toString().trim());
                long insert = dao.insert(user);
                Log.i(TAG, "插入了" + insert);
                List<User> lisi1 = dao.loadAll(); //查询全部数据
                mAdapter.refreshRecyclerAdapter(lisi1);

                etName.setText("");
                etAge.setText("");
                etSex.setText("");
                break;
            case R.id.button_del:
                //删除
                User p1 = new User();
                p1.setId(5L);
                dao.delete(p1);
                Log.i(TAG, "删除了");
                List<User> lisi2 = dao.loadAll(); //查询全部数据
                mAdapter.refreshRecyclerAdapter(lisi2);
                break;
            case R.id.editor:
                //编辑
                List<User> lisi4 = mAdapter.getList(); //获取当前展示的数据
                ArrayList<User> users = new ArrayList<>();
                for(int i=lisi4.size()-1;i>=0;i-- ){
                    if(lisi4.get(i).getCheck()){
                        User p = new User();
                        p.setId(lisi4.get(i).getId());
                        dao.delete(p);
                        lisi4.remove(i);
                    }
                }
                users.addAll(lisi4);
                mAdapter.refreshRecyclerAdapter(users);
                break;
            case R.id.batch_add:
                //批量插入

                for(int i = 0;i<5;i++){
                    User user5 = new User();
                    user5.setName("李白");
                    user5.setSex("男");
                    user5.setAge("22");
                    list.add(user5);
                }
                dao.insertOrReplaceInTx(list);
                list.clear();
                List<User> lis = dao.loadAll(); //查询全部数据
                mAdapter.refreshRecyclerAdapter(lis);
                break;
            case R.id.button_upd:
                //修改
                Hobby hobby = new Hobby();
                hobby.setAihao("Android");
                ahdao.insertOrReplace(hobby);
                User p2 = new User(1L, "lisi", "男", "24",false,1L);
                dao.update(p2);
                List<User> lisi3 = dao.loadAll(); //查询全部数据
                mAdapter.refreshRecyclerAdapter(lisi3);
                break;
            case R.id.button_que:
                List<User> lisi = dao.loadAll(); //查询全部数据
                String name = etName.getText().toString().trim();
                String age = etAge.getText().toString().trim();
                String sex = etSex.getText().toString().trim();
                //查询
                //selsct * from aa where _id=? and name=? , "2", kk

                if(name.isEmpty() && age.isEmpty() && sex.isEmpty()){
                    mAdapter.refreshRecyclerAdapter(lisi);
                    if (lisi == null && lisi.size() == 0) {
                        Log.i(TAG, "没有数据，去添加数据吧!");
                    }
                    for (int i = 0; i < lisi.size(); i++) {
                        Log.i(TAG, "数据：" + lisi.get(i).toString());
                    }
                }else{
                    List<User> User = dao.queryRaw("where name=? ",name.isEmpty()?" ":name);
                    mAdapter.refreshRecyclerAdapter(User);
                    if (User == null && User.size() == 0) {
                        Log.i(TAG, "没有数据，去添加数据吧!");
                    }
                    for (int i = 0; i < User.size(); i++) {
                        Log.i(TAG, "数据：" + lisi.get(i).toString());
                    }
                }
                break;
        }
    }
}
