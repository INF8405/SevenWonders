package ca.polymtl.inf8405.sevenwonders;

import ca.polymtl.inf8405.sevenwonders.R;
import ca.polymtl.inf8405.sevenwonders.model.CardInfo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.*;

public class BoardView extends LinearLayout{
	
	private void init(Context context){
		setBackgroundResource(R.drawable.seven_wonders_bg);
		setAlpha((float)0.5);
	}
	
	public BoardView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}
	
	public BoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	@Override
	public void onDraw(Canvas canvas) {
	}

	@Override
	protected  void onMeasure(int widthSpec, int heightSpec){
		// Assurer que le canvas est toujours un carre
		int measuredWidth = MeasureSpec.getSize(widthSpec);
		int measureHeigth = MeasureSpec.getSize(heightSpec);
		
		int d = Math.min(measuredWidth, measureHeigth);
		setMeasuredDimension(measuredWidth, d * 3/5);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		// TODO Auto-generated method stub
		
	}
}
