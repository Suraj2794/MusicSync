package com.example.suraj.MusicFolder;

import android.os.Parcel;
import android.os.Parcelable;

public class MusicData implements Parcelable {
	
	String path;
	String songName,songArtist;
	public MusicData(String path, String songName, String songArtist) {
		  this.path=path;
		  this.songName=songName;
		  this.songArtist=songArtist;
}
	public String getPath()
	{
		return path;
	}
	
	public String getTitle()
	{
		return songName;
	}
	public String getArtist()
	{
		return songArtist;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		
		arg0.writeString(path);
		arg0.writeString(songName);
		arg0.writeString(songArtist);
		
	}
}
