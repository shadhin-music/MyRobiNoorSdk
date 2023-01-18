package com.gakk.noorlibrary.model.podcast;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class CommentData implements Parcelable {
    @SerializedName("CommentId")
    @Expose
    private int commentId;
    @SerializedName("ContentId")
    @Expose
    private String contentId;
    @SerializedName("ContentType")
    @Expose
    private String contentType;
    @SerializedName("ContentTitle")
    @Expose
    private String contentTitle;
    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("CreateDate")
    @Expose
    private String createDate;
    @SerializedName("UserName")
    @Expose
    private String userName;
    @SerializedName("UserPic")
    @Expose
    private String userPic;
    @SerializedName("CommentLike")
    @Expose
    private boolean commentLike;
    @SerializedName("TotalCommentLike")
    @Expose
    private int totalCommentLike;
    @SerializedName("CommentFavorite")
    @Expose
    private boolean commentFavorite;
    @SerializedName("TotalCommentFavorite")
    @Expose
    private int totalCommentFavorite;

    @SerializedName("TotalReply")
    @Expose
    private int totalReply;

    @SerializedName("AdminUserType")
    @Expose
    private String userType;

    @SerializedName("CurrentPage")
    @Expose
    private int currentPage;


    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPic() {
        return userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }

    public boolean getCommentLike() {
        return commentLike;
    }

    public void setCommentLike(boolean commentLike) {
        this.commentLike = commentLike;
    }

    public int getTotalCommentLike() {
        return totalCommentLike;
    }

    public void setTotalCommentLike(int totalCommentLike) {
        this.totalCommentLike = totalCommentLike;
    }

    public boolean getCommentFavorite() {
        return commentFavorite;
    }

    public void setCommentFavorite(boolean commentFavorite) {
        this.commentFavorite = commentFavorite;
    }

    public int getTotalCommentFavorite() {
        return totalCommentFavorite;
    }

    public void setTotalCommentFavorite(int totalCommentFavorite) {
        this.totalCommentFavorite = totalCommentFavorite;
    }

    public int getTotalReply() {
        return totalReply;
    }

    public void setTotalReply(int totalReply) {
        this.totalReply = totalReply;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.commentId);
        dest.writeString(this.contentId);
        dest.writeString(this.contentType);
        dest.writeString(this.contentTitle);
        dest.writeString(this.message);
        dest.writeString(this.createDate);
        dest.writeString(this.userName);
        dest.writeString(this.userPic);
        dest.writeByte(this.commentLike ? (byte) 1 : (byte) 0);
        dest.writeInt(this.totalCommentLike);
        dest.writeByte(this.commentFavorite ? (byte) 1 : (byte) 0);
        dest.writeInt(this.totalCommentFavorite);
        dest.writeInt(this.totalReply);
        dest.writeString(this.userType);
        dest.writeInt(this.currentPage);
    }

    public CommentData() {
    }

    protected CommentData(Parcel in) {
        this.commentId = in.readInt();
        this.contentId = in.readString();
        this.contentType = in.readString();
        this.contentTitle = in.readString();
        this.message = in.readString();
        this.createDate = in.readString();
        this.userName = in.readString();
        this.userPic = in.readString();
        this.commentLike = in.readByte() != 0;
        this.totalCommentLike = in.readInt();
        this.commentFavorite = in.readByte() != 0;
        this.totalCommentFavorite = in.readInt();
        this.totalReply = in.readInt();
        this.userType = in.readString();
        this.currentPage = in.readInt();
    }

    public static final Creator<CommentData> CREATOR = new Creator<CommentData>() {
        @Override
        public CommentData createFromParcel(Parcel source) {
            return new CommentData(source);
        }

        @Override
        public CommentData[] newArray(int size) {
            return new CommentData[size];
        }
    };
}
