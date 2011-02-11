package org.wolink.app.freepuzzle;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class PuzzleView extends View{
	private ImageBlock mBlocks[]; 
	private int mFloatPos;
	private int mViewWidth;
	private int mViewHeight;
	private int mBlockWidth;
	private int mBlockHeight;
	private int mHBlocks;
	private int mVBlocks;
	private int mFloatX, mFloatY, mFloatW, mFloatH;
	private Paint mSepPaint;
	
	public PuzzleView(Context context, Bitmap bitmap, int w, int h){
		super(context);
		mViewWidth = bitmap.getWidth();
		mViewHeight = bitmap.getHeight();
		mBlockWidth = w;
		mBlockHeight = h;
		mHBlocks = mViewWidth / mBlockWidth;
		mVBlocks = mViewHeight / mBlockHeight;
		mBlocks = new ImageBlock[mHBlocks * mVBlocks];
		
        for(int i = 0; i < mBlocks.length; i++) {
        	Bitmap bm = Bitmap.createBitmap(bitmap, 
        		i % mHBlocks * mBlockWidth , i / mHBlocks * mBlockHeight, 
        		mBlockWidth, mBlockHeight);
        	mBlocks[i] = new ImageBlock(bm, i);
        }
        
        Random rdm = new Random(System.currentTimeMillis());
        for(int i = 0; i < mBlocks.length - 1; i++) {
        	int randPos = Math.abs(rdm.nextInt()) % (mBlocks.length - 1);
        	ImageBlock temp = mBlocks[i];
        	mBlocks[i] = mBlocks[randPos];
        	mBlocks[randPos] = temp;
        }	
        
        mFloatPos = -1;
        
        mSepPaint = new Paint();
        mSepPaint.setStyle(Paint.Style.FILL);
        mSepPaint.setColor(0x88FFFFFF);
	}
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	setMeasuredDimension(mViewWidth, mViewHeight);
    }
    
    @Override 
    protected void onDraw(Canvas canvas) {
        for(int i = 0; i < mBlocks.length; i++) {
        	int col = i % mHBlocks;
        	int row = i / mHBlocks;
        	
        	if (i != mFloatPos) {
        		int x = col * mBlockWidth;
        		int y = row * mBlockHeight;
        		canvas.drawBitmap(mBlocks[i].mBm, x, y, null);     
        	}
        	
        	if (col != (mHBlocks - 1)) {
        		if (mBlocks[i].mId != mBlocks[i + 1].mId - 1) {
        			// Draw right line ...
        			canvas.drawLine((col + 1) * mBlockWidth - 1, 
        					row * mBlockHeight, (col + 1) * mBlockWidth, 
        					(row + 1) * mBlockHeight, mSepPaint);
        		}
        	}
        	
        	if (row != (mVBlocks - 1)) {
        		if (mBlocks[i].mId != mBlocks[i + mHBlocks].mId - mHBlocks) {
        			// Draw bottom line ...
           			canvas.drawLine(col * mBlockWidth, 
        					(row + 1) * mBlockHeight - 1, (col + 1) * mBlockWidth, 
        					(row + 1) * mBlockHeight, mSepPaint);
        		}
        	}
        }
        
        if (mFloatPos != -1) {
        	canvas.drawBitmap(mBlocks[mFloatPos].mBm, mFloatX - mFloatW, mFloatY - mFloatH, null);
        }
    }
    
    private class ImageBlock { 	
    	public Bitmap mBm = null;
    	public int mId = 0;
    	
    	public ImageBlock(Bitmap bm, int id) {
    		mBm = bm;
    		mId = id;
    	}
    }

	@Override
	public boolean onTouchEvent(MotionEvent event){
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mFloatPos = (int)event.getY() / mBlockHeight * mHBlocks + 
				(int)event.getX() / mBlockWidth;
			mFloatX = (int)event.getX();
			mFloatY = (int)event.getY();
			mFloatW = mFloatX - mFloatPos % mHBlocks * mBlockWidth ;
			mFloatH = mFloatY - mFloatPos / mHBlocks * mBlockHeight;
			//Log.e("PuzzleView", "ACTION_DOWN " + event.getX() + " " + event.getY());
			break;
		case MotionEvent.ACTION_MOVE:
			if (mFloatPos != -1) {
				mFloatX = (int)event.getX();
				mFloatY = (int)event.getY();
				this.invalidate();
			}
			//Log.e("PuzzleView", "ACTION_MOVE " + event.getX() + " " + event.getY());
			break;
		case MotionEvent.ACTION_UP:
			if (mFloatPos != -1) {
				mFloatX = (int)event.getX();
				mFloatY = (int)event.getY();
				if (mFloatX < 0 || mFloatX > mViewWidth || mFloatY < 0 || mFloatY > mViewHeight) {
					// Nothing;
				} else {
					int newPos = (int)event.getY() / mBlockHeight * mHBlocks + 
						(int)event.getX() / mBlockWidth;
					ImageBlock temp = mBlocks[mFloatPos];
					mBlocks[mFloatPos] = mBlocks[newPos];
					mBlocks[newPos] = temp;
				}
				
				mFloatPos = -1;
				this.invalidate();
				// check finish 
			}
			//Log.e("PuzzleView", "ACTION_UP " + event.getX() + " " + event.getY());
			break;
		}
		return true;
	}
}
