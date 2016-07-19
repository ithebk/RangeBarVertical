package com.opalox.rangebarvertical;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by bharath on 16/7/16.
 */
public class RangeBarVertical extends RelativeLayout {
    private static final int TOTAL_DIVISION_COUNT = 100;
    private static final int MAX_CLICK_DURATION = 200;
    public OnRangeBarChangeListener onRangeBarChangeListener;
    private int inactiveColor;
    private int activeColor;
    private double heightParent;
    private View viewFilterMain, viewThumbMin, viewThumbMax;
    private RelativeLayout relFilterMin, relFilterMax;
    private float startYMin, startYMax;
    private float movedYMin, movedYMax;
    private int initialHeightMin;
    private float dTopMin, dTopMax;
    private int currentHeightMin, currentHeightMax;
    private double resultMin = 0.0;
    private double resultMax = 100.0;
    private View viewParent;
    private TextView tvFilterMin, tvFilterMax;
    private Context context;
    private long startClickTime;
    private RelativeLayout relativeLayout;
    private int minRange = 0, maxRange = 100;
    private View viewInActiveTop, viewInActiveBottom;

    public RangeBarVertical(Context context) {
        super(context);
        this.context = context;
        initialize(context);
    }

    public RangeBarVertical(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RangeBarVertical, 0, 0);
        System.out.println(a.getIndexCount());
        activeColor = a.getColor(R.styleable.RangeBarVertical_activeColor, Color.parseColor("#007FFF"));
        inactiveColor = a.getColor(R.styleable.RangeBarVertical_inactiveColor, Color.parseColor("#808080"));
        a.recycle();
        initialize(context);
    }

    public RangeBarVertical(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initialize(context);
    }


    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    private void initialize(Context context) {
        inflate(context, R.layout.rangebar_view_temp, this);
        onRangeBarChangeListener = (OnRangeBarChangeListener) context;
        onRangeBarChangeListener.onRangeBarChange((int) resultMin, (int) resultMax);
        relativeLayout = (RelativeLayout) findViewById(R.id.rel_main);
        tvFilterMin = (TextView) findViewById(R.id.tv_filter_min);
        tvFilterMax = (TextView) findViewById(R.id.tv_filter_max);
        relFilterMin = (RelativeLayout) findViewById(R.id.rel_filter_min);
        relFilterMax = (RelativeLayout) findViewById(R.id.rel_filter_max);
        viewThumbMax = findViewById(R.id.oval_thumb_max);
        viewThumbMin = findViewById(R.id.oval_thumb_min);
        viewFilterMain = findViewById(R.id.filter_main_view);
        viewParent = findViewById(R.id.view_filter_parent);
        viewInActiveTop = findViewById(R.id.view_inactive_line_top);
        viewInActiveBottom = findViewById(R.id.view_inactive_line_bottom);


        init();

        relFilterMin.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startYMin = event.getRawY();
                        // startClickTime = Calendar.getInstance().getTimeInMillis();
                        break;
                    case MotionEvent.ACTION_UP: {
//                        long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
//                        if (clickDuration < MAX_CLICK_DURATION) {
//                            //Click event triggered
//
//                        }
                        break;
                    }

                    case MotionEvent.ACTION_MOVE:
                        movedYMin = event.getRawY() - startYMin;
                        startYMin = event.getRawY();
                        if (v.getHeight() + movedYMin <= initialHeightMin || dTopMin + v.getHeight() + movedYMin >= dTopMax) {
                            currentHeightMin = v.getHeight();
                            getResultMin();
                            break;
                        }

                        ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                        layoutParams.height += movedYMin;
                        v.setLayoutParams(layoutParams);
                        dTopMin = v.getY();
                        currentHeightMin = v.getHeight();
                        getResultMin();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });

        relFilterMax.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startYMax = event.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        movedYMax = event.getRawY() - startYMax;
                        startYMax = event.getRawY();
                        if (v.getHeight() - movedYMax <= initialHeightMin || v.getY() + movedYMax <= currentHeightMin + dTopMin) {
                            currentHeightMax = v.getHeight();
                            getResultMax();
                            break;
                        }

                        ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                        layoutParams.height -= movedYMax;
                        v.setLayoutParams(layoutParams);
                        dTopMax = v.getY();
                        currentHeightMax = v.getHeight();
                        getResultMax();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });


    }

    private void init() {
        ViewCompat.setElevation(tvFilterMin, 100f);
        viewFilterMain.setBackgroundColor(activeColor);
        viewInActiveBottom.setBackgroundColor(inactiveColor);
        viewInActiveTop.setBackgroundColor(inactiveColor);
        initialHeightMin = (int) convertDpToPixel(30, context);
        final ViewTreeObserver viewTreeObserver = relativeLayout.getViewTreeObserver();
        //  if (viewTreeObserver.isAlive())
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    viewParent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    viewParent.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                dTopMin = relFilterMin.getY();
                dTopMax = relFilterMax.getY();
                currentHeightMin = relFilterMin.getHeight();
                System.out.println("viewParentGetHeight:" + viewParent.getHeight());
                heightParent = viewParent.getHeight() - 2 * initialHeightMin;

            }
        });


    }

    public void getResultMin() {
        //Max
        resultMin = Math.floor(100 * (Math.abs(currentHeightMin - initialHeightMin)) / heightParent);
        tvFilterMin.setText((int) resultMin + "");
        onRangeBarChangeListener.onRangeBarChange((int) resultMin, (int) resultMax);

    }

    public void getResultMax() {
        resultMax = Math.floor(100 * (Math.abs(currentHeightMax - initialHeightMin)) / heightParent);
        resultMax = Math.abs(resultMax - 100);
        tvFilterMax.setText(((int) resultMax + ""));
        onRangeBarChangeListener.onRangeBarChange((int) resultMin, (int) resultMax);
    }

    public int getMinimumProgress() {
        return (int) resultMin;
    }

    public void setMinimumProgress(final int minProgress) {
        if (minProgress >= 0 && minProgress < 100 && minProgress < resultMax) {
            resultMin = minProgress;
            viewParent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        viewParent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        viewParent.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                    currentHeightMin = ((minProgress * (viewParent.getHeight() - 2 * initialHeightMin) / 100) + initialHeightMin);
                    ViewGroup.LayoutParams layoutParams = relFilterMin.getLayoutParams();
                    layoutParams.height = currentHeightMin;
                    relFilterMin.setLayoutParams(layoutParams);
                }
            });
            tvFilterMin.setText((int) resultMin + "");
            onRangeBarChangeListener.onRangeBarChange((int) resultMin, (int) resultMax);
        }
    }

    public int getMaximumProgress() {
        return (int) resultMax;
    }

    public void setMaximumProgress(final int maxProgress) {
        if (maxProgress >= 0 && maxProgress <= 100 && maxProgress > resultMin) {
            resultMax = maxProgress;
            viewParent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        viewParent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        viewParent.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                    currentHeightMax = ((Math.abs(maxProgress - 100) * (viewParent.getHeight() - 2 * initialHeightMin) / 100) + initialHeightMin);
                    ViewGroup.LayoutParams layoutParams = relFilterMax.getLayoutParams();
                    layoutParams.height = currentHeightMax;
                    relFilterMax.setLayoutParams(layoutParams);
                }
            });
            tvFilterMax.setText((int) resultMax + "");
            onRangeBarChangeListener.onRangeBarChange((int) resultMin, (int) resultMax);
        }
    }

    public interface OnRangeBarChangeListener {
        void onRangeBarChange(int min, int max);
    }


}
