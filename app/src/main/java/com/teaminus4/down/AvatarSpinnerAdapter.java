package com.teaminus4.down;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


public class AvatarSpinnerAdapter extends BaseAdapter {
    Context myContext;
    TypedArray images;
    LayoutInflater inflter;

    public AvatarSpinnerAdapter(Context applicationContext, TypedArray avatars) {
        myContext = applicationContext;
        this.images = avatars;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getCount() {
        return images.length();
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.spinner_avatar, parent, false);
        ImageView av = (ImageView) row.findViewById(R.id.avatar_image);
        av.setImageDrawable(images.getDrawable(position));
        return row;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
