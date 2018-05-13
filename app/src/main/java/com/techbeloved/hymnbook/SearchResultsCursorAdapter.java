package com.techbeloved.hymnbook;

import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techbeloved.hymnbook.R;

/**
 * Created by kennedy on 5/13/18.
 */

public class SearchResultsCursorAdapter extends CursorAdapter {

    public SearchResultsCursorAdapter(Context context, Cursor c) {
        super(context, c, false);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.search_result_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvHymnNo = view.findViewById(R.id.hymn_number);
        TextView tvHymnTitle = view.findViewById(R.id.title);
        TextView tvFirstLine = view.findViewById(R.id.first_line);

        int id = cursor.getInt(cursor.getColumnIndex(SearchDatabaseTable.COL_ID));
        String title = cursor.getString(cursor.getColumnIndex(SearchDatabaseTable.COL_TITLE));
        String firstLine = cursor.getString(cursor.getColumnIndex(SearchDatabaseTable.COL_CONTENT));

        tvHymnNo.setText(String.valueOf(id));
        tvHymnTitle.setText(title);
        tvFirstLine.setText(firstLine);
    }
}
