package org.cyanogenmod.launcher.home.api.cards;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.util.Date;

/**
 * A data object that represents a Place Card that will be shown in CM Home.
 * It contains an image, a rating out of five stars, and some description fields
 * about that place.
 */
public class PlaceCardData extends CardData {
    /**
     * @hide
     */
    static final String KEY_PLACE_CARD = "place_card";

    /**
     * Constructs a new PlaceCardData.
     * @param title A String to be used for the title of the card.
     * @param contentCreatedDate The date that the content was created.
     */
    public PlaceCardData(String title, Date contentCreatedDate) {
        super(title, contentCreatedDate);
        setInternalId(KEY_PLACE_CARD);
    }

    /**
     * Creates a new PlaceCardData from an existing CardData.
     * @param cardData A CardData object to copy into this PlaceCardData.
     */
    public PlaceCardData(CardData cardData) {
        super(cardData);
        setInternalId(KEY_PLACE_CARD);
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
     * @see org.cyanogenmod.launcher.home.api.cards.PlaceCardData#setContentSourceImage(Uri)
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
     * @see org.cyanogenmod.launcher.home.api.cards.PlaceCardData#setContentSourceImage(int)
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
     * @see org.cyanogenmod.launcher.home.api.cards.PlaceCardData#setAvatarImage(android.net.Uri)
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
     * @see org.cyanogenmod.launcher.home.api.cards.PlaceCardData#setAvatarImage(android.net.Uri)
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
    public Uri getAvatarImageUri() {
        return getAvatarImageUriInternal();
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
     * @see org.cyanogenmod.launcher.home.api.cards.PlaceCardData#setSmallText(String)
     * @return The currently set String to be used as the small text value.
     */
    public String getSmallText() {
        return getSmallTextInternal();
    }

    /**
     * <p>Sets the reason text for this CardData. This optional field is intended to be used to
     * describe why certain content was displayed.</p>
     *
     * <p>For example, a news extension might publish a Card to the user for a news article
     * that the user did not explicitly request. The reason text could be: "Suggested because
     * you read about Seattle news in the past."</p>
     *
     * @param reasonText A String describing the reason this card is displayed.
     */
    public void setReasonText(String reasonText) {
        setReasonTextInternal(reasonText);
    }

    /**
     * Retrieves the current reason text string for this CardData.
     * @see org.cyanogenmod.launcher.home.api.cards.PlaceCardData#setReasonText(String)
     * @return The currently set text describing the reason this content was displayed.
     */
    public String getReasonText() {
        return getReasonTextInternal();
    }

    /**
     * <p>Sets the body text of this PlaceCardData.</p>
     *
     * <p>This text will be used as the main content of this card. For example,
     * for a Card representing a message, this would contain the actual text of the message.</p>
     * @param bodyText A String containing the main content text of this PlaceCardData.
     */
    public void setBodyText(String bodyText) {
        setBodyTextInternal(bodyText);
    }

    /**
     * Retrieves the currently set body text String for this CardData.
     * @see org.cyanogenmod.launcher.home.api.cards.PlaceCardData#setBodyText(java.lang.String)
     * @return A String that is currently set as the body text for this Card.
     */
    public String getBodyText() {
        return getBodyTextInternal();
    }

    /**
     * Sets the thumbnail image that will be displayed for this place.
     * @param cardDataImage A CardDataImage to use as the thumbnail.
     */
    public void setThumbnailImage(CardDataImage cardDataImage) {
        clearImagesInternal();
        addCardDataImageInternal(cardDataImage);
    }

    /**
     * Removes the thumbnail image associated with this card, if one has been set.
     */
    public void removeThumbnailImage() {
        clearImagesInternal();
    }

    /**
     * Gets the thumbnail image that has been set for this PlaceCardData.
     * @return A CardDataImage that has been set as the thumbnail image for this PlaceCardData.
     */
    public CardDataImage getThumbnailImage() {
        if (getImages().size() > 0) {
            return getImages().get(0);
        } else {
            return null;
        }
    }

    @Override
    public void publishSynchronous(Context context) throws MissingFieldPublishException {
        // Override the internal ID, a place card must use this value.
        setInternalId(KEY_PLACE_CARD);
        super.publishSynchronous(context);
    }
}