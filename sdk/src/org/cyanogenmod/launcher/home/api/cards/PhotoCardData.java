package org.cyanogenmod.launcher.home.api.cards;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.Date;

/**
 * A data object that represents a Photo Card that will be displayed in CM Home.
 * This has a top bar containing the title and small text, then a square image below
 * that displays the photo.
 */
public class PhotoCardData extends CardData {

    /**
     * Constructs a new PhotoCardData.
     * @param title A String to be used for the title of the card.
     * @param contentCreatedDate The date that the content was created.
     */
    public PhotoCardData(String title, Date contentCreatedDate) {
        super(title, contentCreatedDate);
    }

    /**
     * Creates a new PhotoCardData from an existing CardData.
     * @param cardData A CardData object to copy into this PhotoCardData.
     */
    public PhotoCardData(CardData cardData) {
        super(cardData);
    }

    /**
     * <p>Sets a Uri that will resolve to an Image to be used to identify the content source.</p>
     *
     * <p>For example, a news application could use this image to display a logo representing the
     * news outlet that produced the article.</p>
     *
     * <p>Another example is a Social Media extension that aggregates content from multiple sources.
     * This image may display the icon of the Social network where the content originated from.</p>
     *
     * @param resId A resourceId that resolves to the content source image.
     */
    public void setContentSourceImage(int resId) {
        setContentSourceImageInternal(resId);
    }

    /**
     * <p>Sets a Bitmap that will resolve to an Image to be used to identify the content source.</p>
     *
     * <p>When {@link org.cyanogenmod.launcher.home.api.cards.CardData#publishSynchronous(Context)} (Context)} is
     * called on this CardData, this bitmap is saved to a cache within the internal storage for
     * this application, to be accessed only by CM Home through a ContentProvider. If the same
     * Bitmap is passed for any field, the same cached image will be used. </p>
     *
     * @see org.cyanogenmod.launcher.home.api.cards.PhotoCardData#setContentSourceImage(Uri)
     * @param bitmap A Bitmap to be used as the content source image.
     */
    public void setContentSourceImage(Bitmap bitmap) {
        setContentSourceImageInternal(bitmap);
    }

    /**
     * <p>Sets a resource ID that will resolve to an Image to be used to identify
     * the content source.</p>
     *
     * <p>When {@link org.cyanogenmod.launcher.home.api.cards.CardData#publishSynchronous(Context)} (Context)} is
     * called on this CardData, this resource is saved to a cache within the internal storage for
     * this application, to be accessed only by CM Home through a ContentProvider. If the same
     * Bitmap is passed for any field, the same cached image will be used. </p>
     *
     * @see org.cyanogenmod.launcher.home.api.cards.PhotoCardData#setContentSourceImage(int)
     * @param uri A URI that resolves to the content source image (all types,
     *                              including internet resources, are allowed).
     */
    public void setContentSourceImage(Uri uri) {
        setContentSourceImageInternal(uri);
    }

    /**
     * Retrieve the Uri that points to the Content Source image.
     * @return A {@link android.net.Uri} object that will resolve to the image file for the
     * Content Source image.
     */
    public Uri getContentSourceImageUri() {
        return getContentSourceImageUriInternal();
    }

    /**
     * <p>Sets a resource that will resolve to an image to be used to identify the author
     * of the content.</p>
     *
     * <p>When {@link org.cyanogenmod.launcher.home.api.cards.CardData#publish(Context)} is
     * called on this CardData, this resource is saved to a cache within the internal storage for
     * this application, to be accessed only by CM Home through a ContentProvider. If the same
     * Bitmap is passed for any field, the same cached image will be used. </p>
     *
     * <p>For example, a Social Media extension could use this image to display the profile
     * picture of the user that posted a status update. </p>
     *
     * @see org.cyanogenmod.launcher.home.api.cards.MessageCardData#setAvatarImage(android.net.Uri)
     * @param resId A resourceId that resolves to the image to use as the avatar image.
     */
    public void setAvatarImage(int resId) {
        setAvatarImageInternal(resId);
    }

    /**
     * <p>Sets a Bitmap that will resolve to an image to be used to identify the author
     * of the content.</p>
     *
     * <p>When {@link org.cyanogenmod.launcher.home.api.cards.CardData#publish(Context)} is
     * called on this CardData, this image is saved to a cache within the internal storage for
     * this application, to be accessed only by CM Home through a ContentProvider. If the same
     * Bitmap is passed for any field, the same cached image will be used. </p>
     *
     * <p>For example, a Social Media extension could use this image to display the profile
     * picture of the user that posted a status update. </p>
     *
     * @see org.cyanogenmod.launcher.home.api.cards.MessageCardData#setAvatarImage(android.net.Uri)
     * @param bitmap A {@link android.graphics.Bitmap} to use as the avatar image.
     */
    public void setAvatarImage(Bitmap bitmap) {
        setAvatarImageInternal(bitmap);
    }

    /**
     * <p>Sets a Uri that will resolve to an image to be used to identify the author
     * of the content.</p>
     *
     * <p>For example, a Social Media extension could use this image to display the profile
     * picture of the user that posted a status update. </p>
     *
     * @param uri A URI that resolves to the avatar image (all types,
     *                              including internet resources, are allowed).
     */
    public void setAvatarImage(Uri uri) {
        setAvatarImageInternal(uri);
    }

    /**
     * <p>Gets the Uri that will resolve to an image that represents the author
     * of the content.</p>
     *
     * @return A {@link android.net.Uri} that resolves to the avatar image.
     */
    public Uri getAvatarImageUri()  {
        return getAvatarImageUriInternal();
    }

    /**
     * Set the photo of the PhotoCard that will be displayed in CM Home.
     * This photo will be displayed as a square.
     * @param cardDataImage The image to be used as the photo.
     */
    public void setPhoto(CardDataImage cardDataImage) {
        clearImagesInternal();
        addCardDataImageInternal(cardDataImage);
    }

    /**
     * Clears the photo for this PhotoCardData, if one has been set.
     */
    public void clearPhoto() {
        clearImagesInternal();
    }

    /**
     * Get the previously set photo associated with this PhotoCardData.
     * @return A CardDataImage containing information about this photo.
     */
    public CardDataImage getPhoto() {
        if (getImages().size() > 0) {
            return getImages().get(0);
        } else {
            return null;
        }
    }

    /**
     * <p>Sets a String to use as the "Small Text" for this Card.</p>
     *
     * <p> This text will be displayed below or near to the title of this Card.
     * This field can be used for a small piece of identifying information such as the
     * author of an article or the location of a post.</p>
     *
     * @param text A String that will be displayed in smaller text near the title of the card.
     */
    public void setSmallText(String text) {
        setSmallTextInternal(text);
    }

    /**
     * Gets the currently set small text String for this CardData.
     * @see org.cyanogenmod.launcher.home.api.cards.MessageCardData#setSmallText(String)
     * @return The currently set String to be used as the small text value.
     */
    public String getSmallText() {
        return getSmallTextInternal();
    }

    @Override
    protected boolean isValid() {
        boolean cardValid =
                getContentCreatedDate() != null &&
                (getContentSourceImageUriInternal() != null
                        || getContentSourceImageResourceIdInternal() > 0
                        || getContentSourceImageBitmapInternal() != null) &&
                !TextUtils.isEmpty(getTitle()) &&
                getAvatarImageUriInternal() != null &&
                !getImages().isEmpty();
        return cardValid;
    }
}
