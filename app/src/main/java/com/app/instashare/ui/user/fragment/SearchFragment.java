package com.app.instashare.ui.user.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.app.instashare.R;
import com.app.instashare.adapter.UsersRVAdapter;
import com.app.instashare.singleton.DatabaseSingleton;
import com.app.instashare.ui.user.model.UserBasic;
import com.app.instashare.utils.Constants;
import com.app.instashare.utils.Utils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * Created by Pitisflow on 17/4/18.
 */

public class SearchFragment extends Fragment {


    private UsersRVAdapter adapter;

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, null);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);





        System.out.println("FEWFEWFF");

        if (savedInstanceState == null) {
            recyclerView = view.findViewById(R.id.recycler);

            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            adapter = new UsersRVAdapter();
            recyclerView.setAdapter(adapter);


            UserBasic user = new UserBasic();
            user.setUsername("Pitisflow");
            user.setMainImage("https://i.pinimg.com/originals/c9/da/eb/c9daeb9cbe4c46ff950b29ed7143bb8a.jpg");
            user.setName("Javier Garcia");


            UserBasic user1 = new UserBasic();
            user1.setUsername("Maria234");
            user1.setMainImage("https://i.pinimg.com/originals/97/2a/5f/972a5faa34fa091f693ddc0a4d61c2ca.jpg");
            user1.setName("Maria Rodriguez");


            UserBasic user2 = new UserBasic();
            user2.setUsername("Laura29");
            user2.setMainImage("https://78.media.tumblr.com/c3d24f25012f919eb38b475ce028a1b3/tumblr_p5et2fwYSw1vb9vwto1_500.jpg");


            UserBasic user3 = new UserBasic();
            user3.setUsername("Raquel_S");
            user3.setMainImage("https://pics.pof.com/dating/112334/3tumqu2554vemqa1rprsu4fdr502645910.jpg");
            user3.setName("Raquel Sanchez");


            adapter.addCard(user);
            adapter.addCard(user1);
            adapter.addCard(user2);
            adapter.addCard(user3);
            adapter.addCard(user);
            adapter.addCard(user1);
            adapter.addCard(user2);
            adapter.addCard(user3);
            adapter.addCard(user);
            adapter.addCard(user1);
            adapter.addCard(user2);
            adapter.addCard(user3);


        } else {
            recyclerView = view.findViewById(R.id.recycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable("recycler"));
        }
    }






    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("recycler", recyclerView.getLayoutManager().onSaveInstanceState());
    }
}
