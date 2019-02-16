package com.techbeloved.hymnbook.hymns;


import android.content.Context;
import android.database.Cursor;
import androidx.cursoradapter.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techbeloved.hymnbook.R;
import com.techbeloved.hymnbook.data.HymnContract;

/**
 * Created by kennedy on 4/29/18.
 */

public class HymnCursorAdapter extends CursorAdapter {
    public HymnCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.hymn_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvHymnTitle = view.findViewById(R.id.hymn_title);
        TextView tvHymnNumber = view.findViewById(R.id.hymn_number);

        String hymnTitle = cursor.getString(cursor.getColumnIndexOrThrow(HymnContract.HymnEntry.COLUMN_TITLE));
        String hymnNumber = cursor.getString(cursor.getColumnIndexOrThrow(HymnContract.HymnEntry._ID));

        tvHymnTitle.setText(hymnTitle);
        tvHymnNumber.setText(hymnNumber);
    }
}
