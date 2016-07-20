package com.opalox;

import android.app.Activity;
import android.os.Bundle;

import com.opalox.rangebarvertical.RangeBarVertical;

public class RangeBarVerticalSample extends Activity implements RangeBarVertical.OnRangeBarChangeListener {

    private RangeBarVertical rangeBarVertical;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        rangeBarVertical= (RangeBarVertical) findViewById(R.id.timer1);

//        rangeBarVertical.setMinimumProgress(40);
//        rangeBarVertical.setMaximumProgress(70);

        //   tvMin = (TextView) findViewById(R.id.tv_filter_min);


    }

    public void onRangeBarChange(int min, int max) {

        System.out.println("min:"+min);
        System.out.println("max:"+max);
    }

}