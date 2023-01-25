package com.gakk.noorlibrary.ui.adapter.podcast;

import static com.gakk.noorlibrary.util.ConstantsKt.CONTENT_BASE_URL;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.gakk.noorlibrary.R;
import com.gakk.noorlibrary.model.podcast.CommentListResponse;
import com.gakk.noorlibrary.ui.activity.podcast.ItemClickListener;
import com.gakk.noorlibrary.util.CircleImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentPagingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private final Context context;
    private boolean isLoadingAdded = false;
    private final List<CommentListResponse.Data> categoryContentsList;
    private final ItemClickListener clickListener;


    public CommentPagingAdapter(Context context, ItemClickListener clickListener) {
        this.context = context;
        this.categoryContentsList = new ArrayList<>();
        this.clickListener = clickListener;

    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View view = inflater.inflate(R.layout.comment_pagination_prog,parent,false);
                viewHolder = new LoadingVH(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {

        View view = inflater.inflate(R.layout.layout_child_comment_item,parent,false);

        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View binding;

        public ViewHolder(View binding) {
            super(binding);
            this.binding = binding;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder mainHolder, int position) {
        switch (getItemViewType(position)) {
            case ITEM:
                final ViewHolder holder = (ViewHolder) mainHolder;

                CommentListResponse.Data data = categoryContentsList.get(position);

                //view

                AppCompatTextView user_name = holder.binding.findViewById(R.id.user_name);
                CircleImageView user_icon = holder.binding.findViewById(R.id.user_icon);
                AppCompatTextView comment_times = holder.binding.findViewById(R.id.comment_times);
                ConstraintLayout llLike = holder.binding.findViewById(R.id.llLike);
                AppCompatImageView ivLike = holder.binding.findViewById(R.id.ivLike);
                AppCompatTextView like = holder.binding.findViewById(R.id.like);

                user_name.setText(data.getUserName());


                Glide.with(context.getApplicationContext())
                        .load(CONTENT_BASE_URL + data.getImageUrl())
                        .apply(new RequestOptions().placeholder(R.drawable.ic_profile_default).error(R.drawable.ic_profile_default)
                                .diskCacheStrategy(DiskCacheStrategy.DATA)).dontAnimate()
                        .into(user_icon);

                String strDate = data.getCreatedOn();

                SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.ENGLISH);
                try {
                    Date date = dateFormat2.parse(strDate);
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
                    if (date != null) {
                        comment_times.setText(sdf.format(date));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                llLike.setOnClickListener(v -> {
                    if (data.getCommentLike()) {
                        return;
                    }
                    ivLike.setClickable(false);
                    ivLike.setColorFilter(context.getResources().getColor(R.color.colorPrimary));
                    data.setCommentLike(true);
                    like.setText(String.valueOf(data.getTotalCommentLike() + 1));
                    like.setText("Liked" + "(" + (data.getTotalCommentLike() + 1) + ")");
                    data.setTotalCommentLike(data.getTotalCommentLike() + 1);
                    clickListener.onLikeButtonClicked(data.getCommentId(), false, data.getCurrentPage());
                });

                if (data.getCommentLike()) {
                    llLike.setClickable(false);
                    ivLike.setColorFilter(context.getResources().getColor(R.color.colorPrimary));
                    like.setText("Liked" + "(" + data.getTotalCommentLike() + ")");
                } else {
                    ivLike.setColorFilter(context.getResources().getColor(R.color.ash));
                    llLike.setClickable(true);
                    like.setText("Like" + "(" + data.getTotalCommentLike() + ")");
                }

                break;

            case LOADING:

                final LoadingVH vh = (LoadingVH) mainHolder;

                //view
                AppCompatTextView more_btn = vh.binding.findViewById(R.id.more_btn);
                ProgressBar progressBar = vh.binding.findViewById(R.id.progressBar);

                more_btn.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);


                more_btn.setOnClickListener(v -> {
                    clickListener.onMoreButtonClicked();
                    more_btn.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                });
                break;
        }
    }


    protected static class LoadingVH extends RecyclerView.ViewHolder {
        View binding;

        LoadingVH(View binding) {
            super(binding);
            this.binding = binding;
        }

    }

    @Override
    public int getItemCount() {

        return categoryContentsList == null ? 0 : categoryContentsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == categoryContentsList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }


    public void add(CommentListResponse.Data r) {
        categoryContentsList.add(r);
        notifyItemInserted(categoryContentsList.size() - 1);
    }

    public void addAll(List<CommentListResponse.Data> moveResults) {
        for (CommentListResponse.Data result : moveResults) {
            add(result);
        }
    }

    public void remove(CommentListResponse.Data r) {
        int position = categoryContentsList.indexOf(r);
        if (position > -1) {
            categoryContentsList.remove(position);
            notifyItemRemoved(position);

        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public void clearAll() {
        categoryContentsList.clear();
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new CommentListResponse.Data());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = categoryContentsList.size() - 1;
        CommentListResponse.Data result = getItem(position);

        if (result != null) {
            categoryContentsList.remove(position);
            notifyItemRemoved(position);
        }
    }

    private CommentListResponse.Data getItem(int position) {
        return categoryContentsList.get(position);
    }

    public void modifyItem(int index, CommentListResponse.Data commentData) {
        categoryContentsList.set(index, commentData);
        notifyItemChanged(index);
    }
}
