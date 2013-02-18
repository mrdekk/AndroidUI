package ru.mrdekk.android.ui.activities;

import java.util.Stack;

import ru.mrdekk.android.ui.R;
import ru.mrdekk.android.ui.fragments.NavigationBarFragment;
import ru.mrdekk.android.ui.fragments.NavigationableFragment;
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
	private Stack< NavigationableFragment > _contentFragments = new Stack< NavigationableFragment >( );
	private Stack< String > _navigationBarTitles = new Stack< String >( );
	
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
					if ( _contentFragments.size( ) == 1 )
					{
						SlideLayout slayout = ( SlideLayout )SideSliderActivity.this.findViewById( R.id.slideLayout );
						if ( null != slayout )
							slayout.toggle( );
					}
					else
					{
						SideSliderActivity.this.popFragmentFromContentView( );
					}
				}
				
				if ( null != _navBarListener )
					_navBarListener.onNavigationButtonClick( which );
			}
		} );
	}
	
	@Override
	public void onBackPressed( )
	{
		if ( _contentFragments.size( ) > 0 )
			this.popFragmentFromContentView( );
		else
			super.onBackPressed( );
	}
	
	private void ensureMenuWidth( float percentage )
	{
		View sideScroll = this.findViewById( R.id.sidebar );

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
	
	public void pushFragmentToContentView( NavigationableFragment f )
	{
		if ( _contentFragments.size( ) > 0 )
		{
			NavigationableFragment prev = _contentFragments.peek( );
			
			FragmentTransaction ft = getSupportFragmentManager( ).beginTransaction( );
			ft.hide( prev );
			ft.commit( );
		}

		NavigationBar navbar = getNavigationBar( );
		if ( null != navbar )
		{
			_navigationBarTitles.push( navbar.barTitle( ) );
			navbar.setLeftBackButton( navbar.barTitle( ) );
		}
		
		if ( null != f )
		{			
			_contentFragments.push( f );
			f.setNavigationBar( getNavigationBar( ) );
		
			FragmentTransaction ft = getSupportFragmentManager( ).beginTransaction( );
			ft.add( R.id.contentView, f );
			ft.commit( );
		}
	}
	
	public void popFragmentFromContentView( )
	{
		if ( _contentFragments.size( ) > 0 )
		{
			NavigationableFragment prev = _contentFragments.pop( );
			
			FragmentTransaction ft = getSupportFragmentManager( ).beginTransaction( );
			ft.remove( prev );
			ft.commit( );
		}
		
		if ( _contentFragments.size( ) > 0 )
		{
			NavigationableFragment prev = _contentFragments.peek( );
			
			FragmentTransaction ft = getSupportFragmentManager( ).beginTransaction( );
			ft.show( prev );
			ft.commit( );
			
			prev.refresh( );
		}

		NavigationBar navbar = getNavigationBar( );
		if ( null != navbar )
		{
			navbar.setBarTitle( _navigationBarTitles.pop( ) );
			navbar.setLeftBackButton( _navigationBarTitles.peek( ) );
		}
	}
}
