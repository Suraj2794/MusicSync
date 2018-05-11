package com.example.suraj.MusicFolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.suraj.projectdemo.R;

import java.util.ArrayList;

public class MusicAdapter extends BaseAdapter {

	LayoutInflater inflater;
	ArrayList<MusicData> song_list;
	public MusicAdapter(LayoutInflater inflater, ArrayList<MusicData> song_list)
	{
		this.inflater=inflater;
		this.song_list=song_list;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return song_list.size();
	}

	@Override
	public MusicData getItem(int arg0) {
		// TODO Auto-generated method stub
		return song_list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		if(arg1==null)
		{
			arg1=inflater.inflate(R.layout.song_item, null);
		}
		TextView title=(TextView)arg1.findViewById(R.id.title);
		TextView artist=(TextView)arg1.findViewById(R.id.artist);
		title.setText(getItem(arg0).getTitle());
		artist.setText(getItem(arg0).getArtist());
		return arg1;
	}

}
