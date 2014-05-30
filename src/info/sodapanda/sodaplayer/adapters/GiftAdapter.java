package info.sodapanda.sodaplayer.adapters;

import info.sodapanda.sodaplayer.R;
import info.sodapanda.sodaplayer.pojo.GiftItem;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class GiftAdapter extends BaseAdapter {
	LayoutInflater inflater;
	Context context;
	ArrayList<GiftItem> gift_list;

	public GiftAdapter(ArrayList<GiftItem> giftlist,Context context) {
		gift_list = giftlist;
		this.context = context;
	}

	@Override
	public int getCount() {
		return gift_list.size();
	}

	@Override
	public Object getItem(int position) {
		return gift_list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		GiftItem thisGift = gift_list.get(position);
		View giftItemView = inflater.inflate(R.layout.room_pop_gift_item,parent,false);
		ImageView gift_img = (ImageView) giftItemView.findViewById(R.id.gift_thumb);
		Picasso.with(context).load(thisGift.getGift_img_url()).into(gift_img);
		TextView giftName = (TextView) giftItemView.findViewById(R.id.gift_name);
		giftName.setText(thisGift.getGift_name());
		TextView giftprice = (TextView) giftItemView.findViewById(R.id.gift_price);
		giftprice.setText(thisGift.getGift_price());
		return giftItemView;
	}
}
