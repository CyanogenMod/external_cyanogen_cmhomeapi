package org.cyanogenmod.launcher.home.api.cards;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataCard {
    private static final int PRIORITY_HIGH = 1;
    private static final int PRIORITY_MID  = 2;
    private static final int PRIORITY_LOW  = 3;

    private int    mId;
    private String mSubject;
    private Date   mContentCreatedDate;
    private Date   mCreatedDate;
    private Date   mLastModifiedDate;
    private Uri    mContentSourceImageUri;
    private Uri    mAvatarImageUri;
    private String mTitle;
    private String mSmallText;
    private String mBodyText;
    private String mAction1Text;
    private Uri    mAction1Uri;
    private String mAction2Text;
    private Uri    mAction2Uri;
    private int    mPriority = 3;

    private List<DataCardImage> mImages = new ArrayList<DataCardImage>();

    public DataCard(String subject, Date contentCreatedDate) {
        mSubject = subject;
        mContentCreatedDate = contentCreatedDate;
    }

    public void addDataCardImage(Uri uri) {
        mImages.add(new DataCardImage(getId(), uri));
    }

    public void addDataCardImage(DataCardImage image) {
        mImages.add(image);
    }

    public void clearImages() {
        mImages.clear();
    }

    public void removeDataCardImage(DataCardImage image) {
        mImages.remove(image);
    }

    public int getId() {
        return mId;
    }

    private void setId(int id) {
        mId = id;
    }

    public Date getCreatedDate() {
        return mCreatedDate;
    }

    public String getSubject() {
        return mSubject;
    }

    public void setSubject(String subject) {
        this.mSubject = subject;
    }

    public Date getContentCreatedDate() {
        return mContentCreatedDate;
    }

    public void setContentCreatedDate(Date contentCreatedDate) {
        this.mContentCreatedDate = contentCreatedDate;
    }

    public Date getLastModifiedDate() {
        return mLastModifiedDate;
    }

    public Uri getContentSourceImageUri() {
        return mContentSourceImageUri;
    }

    public void setContentSourceImageUri(Uri contentSourceImageUri) {
        this.mContentSourceImageUri = contentSourceImageUri;
    }

    public Uri getAvatarImageUri() {
        return mAvatarImageUri;
    }

    public void setAvatarImageUri(Uri avatarImageUri) {
        this.mAvatarImageUri = avatarImageUri;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getSmallText() {
        return mSmallText;
    }

    public void setSmallText(String smallText) {
        this.mSmallText = smallText;
    }

    public String getBodyText() {
        return mBodyText;
    }

    public void setBodyText(String bodyText) {
        this.mBodyText = bodyText;
    }

    public String getAction1Text() {
        return mAction1Text;
    }

    public void setAction1Text(String action1Text) {
        this.mAction1Text = action1Text;
    }

    public Uri getAction1Uri() {
        return mAction1Uri;
    }

    public void setAction1Uri(Uri action1Uri) {
        this.mAction1Uri = action1Uri;
    }

    public String getAction2Text() {
        return mAction2Text;
    }

    public void setAction2Text(String action2Text) {
        this.mAction2Text = action2Text;
    }

    public Uri getAction2Uri() {
        return mAction2Uri;
    }

    public void setAction2Uri(Uri action2Uri) {
        this.mAction2Uri = action2Uri;
    }

    public int getPriority() {
        return mPriority;
    }

    public void setPriority(int priority) {
        this.mPriority = priority;
    }
}
