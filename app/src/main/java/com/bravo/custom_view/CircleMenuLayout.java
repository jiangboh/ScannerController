package com.bravo.custom_view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bravo.R;

public class CircleMenuLayout extends ViewGroup
{
	private int mRadius;
	/**
	 * 该容器内child item的默认尺寸
	 */
	private static final float RADIO_DEFAULT_CHILD_DIMENSION = 177 / 381f;
	/**
	 * 菜单的中心child的默认尺寸
	 */
	private float RADIO_DEFAULT_CENTERITEM_DIMENSION = 1 / 2f;
	/**
	 * 该容器的内边距,无视padding属性，如需边距请用该变量
	 */
	private static final float RADIO_PADDING_LAYOUT = 1 / 32f;
//	private static final float RADIO_PADDING_LAYOUT = 0f;

	/**
	 * 当每秒移动角度达到该值时，认为是快速移动
	 */
	private static final int FLINGABLE_VALUE = 300;

	/**
	 * 如果移动角度达到该值，则屏蔽点击
	 */
	private static final int NOCLICK_VALUE = 3;

	/*
	* 只有点击效果没有滑动
	* */
	public static final int EFFECT_ONE = 0;

	/*
	* 有滑动，中间按钮点击
	* */
	public static final int EFFECT_TWO = 1;

	/*
	* 点击后滑动，中间按钮点击
	* */
	public static final int EFFECT_THREE = 2;

	/*
	* 有滑动，中间按钮点击,选中项固定在正上方
	* */
	public static final int EFFECT_FOUR = 3;

	/*
	* 选择的效果
	* */
	private int selectedEffect = EFFECT_ONE;

	/*
	* 选中的位置
	* */
	private int selectedPos = 1;

	/**
	 * 当每秒移动角度达到该值时，认为是快速移动
	 */
	private int mFlingableValue = FLINGABLE_VALUE;
	/**
	 * 该容器的内边距,无视padding属性，如需边距请用该变量
	 */
	private float mPadding;
	/**
	 * 布局时的开始角度
	 */
	private float mStartAngle = -90;

	/**
	 * 菜单项的图标
	 */
	private int[] mItemImgs;

	/**
	 * 菜单的个数
	 */
	private int mMenuItemCount;

	/**
	 * 检测按下到抬起时旋转的角度
	 */
	private float mTmpAngle;
	/**
	 * 检测按下到抬起时使用的时间
	 */
	private long mDownTime;

	/**
	 * 判断是否正在自动滚动
	 */
	private boolean isFling;

	/*
	* 第四种选中效果处理
	* */
	private Handler handler = null;

	/*
	* 时间拦截区域
	* */
	private Rect rect = new Rect();

	public CircleMenuLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// 无视padding
		setPadding(0, 0, 0, 0);
	}

	/**
	 * 设置布局的宽高，并策略menu item宽高
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int resWidth = 0;
		int resHeight = 0;

		/**
		 * 根据传入的参数，分别获取测量模式和测量值
		 */
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);

		int height = MeasureSpec.getSize(heightMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);

		/**
		 * 如果宽或者高的测量模式非精确值
		 */
		if (widthMode != MeasureSpec.EXACTLY
				|| heightMode != MeasureSpec.EXACTLY)
		{
			// 主要设置为背景图的高度
			resWidth = getSuggestedMinimumWidth();
			// 如果未设置背景图片，则设置为屏幕宽高的默认值
			resWidth = resWidth == 0 ? getDefaultWidth() : resWidth;

			resHeight = getSuggestedMinimumHeight();
			// 如果未设置背景图片，则设置为屏幕宽高的默认值
			resHeight = resHeight == 0 ? getDefaultWidth() : resHeight;
		} else
		{
			// 如果都设置为精确值，则直接取小值；
			resWidth = resHeight = Math.min(width, height);
		}

		setMeasuredDimension(resWidth, resHeight);

		// 获得半径
		mRadius = Math.max(getMeasuredWidth(), getMeasuredHeight());

		// menu item数量
		final int count = getChildCount();
		// menu item尺寸
		int childSize = (int) (mRadius * RADIO_DEFAULT_CHILD_DIMENSION);
		// menu item测量模式
		int childMode = MeasureSpec.EXACTLY;

		// 迭代测量
		for (int i = 0; i < count; i++)
		{
			final View child = getChildAt(i);

			if (child.getVisibility() == GONE)
			{
				continue;
			}

			// 计算menu item的尺寸；以及和设置好的模式，去对item进行测量
			int makeMeasureSpec = -1;

			if (child.getId() == R.id.id_circle_menu_item_center)
			{
				makeMeasureSpec = MeasureSpec.makeMeasureSpec(
						(int) (mRadius * RADIO_DEFAULT_CENTERITEM_DIMENSION),
						childMode);
			} else
			{
				makeMeasureSpec = MeasureSpec.makeMeasureSpec(childSize,
						childMode);
			}
			child.measure(makeMeasureSpec, makeMeasureSpec);
		}

		mPadding = RADIO_PADDING_LAYOUT * mRadius;

	}

	/**
	 * MenuItem的点击事件接口
	 * 
	 * @author zhy
	 * 
	 */
	public interface OnMenuItemClickListener
	{
		void itemClick(View view, int pos);

		void itemCenterClick(View view);
	}

	/**
	 * MenuItem的点击事件接口
	 */
	private OnMenuItemClickListener mOnMenuItemClickListener;

	/**
	 * 设置MenuItem的点击事件接口
	 * 
	 * @param mOnMenuItemClickListener
	 */
	public void setOnMenuItemClickListener(
			OnMenuItemClickListener mOnMenuItemClickListener)
	{
		this.mOnMenuItemClickListener = mOnMenuItemClickListener;
	}

	public void setSelectedEffect(int selectedEffect) {
		this.selectedEffect = selectedEffect;
		if(selectedPos != -1 && selectedPos < getChildCount()){
			getChildAt(selectedPos).setSelected(false);
		}
		selectedPos = 1;
		mStartAngle = -90;
		requestLayout();
	}

	public void setSelectedPos(int selectedPos) {
		this.selectedPos = selectedPos;
	}

	public int getSelectedPos() {
		return selectedPos;
	}

	public boolean isFling() {
		return isFling;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	/**
	 * 设置menu item的位置
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		int layoutRadius = mRadius;

		// Laying out the child views
		final int childCount = getChildCount();

		int left, top;
		// menu item 的尺寸
		int cWidth = (int) (layoutRadius * RADIO_DEFAULT_CHILD_DIMENSION);
		int cHeight = cWidth * 24 / 45;

		// 根据menu item的个数，计算角度
		float angleDelay = 360 / (getChildCount() - 1);

		// 遍历去设置menuitem的位置
		for (int i = 0; i < childCount; i++)
		{
			final View child = getChildAt(i);
			if (child.getId() == R.id.id_circle_menu_item_center)
				continue;

			if (child.getVisibility() == GONE)
			{
				continue;
			}
			if(selectedEffect == EFFECT_TWO || selectedEffect == EFFECT_FOUR){
				child.setEnabled(false);
			}else{
				child.setEnabled(true);
			}
			mStartAngle %= 360;

			// 计算，中心点到menu item中心的距离
			float tmp = layoutRadius / 2f - cHeight / 2 - mPadding;

			// tmp cosa 即menu item中心点的横坐标
			left = layoutRadius
					/ 2
					+ (int) Math.round(tmp
							* Math.cos(Math.toRadians(mStartAngle)) - 1 / 2f
							* cWidth);
			// tmp sina 即menu item的纵坐标
			top = layoutRadius
					/ 2
					+ (int) Math.round(tmp
							* Math.sin(Math.toRadians(mStartAngle)) - 1 / 2f
							* cHeight);

			child.layout(left, top, left + cWidth, top + cHeight);
			ObjectAnimator//
					.ofFloat(child, "rotation", 0f, mStartAngle + 90f)//
					.setDuration(0)//
					.start();
			// 叠加尺寸
			mStartAngle += angleDelay;
		}

		// 找到中心的view，如果存在设置onclick事件
		View cView = findViewById(R.id.id_circle_menu_item_center);
		if (cView != null)
		{
			ImageView point = (ImageView) cView.findViewById(R.id.id_circle_center_point_iv);
			ImageView centerIV = (ImageView) cView.findViewById(R.id.id_circle_center_iv);

			int[] centerViewToScreen = new int[2];
			centerIV.getLocationOnScreen(centerViewToScreen);
			rect.left = centerViewToScreen[0];
			rect.top = centerViewToScreen[1];
			rect.right = rect.left + centerIV.getWidth();
			rect.bottom = rect.top + centerIV.getHeight();

			if(selectedEffect == EFFECT_ONE){
				point.setVisibility(GONE);
				centerIV.setImageResource(R.mipmap.logo);
				centerIV.setBackgroundResource(R.drawable.circle_center_bg_default);
				cView.setOnClickListener(null);
			}else{
				point.setVisibility(VISIBLE);
				centerIV.setBackgroundResource(R.drawable.circle_center_bg_selector);
				selecteEffectImage(centerIV,selectedPos);
				cView.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{

						if (mOnMenuItemClickListener != null)
						{
							mOnMenuItemClickListener.itemCenterClick(v);
						}
					}
				});
			}
			// 设置center item位置
			int cl = layoutRadius / 2 - cView.getMeasuredWidth() / 2;
			int cr = cl + cView.getMeasuredWidth();
			cView.layout(cl, cl, cr, cr);
		}

	}

	private void selecteEffectImage(ImageView centerIV, int pos){
		if(pos == -1 || pos >= getChildCount()){
			return;
		}
		if(selectedPos != -1){
			getChildAt(selectedPos).setSelected(false);
		}
		if(selectedEffect != EFFECT_FOUR){
			getChildAt(pos).setSelected(true);
		}
		selectedPos = pos;
		switch (pos){
			case 1:
				centerIV.setImageResource(R.drawable.femto_center);
				break;
			case 2:
				centerIV.setImageResource(R.drawable.config_center);
				break;
			case 3:
				centerIV.setImageResource(R.drawable.log_center);
				break;
			case 4:
				centerIV.setImageResource(R.drawable.status_center);
				break;
			case 5:
				centerIV.setImageResource(R.drawable.system_center);
				break;
			case 6:
				centerIV.setImageResource(R.drawable.test_center);
				break;
		}
	}

	/**
	 * 记录上一次的x，y坐标
	 */
	private float mLastX;
	private float mLastY;

	/**
	 * 自动滚动的Runnable
	 */
	private AutoFlingRunnable mFlingRunnable;

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {

		float x = event.getX();
		float y = event.getY();

		if(selectedEffect != EFFECT_TWO && selectedEffect != EFFECT_FOUR){
			return super.dispatchTouchEvent(event);
		}else{

			float rx = event.getRawX();
			float ry = event.getRawY();
			if(rect.left <= rx && rx <= rect.right && rect.top <= ry && rect.bottom >= ry){
				return super.dispatchTouchEvent(event);
			}
			/*if(selectedPos != -1 && selectedPos < getChildCount()){
				getChildAt(selectedPos).setSelected(false);
				selectedPos = -1;
			}*/
		}

		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:

			mLastX = x;
			mLastY = y;
			mDownTime = System.currentTimeMillis();
			mTmpAngle = 0;

			// 如果当前已经在快速滚动
			if (isFling)
			{
				// 移除快速滚动的回调
				removeCallbacks(mFlingRunnable);
				isFling = false;
				return true;
			}

			break;
		case MotionEvent.ACTION_MOVE:

			/**
			 * 获得开始的角度
			 */
			float start = getAngle(mLastX, mLastY);
			/**
			 * 获得当前的角度
			 */
			float end = getAngle(x, y);

			// Logs.e("TAG", "start = " + start + " , end =" + end);
			// 如果是一、四象限，则直接end-start，角度值都是正值
			if (getQuadrant(x, y) == 1 || getQuadrant(x, y) == 4)
			{
				mStartAngle += end - start;
				mTmpAngle += end - start;
			} else
			// 二、三象限，色角度值是付值
			{
				mStartAngle += start - end;
				mTmpAngle += start - end;
			}
			// 重新布局
			requestLayout();

			mLastX = x;
			mLastY = y;

			break;
		case MotionEvent.ACTION_UP:

			// 计算，每秒移动的角度
			float anglePerSecond = mTmpAngle * 1000
					/ (System.currentTimeMillis() - mDownTime);

			// Logs.e("TAG", anglePrMillionSecond + " , mTmpAngel = " +
			// mTmpAngle);
			if(!isFling){

				// 如果达到该值认为是快速移动
				if (Math.abs(anglePerSecond) > mFlingableValue)
				{
					// post一个任务，去自动滚动
					post(mFlingRunnable = new AutoFlingRunnable(anglePerSecond));
					return true;
				}else{
					post(new FlingRunnable());
					return true;
				}
			}

			// 如果当前旋转角度超过NOCLICK_VALUE屏蔽点击
			if (Math.abs(mTmpAngle) > NOCLICK_VALUE)
			{
				return true;
			}

			break;
		}
		return super.dispatchTouchEvent(event);
	}

	/**
	 * 主要为了action_down时，返回true
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		return true;
	}

	/**
	 * 根据触摸的位置，计算角度
	 * 
	 * @param xTouch
	 * @param yTouch
	 * @return
	 */
	private float getAngle(float xTouch, float yTouch)
	{
		double x = xTouch - (mRadius / 2d);
		double y = yTouch - (mRadius / 2d);
		return (float) (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
	}

	/**
	 * 根据当前位置计算象限
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private int getQuadrant(float x, float y)
	{
		int tmpX = (int) (x - mRadius / 2);
		int tmpY = (int) (y - mRadius / 2);
		if (tmpX >= 0)
		{
			return tmpY >= 0 ? 4 : 1;
		} else
		{
			return tmpY >= 0 ? 3 : 2;
		}

	}

	/**
	 * 设置菜单条目的图标
	 *
	 * @param resIds
	 */
	public void setMenuItemIcons(int[] resIds)
	{
		mItemImgs = resIds;

		// 参数检查
		if (resIds == null)
		{
			throw new IllegalArgumentException("菜单项文本和图片至少设置其一");
		}

		// 初始化mMenuCount
		mMenuItemCount = resIds.length;

		addMenuItems();
	}

	/**
	 * 设置MenuItem的布局文件，必须在setMenuItemIconsAndTexts之前调用
	 * 
	 * @param mMenuItemLayoutId
	 */
	public void setMenuItemLayoutId(int mMenuItemLayoutId)
	{
//		this.mMenuItemLayoutId = mMenuItemLayoutId;
	}

	/**
	 * 添加菜单项
	 */
	private void addMenuItems()
	{
		if(getChildCount()>mMenuItemCount){

			for (int i = 0; i < mMenuItemCount; i++) {
				removeViewAt(1);
			}
		}
		/**
		 * 根据用户设置的参数，初始化view
		 */
		for (int i = 0; i < mMenuItemCount; i++)
		{
			final int j = i;
			ImageView view = new ImageView (getContext());
//			view.setPadding(0,13,0,0);
//			view.setScaleType(ImageView.ScaleType.FIT_CENTER);
			view.setImageResource(mItemImgs[i]);
			view.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if(selectedEffect == EFFECT_THREE){
						post(new ClickedFlingRunnable(j));
					}else{
						if (mOnMenuItemClickListener != null){
							mOnMenuItemClickListener.itemClick(v, j);
						}
					}
				}
			});

			// 添加view到容器中
			addView(view);
		}
	}

	/**
	 * 如果每秒旋转角度到达该值，则认为是自动滚动
	 * 
	 * @param mFlingableValue
	 */
	public void setFlingableValue(int mFlingableValue)
	{
		this.mFlingableValue = mFlingableValue;
	}

	/**
	 * 设置内边距的比例
	 * 
	 * @param mPadding
	 */
	public void setPadding(float mPadding)
	{
		this.mPadding = mPadding;
	}

	/**
	 * 获得默认该layout的尺寸
	 * 
	 * @return
	 */
	private int getDefaultWidth()
	{
		WindowManager wm = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return Math.min(outMetrics.widthPixels, outMetrics.heightPixels);
	}

	private int getTopViewPosition(){
		int p = 0;
		for(int i = 1,total = getChildCount();i < total;i++){
			if(getChildAt(p).getTop()>getChildAt(i).getTop()){
				p = i;
			}
		}
		return p;
	}

	/**
	 * 自动滚动的任务
	 * 
	 * @author zhy
	 * 
	 */
	private class AutoFlingRunnable implements Runnable
	{

		private float angelPerSecond;

		public AutoFlingRunnable(float velocity)
		{
			this.angelPerSecond = velocity;
		}

		public void run()
		{
			isFling = true;
			// 如果小于20,则停止
			if ((int) Math.abs(angelPerSecond) < 20)
			{
				double temp = (mStartAngle + 30d) % 60;
//				Logs.d("123456","mStartAngle == " + mStartAngle);
				if(((int)temp) == 0){
					ImageView iv = (ImageView)findViewById(R.id.id_circle_center_iv);
					selecteEffectImage(iv,getTopViewPosition());
					if(selectedEffect == EFFECT_FOUR ){
						if(handler != null){
							Message msg = handler.obtainMessage();
							msg.what = EFFECT_FOUR;
							msg.arg1 = getTopViewPosition();
							handler.sendMessage(msg);
						}
					}
					isFling = false;
					return;
				}else{
					mStartAngle += (angelPerSecond / 20);
				}
			}else{
				// 不断改变mStartAngle，让其滚动，/30为了避免滚动太快
				mStartAngle += (angelPerSecond / 30);
				// 逐渐减小这个值
				angelPerSecond /= 1.0666F;
				if(selectedEffect == EFFECT_FOUR ){
					if(handler != null){
						Message msg = handler.obtainMessage();
						msg.what = EFFECT_FOUR;
						msg.arg1 = getTopViewPosition();
						handler.sendMessage(msg);
					}
				}
			}
			postDelayed(this, 30);
			// 重新布局
			requestLayout();
		}
	}

	/**
	 * 前后滚动的任务
	 *
	 * @author zhy
	 *
	 */
	private class FlingRunnable implements Runnable
	{
		public void run()
		{
			isFling = true;

			double temp = (mStartAngle + 30d) % 60;
//				Logs.d("123456","mStartAngle == " + mStartAngle);
			if(((int)temp) == 0) {
				ImageView iv = (ImageView)findViewById(R.id.id_circle_center_iv);
				selecteEffectImage(iv, getTopViewPosition());
				if(selectedEffect == EFFECT_FOUR ){
					if(handler != null){
						Message msg = handler.obtainMessage();
						msg.what = EFFECT_FOUR;
						msg.arg1 = getTopViewPosition();
						handler.sendMessage(msg);
					}
				}
				isFling = false;
				return;
			}else{
				if(Math.abs(temp) < 30){
					mStartAngle -= 1;
				}else{
					mStartAngle += 1;
				}
			}
			postDelayed(this, 30);
			// 重新布局
			requestLayout();
		}
	}

	/**
	 * 前后滚动的任务
	 *
	 * @author zhy
	 *
	 */
	private class ClickedFlingRunnable implements Runnable
	{
		private int scrollAngle = 0;
		private int speed = 3;

		public ClickedFlingRunnable(int clickedPos) {
			scrollAngle = (clickedPos - selectedPos + 1)*60;
			if(scrollAngle < -180){
				scrollAngle += 360;
			}
			if(scrollAngle > 180){
				scrollAngle -= 360;
			}
			if(scrollAngle == 180){
				scrollAngle = -180;
			}

			if(Math.abs(scrollAngle) == 180){
				speed = 15;
			}else if(Math.abs(scrollAngle) == 120){
				speed = 10;
			}else if(Math.abs(scrollAngle) == 60){
				speed = 5;
			}
			/*Logs.d("123456","clickedPos == " + clickedPos + "  selectedPos == "
					+ selectedPos+"   距离为：" + scrollAngle);*/
		}

		public void run()
		{
			isFling = true;
//				Logs.d("123456","mStartAngle == " + mStartAngle);
			if(Math.abs(scrollAngle%360) == 0) {
				ImageView iv = (ImageView)findViewById(R.id.id_circle_center_iv);
				selecteEffectImage(iv, getTopViewPosition());
				isFling = false;
				return;
			}else{
				if(scrollAngle < 0){
					scrollAngle += speed;
					mStartAngle += speed;
				}else{
					scrollAngle -= speed;
					mStartAngle -= speed;
				}
			}
			postDelayed(this, 30);
			// 重新布局
			requestLayout();
		}
	}
}
