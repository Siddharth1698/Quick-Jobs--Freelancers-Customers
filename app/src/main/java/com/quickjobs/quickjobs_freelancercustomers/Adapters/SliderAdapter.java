package com.quickjobs.quickjobs_freelancercustomers.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quickjobs.quickjobs_freelancercustomers.R;


public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context){
        this.context = context;
    }

    public String[] slide_headings ={
       "WORK",
       "ENJOY",
       "PAY"
    };

    public String[] slide_desc = {
            "Find jobs nearby without any hassle and you dont need to be a techy!",
            "Choose the right guy for your job and get it done!",
            "Pay the guy if you're satisfied or else dont! Get paid more for doing it in a better way"


    };

    public int[] slide_backg = {
         R.drawable.googleg_disabled_color_18,
         R.drawable.googleg_standard_color_18,
         R.drawable.send_message
    };

    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout,container,false);

        TextView heading = (TextView)view.findViewById(R.id.welcome_heading);
        TextView description = (TextView)view.findViewById(R.id.welcome_description);

        heading.setText(slide_headings[position]);
        description.setText(slide_desc[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }
}
