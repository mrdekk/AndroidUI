package ru.mrdekk.android.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

public class SlideLayout extends FrameLayout 
{
	protected final static int STATE_READY = 0;
	protected final static int STATE_SLIDING = 1;
	protected final static int STATE_FINISHED = 2;
	
	private int _slidingState = STATE_READY;
	
	private int _currentOffset = 0;
	private int _startOffset;
	private int _endOffset;
	
	private byte _frame = 0;
	
	private int _historicalX = 0;
	
	private boolean _enabled = true;
	private boolean _interceptTouch = true;
	private boolean _openedAlways = false;
	private boolean _closing = false;
	
	private OnSlideListener _listener = null;
	
	private Canvas _cachedCanvas = null;
	private Bitmap _cachedBitmap = null;
	private View _topView = null;

	public SlideLayout( Context context, AttributeSet attrs, int defStyle )
	{
		super( context, attrs, defStyle );
	}
	
	public SlideLayout( Context context, AttributeSet attrs )
	{
		super( context, attrs );
	}
	
	public SlideLayout( Context context )
	{
		super( context );
	}
	
	@Override
	protected void onLayout( boolean changed, int l, int t, int r, int b )
	{
		if ( ! _openedAlways )
		{
			super.onLayout( changed, l, t, r, b );
			return ;
		}
		
		final int parentLeft = 0;
		final int parentTop = 0;
		final int parentBottom = b - t;
		
		View menu = this.findViewWithTag( "sidebar" );
		int menuWidth = menu.getMeasuredWidth( );
		
		menu.layout( parentLeft, parentTop, parentLeft + menuWidth, parentBottom );
		
		View content = this.findViewWithTag( "contentView" );
		content.layout( parentLeft + menuWidth, parentTop, parentLeft + menuWidth + content.getMeasuredWidth( ), parentBottom );
		
		invalidate( );
	}
	
	@Override
	protected void onMeasure( int wSp, int hSp )
	{
		if ( _openedAlways )
		{
			View menu = this.findViewWithTag( "sidebar" );
			View content = this.findViewWithTag( "contentView" );
			
			if ( null != menu && null != content )
			{
				LayoutParams lp = ( LayoutParams )content.getLayoutParams( );
				lp.leftMargin = menu.getMeasuredWidth( );
			}
		}
		
		super.onMeasure( wSp, hSp );
	}
	
	@Override
	public void setEnabled( boolean enabled )
	{
		_enabled = enabled;
	}
	
	@Override
	public boolean isEnabled( )
	{
		return _enabled;
	}
	
	public void allowInterceptTouch( boolean allow ) 
	{
		_interceptTouch = allow;
	}
	
	public boolean isAllowedInterceptTouch( )
	{
		return _interceptTouch;
	}
	
	public void setOpenedAlways( boolean always )
	{
		_openedAlways = always;
		requestLayout( );
	}
	
	public void setOnSlideListener( OnSlideListener listener )
	{
		_listener = listener;
	}
	
	public boolean isOpened( )
	{
		return _slidingState == STATE_FINISHED;
	}
	
	public void toggle( )
	{
		if ( isOpened( ) )
			close( );
		else
			open( );
	}
	
	public boolean open( )
	{
		if ( isOpened( ) || _openedAlways )
			return false;
		
		startSliding( );
		
		Animation anim = new SlideAnimation( _currentOffset, _endOffset );
		anim.setAnimationListener( _openListener );
		startAnimation( anim );
		
		invalidate( );
		
		return true;
	}
	
	public boolean close( )
	{
		if ( ! isOpened( ) || _openedAlways )
			return false;
		
		startSliding( );
		
		Animation anim = new SlideAnimation( _currentOffset, _endOffset );
		anim.setAnimationListener( _closeListener );
		startAnimation( anim );
		
		invalidate( );
		
		return true;
	}
	
	@Override
	protected void dispatchDraw( Canvas canvas )
	{
		if ( _openedAlways )
		{
			super.dispatchDraw( canvas );
			return ;
		}
		
		try
		{
			if ( _slidingState == STATE_READY )
			{
				findViewWithTag( "contentView" ).draw( canvas );
			}
			else if ( _slidingState == STATE_SLIDING || _slidingState == STATE_FINISHED )
			{
				if ( ++_frame % 5 == 0 )
					findViewWithTag( "contentView" ).draw( _cachedCanvas );
				
				View menu = findViewWithTag( "sidebar" );
				final int scrollX = menu.getScrollX( );
				final int scrollY = menu.getScrollY( );
				
				canvas.save( );
				
				canvas.clipRect( 0, 0, _currentOffset, getHeight( ), Op.REPLACE );
				canvas.translate( -scrollX, -scrollY );
				
				menu.draw( canvas );
				
				canvas.restore( );
				canvas.drawBitmap( _cachedBitmap, _currentOffset, 0, null );
			}
		}
		catch ( Exception exc ) { }
	}
	
	@Override
	public boolean dispatchTouchEvent( MotionEvent ev )
	{
		if ( ! _enabled || _openedAlways || ! _interceptTouch )
			return super.dispatchTouchEvent( ev );
		
		if ( _slidingState != STATE_FINISHED )
		{
			onTouchEvent( ev );
			
			if ( _slidingState != STATE_SLIDING )
				super.dispatchTouchEvent( ev );
			else
			{
				MotionEvent cancelEvent = MotionEvent.obtain( ev );
				cancelEvent.setAction( MotionEvent.ACTION_CANCEL );
				super.dispatchTouchEvent( cancelEvent );
			}
			
			return true;
		}
		else
		{
			Rect rect = new Rect( );
			View menu = findViewWithTag( "sidebar" );
			menu.getHitRect( rect );
			
			if ( ! rect.contains( ( int )ev.getX( ), ( int )ev.getY( ) ) ) 
			{
				_closing = true;
				onTouchEvent( ev );
				
				return true;
			}
			else
			{
				onTouchEvent( ev );
				
				ev.offsetLocation( -menu.getLeft( ), -menu.getTop( ) );
				menu.dispatchTouchEvent( ev );
				
				return true;
			}
		}
	}
	
	@Override
	public boolean onTouchEvent( MotionEvent ev )
	{
		if ( ! _enabled )
			return false;
		
		float x = ev.getX( );
		
		if ( ev.getAction( ) == MotionEvent.ACTION_DOWN )
		{
			_historicalX = ( int )x;
			return _closing;
		}
		
		if ( ev.getAction( ) == MotionEvent.ACTION_MOVE )
		{
			float diff = x - _historicalX;
			
			if ( ( diff > 50 && _slidingState == STATE_READY ) ||
				 ( diff < -50 && _slidingState == STATE_FINISHED ) )
			{
				_historicalX = ( int )x;
				startSliding( );
			}
			else if ( _slidingState == STATE_SLIDING )
			{
				_currentOffset += ( int )x - _historicalX;
				_historicalX = ( int )x;
				
				if ( ! isSlidingAllowed( ) )
					finishSliding( );
			}
			else
			{
				return false;
			}
			
			invalidate( );
		}
		
		if ( ev.getAction( ) == MotionEvent.ACTION_UP )
		{
			if ( _slidingState == STATE_SLIDING )
					finishSliding( );
			// else if ( _slidingState == STATE_FINISHED )
			//	close( );
			
			invalidate( );
			
			return false;
		}
		
		invalidate( );
		
		return _slidingState == STATE_SLIDING;
	}
	
	private void startSliding( )
	{
		View v = findViewWithTag( "contentView" );
		
		if ( _slidingState == STATE_READY )
		{
			_startOffset = 0;
			_endOffset = findViewWithTag( "sidebar" ).getWidth( );
		}
		else
		{
			_startOffset = findViewWithTag( "sidebar" ).getWidth( );
			_endOffset = 0;
		}
		
		_currentOffset = _startOffset;
		
		if ( null == _cachedBitmap || _cachedBitmap.isRecycled( ) || _cachedBitmap.getWidth( ) != v.getWidth( ) )
		{
			_cachedBitmap = Bitmap.createBitmap( v.getWidth( ), v.getHeight( ), Bitmap.Config.ARGB_8888 );
			_cachedCanvas = new Canvas( _cachedBitmap );
		}
		
		v.setVisibility( View.VISIBLE );
		
		_cachedCanvas.translate( -v.getScrollX( ), -v.getScrollY( ) );
		v.draw( _cachedCanvas );
		
		_topView = v;
		_slidingState = STATE_SLIDING;
	}
	
	private boolean isSlidingAllowed( )
	{
		return ( _endOffset > 0 && _currentOffset < _endOffset && _currentOffset >= _startOffset ) ||
			   ( _endOffset == 0 && _currentOffset > _endOffset && _currentOffset <= _startOffset );
	}
	
	private Animation.AnimationListener _openListener = new Animation.AnimationListener( )
	{
		@Override
		public void onAnimationStart( Animation animation ) { }
		
		@Override
		public void onAnimationRepeat( Animation animation ) { }
		
		@Override
		public void onAnimationEnd( Animation animation )
		{
			_slidingState = STATE_FINISHED;
			_topView.setVisibility( View.GONE );
			
			if ( null != _listener )
				_listener.onSlideCompleted( true );
		}
	};
	
	private Animation.AnimationListener _closeListener = new Animation.AnimationListener( ) 
	{
		@Override
		public void onAnimationStart( Animation animation ) { }
		
		@Override
		public void onAnimationRepeat( Animation animation ) { }
		
		@Override
		public void onAnimationEnd( Animation animation ) 
		{
			_slidingState = STATE_READY;
			_topView.setVisibility( View.VISIBLE );
			
			if ( null != _listener )
				_listener.onSlideCompleted( true );
		}
	};
	
	private void finishSliding( )
	{
		if ( _endOffset > 0 )
		{
			if ( _currentOffset > _endOffset / 2 )
			{
				if ( _currentOffset > _endOffset )
					_currentOffset = _endOffset;
				
				Animation anim = new SlideAnimation( _currentOffset, _endOffset );
				anim.setAnimationListener( _openListener );
				startAnimation( anim );
			}
			else
			{
				if ( _currentOffset < _startOffset )
					_currentOffset = _startOffset;
				
				Animation anim = new SlideAnimation( _currentOffset, _startOffset );
				anim.setAnimationListener( _closeListener );
				startAnimation( anim );
			}
		}
		else
		{
			if ( _currentOffset < _startOffset / 2 )
			{
				if ( _currentOffset < _endOffset )
					_currentOffset = _endOffset;
				
				Animation anim = new SlideAnimation( _currentOffset, _endOffset );
				anim.setAnimationListener( _closeListener );
				startAnimation( anim );
			}
			else
			{
				if ( _currentOffset > _startOffset )
					_currentOffset = _startOffset;
				
				Animation anim = new SlideAnimation( _currentOffset, _startOffset );
				anim.setAnimationListener( _openListener );
				startAnimation( anim );
			}
		}
	}
	
	private class SlideAnimation extends Animation
	{
		private static final float SPEED = 0.6f;
		
		private float _start;
		private float _end;
		
		public SlideAnimation( float fromX, float toX )
		{
			_start = fromX;
			_end = toX;
			
			setInterpolator( new DecelerateInterpolator( ) );
			
			float duration = Math.abs( _end - _start ) / SPEED;
			setDuration( ( long )duration );
		}
		
		@Override
		protected void applyTransformation( float interpolatedTime, Transformation t )
		{
			super.applyTransformation( interpolatedTime, t );
			
			float offset = ( _end - _start ) * interpolatedTime + _start;
			_currentOffset = ( int )offset;
			postInvalidate( );
		}
	}
	
	public static interface OnSlideListener
	{
		public void onSlideCompleted( boolean opened );
	}
}
