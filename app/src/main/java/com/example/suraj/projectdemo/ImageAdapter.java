package com.example.suraj.projectdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;


/**
 * Created by tanay on 3/6/18.
 */

public class ImageAdapter extends BaseAdapter {

    private Context context;
    private int [] tileIconIds = {
            R.mipmap.ic_action_person_black,
            R.mipmap.ic_action_person_black,
            R.mipmap.ic_action_person_black,
            R.mipmap.ic_action_person_black,
            R.mipmap.ic_action_person_black,
            R.mipmap.ic_action_person_black
    };

    public ImageAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return tileIconIds.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    /**
     * Creating new view for items in grid.
     *
     * @param position
     * @param view
     * @param viewGroup
     * @return
     */
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView = null;

        if(null == view){
            gridView = new ImageView(context);
            gridView = inflater.inflate(R.layout.stations, null);

        }else{
            gridView  = view;

        }

        gridView.setElevation(5);

        //! Set tile image.
        return gridView;
    }
}
