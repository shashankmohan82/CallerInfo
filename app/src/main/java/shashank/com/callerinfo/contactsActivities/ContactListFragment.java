package shashank.com.callerinfo.contactsActivities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import shashank.com.callerinfo.BuildConfig;
import shashank.com.callerinfo.R;
import shashank.com.callerinfo.contactsActivities.util.ImageLoader;
import shashank.com.callerinfo.contactsActivities.util.ImageLoaderPicasso;


public class ContactListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = ContactListFragment.class.getName();

    private static final String STATE_PREVIOUSLY_SELECTED_KEY = "";

    private ContactsAdapter adapter;
    private ImageLoaderPicasso imageLoader;
    private String searchTerm;
    private OnFragmentInteractionListener onContactSelectedListener;

    private int previouslySelectedSearchItem = 0;

    private boolean searchQueryChanged;

    private boolean isTwoPaneLayout;

    private RecyclerView contactList;
    private RecyclerView.LayoutManager layoutManager;

    public ContactListFragment() {
        // Required empty public constructor
    }

    public static ContactListFragment newInstance() {
        return new ContactListFragment();
    }

    public void setSearchQuery(String query) {
        searchTerm = query;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  isTwoPaneLayout = getResources().getBoolean(R.bool.has_two_panes);


        adapter = new ContactsAdapter(getActivity(), null);

        if(savedInstanceState != null) {
            searchTerm = savedInstanceState.getString(SearchManager.QUERY);
            previouslySelectedSearchItem = savedInstanceState.getInt(STATE_PREVIOUSLY_SELECTED_KEY);
        }

//        imageLoader = new ImageLoader(getActivity(), getListPreferredHeight()) {
//            @Override
//            protected Bitmap processBitmap(Object data) {
//                return loadContactPhotoThumbnail((String) data, getImageSize());
//            }
//        };

//        imageLoader.setLoadingImage(R.drawable.user);
//        imageLoader.addImageCache(getActivity().getSupportFragmentManager(), 0.1f);

        imageLoader = new ImageLoaderPicasso(getActivity());
        imageLoader.setLoadingBitmap(R.drawable.user);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_list, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        contactList = (RecyclerView) getActivity().findViewById(R.id.recView);
        layoutManager = new LinearLayoutManager(getActivity());
        setRecViewLayoutManager();
        contactList.setAdapter(adapter);
        contactList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if(newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    imageLoader.setPauseWork(true);
                }
                else {
                    imageLoader.setPauseWork(false);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        if(isTwoPaneLayout) {
        }

        if(previouslySelectedSearchItem == 0) {
            getLoaderManager().initLoader(ContactsQuery.QUERY_ID, null, this);
        }
    }

    private void setRecViewLayoutManager() {
        int scrollPosition = 0;
        if(contactList.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) contactList.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        }
        contactList.setLayoutManager(layoutManager);
        contactList.scrollToPosition(scrollPosition);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            onContactSelectedListener = (OnFragmentInteractionListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        imageLoader.setPauseWork(false);
    }

    private void onSelectionCleared() {
        onContactSelectedListener.onSelectionCleared();

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.contact_list_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        final SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);



        SearchView.SearchAutoComplete textArea = (SearchView.SearchAutoComplete)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        textArea.setTextColor(Color.WHITE);
        textArea.setHintTextColor(Color.WHITE);

        ImageView searchCloseIccon = (ImageView)searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        searchCloseIccon.setImageDrawable(getActivity().getDrawable(R.drawable.multiply1));




        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("Test", newText+" and "+searchTerm);
                String newFilter = !TextUtils.isEmpty(newText) ? newText : null;

                if(searchTerm == null && newFilter == null) {
                    return true;
                }

                if(searchTerm  != null && searchTerm.equals(newFilter)) {
                    return true;
                }
                searchTerm = newFilter;
                searchQueryChanged = true;
                getLoaderManager().restartLoader(ContactsQuery.QUERY_ID, null, ContactListFragment.this);
                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {

                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if(!TextUtils.isEmpty(searchTerm)) {
                    onSelectionCleared();
                }
                searchTerm = null;
                getLoaderManager().restartLoader(ContactsQuery.QUERY_ID, null, ContactListFragment.this);
                return true;
            }
        });

        if(searchTerm != null) {
            final String savedSearchTerm = searchTerm;
            searchItem.expandActionView();
            searchView.setQuery(savedSearchTerm, false);
        }

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(!TextUtils.isEmpty(searchTerm)) {
            outState.putString(SearchManager.QUERY, searchTerm);
            //outState.putInt(STATE_PREVIOUSLY_SELECTED_KEY, contactList.);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_search : break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onContactSelectedListener = null;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (onContactSelectedListener != null) {
            onContactSelectedListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id == ContactsQuery.QUERY_ID) {
            Uri contentUri;
            Log.d("Test", searchTerm+"");
            if(searchTerm == null) {
                contentUri = ContactsQuery.CONTENT_URI;
            }
            else {
                contentUri = Uri.withAppendedPath(ContactsQuery.FILTER_URI, Uri.encode(searchTerm));
            }

            return new CursorLoader(getActivity(),
                    contentUri,
                    ContactsQuery.PROJECTION,
                    ContactsQuery.SELECTION,
                    null,
                    ContactsQuery.SORT_ORDER);

        }
        Log.e(TAG, "onCreateLoader - incorrect ID provided ("+ id +")");
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId() == ContactsQuery.QUERY_ID) {
            Log.d("Test", searchTerm+"  "+data.getCount());
            adapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(loader.getId() == ContactsQuery.QUERY_ID) {
            adapter.swapCursor(null);
        }
    }

    private int getListPreferredHeight() {
        final TypedValue typedValue = new TypedValue();

        getActivity().getTheme().resolveAttribute(android.R.attr.listPreferredItemHeight, typedValue, true);

        final DisplayMetrics metrics = new DisplayMetrics();

        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        return (int) typedValue.getDimension(metrics);
    }

    private Bitmap loadContactPhotoThumbnail(String photoData, int imageSize ) {
        if(!isAdded() || getActivity()==null) {
            return null;
        }
        AssetFileDescriptor descriptor = null;

        try {
            Uri thumbUri;
            thumbUri = Uri.parse(photoData);
            descriptor = getActivity().getContentResolver().openAssetFileDescriptor(thumbUri, "r");

            FileDescriptor fileDescriptor = descriptor.getFileDescriptor();
            if(fileDescriptor != null) {
                return ImageLoader.decodeSampledBitmapFromDescriptor(fileDescriptor, imageSize, imageSize);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            if(BuildConfig.DEBUG) {
                Log.d(TAG, "Unable to load thumbnail. It may not exist" + photoData + ":"+ e.toString());
            }
        }
        finally {
            if(descriptor != null) {
                try {
                    descriptor.close();
                } catch (IOException e) {

                }
            }
        }
        return null;
    }



    private class ContactsAdapter extends RecViewCursorAdapter<ContactsAdapter.ViewHolder> implements SectionIndexer{

        private Context context;
        private LayoutInflater inflater;
        private TextAppearanceSpan highlightTextSpan;

        ContactsAdapter(Context context, Cursor cursor) {
            super(context, cursor);
            this.context = context;
            inflater = LayoutInflater.from(context);
            highlightTextSpan = new TextAppearanceSpan(getActivity(), R.style.searchTextHiglight);
        }

        private int indexOfSearchQuery(String displayName) {
            if(!TextUtils.isEmpty(searchTerm)) {
                return displayName.toLowerCase(Locale.getDefault()).indexOf(searchTerm.toLowerCase(Locale.getDefault()));
            }
            return -1;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_recycler, parent, false);
            ViewHolder vh = new ViewHolder(rootView);
            return vh;
        }

        @Override
        public Object[] getSections() {
            return super.getSections();
        }

        @Override
        public int getPositionForSection(int sectionIndex) {
            if (getCursor() == null) {
                return 0;
            }
            return super.getPositionForSection(sectionIndex);
        }

        @Override
        public int getSectionForPosition(int position) {
            if(getCursor() == null) {
                return 0;
            }
            return super.getSectionForPosition(position);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, Context context, Cursor cursor) {
            final String photoUri = cursor.getString(ContactsQuery.PHOTO_THUMBNAIL_DATA);
            final String displayName = cursor.getString(ContactsQuery.DISPLAY_NAME);

            int startIndex = indexOfSearchQuery(displayName);
            if(startIndex == -1) {
                /* If the user didn't search for the contact , show the display name
                * without highlighting the contact*/
                holder.list_name.setText(displayName);

                if(TextUtils.isEmpty(searchTerm)) {
                    /* If the search is empty, hide the second line*/
                    //holder.secondary_text.setVisibility(View.GONE);
                } else {
                    /* Shows the second line if the search matches other contact*/
                    //holder.secondary_text.setVisibility(View.VISIBLE);
                }
            } else {
                final SpannableString highlightedName = new SpannableString(displayName);
                highlightedName.setSpan(highlightTextSpan, startIndex, startIndex+searchTerm.length(), 0);

                //Binds the SpannableString to the display name View object
                holder.list_name.setText(highlightedName);
                //Since the search string matched the name, this hides the secondary message
                //holder.secondary_text.setVisibility(View.GONE);
            }

            final Uri contactUri = ContactsContract.Contacts.getLookupUri(
                    cursor.getLong(ContactsQuery.ID),
                    cursor.getString(ContactsQuery.LOOKUP_KEY)
            );

            //Bind the contactUri to the QuickContactBadge
            //Load the thumbnail image using imageLoader

            Uri data = null;
            if(photoUri != null){
                data = Uri.parse(photoUri);
            }
            imageLoader.loadImage(data, holder.list_image);

        }

        @Override
        public Cursor swapCursor(Cursor newCursor) {
            return super.swapCursor(newCursor);
        }

        @Override
        public int getItemCount() {
            if(getCursor() == null) {
                return 0;
            }
            return super.getItemCount();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            CircleImageView list_image;
            TextView list_name;
            TextView secondary_text;

            ViewHolder(View itemView) {
                super(itemView);
                list_image = (CircleImageView) itemView.findViewById(R.id.item_image);
                list_name = (TextView) itemView.findViewById(R.id.item_name);
                //secondary_text = (TextView) itemView.findViewById(R.id.secondary_text);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Cursor cursor = getCursor();
                            if(cursor.moveToPosition(getAdapterPosition())) {
                                Log.d("Test", cursor.getString(ContactsQuery.DISPLAY_NAME));
                                Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI,
                                        String.valueOf(cursor.getLong(ContactsQuery.ID)));
                                onContactSelectedListener.onContactSelected(uri);
                            }
                        }
                        catch(Exception e) {
                            Toast.makeText(v.getContext(), "Something is wrong!", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                            if(BuildConfig.DEBUG) {
                                Log.d(TAG, "Error in accessing data at position :"+getAdapterPosition(), e);
                            }
                        }
                    }
                });
            }

        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void onContactSelected(Uri contactUri);
        void onSelectionCleared();
    }

    public interface ContactsQuery {
        final static int QUERY_ID = 1;
        final static Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        final static Uri FILTER_URI = ContactsContract.Contacts.CONTENT_FILTER_URI;

        final static String SELECTION = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY +
                "<>''" + " AND " + ContactsContract.Contacts.IN_VISIBLE_GROUP + "=1";
        final static String SORT_ORDER = ContactsContract.Contacts.SORT_KEY_PRIMARY;
        final static String[] PROJECTION = {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.LOOKUP_KEY,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
                SORT_ORDER
        };
        final static int ID = 0;
        final static int LOOKUP_KEY = 1;
        final static int DISPLAY_NAME = 2;
        final static int PHOTO_THUMBNAIL_DATA = 3;
        final  static int SORT_KEY = 4;
    }

}
