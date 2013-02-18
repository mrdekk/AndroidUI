package ru.mrdekk.android.ui.adapters;

import ru.mrdekk.android.ui.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RowSubtitleAdapter extends BaseAdapter
{
	public static class Item
	{
		public String title;
		public String subtitle;
		public Drawable image;
		public Drawable accessory;
		public Drawable bg;
		
		public Item( String title, String subtitle, Drawable image, Drawable accessory, Drawable bg )
		{
			this.title = title;
			this.subtitle = subtitle;
			this.image = image;
			this.accessory = accessory;
			this.bg = bg;
		}
		
		public String titleColor = null;
		public String subtitleColor = null;
		public int cellHeight = -1;
	}
	
	private final Context _context;
	private final Item[ ] _items;
	private final Drawable _bg;
	
	public RowSubtitleAdapter( Context context, Item[ ] items, Drawable bg )
	{		
		_context = context;
		_items = items;
		_bg = bg;
	}
	
	@Override
	public int getCount( ) 
	{
		if ( null == _items )
			return 0;
		
		return _items.length;
	}

	@Override
	public Object getItem( int position ) 
	{
		if ( null == _items )
			return null;
		
		return _items[ position ];
	}

	@Override
	public long getItemId( int position ) 
	{
		return position;
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent ) 
	{
		LayoutInflater inflater = ( LayoutInflater )_context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		
		View itemView = inflater.inflate( R.layout.row_subtitle_layout, parent, false );
		
		ImageView backgroundView = ( ImageView )itemView.findViewById( R.id.background );
		TextView titleView = ( TextView )itemView.findViewById( R.id.title );
		TextView subtitleView = ( TextView )itemView.findViewById( R.id.subtitle );
		ImageView imageView = ( ImageView )itemView.findViewById( R.id.image );
		ImageView accessoryView = ( ImageView )itemView.findViewById( R.id.accessory );
				
		Item item = _items[ position ];
		
		int cellHeightDp = 120;
		if ( -1 != item.cellHeight )
			cellHeightDp = item.cellHeight;
		
		final float scale = _context.getResources( ).getDisplayMetrics( ).density;
		itemView.getLayoutParams( ).height = ( int )( ( float )cellHeightDp * scale + 0.5f );
		
		if ( null != item.bg )
		{
			backgroundView.setImageDrawable( item.bg );
			backgroundView.setVisibility( View.VISIBLE );
		}
		else if ( null != _bg )
		{
			backgroundView.setImageDrawable( _bg );
			backgroundView.setVisibility( View.VISIBLE );
		}
		else
		{
			backgroundView.setVisibility( View.GONE );
		}		
		
		if ( null != item.title )
		{
			titleView.setVisibility( View.VISIBLE );
			titleView.setText( item.title );
			
			if ( null != item.titleColor )
				titleView.setTextColor( Color.parseColor( item.titleColor ) );
			else
				titleView.setTextColor( 0xff000000 );
			
			titleView.setShadowLayer( 1.0f, 0.0f, 1.0f, 0xffffff );
		}
		else
		{
			titleView.setVisibility( View.GONE );
		}
		
		if ( null != item.subtitle )
		{
			subtitleView.setVisibility( View.VISIBLE );
			subtitleView.setText( item.subtitle );
			
			if ( null != item.subtitleColor )
				subtitleView.setTextColor( Color.parseColor( item.subtitleColor ) );
			else
				subtitleView.setTextColor( 0xff7e7e82 );
		}
		else
		{
			subtitleView.setVisibility( View.GONE );
		}
						
		if ( null != item.image )
		{
			imageView.setVisibility( View.VISIBLE );
			imageView.getLayoutParams( ).width = 77;
			imageView.getLayoutParams( ).height = 77;
			imageView.setImageDrawable( item.image );
		}
		else
		{
			imageView.setVisibility( View.GONE );
		}
		
		if ( null != item.accessory )
		{
			accessoryView.setVisibility( View.VISIBLE );
			accessoryView.setImageDrawable( item.accessory );
			accessoryView.getLayoutParams( ).width = 28;
			accessoryView.getLayoutParams( ).height = 42;
		}
		else
		{
			accessoryView.setVisibility( View.GONE );
		}
		
		return itemView;
	}	
	
}
