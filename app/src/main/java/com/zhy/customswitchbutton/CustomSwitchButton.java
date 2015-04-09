package com.zhy.customswitchbutton;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CustomSwitchButton extends View implements View.OnClickListener {
    private Bitmap btnBackground;
    private Bitmap btnSwitch;
    private boolean switchState;

    /**
     * 画笔对象
     */
    private Paint paint;

    /**
     * 滑动的距离
     */
    private float offset;
    /**
     * 是否发生拖动
     */
    private boolean isDrag = false;

    /**
     * 代码中new出来的，执行次构造方法
     * @param context
     */
    public CustomSwitchButton(Context context) {
        this(context, null);
    }

    /**
     * xml中使用时，系统会调用此构造方法
     * @param context
     * @param attrs
     */

    public CustomSwitchButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSwitchButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {

        paint = new Paint();
        paint.setAntiAlias(true);//设置坑锯齿


        /**
         * 获取各个属性的值
         */
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CustomSwitchButton);
        btnBackground = BitmapFactory.decodeResource(getResources(),
                ta.getResourceId(R.styleable.CustomSwitchButton_btnBackground, 0));
        btnSwitch = BitmapFactory.decodeResource(getResources(),
                ta.getResourceId(R.styleable.CustomSwitchButton_btnSwitch, 0));
        switchState = ta.getBoolean(R.styleable.CustomSwitchButton_switchState, false);

        //注册单机事件
        setOnClickListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(btnBackground.getWidth(), btnBackground.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        /**
         * 绘制背景
         *Bitmap bitmap 要绘制的图像
         *float left 左边距
         *float top  上边距
         *Paint paint 画笔对象
         */
        canvas.drawBitmap(btnBackground, 0, 0, paint);
        /**
         * 绘制开关
         */
        canvas.drawBitmap(btnSwitch, offset, 0, paint);
    }


    /**
     * down 事件时的X坐标
     */
    private float firstX;
    /**
     * up 事件时上次的X坐标
     */
    private float lastX;

    /**
     * 重写onTouchEvent实现滑动效果
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        float curX = event.getX();
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                isDrag = false;
                firstX = lastX = curX;
                break;
            case MotionEvent.ACTION_MOVE:
                //判定是否进行了滑动
                if (Math.abs(lastX - firstX) > 5) {
                    isDrag = true;
                }
                float dis = curX - lastX;
                offset += dis;
                lastX = curX;
                break;
            case MotionEvent.ACTION_UP:
                //未滑完时，判定最终的开关状态
                if (isDrag) {
                    //能滑动的最大距离
                    float maxDis = btnBackground.getWidth() - btnSwitch.getWidth();
                    switchState = offset > maxDis / 2 ? true : false;
                    changeState();
                }
                break;
        }

        refreshView();
        return true;
    }

    /**
     * 刷新界面
     */
    private void refreshView() {
        //判断是否已经超出边界
        float maxDis = btnBackground.getWidth() - btnSwitch.getWidth();
        offset = offset < 0 ? 0 : offset;
        offset = offset > maxDis ? maxDis : offset;
        invalidate();
    }

    @Override
    public void onClick(View v) {
        if (!isDrag) {
            switchState = !switchState;
            changeState();
        }

    }

    private void changeState() {
        offset = switchState ? btnBackground.getWidth() - btnSwitch.getWidth() : 0;
        //重绘制界面
        invalidate();
    }
}
