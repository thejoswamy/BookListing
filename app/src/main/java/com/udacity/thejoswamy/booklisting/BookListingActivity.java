package com.udacity.thejoswamy.booklisting;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class BookListingActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    private static final String LOG_TAG = BookListingActivity.class.getSimpleName();
    private static final String GOOGLE_BOOKS_API = "https://www.googleapis.com/books/v1/volumes?q=";
    private static final String QUERY_STRING = "QueryText";
    private static final int BOOKS_LOADER = 1;

    private String mRequestUrl;
    private EditText mQueryText;
    private BookAdapter mBookAdapter;
    private TextView mEmptyView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_listing_activity);

        mQueryText = (EditText) findViewById(R.id.query_edit_text);
        mEmptyView = (TextView) findViewById(R.id.empty_view);
        mProgressBar = (ProgressBar) findViewById(R.id.loading_indicator);

        ListView booksListView = (ListView) findViewById(R.id.list_view);
        mBookAdapter = new BookAdapter(this, new ArrayList<Book>());
        booksListView.setAdapter(mBookAdapter);
        booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book currentBook = (Book) parent.getItemAtPosition(position);
                Uri webpage = Uri.parse(currentBook.getCanonicalUrl());
                Intent newIntent = new Intent(Intent.ACTION_VIEW, webpage);
                if (newIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(newIntent);
                }
            }
        });
        booksListView.setEmptyView(mEmptyView);

        // Get saved query during configuration changes
        String query = null;
        if (savedInstanceState != null) {
            query = savedInstanceState.getString(QUERY_STRING);
        }

        // Update loader depending on state
        if (isUpdateLoaderRequired(query)) {
            getSupportLoaderManager().initLoader(BOOKS_LOADER, null, this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        String query = mQueryText.getText().toString();
        if (!TextUtils.isEmpty(query)) {
            outState.putString(QUERY_STRING, query);
        }
        super.onSaveInstanceState(outState);
    }

    public void onSearch(View view) {
        if (isUpdateLoaderRequired(mQueryText.getText().toString())) {
            getSupportLoaderManager().restartLoader(BOOKS_LOADER, null, this);
        }
    }

    private void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private boolean isUpdateLoaderRequired(String query) {
        // clearing existing results
        mBookAdapter.clear();
        hideSoftKeyboard();

        if (!TextUtils.isEmpty(query)) {
            mProgressBar.setVisibility(View.VISIBLE);
            mEmptyView.setText("");

            // updating request url
            try {
                mRequestUrl = GOOGLE_BOOKS_API + URLEncoder.encode(query, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                Log.e(LOG_TAG, "Encoding exception while encoding query string");
            }

            // checking state
            if (isNetworkConnected()) {
                return true;
            } else {
                mProgressBar.setVisibility(View.GONE);
                mEmptyView.setText(R.string.no_internet_connection);
            }
        } else {
            mProgressBar.setVisibility(View.GONE);
            mEmptyView.setText(R.string.search_published_books);
        }
        return false;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().
                getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        boolean isConnected = (networkInfo != null && networkInfo.isConnectedOrConnecting());
        return isConnected;
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        return new BookLoader(this, mRequestUrl);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> data) {
        mProgressBar.setVisibility(View.GONE);
        mEmptyView.setText(R.string.no_books_found);
        if (data != null && data.size() > 0) {
            mBookAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        mBookAdapter.clear();
    }
}
