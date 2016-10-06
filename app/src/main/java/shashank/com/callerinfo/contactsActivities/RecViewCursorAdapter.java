package shashank.com.callerinfo.contactsActivities;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.AlphabetIndexer;

import shashank.com.callerinfo.R;

/**
 * Created by sHIVAM on 9/30/2016.
 */

public abstract class RecViewCursorAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private final AlphabetIndexer alphabetIndexer;
    private Cursor cursor;
    private Context context;
    private boolean isDataValid;
    private int rowIdColumn;
    private DataSetObserver dataSetObserver;


    public RecViewCursorAdapter(Context context, Cursor cursor) {
        super();
        this.context = context;
        this.cursor = cursor;
        isDataValid = cursor != null;
        rowIdColumn = isDataValid ? cursor.getColumnIndex("_id") : -1;
        dataSetObserver = new NotifyingDataSetObserver();
        if (cursor != null) {
            cursor.registerDataSetObserver(dataSetObserver);
        }

        final String alphabet = context.getString(R.string.alphabet);
        alphabetIndexer = new AlphabetIndexer(cursor, ContactListFragment.ContactsQuery.SORT_KEY, alphabet);
    }

    public Cursor getCursor() {
        return cursor;
    }

    @Override
    public int getItemCount() {
        if (isDataValid && cursor != null) {
            return cursor.getCount();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        if (isDataValid && cursor != null && cursor.moveToPosition(position)) {
            return cursor.getLong(rowIdColumn);
        }
        return 0;
    }

    public abstract void onBindViewHolder(VH holder, Context context, Cursor cursor);

    @Override
    public void onBindViewHolder(VH holder, int position) {
        if(!isDataValid) {
            throw new IllegalStateException("Should only be called when there is a valid cursor");
        }
        if(!cursor.moveToPosition(position)) {
            throw new IllegalStateException("Couldn't move cursor to the position :"+position);
        }
        onBindViewHolder(holder, context, cursor);
    }

    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if(old != null) {
            old.close();
        }
    }

    public Cursor swapCursor(Cursor newCursor) {
        alphabetIndexer.setCursor(newCursor);
        if(newCursor == cursor) {
            return null;
        }
        final Cursor oldCursor = cursor;
        if(oldCursor != null && dataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(dataSetObserver);
        }
        cursor = newCursor;
        if(cursor != null) {
            if(dataSetObserver != null) {
                cursor.registerDataSetObserver(dataSetObserver);
            }
            rowIdColumn = newCursor.getColumnIndexOrThrow("_id");
            isDataValid = true;
            notifyDataSetChanged();
        } else {
            rowIdColumn = -1;
            isDataValid = false;
            notifyDataSetChanged();
        }
        return oldCursor;
    }

    public int getPositionForSection(int sectionIndex) {
        return alphabetIndexer.getPositionForSection(sectionIndex);
    }

    public int getSectionForPosition(int position) {
        return alphabetIndexer.getSectionForPosition(position);
    }

    public Object[] getSections() {
        return alphabetIndexer.getSections();
    }

    private class NotifyingDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            isDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            isDataValid = false;
            notifyDataSetChanged();
        }
    }

}