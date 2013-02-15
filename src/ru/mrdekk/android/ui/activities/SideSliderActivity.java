package ru.mrdekk.android.ui.activities;

import ru.mrdekk.android.ui.R;
import ru.mrdekk.android.ui.fragments.NavigationBarFragment;
import ru.mrdekk.android.ui.views.NavigationBar;
import ru.mrdekk.android.ui.views.SlideLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class SideSliderActivity extends FragmentActivity 
{
	private NavigationBar.NavigationBarListener _navBarListener = null;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_side_slider );
		
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
	
	public void addFragmentToContentView( Fragment f )
	{
		FragmentTransaction ft = getSupportFragmentManager( ).beginTransaction( );
		ft.add( R.id.contentView, f );
		ft.commit( );
	}
}
