package com.app.instashare.ui.user.fragment;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.app.instashare.R;
import com.app.instashare.adapter.UsersRVAdapter;
import com.app.instashare.singleton.DatabaseSingleton;
import com.app.instashare.ui.user.model.UserBasic;
import com.app.instashare.ui.user.presenter.SearchPresenter;
import com.app.instashare.ui.user.view.SearchView;
import com.app.instashare.utils.Constants;
import com.app.instashare.utils.Utils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by Pitisflow on 17/4/18.
 */

public class SearchFragment extends Fragment implements SearchView{

    private RecyclerView recyclerView;
    private EditText editText;
    private SearchPresenter presenter;


    private String searchState;
    private Parcelable recyclerState = null;
    private ArrayList<Parcelable> recyclerItemsState = null;
    private int orientationState = 0;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, null);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);





//        UserBasic user = new UserBasic();
//        user.setUsername("Pitisflow");
//        user.setMainImage("https://i.pinimg.com/originals/c9/da/eb/c9daeb9cbe4c46ff950b29ed7143bb8a.jpg");
//        user.setName("Javier Garcia");
//
//
//        UserBasic user1 = new UserBasic();
//        user1.setUsername("Maria234");
//        user1.setMainImage("https://i.pinimg.com/originals/97/2a/5f/972a5faa34fa091f693ddc0a4d61c2ca.jpg");
//        user1.setName("Maria Rodriguez");
//
//
//        UserBasic user2 = new UserBasic();
//        user2.setUsername("Laura29");
//        user2.setMainImage("https://78.media.tumblr.com/c3d24f25012f919eb38b475ce028a1b3/tumblr_p5et2fwYSw1vb9vwto1_500.jpg");
//
//
//        UserBasic user3 = new UserBasic();
//        user3.setUsername("Raquel_S");
//        user3.setMainImage("https://pics.pof.com/dating/112334/3tumqu2554vemqa1rprsu4fdr502645910.jpg");
//        user3.setName("Raquel Sanchez");
//
//
//        adapter.addCard(user);
//        adapter.addCard(user1);
//        adapter.addCard(user2);
//        adapter.addCard(user3);
//        adapter.addCard(user);
//        adapter.addCard(user1);
//        adapter.addCard(user2);
//        adapter.addCard(user3);
//        adapter.addCard(user);
//        adapter.addCard(user1);
//        adapter.addCard(user2);
//        adapter.addCard(user3);



        if (savedInstanceState != null )
        {
            recyclerItemsState = savedInstanceState.getParcelableArrayList("recyclerItems");
            recyclerState = savedInstanceState.getParcelable("recycler");
            orientationState = savedInstanceState.getInt("orientation");
            searchState = savedInstanceState.getString("search");
        }


        presenter = new SearchPresenter(getContext(), this);

        bindEditView(view);
        bindRecyclerView(view);


    }




    private void bindRecyclerView(View view)
    {
        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(new UsersRVAdapter());
    }



    private void bindEditView(View view)
    {
        editText = view.findViewById(R.id.editText);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (orientationState != 1) {
                    presenter.onSearchChanged(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }




    @Override
    public void onResume() {
        super.onResume();

        if (recyclerState != null && recyclerItemsState != null)
        {
            if (recyclerView.getAdapter() != null && recyclerView.getAdapter() instanceof UsersRVAdapter) {
                for (Object user : recyclerItemsState)
                {
                    ((UsersRVAdapter) recyclerView.getAdapter()).addCard(user);
                }

                recyclerView.getLayoutManager().onRestoreInstanceState(recyclerState);
                orientationState = 0;
                editText.setText(searchState);
            }
        }
    }




    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);


        if (recyclerView.getAdapter() != null && recyclerView.getAdapter() instanceof UsersRVAdapter)
        {
            ArrayList<UserBasic> users = new ArrayList<>();
            for (Object user : ((UsersRVAdapter) recyclerView.getAdapter()).getItemList())
            {
                if (user instanceof UserBasic) users.add((UserBasic) user);
            }


            outState.putParcelableArrayList("recyclerItems", users);
            outState.putParcelable("recycler", recyclerView.getLayoutManager().onSaveInstanceState());
            outState.putInt("orientation", 1);
            outState.putString("search", editText.getText().toString());
        }
    }

    @Override
    public void refreshRecycler() {
        if (recyclerView.getAdapter() != null && recyclerView.getAdapter() instanceof UsersRVAdapter) {
            ((UsersRVAdapter) recyclerView.getAdapter()).removeAllCards();
        }
    }

    @Override
    public void addCard(UserBasic user) {
        if (recyclerView.getAdapter() != null && recyclerView.getAdapter() instanceof UsersRVAdapter) {
            ((UsersRVAdapter) recyclerView.getAdapter()).addCard(user);
        }
    }
}
