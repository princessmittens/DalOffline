package com.example.achristians.gpproject;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by AChristians on 2018-05-14.
 */

public class weeklySchedule extends AppCompatActivity {

    String[] days = {"Mon","Tue","Wed","Thu","Fri"};
    ArrayList<TextView> daysNames=new ArrayList<>();
    ArrayList<LinearLayout> daysColumns = new ArrayList<>();
    LinearLayout daysLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weekly_sched);


        //https://stackoverflow.com/questions/17481341/how-to-get-android-screen-size-programmatically-once-and-for-all
        //to create columns with even size
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        daysLayout = findViewById(R.id.horizontalLayout);
        for (int i = 0;i<days.length;i++) {
            LinearLayout daysColumn = new LinearLayout(this);
            daysColumn.setOrientation(LinearLayout.VERTICAL);

            daysColumn.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            daysColumn.setHorizontalGravity(1);

            daysLayout.addView(daysColumn);
            daysColumns.add(daysColumn);
            TextView day=new TextView(this);
            day.setWidth(width/days.length);
            day.setText(days[i]);
            daysColumn.addView(day);
            daysNames.add(day);
        }
    }
}

