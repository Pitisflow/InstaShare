package com.app.instashare.ui.user.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.app.instashare.R;
import com.app.instashare.adapter.UsersRVAdapter;
import com.app.instashare.custom.SquareImagesDecorator;
import com.app.instashare.interactor.UserInteractor;
import com.app.instashare.utils.Constants;

/**
 * Created by Pitisflow on 2/6/18.
 */

public class UserChangeBackgroundActivity extends AppCompatActivity implements UsersRVAdapter.OnImageClick{

    private RecyclerView recyclerView;
    private UsersRVAdapter adapter;


    private String[] imagesURLs = {"https://upload.wikimedia.org/wikipedia/commons/d/d1/Mount_Everest_as_seen_from_Drukair2_PLW_edit.jpg",
            "http://medienportal.univie.ac.at/typo3temp/pics/cd09f0a626.jpg",
            "https://i.ytimg.com/vi/dpknkw2QzTA/maxresdefault.jpg",
            "https://imagenescityexpress.scdn6.secure.raxcdn.com/sites/default/files/2017-02/shutterstock_365514371.jpg",
            "http://www.hdfondos.eu/pictures/2012/1219/1/melbourne-australia-world-cities-skyline-cityscape-hdr-rivers-water-canal-night-lights-reflection-color-path-sidewalk-walk-park-sky-stars-scenic-view-window-background-151997.jpg",
            "http://www.historiasdenuestroplaneta.com/imagenes//2013/10/rascacielos-dubai-edificios-mas-alto-mundo.jpg",
            "https://i.ytimg.com/vi/XkEzEXTgab4/hqdefault.jpg",
            "http://assets4.bigthink.com/system/idea_thumbnails/63901/size_1024/wormholecropped.jpg?1510600578",
            "https://st-listas.20minutos.es/images/2014-07/384245/list_640px.jpg?1405272726",
            "https://cdn.pixabay.com/photo/2015/09/03/07/06/ocean-920085_960_720.jpg",
            "https://www.definicionabc.com/wp-content/uploads/oceano.jpg",
            "https://upload.wikimedia.org/wikipedia/commons/4/42/Palma_Aquarium-La_jungla.jpg",
            "https://sofiserrano98.files.wordpress.com/2014/03/colombia.jpg",
            "https://www.proandroid.com/wp-content/uploads/2017/04/E36.jpg",
            "https://1079638729.rsc.cdn77.org/pic/v2/gallery/preview/doma-ozera-pejzazh-zima-22041.jpg",
            "https://i.pinimg.com/originals/05/4b/46/054b461eb95830a7f1d6d3108455b29c.jpg"};


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_background);


        ConstraintLayout content = findViewById(R.id.content);
        Snackbar.make(content, getString(R.string.profile_change_background_select), Snackbar.LENGTH_INDEFINITE).show();

        bindRecyclerView();
        bindToolbarView();
    }


    private void bindRecyclerView()
    {
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new UsersRVAdapter(getApplicationContext(), getWindowManager());
        adapter.setImageClickListener(this);

        SquareImagesDecorator decorator = new SquareImagesDecorator(getWindowManager(), getResources());
        recyclerView.addItemDecoration(decorator);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        recyclerView.setAdapter(adapter);

        for (int i = 0; i < imagesURLs.length; i++) {
            adapter.addCard(imagesURLs[i], Constants.CARD_USER_IMAGE);
        }
    }

    private void bindToolbarView()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            getSupportActionBar().setTitle(getString(R.string.profile_menu_background));
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void imageClicked(String imageURL) {
        if (UserInteractor.getUserKey() != null) {
            UserInteractor.setBackgroundImage(UserInteractor.getUserKey(), imageURL);
            finish();
        }
    }

    @Override
    public void imageLongClicked(String imageURL) {

    }
}
