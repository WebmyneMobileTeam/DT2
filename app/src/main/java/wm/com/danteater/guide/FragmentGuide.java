package wm.com.danteater.guide;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import wm.com.danteater.R;
import wm.com.danteater.circle_indicator.CirclePageIndicator;
import wm.com.danteater.circle_indicator.PageIndicator;


/**
 * App overview page with swipe
 *
 */

public class FragmentGuide extends Fragment {

    private PagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private PageIndicator mIndicator;

    public static FragmentGuide newInstance(String param1, String param2) {
        FragmentGuide fragment = new FragmentGuide();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    public FragmentGuide() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_guide, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.guideSlider);
        mIndicator = (CirclePageIndicator)view.findViewById(R.id.guideIndicator);
        int[]   guideImages={R.drawable.tutorial1,R.drawable.tutorial2,R.drawable.tutorial3};
        pagerAdapter= new GuideAdapter(getActivity(), guideImages);
        viewPager.setAdapter(pagerAdapter);
        mIndicator.setViewPager(viewPager);
        return view;
    }



    public class GuideAdapter extends PagerAdapter {

        Context context;
        LayoutInflater inflater;
        int[] guideImages;
        public GuideAdapter(Context context, int[] guideImages) {
            this.context = context;
            this.guideImages=guideImages;
        }

        @Override
        public int getCount() {
            return guideImages.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((ImageView) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
           ImageView guideImage=new ImageView(context);
            guideImage.setImageResource(guideImages[position]);
            ((ViewPager) container).addView(guideImage, 0);
            return guideImage;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((ImageView) object);
        }
    }



}
