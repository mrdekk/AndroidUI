package ru.mrdekk.android.ui.views;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

import ru.mrdekk.android.ui.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NavigationBar extends RelativeLayout implements OnClickListener 
{
	public static final int NAVIGATION_BUTTON_LEFT = 0;
	public static final int NAVIGATION_BUTTON_RIGHT = 1;
	
	private Context _context;
	private NavigationBarListener _listener;
	
	public NavigationBar( Context context, AttributeSet attrs, int defStyle )
	{
		super( context, attrs, defStyle );
		init( context );
	}
	
	public NavigationBar( Context context, AttributeSet attrs )
	{
		super( context, attrs );
		init( context );
	}
	
	public NavigationBar( Context context )
	{
		super( context );
		init( context );
	}
	
	private void init( Context context )
	{
		_context = context;
		
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams( -1, -2 );
		this.setLayoutParams( lp );
		this.setBackgroundResource( R.drawable.navigation_bar_background );
		
		setLeftMenuButton( );
	}
	
	public void setLeftMenuButton( )
	{
		SVG svg = SVGParser.getSVGFromResource( getResources( ), R.raw.menu, 0xff000000, 0xffffffff );
		Drawable menuDrawable = svg.createPictureDrawable( );
		
		this.setLeftBarButton( menuDrawable );		
	}
	
	public void setLeftBackButton( String title )
	{
		if ( title.equals( "" ) )
		{
			setLeftMenuButton( );
			return ;
		}
		
		if ( title.length( ) > 5 )
			title = title.substring( 0, 5 );
		
		this.setLeftBarButton( title );
	}
	
	public void setLeftBarButton( String title )
	{
		setButton( title, NAVIGATION_BUTTON_LEFT );
	}
	
	public void setLeftBarButton( Drawable drawable )
	{
		setButton( drawable, NAVIGATION_BUTTON_LEFT );
	}
	
	public void setRightBarButton( String title )
	{
		setButton( title, NAVIGATION_BUTTON_RIGHT );
	}
	
	public void setRightBarButton( Drawable drawable )
	{
		setButton( drawable, NAVIGATION_BUTTON_RIGHT );
	}
	
	private void setButton( String title, int which )
	{
		removeButton( which );
		
		Button newButton = new Button( _context );
		newButton.setTag( Integer.valueOf( which ) );
		
		newButton.setOnClickListener( this );
		
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams( -2, -2 );
		
		if ( which == NAVIGATION_BUTTON_LEFT )
			lp.addRule( RelativeLayout.ALIGN_PARENT_LEFT );
		else if ( which == NAVIGATION_BUTTON_RIGHT )
			lp.addRule( RelativeLayout.ALIGN_PARENT_RIGHT );
		else
			throw new IllegalArgumentException( "Parameter 'which' must be NAVIGATION_BUTTON_LEFT or NAVIGATION_BUTTON_RIGHT" );
		
		lp.addRule( RelativeLayout.CENTER_VERTICAL );
		lp.setMargins( 10, 0, 10, 0 );
		newButton.setLayoutParams( lp );
		
		newButton.setText( title );
		newButton.setTextSize( 12 );
		newButton.setTextColor( Color.WHITE );
		
		newButton.setBackgroundResource( R.drawable.navigation_bar_button );
		
		this.addView( newButton );
	}

	private void setButton( Drawable drawable, int which )
	{
		removeButton( which );
		
		ImageButton newButton = new ImageButton( _context );
		newButton.setTag( Integer.valueOf( which ) );
		
		newButton.setOnClickListener( this );
		
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams( -2, -2 );
		
		if ( which == NAVIGATION_BUTTON_LEFT )
			lp.addRule( RelativeLayout.ALIGN_PARENT_LEFT );
		else if ( which == NAVIGATION_BUTTON_RIGHT )
			lp.addRule( RelativeLayout.ALIGN_PARENT_RIGHT );
		else
			throw new IllegalArgumentException( "Parameter 'which' must be NAVIGATION_BUTTON_LEFT or NAVIGATION_BUTTON_RIGHT" );
		
		lp.addRule( RelativeLayout.CENTER_VERTICAL );
		lp.setMargins( 10, 0, 10, 0 );
		
		final float scale = _context.getResources( ).getDisplayMetrics( ).density;
		lp.height = ( int )( 36 * scale + 0.5f ); // 54;
		lp.width = ( int )( 46 * scale + 0.5f );
		
		newButton.setLayoutParams( lp );

		newButton.setImageDrawable( drawable );
		newButton.setScaleType( ScaleType.CENTER_CROP );
		
		newButton.setBackgroundResource( R.drawable.navigation_bar_button );
		
		this.addView( newButton );
	}
	
	private void removeButton( int which )
	{
		View oldButton = this.findViewWithTag( Integer.valueOf( which ) );
		if ( null != oldButton )
				this.removeView( oldButton );
	}
	
	public void setBarTitle( String title )
	{
		TextView oldTitle = ( TextView )this.findViewWithTag( "title" );
		if ( null != oldTitle )
			this.removeView( oldTitle );
		
		TextView newTitle = new TextView( _context );
		newTitle.setTag( "title" );
		
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams( -2, -2 );
		lp.addRule( RelativeLayout.CENTER_IN_PARENT );
		lp.setMargins( 0,  30,  0, 30 );
		newTitle.setLayoutParams( lp );
		
		newTitle.setText( title );
		newTitle.setTextSize( 25 );
		newTitle.setTextColor( Color.WHITE );
		
		this.addView( newTitle );
	}
	
	public String barTitle( )
	{
		TextView oldTitle = ( TextView )this.findViewWithTag( "title" );
		if ( null != oldTitle )
			return oldTitle.getText( ).toString( );
		
		return "";
	}
	
	public void setNavigationBarListener( NavigationBarListener listener )
	{
		_listener = listener;
	}
	
	@Override
	public void onClick( View v ) 
	{
		int which = ( ( Integer )v.getTag( ) ).intValue( );
		if ( null != _listener )
			_listener.onNavigationButtonClick( which );
	}

	public interface NavigationBarListener
	{
		public void onNavigationButtonClick( int which );
	}
}
