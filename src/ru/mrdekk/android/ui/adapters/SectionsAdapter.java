package ru.mrdekk.android.ui.adapters;

import java.util.LinkedHashMap;
import java.util.Map;

import ru.mrdekk.android.ui.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

public class SectionsAdapter extends BaseAdapter 
{
	private final Map< String, Adapter > _sections = new LinkedHashMap< String, Adapter >( );
	private final ArrayAdapter< String > _headers;
	private final static int TYPE_SECTION_HEADER = 0;
	
	public SectionsAdapter( Context context )
	{
		_headers = new ArrayAdapter< String >( context, R.layout.row_list_header );
	}
	
	public void addSection( String section, Adapter adapter )
	{
		_headers.add( section );
		_sections.put( section, adapter ); 
	}
	
	public Object getItem( int position )
	{
		for ( Object section : _sections.keySet( ) )
		{
			Adapter adapter = _sections.get( section );
			int size = adapter.getCount( ) + 1;
			
			if ( 0 == position ) return section;
			if ( position < size ) return adapter.getItem( position - 1 );
			
			position -= size;
		}
		
		return null;
	}

	public int getCount( )
	{
		int total = 0;		
		for ( Adapter adapter : _sections.values( ) )
			total += adapter.getCount( ) + 1;
		
		return total;
	}
	
	@Override
	public int getViewTypeCount( )
	{
		int total = 1;
		for ( Adapter adapter : _sections.values( ) )
			total += adapter.getViewTypeCount( );
		
		return total;
	}
	
	@Override
	public int getItemViewType( int position )
	{
		int type = 1;
		for ( Object section : _sections.keySet( ) )
		{
			Adapter adapter = _sections.get( section );
			int size = adapter.getCount( ) + 1;
			
			if ( 0 == position ) return TYPE_SECTION_HEADER;
			if ( position < size ) return type + adapter.getItemViewType( position - 1 );
			
			position -= size;
			type += adapter.getViewTypeCount( );
		}
		
		return -1;
	}
	
	public boolean areAllItemsSelectable( )
	{
		return false;
	}
	
	@Override
	public boolean isEnabled( int position )
	{
		return ( getItemViewType( position ) != TYPE_SECTION_HEADER );
	}
	
	@Override
	public View getView( int position, View convertView, ViewGroup parent )
	{
		int sectionNum = 0;
		for ( Object section : _sections.keySet( ) )
		{
			Adapter adapter = _sections.get( section );
			int size = adapter.getCount( ) + 1;
			
			if ( 0 == position ) return _headers.getView( sectionNum, convertView, parent );
			if ( position < size ) return adapter.getView( position - 1, convertView, parent );
			
			position -= size;
			sectionNum++;
		}
		
		return null;
	}
	
	@Override
	public long getItemId( int position )
	{
		return position;
	}
}
