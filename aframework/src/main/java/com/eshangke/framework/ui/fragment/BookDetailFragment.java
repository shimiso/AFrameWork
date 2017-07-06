package com.eshangke.framework.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eshangke.framework.R;


/**
 * Created by Chenyc on 2015/6/29.
 */
public class BookDetailFragment extends Fragment {

    public static BookDetailFragment newInstance(String info) {
        Bundle args = new Bundle();
        BookDetailFragment fragment = new BookDetailFragment();
        args.putString("info", info);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.book_detail, null);
        TextView tvInfo = (TextView) view.findViewById(R.id.tvInfo);
        tvInfo.setText(getArguments().getString("info"));
        return view;
    }
}
