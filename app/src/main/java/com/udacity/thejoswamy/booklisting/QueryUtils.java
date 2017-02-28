package com.udacity.thejoswamy.booklisting;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Utility class for fetching data from google books API
 */
public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {

    }

    /**
     * Fetches books data from google books using Google Books API
     */
    public static List<Book> fetchBooks(String requestUrl) {
        URL url = createUrl(requestUrl);
        String httpResponse = "";
        try {
            httpResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException during fetching books data");
        }

        return parseBooksJson(httpResponse);
    }

    /**
     * Returns new URL object from given url string
     */
    private static URL createUrl(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Exception while parsing url");
        }
        return url;
    }

    /**
     * Makes Http Get request to the provided url and returns string response
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String httpResponse = "";
        if (url == null) {
            return httpResponse;
        }

        HttpsURLConnection connection = null;
        InputStream inputStream = null;
        try {
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();
                httpResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Server response status: " + connection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException during network request");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return httpResponse;
    }

    /**
     * Reads from given input stream and returns as string
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder builder = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            builder.append(line);
        }
        return builder.toString();
    }

    /**
     * Parse jsonResponse and return list of {@Link Book} objects
     */
    private static List<Book> parseBooksJson(String jsonResponse) {
        List<Book> booksList = new ArrayList<>();
        if (TextUtils.isEmpty(jsonResponse)) {
            return booksList;
        }

        try {
            JSONObject root = new JSONObject(jsonResponse);
            JSONArray booksArray = root.getJSONArray("items");
            for (int i = 0; i < booksArray.length(); i++) {
                JSONObject book = booksArray.getJSONObject(i);

                // Extracting title, authors and canonical url
                JSONObject bookInfo = book.getJSONObject("volumeInfo");
                String title = bookInfo.getString("title");
                String authors = "";
                if (bookInfo.has("authors")) {
                    JSONArray authorsJsonArray = bookInfo.getJSONArray("authors");
                    StringBuilder builder = new StringBuilder();
                    builder.append(authorsJsonArray.getString(0));
                    for (int j = 1; j < authorsJsonArray.length(); j++) {
                        builder.append("; ");
                        builder.append(authorsJsonArray.getString(j));
                    }
                    authors = builder.toString();
                }
                String canonicalUrl = bookInfo.getString("canonicalVolumeLink");

                // Extracting price info
                JSONObject saleInfo = book.getJSONObject("saleInfo");
                String saleability = saleInfo.getString("saleability");
                double price;
                if (saleability.equals("FREE")) {
                    price = Book.BOOK_FREE;
                } else if (saleability.equals("NOT_FOR_SALE")) {
                    price = Book.BOOK_NOT_FOR_SALE;
                } else if (saleability.equals("FOR_PREORDER")) {
                    price = Book.BOOK_FOR_PREORDER;
                } else {
                    JSONObject listPrice = saleInfo.getJSONObject("listPrice");
                    price = listPrice.getDouble("amount");
                }

                booksList.add(new Book(title, authors, price, canonicalUrl));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error while parsing Json Response", e);
        }

        return booksList;
    }
}
