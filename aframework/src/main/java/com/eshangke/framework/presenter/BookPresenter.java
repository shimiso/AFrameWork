package com.eshangke.framework.presenter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.eshangke.framework.callback.JsonCallback;
import com.eshangke.framework.db.DBManager;
import com.eshangke.framework.db.SQLiteTemplate;
import com.eshangke.framework.bean.Book;
import com.eshangke.framework.bean.ResultData;
import com.eshangke.framework.util.HttpUtil;
import com.eshangke.framework.view.BookListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;


public class BookPresenter {
    //首次加载
    private final int LOAD_FIRST_MODEL = 0;
    //下拉刷新
    private final int LOAD_REFRESH_MODEL = 1;
    //上拉加载更多
    private final int LOAD_MORE_MODEL = 2;

    BookListView bookListView;

    private SQLiteTemplate template;

    public BookPresenter() {
    }

    public BookPresenter(BookListView bookListView) {
        this.bookListView = bookListView;
    }

    public BookPresenter(Context context) {
        DBManager dbManager = DBManager.create(context);
        template = SQLiteTemplate.getInstance(dbManager);
    }

    public void getBookList(final int model, int currentPage, int pageSize){
        JsonCallback jsonCallback=   new JsonCallback<ResultData<List<Book>>>(){

            @Override
            public void onSuccess(com.lzy.okgo.model.Response<ResultData<List<Book>>> result) {
                if (model == LOAD_FIRST_MODEL) {
                    bookListView.loadFirst(result.body().books);
                } else if (model == LOAD_REFRESH_MODEL) {
                    bookListView.loadRefresh(result.body().books);
                } else if (model == LOAD_MORE_MODEL) {
                    bookListView.loadMore(result.body().books);
                }
            }

            @Override
            public void onError(com.lzy.okgo.model.Response<ResultData<List<Book>>> response) {
                super.onError(response);
                bookListView.loadError(response.getException());
            }
        };

        OkGo.get("https://api.douban.com/v2/book/search")//
                .tag(this)//
                .params("q", "三国")//
                .params("start", currentPage)//
                .params("count", pageSize)//
                .execute(jsonCallback);

    }


    public interface OnSearchListener {
        void onData(List<Book> data);

        void onError(Throwable ex);
    }

    /**
     * 通过Xutils联网获取数据
     *
     * @param name
     * @param start
     * @param count
     * @param onSearchListener
     */
    public void searchBooks(String name, int start, int count, final OnSearchListener onSearchListener) {
        RequestParams params = new RequestParams("https://api.douban.com/v2/book/search");
        params.addParameter("q", name);
        params.addParameter("start", start);
        params.addParameter("count", count);
        params.setUseCookie(false);
        HttpUtil.getInstance().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                List<Book> books = parseBookList(result);
                onSearchListener.onData(books);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                onSearchListener.onError(ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 将Gson数据解析为Book数组
     *
     * @param result
     * @return
     */
    private List<Book> parseBookList(String result) {
        if (result != null) {
            Log.i("TAG", result.toString());
        } else {
            return null;
        }
        List<Book> books = null;
        Gson gson = new Gson();
        try {
            JSONObject json = new JSONObject(result);
            JSONArray jaBooks = json.optJSONArray("books");
            books = gson.fromJson(jaBooks.toString(), new TypeToken<List<Book>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();

        }
        return books;
    }


    public List<Book> getLocalBooks() {
        return template.queryForList(new SQLiteTemplate.RowMapper<Book>() {

            @Override
            public Book mapRow(Cursor cursor, int index) {
                Book book = new Book();
                book.setId(cursor.getString(cursor
                        .getColumnIndex("_id")));
                book.setTitle(cursor.getString(cursor
                        .getColumnIndex("title")));
                book.setPrice(cursor.getString(cursor
                        .getColumnIndex("price")));
                return book;
            }
        }, "SELECT * FROM book", null);
    }

    public long addLocalBook(Book book) {
        ContentValues values = new ContentValues();
        values.put("title", book.getTitle());
        values.put("price", book.getPrice());
        return template.insert("book", values);
    }

    public long updateLocalBook(Book book) {
        ContentValues values = new ContentValues();
        values.put("title", book.getTitle());
        values.put("price", book.getPrice());
        return template.updateById("book", book.getId(), values);
    }

    public long deleteLocalBook(Book book) {
        return template.deleteById("book", book.getId());
    }


    /**
     * 通过get方法获取图书数据
     *
     * @param name
     * @param start
     * @param count
     * @param onSearchListener
     */
    public void requestBooksByGet(String name, int start, int count, final OnSearchListener onSearchListener) {
        RequestParams params = new RequestParams("https://api.douban.com/v2/book/search");
        params.addParameter("q", name);
        params.addParameter("start", start);
        params.addParameter("count", count);
        params.setUseCookie(false);
        HttpUtil.getInstance().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                List<Book> books = parseBookList(result);
                onSearchListener.onData(books);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                onSearchListener.onError(ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 通过get方法获取图书数据
     *
     * @param name
     * @param start
     * @param count
     * @param onSearchListener
     */
    public void requestBooksByPost(String name, int start, int count, final OnSearchListener onSearchListener) {
        RequestParams params = new RequestParams("https://api.douban.com/v2/book/search");
        params.addParameter("q", name);
        params.addParameter("start", start);
        params.addParameter("count", count);
        params.setUseCookie(false);
        HttpUtil.getInstance().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                List<Book> books = parseBookList(result);
                onSearchListener.onData(books);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                onSearchListener.onError(ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
}
