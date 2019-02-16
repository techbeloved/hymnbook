package com.techbeloved.hymnbook.about;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techbeloved.hymnbook.R;

import java.util.ArrayList;

public class LicenseAdapter extends RecyclerView.Adapter<LicenseAdapter.LicenseViewHolder> {

    final private ListItemClickListener mOnClickListener;

    private ArrayList<Library> mLibraries;

    public LicenseAdapter(ArrayList<Library> libraries, ListItemClickListener listener) {
        mLibraries = libraries;
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public LicenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.license_list_item, parent, false);

        return new LicenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LicenseViewHolder holder, int position) {
        if (mLibraries != null && mLibraries.size() > 0) {
            Library library = mLibraries.get(position);
            holder.libraryName.setText(library.getName());
            holder.copyrightHolder.setText(library.getCopyright());
            holder.licenseType.setText(library.getLicense());
        }
    }

    @Override
    public int getItemCount() {
        return mLibraries.size();
    }

    public interface ListItemClickListener {
        void onListItemClick(Library library);
    }

    public void swapData(ArrayList<Library> libraries) {
        mLibraries = libraries;
        notifyDataSetChanged();
    }


    class LicenseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView libraryName;
        TextView copyrightHolder;
        TextView licenseType;

        public LicenseViewHolder(View itemView) {
            super(itemView);

            libraryName = itemView.findViewById(R.id.library_name);
            copyrightHolder = itemView.findViewById(R.id.copyright_text);
            licenseType = itemView.findViewById(R.id.license_text);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(mLibraries.get(clickedPosition));
        }
    }
}
