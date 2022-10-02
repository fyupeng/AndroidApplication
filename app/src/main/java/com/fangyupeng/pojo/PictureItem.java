package com.fangyupeng.pojo;

/**
 * @Auther: fyp
 * @Date: 2022/5/27
 * @Description:
 * @Package: com.fangyupeng.pojo
 * @Version: 1.0
 */
public class PictureItem {

   private String pictureId;
   private String pictureName;
   private String imageUrl;
   private String pictureDescription;
   private String uploadDate;
   private String uploadTime;

   public PictureItem() {
   }

   public String getPictureId() {
      return pictureId;
   }

   public void setPictureId(String pictureId) {
      this.pictureId = pictureId;
   }

   public String getPictureName() {
      return pictureName;
   }

   public void setPictureName(String pictureName) {
      this.pictureName = pictureName;
   }

   public String getImageUrl() {
      return imageUrl;
   }

   public void setImageUrl(String imageUrl) {
      this.imageUrl = imageUrl;
   }

   public String getPictureDescription() {
      return pictureDescription;
   }

   public void setPictureDescription(String pictureDescription) {
      this.pictureDescription = pictureDescription;
   }

   public String getUploadDate() {
      return uploadDate;
   }

   public void setUploadDate(String uploadDate) {
      this.uploadDate = uploadDate;
   }

   public String getUploadTime() {
      return uploadTime;
   }

   public void setUploadTime(String uploadTime) {
      this.uploadTime = uploadTime;
   }
}
