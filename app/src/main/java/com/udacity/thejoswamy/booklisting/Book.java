package com.udacity.thejoswamy.booklisting;

/**
 * A {@Link Book} represents details of a book. It consists of title of the book,
 * list of authors, price and canonical url of the book
 */
public class Book {

    public static final int BOOK_FREE = 0;
    public static final int BOOK_NOT_FOR_SALE = -1;
    public static final int BOOK_FOR_PREORDER = -2;

    private String mTitle;
    private String mAuthors;
    private double mPrice;
    private String mCanonicalUrl;

    public Book(String title, String authorList, double price, String url) {
        mTitle = title;
        mAuthors = authorList;
        mPrice = price;
        mCanonicalUrl = url;
    }

    /**
     * Returns the title of the book
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Returns the Author of the book
     */
    public String getAuthor() {
        return mAuthors;
    }

    /**
     * Returns the price of the book
     */
    public double getPrice() {
        return mPrice;
    }

    /**
     * Returns the canonical url of the book
     */
    public String getCanonicalUrl() {
        return mCanonicalUrl;
    }

    @Override
    public String toString() {
        // For logging purposes only
        return "Book{" +
                "mTitle='" + mTitle + '\'' +
                ", mAuthors='" + mAuthors + '\'' +
                ", mPrice=" + mPrice +
                ", mCanonicalUrl='" + mCanonicalUrl + '\'' +
                '}';
    }
}
