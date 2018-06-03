package com.app.instashare.ui.base.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.app.instashare.R;
import com.app.instashare.adapter.ViewPagerAdapter;
import com.app.instashare.ui.post.fragment.ClosePostsFragment;
import com.app.instashare.ui.post.fragment.FollowingPostsFragment;
import com.app.instashare.ui.user.fragment.SearchFragment;

import java.util.ArrayList;

/**
 * Created by Pitisflow on 17/4/18.
 */

public class MainFragment extends Fragment {

    private ViewPager viewPager;
    private BottomNavigationView navigationView;


    private ArrayList<Fragment> fragments;
    private String currentHomeTitle;




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        fragments = new ArrayList<>();
        fragments.add(new ClosePostsFragment());
        fragments.add(ClosePostsFragment.newInstance(true));
        fragments.add(new SearchFragment());
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViewPager(view);
        bindNavigationView(view);
    }




    private void bindViewPager(View view)
    {
        viewPager = view.findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(2);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.setFragments(fragments);


        viewPager.setAdapter(adapter);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < fragments.size(); i++)
                {
                    if (i != position) navigationView.getMenu().getItem(i).setChecked(false);
                }

                navigationView.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void bindNavigationView(View view)
    {
        navigationView = view.findViewById(R.id.navigation);

        navigationView.setOnNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.home:
                            viewPager.setCurrentItem(0);

                        case R.id.inbox:
                            viewPager.setCurrentItem(1);
                            break;

                        case R.id.search:
                            viewPager.setCurrentItem(2);
                            break;
                    }
                    return false;
                });
    }
}
