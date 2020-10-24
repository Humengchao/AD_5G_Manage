package org.wower.ad_5g_manage.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;
import org.wower.ad_5g_manage.R;
import org.wower.ad_5g_manage.model.Video;

import java.util.List;

public class AdAdapter extends RecyclerView.Adapter<AdAdapter.ViewHolder> {
    private List<Video> mVideo;
    private int i=1;    // 广告视频的序号
    String isSuccessDelete = null;  // 是否成功删除了视频

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView adNO;              // 广告视频的序号
        TextView adName;            // 广告视频的名称
        TextView adPlayedNumber;    // 广告播放的次数
        Button delete_ad;           // 删除广告的按钮

        public ViewHolder(View view) {
            super(view);
            adNO = (TextView) view.findViewById(R.id.ad_number);
            adName = (TextView) view.findViewById(R.id.ad_name);
            adPlayedNumber = (TextView) view.findViewById(R.id.ad_played_number);
            delete_ad = (Button) view.findViewById(R.id.delete_ad);
        }
    }

    public AdAdapter(List<Video> videoList) {
        mVideo = videoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.delete_ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Video video = mVideo.get(position);
                notifyItemRemoved(position);  // 删除
                // 删除后adapter里的list也要删除这一项
                mVideo.remove(position);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 从数据库中删除
                        isSuccessDelete = Post.deleteVideo(video);
                    }
                }).start();
            }
        });
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Video video = mVideo.get(position);
        int i = mVideo.indexOf(video) + 1;
        holder.adNO.setText(String.valueOf(i));
        holder.adName.setText(video.getVideoName());
        holder.adPlayedNumber.setText(String.valueOf(video.getPlayedNumber()));
    }

    @Override
    public int getItemCount() {
        return mVideo.size();
    }
}
