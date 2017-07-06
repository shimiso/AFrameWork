package com.eshangke.framework.ui.activities;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.eshangke.framework.R;
import com.eshangke.framework.ui.adapter.SaveRecyclerAdapter;
import com.eshangke.framework.bean.Book;
import com.eshangke.framework.presenter.BookPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * 存储示例界面
 * Created by g on 2016/1/26.
 */
public class SaveActivity extends BaseActivity implements View.OnClickListener {

    private static final String IS_SHOW_ID = "is_show_id";

    private TextView bookEditTitle;
    private EditText bookTitleEdit;
    private EditText bookPriceEdit;
    private Button addBookButton;
    private Button cancelUpdateBookButton;
    private Switch idSwitch;
    private TextView noDataView;
    private RecyclerView listView;
    private SaveRecyclerAdapter adapter;

    private BookPresenter presenter;
    private boolean isShowId = true;
    private boolean isAddBook = true;

    private List<Book> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sqlite_sample);
        isShowId = spUtil.getBoolean(IS_SHOW_ID, true);
        presenter = new BookPresenter(this);
        initView();
        getData();
    }

    private void initView() {
        //初始化标题部分
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getTitle());
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        //初始化数据部分
        bookEditTitle = (TextView) findViewById(R.id.act_save_input_book_info_tv);
        bookTitleEdit = (EditText) findViewById(R.id.act_save_title_et);
        bookPriceEdit = (EditText) findViewById(R.id.act_save_price_et);
        addBookButton = (Button) findViewById(R.id.act_save_add_button);
        cancelUpdateBookButton = (Button) findViewById(R.id.act_save_cancel_update_button);
        addBookButton.setOnClickListener(this);
        cancelUpdateBookButton.setOnClickListener(this);
        idSwitch = (Switch) findViewById(R.id.act_save_show_book_switch);
        idSwitch.setChecked(isShowId);
        idSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isShowId = isChecked;
            spUtil.setBoolean(IS_SHOW_ID, isShowId);
            refreshListView();
        });
        noDataView = (TextView) findViewById(R.id.act_save_no_data_tv);
        listView = (RecyclerView) findViewById(R.id.act_save_data_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(layoutManager);
        listView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        adapter = new SaveRecyclerAdapter();
        adapter.setOnSetBookInfoListener(onSetBookInfoListener);
        listView.setAdapter(adapter);
    }

    private void getData() {
        List<Book> books = presenter.getLocalBooks();
        if (books != null && books.size() > 0)
            data.addAll(books);
        refreshListView();
    }

    private SaveRecyclerAdapter.OnSetBookInfoListener onSetBookInfoListener = new SaveRecyclerAdapter.OnSetBookInfoListener() {

        @Override
        public void onSetBookInfoListener(int optionType, Book book) {
            switch (optionType) {
                case SaveRecyclerAdapter.OnSetBookInfoListener.OPTION_TYPE_UPDATE:
                    isAddBook = false;
                    bookEditTitle.setText(getString(R.string.edit_book_info, book.getId()));
                    bookTitleEdit.setText(book.getTitle());
                    bookPriceEdit.setText(book.getPrice());
                    idSwitch.setVisibility(View.GONE);
                    listView.setVisibility(View.GONE);
                    cancelUpdateBookButton.setVisibility(View.VISIBLE);
                    addBookButton.setText(R.string.update_book);
                    addBookButton.setTag(book);
                    break;
                case SaveRecyclerAdapter.OnSetBookInfoListener.OPTION_TYPE_DELETE:
                    presenter.deleteLocalBook(book);
                    data.remove(book);
                    refreshListView();
                    break;
            }
        }
    };


    private void refreshListView() {
        if (data.size() > 0) {
            noDataView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            adapter.setData(data);
            adapter.setShowId(isShowId);
            adapter.notifyDataSetChanged();
        } else {
            noDataView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.act_save_cancel_update_button:
                isAddBook = true;
                idSwitch.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);
                bookEditTitle.setText(R.string.input_book_info);
                cancelUpdateBookButton.setVisibility(View.GONE);
                addBookButton.setTag(null);
                addBookButton.setText(R.string.add_book);
                bookTitleEdit.setText("");
                bookPriceEdit.setText("");
                hideSoftKeyboard();
                break;
            case R.id.act_save_add_button:
                String bookTitle = bookTitleEdit.getText().toString().trim();
                String bookPrice = bookPriceEdit.getText().toString().trim();
                if (TextUtils.isEmpty(bookTitle) || TextUtils.isEmpty(bookPrice)) {
                    Toast.makeText(this, getString(R.string.add_book_warn), Toast.LENGTH_SHORT).show();
                } else {
                    if (isAddBook) {
                        Book book = new Book();
                        book.setTitle(bookTitle);
                        book.setPrice(bookPrice);
                        long id = presenter.addLocalBook(book);
                        book.setId("" + id);
                        data.add(book);
                    } else {
                        Book book = (Book) addBookButton.getTag();
                        if (book != null) {
                            isAddBook = true;
                            addBookButton.setTag(null);
                            book.setTitle(bookTitle);
                            book.setPrice(bookPrice);
                            presenter.updateLocalBook(book);
                            idSwitch.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.VISIBLE);
                            cancelUpdateBookButton.setVisibility(View.GONE);
                            bookEditTitle.setText(R.string.input_book_info);
                            addBookButton.setText(R.string.add_book);
                        } else {
                            Toast.makeText(this, "update book info error,book is null", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    refreshListView();
                    bookTitleEdit.setText("");
                    bookPriceEdit.setText("");
                    hideSoftKeyboard();
                }
                break;
        }

    }

    @SuppressWarnings("deprecation")
    static class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private static final int[] ATTRS = new int[]{
                android.R.attr.listDivider
        };

        public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

        public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

        private Drawable mDivider;

        private int mOrientation;

        public DividerItemDecoration(Context context, int orientation) {
            final TypedArray a = context.obtainStyledAttributes(ATTRS);
            mDivider = a.getDrawable(0);
            if (mDivider == null)
                mDivider = context.getResources().getDrawable(android.R.color.transparent);
            a.recycle();
            setOrientation(orientation);
        }

        public void setOrientation(int orientation) {
            if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
                throw new IllegalArgumentException("invalid orientation");
            }
            mOrientation = orientation;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent) {
            if (mOrientation == VERTICAL_LIST) {
                drawVertical(c, parent);
            } else {
                drawHorizontal(c, parent);
            }

        }

        public void drawVertical(Canvas c, RecyclerView parent) {
            final int left = parent.getPaddingLeft();
            final int right = parent.getWidth() - parent.getPaddingRight();

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int top = child.getBottom() + params.bottomMargin;
                final int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }

        public void drawHorizontal(Canvas c, RecyclerView parent) {
            final int top = parent.getPaddingTop();
            final int bottom = parent.getHeight() - parent.getPaddingBottom();

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int left = child.getRight() + params.rightMargin;
                final int right = left + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
            if (mOrientation == VERTICAL_LIST) {
                outRect.set(0, 0, 0, 20 + mDivider.getIntrinsicHeight());
            } else {
                outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
            }
        }
    }

    private void hideSoftKeyboard() {
        View currentFocus = getCurrentFocus();
        if (currentFocus != null)
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(currentFocus
                                    .getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
    }

}