package com.app.instashare.ui.view_holders;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.instashare.R;
import com.app.instashare.adapter.PostRVAdapter;
import com.app.instashare.interactor.PostInteractor;
import com.app.instashare.interactor.UserInteractor;
import com.app.instashare.ui.post.model.Post;
import com.app.instashare.utils.Constants;
import com.app.instashare.utils.DateUtils;
import com.app.instashare.utils.Utils;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Pitisflow on 20/4/18.
 */

public class PostViewHolder extends RecyclerView.ViewHolder {

    private CircleImageView userImage;
    private TextView username;
    private TextView date;
    private TextView contentText;
    private TextView likes;
    private TextView comments;
    private ImageView contentImage;


    private Button likeButton;
    private Button commentButton;
    private Button shareButton;
    private ImageButton optionsButton;


    private static final int BUTTON_LIKE = 1;
    private static final int BUTTON_COMMENT = 2;
    private static final int BUTTON_SHARE = 3;
    private static final int SHORT_TEXT_SIZE = 500;


    public PostViewHolder(View itemView) {
        super(itemView);

        userImage = itemView.findViewById(R.id.userImage);
        username = itemView.findViewById(R.id.title);
        date = itemView.findViewById(R.id.date);
        contentText = itemView.findViewById(R.id.contentText);
        likes = itemView.findViewById(R.id.likesNumber);
        comments = itemView.findViewById(R.id.commentsNumber);
        contentImage = itemView.findViewById(R.id.contentImage);

        likeButton = itemView.findViewById(R.id.likeButton);
        commentButton = itemView.findViewById(R.id.commentButton);
        shareButton = itemView.findViewById(R.id.shareButton);
        optionsButton = itemView.findViewById(R.id.options);


        username.setMovementMethod(LinkMovementMethod.getInstance());
        contentText.setMovementMethod(LinkMovementMethod.getInstance());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            contentText.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        }
    }



    public void bind(Post post, Context context, PostRVAdapter.OnPostInteraction postInteractor)
    {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        contentImage.getLayoutParams().width = metrics.widthPixels > metrics.heightPixels ?
                metrics.heightPixels : metrics.widthPixels;


        //BINDING USER
        date.setText(DateUtils.getPostDateFromTimestamp(post.getTimestamp(), context));
        userImage.setCircleBackgroundColor(context.getResources().getColor(R.color.black));
        if (post.isAnonymous()){
            userImage.setImageResource(R.mipmap.ic_launcher);
            userImage.setOnClickListener(null);
            username.setText(context.getString(R.string.post_anonymous));
        }
        else {
            Picasso.get()
                    .load(post.getUser().getMainImage())
                    .resize(userImage.getLayoutParams().width, userImage.getLayoutParams().height)
                    .into(userImage);
            userImage.setOnClickListener((view) -> postInteractor.onUserClicked(post.getUser().getUserKey()));


            SpannableString spannableString = Utils.getSpannableFromString(post.getUser().getUsername(),
                    context.getResources().getColor(R.color.black), false, false, () -> {
                        postInteractor.onUserClicked(post.getUser().getUserKey());
                    });
            username.setText(spannableString);
        }



        //BINDING POST IMAGE
        if (post.getMediaURL() != null) {
            contentImage.setVisibility(View.VISIBLE);
            contentImage.setOnClickListener((view -> postInteractor.onImageClicked(post.getMediaURL(), contentImage)));


            Picasso.get()
                    .load(post.getMediaURL())
                    .resize(contentImage.getLayoutParams().width, 0)
                    .into(contentImage);
        } else {
            contentImage.setVisibility(View.GONE);
            contentImage.setOnClickListener(null);
        }




        //BINDING POST CONTENT TEXT
        if (post.getContentText() != null && !post.getType().equals(PostInteractor.POST_TYPE_SHARED))
        {
            contentText.setText(Utils.urlChecker(post.getContentText(), context));
            if (post.getContentText().length() > SHORT_TEXT_SIZE) {
                String sortText = post.getContentText().substring(0, SHORT_TEXT_SIZE) + "… ";

                contentText.setText(TextUtils.concat(Utils.urlChecker(sortText, context),
                        setContentTextMore(post.getContentText(), context)));
            }
        } else if (post.getContentText() != null)
        {
            String text = context.getString(R.string.post_shared_text);
            String[] words = text.split(" ");

            SpannableString ss = Utils.getSpannableFromString(words[words.length - 1],
                    context.getResources().getColor(R.color.colorPrimary), false, true,
                    () -> postInteractor.onSharedPostClicked(post));

            words[words.length - 1] = "";

            contentText.setText(TextUtils.concat(TextUtils.join(" ", words), ss));
        }
        setLikesNumber(post.getNumLikes(), context);
        setCommentsNumber(post.getNumComments(), context);




        //BINDING BUTTON
        setButton(likeButton, context, false, BUTTON_LIKE);
        setButton(commentButton, context, false, BUTTON_COMMENT);
        setButton(shareButton, context, false, BUTTON_SHARE);

        if (UserInteractor.getUserKey() != null) {
            PostInteractor.checkPostOnList(post, UserInteractor.getUserKey(), Constants.POSTS_LIKED_T,
                    new PostInteractor.OnCheckedList() {
                        @Override
                        public void isOnList() {
                            setButton(likeButton, context, true, BUTTON_LIKE);
                            post.setLiked(true);
                        }

                        @Override
                        public void isNotOnlist() {
                            setButton(likeButton, context, false, BUTTON_LIKE);
                            post.setLiked(false);
                        }
                    });

            PostInteractor.checkPostOnList(post, UserInteractor.getUserKey(), Constants.POSTS_SHARED_T,
                    new PostInteractor.OnCheckedList() {
                        @Override
                        public void isOnList() {
                            setButton(shareButton, context, true, BUTTON_SHARE);
                            post.setShared(true);
                        }

                        @Override
                        public void isNotOnlist() {
                            setButton(shareButton, context, false, BUTTON_SHARE);
                            post.setShared(false);
                        }
                    });
        }



        likeButton.setOnClickListener((view)-> {
            if (post.isLiked()) {
                setLikesNumber(post.getNumLikes() - 1, context);
                post.setNumLikes(post.getNumLikes() - 1);
                setButton(likeButton, context, false, BUTTON_LIKE);
                post.setLiked(false);
            }
            else {
                setLikesNumber(post.getNumLikes() + 1, context);
                post.setNumLikes(post.getNumLikes() + 1);
                setButton(likeButton, context, true, BUTTON_LIKE);
                post.setLiked(true);
            }

            postInteractor.onLikeClicked(post, post.isLiked());
        });
        shareButton.setOnClickListener((view)-> postInteractor.onShareClicked(post, post.isShared()));
        commentButton.setOnClickListener((view)-> postInteractor.onCommentClicked(post));
        optionsButton.setOnClickListener((view)-> postInteractor.onOptionsClicked(post, optionsButton));

        itemView.setOnLongClickListener((view)-> {
            postInteractor.onPostLongClicked(post);
            return true;
        });
    }


    private void setButton(Button button, Context context, boolean isPressed, int buttonType)
    {
        int color;
        Drawable drawable = null;

        if(isPressed) color = R.color.colorPrimary;
        else color = R.color.grayDark;

        switch (buttonType)
        {
            case BUTTON_LIKE:
                if (isPressed) drawable = context.getDrawable(R.drawable.ic_thumb_up_black_24);
                else drawable = context.getDrawable(R.drawable.ic_outline_thumb_up_black_24);
                break;

            case BUTTON_COMMENT:
                drawable = context.getDrawable(R.drawable.ic_mode_comment_black_24);
                break;

            case BUTTON_SHARE:
                if (isPressed) drawable = context.getDrawable(R.drawable.ic_reply_black_24);
                else drawable = context.getDrawable(R.drawable.ic_outline_reply_black_24);
                break;
        }

        drawable = Utils.changeDrawableColor(drawable, color, context);

        button.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);
        button.setTextColor(context.getResources().getColor(color));
    }



    private CharSequence setContentTextMore(String text, Context context)
    {
        SpannableString seeMore = Utils.getSpannableFromString(context.getString(R.string.post_options_see_more),
                context.getResources().getColor(R.color.grayDark),
                false, true, () -> {
                    contentText.setText(TextUtils.concat(Utils.urlChecker(text, context), setContentTextLess(text, context)));
                });

        return seeMore;
    }


    private CharSequence setContentTextLess(String text, Context context)
    {
        SpannableString seeLess = Utils.getSpannableFromString(context.getString(R.string.post_options_see_less),
                context.getResources().getColor(R.color.grayDark),
                false, true, () -> {
                    String sortText = text.substring(0, SHORT_TEXT_SIZE) + "… ";
                    contentText.setText(TextUtils.concat(Utils.urlChecker(sortText, context), setContentTextMore(text, context)));
                });

        return seeLess;
    }

    private void setLikesNumber(long number, Context context)
    {
        if (number == 1) likes.setText(context.getString(R.string.post_likes_single));
        else if (number > 1) likes.setText(context.getString(R.string.post_likes_plural, number));
        else likes.setText("");
    }


    private void setCommentsNumber(long number, Context context)
    {
        if (number == 1) comments.setText(context.getString(R.string.post_comments_single));
        else if (number > 1) comments.setText(context.getString(R.string.post_comments_plural, number));
        else comments.setText("");
    }
}
