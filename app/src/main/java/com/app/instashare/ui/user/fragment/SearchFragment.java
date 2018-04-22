package com.app.instashare.ui.user.fragment;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.icu.text.SymbolTable;
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


    private static final int SPAN_COUNT_PORTRAIT = 1;
    private static final int SPAN_COUNT_LANDSCAPE = 2;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, null);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), SPAN_COUNT_PORTRAIT));

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
                if (orientationState == 0) presenter.onSearchChanged(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        presenter = null;
    }



    @Override
    public void onResume() {
        super.onResume();

        if (recyclerState != null && recyclerItemsState != null && orientationState != 0)
        {
            if (recyclerView.getAdapter() != null && recyclerView.getAdapter() instanceof UsersRVAdapter) {
                for (Object user : recyclerItemsState)
                {
                    ((UsersRVAdapter) recyclerView.getAdapter()).addCard(user, Constants.CARD_USER_BASIC);
                }

                recyclerView.getLayoutManager().onRestoreInstanceState(recyclerState);
                editText.setText(searchState);

                if (orientationState == Configuration.ORIENTATION_LANDSCAPE) {
                    ((GridLayoutManager) recyclerView.getLayoutManager()).setSpanCount(SPAN_COUNT_LANDSCAPE);
                } else ((GridLayoutManager) recyclerView.getLayoutManager()).setSpanCount(SPAN_COUNT_PORTRAIT);


                recyclerState = null;
                recyclerItemsState = null;
                orientationState = 0;
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
            outState.putString("search", editText.getText().toString());

            if (getContext() != null) outState.putInt("orientation", getContext().getResources().getConfiguration().orientation);
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
            ((UsersRVAdapter) recyclerView.getAdapter()).addCard(user, Constants.CARD_USER_BASIC);
        }
    }
}
