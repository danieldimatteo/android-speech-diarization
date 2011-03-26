/**
 * Android Speech Diarization - Calculates the amount of time spent speaking
 * 								by each speaker in a conversation
 * 
 * Copyright (C) 2011  Daniel Di Matteo
 *
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package com.example.frontend;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.BitmapFactory.Options;
import android.util.AttributeSet;
import android.view.View;
import java.util.List;

public class PieChartView extends View {
	private static final int WAIT = 0;
	private static final int IS_READY_TO_DRAW = 1;
	private static final int IS_DRAW = 2;
	private static final float START_INC = 30;
	private Paint mBgPaints   = new Paint();
	private Paint mLinePaints = new Paint();
	private int   mWidth;
	private int   mHeight;
	private int   mGapLeft;
	private int   mGapRight;
	private int   mGapTop;
	private int   mGapBottom;
	private int   mBgColor;
	private int   mState = WAIT;
	private float mStart;
	private float mSweep;
	private int   mMaxConnection;
	private List<PieChartItem> mDataArray;
	

	public PieChartView (Context context){
		super(context);
	}

	public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
	}

    @Override 
    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);

    	if (mState != IS_READY_TO_DRAW) return;
    	canvas.drawColor(mBgColor);

    	mBgPaints.setAntiAlias(true);
    	mBgPaints.setStyle(Paint.Style.FILL);
    	mBgPaints.setColor(0x00000000);
    	mBgPaints.setStrokeWidth(0.5f);

    	mLinePaints.setAntiAlias(true);
    	mLinePaints.setStyle(Paint.Style.STROKE);
    	mLinePaints.setColor(0x00000000);
    	mLinePaints.setStrokeWidth(0.5f);

    	RectF mOvals = new RectF( mGapLeft, mGapTop, mWidth - mGapRight, mHeight - mGapBottom);

    	mStart = START_INC;
    	PieChartItem Item;
    	for (int i = 0; i < mDataArray.size(); i++) {
    		Item = (PieChartItem) mDataArray.get(i);
    		mBgPaints.setColor(Item.colour);
    		mSweep = (float) 360 * ( (float)Item.count / (float)mMaxConnection );
    		canvas.drawArc(mOvals, mStart, mSweep, true, mBgPaints);
    		canvas.drawArc(mOvals, mStart, mSweep, true, mLinePaints);
    		mStart += mSweep;
        }

    	Options options = new BitmapFactory.Options();
        options.inScaled = false;

    	mState = IS_DRAW;
    }

    public void setGeometry(int width, int height, int GapLeft, int GapRight, int GapTop, int GapBottom) {
    	mWidth     = width;
   	 	mHeight    = height;
   	 	mGapLeft   = GapLeft;
   	 	mGapRight  = GapRight;
   	 	mGapTop    = GapTop;
   	 	mGapBottom = GapBottom;
    }

    public void setSkinParams(int bgColor) {
   	 	mBgColor   = bgColor;
    }

    public void setData(List<PieChartItem> data, int MaxConnection) {
    	mDataArray = data;
    	mMaxConnection = MaxConnection;
    	mState = IS_READY_TO_DRAW;
    }

    public void setState(int State) {
    	mState = State;
    }

    public int getColorValue( int Index ) {
   	 	if (mDataArray == null) return 0;
   	 	if (Index < 0){
   	 		return ((PieChartItem)mDataArray.get(0)).colour;
   	 	} else if (Index >= mDataArray.size()){
   	 		return ((PieChartItem)mDataArray.get(mDataArray.size()-1)).colour;
   	 	} else {
   	 		return ((PieChartItem)mDataArray.get(mDataArray.size()-1)).colour;
   	 	}
    }

}
