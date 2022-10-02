package com.fangyupeng.pojo;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * @Auther: fyp
 * @Date: 2022/5/25
 * @Description:
 * @Package: com.fangyupeng.pojo
 * @Version: 1.0
 */
public class SpotResult {

    /**
     *name: "建兰",
     *           score: 0.3,
     *           baike_info: {
     *             baike_url: "http://baike.baidu.com/item/%E5%BB%BA%E5%85%B0/1068570",
     *             image_url:
     *               "https://bkimg.cdn.bcebos.com/pic/94cad1c8a786c9179314ab54ca3d70cf3bc7574c",
     *             description:
     */
    private TextView name;
    private ProgressBar score;
    private ImageView image;
    private TextView description;

    public SpotResult() {
    }

    public TextView getName() {
        return name;
    }

    public void setName(TextView name) {
        this.name = name;
    }

    public ProgressBar getScore() {
        return score;
    }

    public void setScore(ProgressBar score) {
        this.score = score;
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }

    public TextView getDescription() {
        return description;
    }

    public void setDescription(TextView description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "SpotResult{" +
                "name=" + name +
                ", score=" + score +
                ", image=" + image +
                ", description=" + description +
                '}';
    }
}
