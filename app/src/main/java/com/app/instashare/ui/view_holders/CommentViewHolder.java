package com.app.instashare.ui.view_holders;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.SpannableString;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.instashare.R;
import com.app.instashare.adapter.PostRVAdapter;
import com.app.instashare.custom.AudioBar;
import com.app.instashare.ui.post.model.Comment;
import com.app.instashare.utils.DateUtils;
import com.app.instashare.utils.Utils;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Pitisflow on 30/5/18.
 */

public class CommentViewHolder extends RecyclerView.ViewHolder {

    private CircleImageView userImage;
    private TextView username;
    private TextView commentText;
    private TextView date;
    private AudioBar commentAudio;
    private ImageButton commentOptions;

    private RelativeLayout commentContent;


    public CommentViewHolder(View itemView) {
        super(itemView);

        userImage = itemView.findViewById(R.id.userImage);
        username = itemView.findViewById(R.id.username);
        commentText = itemView.findViewById(R.id.commentText);
        date = itemView.findViewById(R.id.date);
        commentAudio = itemView.findViewById(R.id.commentAudio);
        commentOptions = itemView.findViewById(R.id.commentOptions);
        commentContent = itemView.findViewById(R.id.commentContent);

        commentAudio.setIconsColor(R.color.black);
        commentAudio.setSeekBarColor(R.color.black);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            commentText.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        }
    }


    public void bind(Comment comment, Context context, PostRVAdapter.OnCommentInteraction listener)
    {

        //BINDING USER IMAGE
        userImage.setCircleBackgroundColor(context.getResources().getColor(R.color.black));
        Picasso.get().load(comment.getUser().getMainImage())
                .resize(userImage.getLayoutParams().width, userImage.getLayoutParams().height)
                .error(R.mipmap.ic_launcher)
                .placeholder(R.mipmap.ic_launcher)
                .into(userImage);


        //BINDING TEXT VIEWS
        SpannableString usernameSS = Utils.getSpannableFromString(comment.getUser().getUsername(),
                context.getResources().getColor(R.color.black), false, false, () ->{
                    listener.onUserClicked(comment.getUser().getUserKey());
                });
        username.setText(usernameSS);
        commentText.setText(comment.getCommentText());
        date.setText(DateUtils.getPostDateFromTimestamp(comment.getTimestamp(), context));


        //BINDING AUDIO
        if (comment.getAudioURL() != null) commentAudio.setAsyncFile(comment.getAudioURL());
        else commentAudio.setVisibility(View.GONE);



        //BINDING OPTIONS
        Drawable drawable = Utils.changeDrawableColor(context.getDrawable(R.drawable.ic_keyboard_arrow_down_black_24),
                R.color.grayDark, context);
        commentOptions.setImageDrawable(drawable);
        commentOptions.setOnClickListener((view) -> listener.onOptionsClicked(comment));


        //OTHER
        if (comment.isNew())
        {
            itemView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryTransparent));

            ValueAnimator colorAnim = ObjectAnimator.ofInt(itemView, "backgroundColor",
                    context.getResources().getColor(R.color.colorPrimaryTransparent),
                    context.getResources().getColor(R.color.white));

            colorAnim.setDuration(3000);
            colorAnim.setEvaluator(new ArgbEvaluator());
            colorAnim.setRepeatCount(0);


            ValueAnimator colorAnim2 = ObjectAnimator.ofInt(commentContent, "backgroundColor",
                    context.getResources().getColor(R.color.colorPrimaryTransparent),
                    context.getResources().getColor(R.color.grayBackground));

            colorAnim2.setDuration(3000);
            colorAnim2.setEvaluator(new ArgbEvaluator());
            colorAnim2.setRepeatCount(0);

            colorAnim.start();
            colorAnim2.start();

            comment.setNew(false);
        }
    }
}
