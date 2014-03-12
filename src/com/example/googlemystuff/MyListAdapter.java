package com.example.googlemystuff;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.googlemystuff.beans.Item;

public class MyListAdapter extends ArrayAdapter<Item> {

	Context context;
	List<Item> itemList;

	public MyListAdapter(Context context, List<Item> objects) {
		super(context, R.layout.list_item, objects);
		this.context = context;
		this.itemList = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View rowView = inflater.inflate(R.layout.list_item, parent, false);
		
		TextView itemNameView = (TextView) rowView.findViewById(R.id.itemName);
		TextView locationView = (TextView) rowView.findViewById(R.id.location);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.image);
		TextView imageId = (TextView) rowView.findViewById(R.id.itemId);
		
		Item item = itemList.get(position);
		
		// change the icon for Windows and iPhone
		itemNameView.setText(item.getName());
		locationView.setText(item.getLocation());
		imageId.setText(""+item.getId());
		if(item.getImage()!=null)
			imageView.setImageBitmap(item.getImage());

		return rowView;

	}

	@Override
	public void remove(Item item) {
		itemList.remove(item);
	}
}
