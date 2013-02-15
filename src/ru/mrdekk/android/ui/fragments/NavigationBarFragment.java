package ru.mrdekk.android.ui.fragments;

import ru.mrdekk.android.ui.R;
import ru.mrdekk.android.ui.views.NavigationBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NavigationBarFragment extends Fragment
{
	private NavigationBar _navigationBar;
	
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		View view = inflater.inflate( R.layout.fragment_navigation_bar, container, false );
		
		if ( view instanceof NavigationBar )
			_navigationBar = ( NavigationBar )view;
		
		return view;
	}
	
	public NavigationBar getNavigationBar( )
	{
		return _navigationBar;
	}
}
