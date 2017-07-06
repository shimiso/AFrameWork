package com.eshangke.framework.view;

import com.eshangke.framework.bean.Book;

import java.util.List;

/**
 * 类的说明：
 * 作者：shims
 * 创建时间：2016/11/23 0023 17:42
 */
public interface  BookListView {

    void loadMore(List<Book> books);

    void loadRefresh(List<Book> books);

    void loadFirst(List<Book> books);

    void loadError(Throwable e);
}
