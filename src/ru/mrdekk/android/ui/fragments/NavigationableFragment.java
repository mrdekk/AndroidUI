package ru.mrdekk.android.ui.fragments;

import ru.mrdekk.android.ui.activities.SideSliderActivity;
import ru.mrdekk.android.ui.views.NavigationBar;
import android.app.Activity;
import android.support.v4.app.Fragment;

public class NavigationableFragment extends Fragment 
{
	protected NavigationBar _navigationBar;
	
	public void setNavigationBar( NavigationBar navigationBar ) { _navigationBar = navigationBar; }
	public NavigationBar navigationBar( ) { return _navigationBar; }
	
	@Override
	public void onAttach( Activity activity )
	{
		super.onAttach( activity );
		
		if ( ! ( activity instanceof SideSliderActivity ) )
			throw new IllegalStateException( "activity must be instance of SideSliderActivity");
	}
	
	public void onLeftNavigationButtonClicked( ) { }
	public void onRightNavigationButtonClicked( ) { }
	
	public void refresh( ) { }
}
