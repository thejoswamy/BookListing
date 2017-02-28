package com.udacity.thejoswamy.booklisting;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Context context, List<Book> books) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.book_list_item, parent, false);
        }

        Book currentBook = getItem(position);

        TextView titleText = (TextView) convertView.findViewById(R.id.title_text);
        titleText.setText(currentBook.getTitle());

        TextView authorText = (TextView) convertView.findViewById(R.id.author_text);
        authorText.setText(getAuthors(currentBook.getAuthor()));

        TextView priceText = (TextView) convertView.findViewById(R.id.price_text);
        priceText.setText(getPrice(currentBook.getPrice()));

        return convertView;
    }

    private String getPrice(double price) {
        if (price == Book.BOOK_FREE) {
            return getContext().getString(R.string.book_free);
        } else if (price == Book.BOOK_NOT_FOR_SALE) {
            return getContext().getString(R.string.book_not_for_sale);
        } else if (price == Book.BOOK_FOR_PREORDER) {
            return getContext().getString(R.string.book_for_preorder);
        } else {
            NumberFormat formatter = NumberFormat.getCurrencyInstance();
            return formatter.format(price);
        }
    }

    private String getAuthors(String authorList) {
        if (TextUtils.isEmpty(authorList)) {
            return getContext().getString(R.string.no_authors);
        }
        return authorList;
    }
}
