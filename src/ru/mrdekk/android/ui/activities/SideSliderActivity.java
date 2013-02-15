package ru.mrdekk.android.ui.activities;

import java.util.Stack;

import ru.mrdekk.android.ui.R;
import ru.mrdekk.android.ui.fragments.NavigationBarFragment;
import ru.mrdekk.android.ui.views.NavigationBar;
import ru.mrdekk.android.ui.views.SlideLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class SideSliderActivity extends FragmentActivity 
{
	private NavigationBar.NavigationBarListener _navBarListener = null;
	private Stack< Fragment > _contentFragments = new Stack< Fragment >( );
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_side_slider );
		
		this.ensureMenuWidth( 0.8f );
		
		NavigationBar navbar = getNavigationBar( );
		navbar.setNavigationBarListener( new NavigationBar.NavigationBarListener( ) 
		{			
			@Override
			public void onNavigationButtonClick( int which ) 
			{
				if ( which == NavigationBar.NAVIGATION_BUTTON_LEFT )
				{
					SlideLayout slayout = ( SlideLayout )SideSliderActivity.this.findViewById( R.id.slideLayout );
					if ( null != slayout )
						slayout.toggle( );
				}
				
				if ( null != _navBarListener )
					_navBarListener.onNavigationButtonClick( which );
			}
		} );
	}
	
	private void ensureMenuWidth( float percentage )
	{
		View sideScroll = this.findViewById( R.id.sideScroll );

		DisplayMetrics metrics = new DisplayMetrics( ); 
		getWindowManager( ).getDefaultDisplay( ).getMetrics( metrics );
		int width = metrics.widthPixels;
		
		LayoutParams lp = sideScroll.getLayoutParams( );
		lp.width = ( int )( ( float )width * percentage + 0.5f );
		sideScroll.setLayoutParams( lp );		
	}
	
	public Fragment getFragment( int resource )
	{
		return this.getSupportFragmentManager( ).findFragmentById( resource );
	}
	
	public NavigationBar getNavigationBar( )
	{
		NavigationBarFragment nbfrag = ( NavigationBarFragment )getSupportFragmentManager( ).findFragmentById( R.id.navigationBar );
		if ( null != nbfrag && nbfrag.isInLayout( ) )
			return nbfrag.getNavigationBar( );
		
		return null;
	}
	
	public void setNavigationBarListener( NavigationBar.NavigationBarListener listener )
	{
		_navBarListener = listener;
	}
	
	public void addFragmentToSidebar( Fragment f )
	{
		FragmentTransaction ft = getSupportFragmentManager( ).beginTransaction( );
		ft.add( R.id.sidebar, f );
		ft.commit( );
	}
	
	/*
	public void addFragmentToContentView( Fragment f )
	{
		FragmentTransaction ft = getSupportFragmentManager( ).beginTransaction( );
		ft.add( R.id.contentView, f );
		ft.commit( );
	}
	*/
	
	public void pushFragmentToContentView( Fragment f )
	{
		if ( _contentFragments.size( ) > 0 )
		{
			Fragment prev = _contentFragments.peek( );
		
			FragmentTransaction ft = getSupportFragmentManager( ).beginTransaction( );
			ft.hide( prev );
			ft.commit( );
		}
		
		{
			_contentFragments.push( f );
		
			FragmentTransaction ft = getSupportFragmentManager( ).beginTransaction( );
			ft.add( R.id.contentView, f );
			ft.commit( );
		}
	}
	
	public void popFragmentFromContentView( )
	{
		if ( _contentFragments.size( ) > 0 )
		{
			Fragment prev = _contentFragments.pop( );
			
			FragmentTransaction ft = getSupportFragmentManager( ).beginTransaction( );
			ft.remove( prev );
			ft.commit( );
		}
		
		if ( _contentFragments.size( ) > 0 )
		{
			Fragment prev = _contentFragments.peek( );
			
			FragmentTransaction ft = getSupportFragmentManager( ).beginTransaction( );
			ft.show( prev );
			ft.commit( );
		}
	}
}
