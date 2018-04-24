package com.app.instashare.ui.post.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.instashare.R;
import com.app.instashare.adapter.PostRVAdapter;
import com.app.instashare.adapter.UsersRVAdapter;
import com.app.instashare.ui.post.activity.AddPostActivity;
import com.app.instashare.ui.post.model.Post;
import com.app.instashare.ui.user.model.UserBasic;
import com.app.instashare.utils.Constants;

/**
 * Created by Pitisflow on 17/4/18.
 */

public class ClosePostsFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_close_posts, null);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        PostRVAdapter adapter = new PostRVAdapter();
        recyclerView.setAdapter(adapter);


        Post post = new Post();
        post.setUserImageURL("https://i.pinimg.com/originals/c9/da/eb/c9daeb9cbe4c46ff950b29ed7143bb8a.jpg");
        post.setUsername("Pitisflow");
        post.setDate("14:23");
        post.setContentText("Cum turpis ortum, omnes nuptiaes anhelare domesticus, fidelis tumultumquees. Cur ratione trabem? Gratis idoleum velox manifestums gallus est. Cur impositio ortum? Heu. Scutums sunt buxums de rusticus historia. Eheu. Nomen, valebat, et cedrium.");
        post.setContentImageURL("https://i.pinimg.com/originals/97/2a/5f/972a5faa34fa091f693ddc0a4d61c2ca.jpg");



        Post post1 = new Post();
        post1.setUserImageURL("https://i.pinimg.com/originals/97/2a/5f/972a5faa34fa091f693ddc0a4d61c2ca.jpg");
        post1.setUsername("Carlos");
        post1.setDate("15:16");
        post1.setContentText("Cum turpis ortum, omnes nuptiaes anhelare domesticus, fidelis tumultumquees. Cur ratione trabem? Gratis idoleum velox manifestums gallus est. Cur impositio ortum? Heu. Scutums sunt buxums de rusticus historia. Eheu. Nomen, valebat, et cedrium.");
        post1.setContentImageURL("https://78.media.tumblr.com/c3d24f25012f919eb38b475ce028a1b3/tumblr_p5et2fwYSw1vb9vwto1_500.jpg");



        Post post2 = new Post();
        post2.setUserImageURL("https://78.media.tumblr.com/c3d24f25012f919eb38b475ce028a1b3/tumblr_p5et2fwYSw1vb9vwto1_500.jpg");
        post2.setUsername("Marina");
        post2.setDate("15:48");
        post2.setContentText("Cum turpis ortum, omnes nuptiaes anhelare domesticus, fidelis tumultumquees. Cur ratione trabem? Gratis idoleum velox manifestums gallus est. Cur impositio ortum? Heu. Scutums sunt buxums de rusticus historia. Eheu. Nomen, valebat, et cedrium.");
        post2.setContentImageURL("https://pics.pof.com/dating/112334/3tumqu2554vemqa1rprsu4fdr502645910.jpg");



        adapter.addCard(post, Constants.CARD_POST);
        adapter.addCard(post1, Constants.CARD_POST);
        adapter.addCard(post2, Constants.CARD_POST);


        FloatingActionButton fab = view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddPostActivity.class);

                startActivity(intent);
            }
        });
    }
}
