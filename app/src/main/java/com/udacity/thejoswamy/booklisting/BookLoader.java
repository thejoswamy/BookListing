package com.udacity.thejoswamy.booklisting;

import android.content.Context;

import java.util.List;

public class BookLoader extends android.support.v4.content.AsyncTaskLoader<List<Book>> {

    private String mRequestUrl;

    /**
     * Stores away the application context associated with context.
     * Since Loaders can be used across multiple activities it's dangerous to
     * store the context directly; always use {@link #getContext()} to retrieve
     * the Loader's Context, don't use the constructor argument directly.
     * The Context returned by {@link #getContext} is safe to use across
     * Activity instances.
     *
     * @param context used to retrieve the application context.
     */
    public BookLoader(Context context, String url) {
        super(context);
        mRequestUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground() {
        return QueryUtils.fetchBooks(mRequestUrl);
    }
}
