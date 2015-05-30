package org.cyanogenmod.launcher.home.api.cards;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import org.cyanogenmod.launcher.home.api.provider.CmHomeContentProvider;
import org.cyanogenmod.launcher.home.api.provider.CmHomeContract;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Date;
import java.util.List;

/**
 * <p>CardData contains data representing a single card that will appear in CM Home. This class
 * allows an application to create new cards, publish them to CM Home, remove them,
 * and receive callbacks when events related to cards occur.</p>
 *
 * <p>CardData includes many possible fields, and not all of them are required. The idea is that
 * an extension application can publish whatever data or content they have available,
 * and CM Home will do it's very best to find the best Card UI for the data available. At
 * minimum, a card should have a ContentCreatedDate and a Title.
 *
 * More Card types will be created in the future, so publish as much data as you have,
 * so that they will be used in rich ways within CM Home.
 * </p>
 *
 * <p>To publish a new Card to CM Home, just create a new CardData object,
 * set the desired fields, and call {@link #publish(android.content.Context)}. This publishes
 * your data in the ContentProvider backing CardData and notifies CM Home that a new card is
 * available.
 *
 * To query the currently published cards at any time, use
 * {@link #getAllPublishedCardDatas(android.content.Context)} to retrieve a list of CardData objects.
 *
 * To remove any card that is currently published, call
 * {@link #unpublish(android.content.Context)} on that card.</p>
 *
 * <p>CardData also contains a list of related images, retrievable in {@link #getImages()}. Any
 * images that have been added will be published when
 * {@link #publish(android.content.Context)} is called on a CardData object.
 *
 * @see org.cyanogenmod.launcher.home.api.cards.CardDataImage
 * </p>
 */
public class CardData extends PublishableCard {
    private static final String TAG = "CardData";
    /**
     * Store a reference to the Database Contract that represents this object,
     * so that the superclass can figure out what columns to write.
     */
    private static final CmHomeContract.ICmHomeContract sContract =
            new CmHomeContract.CardDataContract();

    private String mInternalId;
    private String mReasonText;
    private Date   mContentCreatedDate;
    private Date   mCreatedDate;
    private Date   mLastModifiedDate;

    // Only one of these fields will be assigned at publish time
    private Uri                   mContentSourceImageUri;
    private Bitmap                mContentSourceImageBitmap;
    private int                   mContentSourceImageResourceId;

    private Uri                   mAvatarImageUri;
    private Bitmap                mAvatarImageBitmap;
    private int                   mAvatarImageResourceId;

    private String mTitle;
    private String mSmallText;
    private String mBodyText;
    private String mCategory;
    private Intent mCardClickIntent;
    private String mAction1Text;
    private Intent mAction1Intent;
    private String mAction2Text;
    private Intent mAction2Intent;
    private Priority mPriority = Priority.MID;

    /**
     * @hide
     */
    public static final int CARD_DATA_TYPE_KEY = 0;

    /**
     * @hide
     */
    public static final int LOGIN_CARD_TYPE_KEY = 1;

    /**
     * @hide
     */
    public static final int MESSAGE_CARD_TYPE_KEY = 2;

    /**
     * @hide
     */
    public static final int NEWS_CARD_TYPE_KEY = 3;

    /**
     * @hide
     */
    public static final int PHOTO_CARD_TYPE_KEY = 4;

    /**
     * @hide
     */
    public static final int PLACE_CARD_TYPE_KEY = 5;

    /**
     * The priority of a Card. Applications can report the priority of a card to hint to CM
     * Home where it should appear in the list of cards. There are three possibilities for
     * priority: low, medium and high.
     */
    public enum Priority {
        /**
         * The highest priority. This hints that this card should appear as high on the list as
         * possible.
         */
        HIGH(0),
        /**
         * Medium priority. This card will appear above low priority cards but below low priority
         * cards.
         */
        MID(1),
        /**
         * Low priority. This card will be shown at the bottom of the list.
         */
        LOW(2);

        private final int mValue;

        private Priority(int value) {
            mValue = value;
        }

        public int getValue() {
            return mValue;
        }

        public static Priority getModeForValue(int value) {
            switch (value) {
                case 0:
                    return HIGH;
                case 1:
                    return MID;
                case 2:
                    return LOW;
                default:
                    return MID;
            }
        }
    }

    /**
     * All associated CardDataImages for this CardData.
     */
    private List<CardDataImage> mImages = new ArrayList<CardDataImage>();

    /**
     * A list of images that have been removed from the card
     */
    private Set<CardDataImage> mRemovedImages = new HashSet<CardDataImage>();

    protected CardData() {
        super(sContract);
    }

    /**
     * Creates a new card that has only a title and content created date.
     * @param title The title string to display on the card.
     * @param contentCreatedDate The date that the content this card represents was created.
     */
    protected CardData(String title, Date contentCreatedDate) {
        super(sContract);

        setTitle(title);
        setContentCreatedDate(contentCreatedDate);
    }

    protected CardData(CardData cardData) {
        super(sContract);
        if (cardData != null) {
            mInternalId = cardData.mInternalId;
            mReasonText = cardData.mReasonText;
            mContentCreatedDate = cardData.mContentCreatedDate;
            mCreatedDate = cardData.mCreatedDate;
            mLastModifiedDate = cardData.mLastModifiedDate;
            mContentSourceImageUri = cardData.mContentSourceImageUri;
            mContentSourceImageBitmap = cardData.mContentSourceImageBitmap;
            mContentSourceImageResourceId = cardData.mContentSourceImageResourceId;
            mAvatarImageUri = cardData.mAvatarImageUri;
            mAvatarImageBitmap = cardData.mAvatarImageBitmap;
            mAvatarImageResourceId = cardData.mAvatarImageResourceId;
            mTitle = cardData.mTitle;
            mSmallText = cardData.mSmallText;
            mBodyText = cardData.mBodyText;
            mCategory = cardData.mCategory;
            mCardClickIntent = cardData.mCardClickIntent;
            mAction1Text = cardData.mAction1Text;
            mAction1Intent = cardData.mAction1Intent;
            mAction2Text = cardData.mAction2Text;
            mAction2Intent = cardData.mAction2Intent;
            mPriority = cardData.mPriority;
            mImages = cardData.mImages;
            setId(cardData.getId());
        }
    }

    /**
     * Add a related CardDataImage object to this CardData. Any images added will be considered
     * for display in CM Home as part of this card.
     * @param uri A URI to this image (all types, including internet resources, are allowed).
     */
    protected void addCardDataImageInternal(Uri uri) {
        CardDataImage image = new CardDataImage(this);
        image.setImage(uri);
        synchronized (mImages) {
            mImages.add(image);
        }
    }

    /**
     * Adds a CardDataImage to this DataCard, if this CardDataImage is not already associated
     * with this image. If a CardDataImage is already associated with this CardData,
     * that instance is removed and replaced with this one.
     * @param newImage The CardDataImage instance to add or update.
     */
    public void addOrUpdateCardDataImage(CardDataImage newImage) {
        CardDataImage matchingImage = null;
        synchronized (mImages) {
            for (CardDataImage image : mImages) {
                if (image.getGlobalId().equals(newImage.getGlobalId())) {
                    matchingImage = image;
                    break;
                }
            }

            // Remove the old one, if it was found, it will be replaced
            if (matchingImage != null) {
                mImages.remove(matchingImage);
                mRemovedImages.add(matchingImage);
            }

            mImages.add(newImage);
        }
    }

    /**
     * Set an internal ID for use in tracking the card. This should be a unique ID per card,
     * and can be any String. To be used by the app developer to track a Card,
     * as it relates to their own internal data. (e.g. a primary key in a third party database
     * table).
     * @param internalId  A string with which to identify this Card, unique among other CardData
     *                    instances.
     */
    public void setInternalId(String internalId) {
        mInternalId = internalId;
    }

    /**
     * Retrieves the currently set internal ID for this CardData.
     * @see org.cyanogenmod.launcher.home.api.cards.CardData#setInternalId(String)
     * @return The internal ID string for this CardData.
     */
    public String getInternalId() {
        return mInternalId;
    }

    /**
     * Sets the created date for this CardData. To be used internally as the actual creation date
     * should be enforced here.
     * @param date The actual date that this CardData was created.
     */
    public void setCreatedDate(Date date) {
        mCreatedDate = date;
    }

    /**
     * Sets the modified date for this CardData. To be used internally as the most recent
     * modified date as reported by the SQLite Database. A database trigger will set this for all
     * modified rows, and this value will be passed here.
     * @param date The actual date that this CardData was created.
     */
    protected void setLastModifiedDateInternal(Date date) {
        mLastModifiedDate = date;
    }

    /**
     * <p>Adds a new CardDataImage to this CardData. A CardData can be associated with any
     * number of images. When the Card is generated in CM Home, a suitable number of images will
     * be shown, depending on what is supported. For example, you can attach images related to a
     * news article or social media event.</p>
     *
     * <p>When this card is published, all added images will be published as well.</p>
     * @param image A CardDataImage to add to this CardData.
     */
    protected void addCardDataImageInternal(CardDataImage image) {
        mImages.add(image);
    }

    /**
     * Removes all associated images.
     *
     * When publish is called on this DataCard, the removed images will be unpublished.
     */
    protected void clearImagesInternal() {
        synchronized (mImages) {
            for (CardDataImage image : mImages) {
                mRemovedImages.add(image);
            }

            mImages.clear();
        }
    }

    /**
     * Removes a CardDataImage from this CardData, if it is currently linked to this CardData.
     * When publish is called on this DataCard, the removed image will be unpublished.
     * @param image The CardDataImage to remove from this CardData.
     */
    public void removeCardDataImage(CardDataImage image) {
        mRemovedImages.add(image);

        synchronized (mImages) {
            mImages.remove(image);
        }
    }

    /**
     * Removes a CardDataImage from this CardData, if it is currently linked to this CardData.
     * When publish is called on this DataCard, the removed image will be unpublished.
     * @param imageGlobalId The global ID of the CardDataImage to remove.
     */
    public void removeCardDataImage(String imageGlobalId) {
        CardDataImage theImage = null;

        synchronized (mImages) {
            for (CardDataImage image : mImages) {
                if (imageGlobalId.equals(image.getGlobalId())) {
                    theImage = image;
                }
            }

            if (theImage != null) {
                mImages.remove(theImage);
            }
        }
    }

    /**
     * Retrieve a List of all CardDataImages that are linked to this CardData.
     * @return A list of CardDataImages that are linked to this CardData.
     */
    public List<CardDataImage> getImages() {
        return mImages;
    }

    /**
     * Retrieves a {@link java.util.Date} object representing the time that this CardData was
     * originally published. If the CardData is unpublished and published again,
     * this will be reset to the most recent publish time. Please note that simply republishing
     * updates to this CardData will not change the CreatedDate value.
     * @return A {@link java.util.Date} with the time that this CardData was published.
     */
    public Date getCreatedDate() {
        return mCreatedDate;
    }

    /**
     * Retrieves the current reason text string for this CardData.
     * @see org.cyanogenmod.launcher.home.api.cards.CardData#setReasonText(String)
     * @return The currently set text describing the reason this content was displayed.
     */
    protected String getReasonTextInternal() {
        return mReasonText;
    }

    /**
     * <p>Sets the reason text for this CardData. This optional field is intended to be used to
     * describe why certain content was displayed.</p>
     *
     * <p>For example, a news extension might publish a Card to the user for a news article
     * that the user did not explicitly request. The reason text could be: "Suggested because
     * you read about Seattle news in the past."</p>
     *
     * @param reason A String describing the reason this card is displayed.
     */
    protected void setReasonTextInternal(String reason) {
        this.mReasonText = reason;
    }

    /**
     * Retrieves the currently set Content Created Date.
     * @see org.cyanogenmod.launcher.home.api.cards.CardData#setContentCreatedDate
     * @return A {@link java.util.Date} representing when this content was created.
     */
    public Date getContentCreatedDate() {
        return mContentCreatedDate;
    }

    /**
     * <p>Retrieves the {@link java.util.Date} representing when this content was created. Please
     * note that this is completely independent of CreatedDate, as this does not necessarily have
     * to be the same time that the card was published.</p>
     *
     * <p>For example, if a News extension is publishing a Card for an article. The content
     * created date may be the date the article was written. For a Social Media post,
     * this would be the time at which the post was made by the original author.</p>
     *
     * <p>This Date will be prioritized over the created date of the card, if set.</p>
     * @param contentCreatedDate The Date that this content was created.
     */
    public void setContentCreatedDate(Date contentCreatedDate) {
        this.mContentCreatedDate = contentCreatedDate;
    }

    /**
     * Gets the modified date for this CardData. This will be set each time a card is published
     * and any field has changed.
     */
    public Date getLastModifiedDate() {
        return mLastModifiedDate;
    }

    /**
     * Retrieve the Uri that points to the Content Source image.
     * @return A {@link android.net.Uri} object that will resolve to the image file for the
     * Content Source image.
     */
    protected Uri getContentSourceImageUriInternal() {
        return mContentSourceImageUri;
    }

    /**
     * Retrieve the Resource id that points to the Content Source image.
     * @return A integer resource id that will resolve to the image file for the
     * Content Source image.
     */
    protected int getContentSourceImageResourceIdInternal() {
        return mContentSourceImageResourceId;
    }

    /**
     * Retrieve the Bitmap that points to the Content Source image.
     * @return A {@link android.graphics.Bitmap} object containing the image file for the
     * Content Source image.
     */
    protected Bitmap getContentSourceImageBitmapInternal() {
        return mContentSourceImageBitmap;
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
     * @param contentSourceImageUri A URI that resolves to the content source image (all types,
     *                              including internet resources, are allowed).
     */
    protected void setContentSourceImageInternal(Uri contentSourceImageUri) {
        this.mContentSourceImageUri = contentSourceImageUri;

        mContentSourceImageResourceId = 0;
        mContentSourceImageBitmap = null;
    }

    /**
     * <p>Sets a Bitmap that will resolve to an Image to be used to identify the content source.</p>
     *
     * <p>When {@link org.cyanogenmod.launcher.home.api.cards.CardData#publish(Context)} is
     * called on this CardData, this bitmap is saved to a cache within the internal storage for
     * this application, to be accessed only by CM Home through a ContentProvider. If the same
     * Bitmap is passed for any field, the same cached image will be used. </p>
     *
     * @see org.cyanogenmod.launcher.home.api.cards.CardData#setContentSourceImage(Uri)
     * @param bitmap A Bitmap to be used as the content source image.
     */
    protected void setContentSourceImageInternal(Bitmap bitmap) {
        mContentSourceImageBitmap = bitmap;

        mContentSourceImageResourceId = 0;
        mContentSourceImageUri = null;
    }

    /**
     * <p>Sets a resource ID that will resolve to an Image to be used to identify
     * the content source.</p>
     *
     * <p>When {@link org.cyanogenmod.launcher.home.api.cards.CardData#publish(Context)} is
     * called on this CardData, this resource is saved to a cache within the internal storage for
     * this application, to be accessed only by CM Home through a ContentProvider. If the same
     * Bitmap is passed for any field, the same cached image will be used. </p>
     *
     * @see org.cyanogenmod.launcher.home.api.cards.CardData#setContentSourceImage(Uri)
     * @param resourceId A resourceId that resolves to the content source image.
     */
    protected void setContentSourceImageInternal(int resourceId) {
        mContentSourceImageResourceId = resourceId;

        mContentSourceImageBitmap = null;
        mContentSourceImageUri = null;
    }


    /**
     * <p>Gets the Uri that will resolve to an image that represents the author
     * of the content.</p>
     *
     * @return A {@link android.net.Uri} that resolves to the avatar image.
     */
    protected Uri getAvatarImageUriInternal() {
        return mAvatarImageUri;
    }

    /**
     * <p>Sets a Uri that will resolve to an image to be used to identify the author
     * of the content.</p>
     *
     * <p>For example, a Social Media extension could use this image to display the profile
     * picture of the user that posted a status update. </p>
     *
     * @param avatarImageUri A URI that resolves to the avatar image (all types,
     *                              including internet resources, are allowed).
     */
    protected void setAvatarImageInternal(Uri avatarImageUri) {
        this.mAvatarImageUri = avatarImageUri;

        mAvatarImageResourceId = 0;
        mAvatarImageBitmap = null;
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
     * @see org.cyanogenmod.launcher.home.api.cards.CardData#setAvatarImage(android.net.Uri)
     * @param bitmap A {@link android.graphics.Bitmap} to use as the avatar image.
     */
    protected void setAvatarImageInternal(Bitmap bitmap) {
        mAvatarImageBitmap = bitmap;

        mAvatarImageResourceId = 0;
        mAvatarImageUri = null;
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
     * @see org.cyanogenmod.launcher.home.api.cards.CardData#setAvatarImage(android.net.Uri)
     * @param resourceId A resourceId that resolves to the image to use as the avatar image.
     */
    protected void setAvatarImageInternal(int resourceId) {
        mAvatarImageResourceId = resourceId;

        mAvatarImageBitmap = null;
        mAvatarImageUri = null;
    }

    /**
     * Retrieves the currently set title String for this card.
     * @return The current title String for this card.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Sets a String to use as the title of this CardData.
     * @param title A String to use as the title of this CardData.
     */
    public void setTitle(String title) {
        this.mTitle = title;
    }

    /**
     * Gets the currently set small text String for this CardData.
     * @see org.cyanogenmod.launcher.home.api.cards.CardData#setSmallText(String)
     * @return The currently set String to be used as the small text value.
     */
    protected String getSmallTextInternal() {
        return mSmallText;
    }

    /**
     * <p>Sets a String to use as the "Small Text" for this Card.</p>
     *
     * <p> This text will be displayed below or near to the title of this Card.
     * This field can be used for a small piece of identifying information such as the
     * author of an article or the location of a post.</p>
     *
     * @param smallText A String that will be displayed in smaller text near the title of the card.
     */
    protected void setSmallTextInternal(String smallText) {
        this.mSmallText = smallText;
    }

    /**
     * Retrieves the currently set body text String for this CardData.
     * @see org.cyanogenmod.launcher.home.api.cards.CardData#setBodyText(java.lang.String)
     * @return A String that is currently set as the body text for this Card.
     */
    protected String getBodyTextInternal() {
        return mBodyText;
    }

    /**
     * <p>Sets the body text of this CardData.</p>
     *
     * <p>This text will be used as the main content of this card. For example,
     * for a Card representing a message, this would contain the actual text of the message.</p>
     * @param bodyText A String containing the main content text of this CardData.
     */
    protected void setBodyTextInternal(String bodyText) {
        this.mBodyText = bodyText;
    }

    /**
     * <p>Sets the category of this CardData.</p>
     *
     * <p>This String will be used only internally to assign the Card a category.
     * Each application can define their own categories. The Category value will be used in
     * sorting to group related cards together. </p>
     * @param category A String representing the category for this card.
     */
    public void setCategory(String category) {
        this.mCategory = category;
    }

    /**
     * Retrieves the currently set category text String for this CardData.
     * @see org.cyanogenmod.launcher.home.api.cards.CardData#setCategory(java.lang.String)
     * @return A String that is currently set as the category text for this Card.
     */
    public String getCategory() {
        return mCategory;
    }

    /**
     * Retrieves the currently set {@link org.cyanogenmod.launcher.home.api.cards.CardData.CardDataIntentInfo}
     * for this CardData.
     * @return An instance of {@link org.cyanogenmod.launcher.home.api.cards.CardData.CardDataIntentInfo}
     *         that contains the Intent and information about it.
     */
    public CardDataIntentInfo getCardClickIntentInfo() {
        return getCardDataIntentInfoForIntent(mCardClickIntent);
    }

    /**
     * <p>Sets an Intent that will be launched when this Card is clicked in CM Home.</p>
     *
     * <p>Please note that Parcelable extras are not supported for this feature.</p>
     * @param cardClickIntent An Action or Broadcast Intent that will be launched when this card
     *                        is clicked.
     * @param isBroadcast Is this Intent in cardClickIntent a Broadcast Intent?
     */
    public void setCardClickIntent(Intent cardClickIntent, boolean isBroadcast) {
        mCardClickIntent = cardClickIntent;
        mCardClickIntent
                .putExtra(CmHomeContract.CardDataContract.IS_BROADCAST_INTENT_EXTRA, isBroadcast);
    }

    /**
     * Retrieves the currently set text for the first action.
     * @return The currently set String for the first action text.
     */
    public String getAction1Text() {
        return mAction1Text;
    }

    /**
     * Sets a String to be displayed on the first action button within the Card. This text should
     * describe what this button will do, using a verb.
     *
     * For example, a news extension may have an action one button to share an article. This
     * field could be set to "Share" for this button.
     * @param action1Text A String to display on the first action button.
     */
    public void setAction1Text(String action1Text) {
        this.mAction1Text = action1Text;
    }

    /**
     * Gets a {@link org.cyanogenmod.launcher.home.api.cards.CardData.CardDataIntentInfo} that
     * contains the Intent that will be fired when the first action button is clicked and related
     * information about the Intent.
     * @return A {@link org.cyanogenmod.launcher.home.api.cards.CardData.CardDataIntentInfo}
     *         containing the Intent for the first action button.
     */
    public CardDataIntentInfo getAction1IntentInfo() {
        return getCardDataIntentInfoForIntent(mAction1Intent);
    }

    /**
     * <p>Sets the Intent that will be launched when the first action button is clicked in CM Home
     * on this card.</p>
     *
     * <p>Please note that Parcelable extras are not supported for this feature.</p>
     * @param action1Intent The Intent to be launched.
     * @param isBroadcast true if action1Intent is a Broadcast Intent, false otherwise.
     */
    public void setAction1Intent(Intent action1Intent, boolean isBroadcast) {
        this.mAction1Intent = action1Intent;
        mAction1Intent.putExtra(CmHomeContract.CardDataContract.IS_BROADCAST_INTENT_EXTRA,
                                isBroadcast);
    }


    /**
     * Retrieves the currently set text for the first action.
     * @return The currently set String for the first action text.
     */
    public String getAction2Text() {
        return mAction2Text;
    }

    /**
     * Sets a String to be displayed on the second action button within the Card. This text should
     * describe what this button will do, using a verb.
     *
     * For example, a news extension may have an action two button to share an article. This
     * field could be set to "Share" for this button.
     * @param action2Text A String to display on the second action button.
    */
    public void setAction2Text(String action2Text) {
        this.mAction2Text = action2Text;
    }

    /**
     * Gets a {@link org.cyanogenmod.launcher.home.api.cards.CardData.CardDataIntentInfo} that
     * contains the Intent that will be fired when the second action button is clicked and related
     * information about the Intent.
     * @return A {@link org.cyanogenmod.launcher.home.api.cards.CardData.CardDataIntentInfo}
     *         containing the Intent for the second action button.
     */
    public CardDataIntentInfo getAction2IntentInfo() {
        return getCardDataIntentInfoForIntent(mAction2Intent);
    }

    /**
     * <p>Sets the Intent that will be launched when the second action button is clicked in CM Home
     * on this card.</p>
     *
     * <p>Please note that Parcelable extras are not supported for this feature.</p>
     * @param action2Intent The Intent to be launched.
     * @param isBroadcast true if action2Intent is a Broadcast Intent, false otherwise.
    */
    public void setAction2Intent(Intent action2Intent, boolean isBroadcast) {
        this.mAction2Intent = action2Intent;
        mAction1Intent.putExtra(CmHomeContract.CardDataContract.IS_BROADCAST_INTENT_EXTRA, isBroadcast);
    }

    /**
     * Gets the {@link org.cyanogenmod.launcher.home.api.cards.CardData.Priority} for this CardData.
     * @return A {@link org.cyanogenmod.launcher.home.api.cards.CardData.Priority} describing the
     *         level of priority that this card has been assigned.
     */
    public Priority getPriority() {
        return mPriority;
    }

    /**
     * Gets the current {@link org.cyanogenmod.launcher.home.api.cards.CardData.Priority} for
     * this CardData as an integer.
     * @return The int value of the {@link org.cyanogenmod.launcher.home.api.cards.CardData
     * .Priority} enum constant that is assigned as the priority of this Card.
     */
    private int getPriorityAsInt() {
        return mPriority.getValue();
    }

    /**
     * Sets a Priority value for this CardData as an int value. This is used to self report the
     * priority of this card. Cards will be ordered in CM Home based on a combination of priority,
     * time and other factors. The highest priority cards will be ordered first.
     *
     * @see org.cyanogenmod.launcher.home.api.cards.CardData.Priority
     * @param value One of {@link org.cyanogenmod.launcher.home.api.cards.CardData.Priority#HIGH},
     *              {@link org.cyanogenmod.launcher.home.api.cards.CardData.Priority#MID},
     *              {@link org.cyanogenmod.launcher.home.api.cards.CardData.Priority#LOW}.
    */
    private void setPriority(int value) {
        mPriority = Priority.getModeForValue(value);
    }

    /**
     * Sets a Priority value for this CardData. This is used to self report the priority of this
     * card. Cards will be ordered in CM Home based on a combination of priority,
     * time and other factors. The highest priority cards will be ordered first.
     *
     * @see org.cyanogenmod.launcher.home.api.cards.CardData.Priority
     * @param priority One of {@link org.cyanogenmod.launcher.home.api.cards.CardData.Priority#HIGH},
     *                 {@link org.cyanogenmod.launcher.home.api.cards.CardData.Priority#MID},
     *                 {@link org.cyanogenmod.launcher.home.api.cards.CardData.Priority#LOW}.
    */
    public void setPriority(Priority priority) {
        this.mPriority = priority;
    }

    /**
     * Gets a {@link org.cyanogenmod.launcher.home.api.cards.CardData.CardDataIntentInfo} for
     * this Intent, populated with information about the Intent.
     * @param intent The Intent to create a CardDataIntentInfo for.
     * @return A CardDataIntentInfo object containing the input Intent and associated information.
     */
    private CardDataIntentInfo getCardDataIntentInfoForIntent(Intent intent) {
        CardDataIntentInfo cardDataIntentInfo = null;
        if (intent != null) {
            boolean isBroadcast = isIntentBroadcast(intent);
            cardDataIntentInfo = new CardDataIntentInfo(isBroadcast, intent);
        }

        return cardDataIntentInfo;
    }

    /**
     * Checks if the currently set configuration of fields are valid for this CardData to be published.
     * To be overridden by subclasses.
     * @return True if the card is valid, false otherwise.
     */
    protected boolean isValid() {
        return true;
    }

    @Override
    public void publishSynchronous(Context context) throws MissingFieldPublishException {
        if (!isPublished(context)) {
            // Initialize the created date and modified date to now.
            mCreatedDate = new Date();
            mLastModifiedDate = new Date();
        }

        if (mContentSourceImageResourceId != 0) {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                                                         mContentSourceImageResourceId);
            setContentSourceImageInternal(bitmap);
        }

        if (mContentSourceImageBitmap != null) {
            Uri uri = CmHomeContentProvider.storeBitmapInCache(mContentSourceImageBitmap,
                                                               context);
            if (uri != null) {
                mContentSourceImageBitmap.recycle();
                mContentSourceImageBitmap = null;
                setContentSourceImageInternal(uri);
            }
        }

        if (mAvatarImageResourceId != 0) {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                                                         mAvatarImageResourceId);
            setAvatarImageInternal(bitmap);
        }

        if (mAvatarImageBitmap != null) {
            Uri uri = CmHomeContentProvider.storeBitmapInCache(mAvatarImageBitmap,
                                                               context);
            if (uri != null) {
                mAvatarImageBitmap.recycle();
                mAvatarImageBitmap = null;
                setAvatarImageInternal(uri);
            }
        }

        if (!isValid()) {
            throw new MissingFieldPublishException("Unable to publish card, not all required "
                    + "fields are present.");
        }
        super.publishSynchronous(context);

        synchronized (mImages) {
            for (CardDataImage image : mImages) {
                if (image.hasValidContent()) {
                    image.publishSynchronous(context);
                } else {
                    Log.e(TAG, "Invalid CardDataImage. At least uri or bitmap must be specified");
                }
            }
        }

        for (CardDataImage image : mRemovedImages) {
            image.unpublish(context);
        }
    }

    /**
     * Updates an existing row in the ContentProvider that represents this card.
     * This will update every column at once.
     * @param context A Context object to retrieve the ContentResolver
     * @return true if the update successfully updates a row, false otherwise.
     */
    @Override
    protected boolean update(Context context) {
        boolean updated = super.update(context);
        if (updated) {
            // Update all associated images as well
            synchronized (mImages) {
                for (CardDataImage image : mImages) {
                    if (image.hasValidContent()) {
                        try {
                            image.publishSynchronous(context);
                        } catch (MissingFieldPublishException e) {
                            Log.e(TAG, "Unable to publish CardDataImage during update.");
                        }
                    } else {
                        Log.e(TAG, "Invalid CardDataImage. At least uri or bitmap must be specified");
                    }
                }
            }

            for (CardDataImage image : mRemovedImages) {
                image.unpublish(context);
            }
        }

        return updated;
    }

    /**
     * Removes this CardData from the feed, so that it is no longer visible to the user.
     * @param context The context of the publishing application.
     * @return True if the card was successfully unpublished, false otherwise.
     */
    @Override
    public boolean unpublish(Context context) {
        // Delete all associated images first
        synchronized (mImages) {
            for (CardDataImage image : mImages) {
                image.unpublish(context);
            }
        }
        return super.unpublish(context);
    }

    @Override
    protected ContentValues getContentValues() {
        ContentValues values = new ContentValues();

        values.put(CmHomeContract.CardDataContract.INTERNAL_ID_COL, getInternalId());
        values.put(CmHomeContract.CardDataContract.REASON_COL, getReasonTextInternal());

        if (getContentCreatedDate() != null) {
            values.put(CmHomeContract.CardDataContract.DATE_CONTENT_CREATED_COL,
                       getContentCreatedDate().getTime());
        }

        if (getContentSourceImageUriInternal() != null) {
            values.put(CmHomeContract.CardDataContract.CONTENT_SOURCE_IMAGE_URI_COL,
                       getContentSourceImageUriInternal().toString());
        }

        if (getAvatarImageUriInternal() != null) {
            values.put(CmHomeContract.CardDataContract.AVATAR_IMAGE_URI_COL,
                       getAvatarImageUriInternal().toString());
        }

        values.put(CmHomeContract.CardDataContract.TITLE_TEXT_COL,
                   getTitle());
        values.put(CmHomeContract.CardDataContract.SMALL_TEXT_COL,
                   getSmallTextInternal());
        values.put(CmHomeContract.CardDataContract.BODY_TEXT_COL,
                   getBodyTextInternal());
        values.put(CmHomeContract.CardDataContract.CATEGORY_COL,
                   getCategory());
        values.put(CmHomeContract.CardDataContract.ACTION_1_TEXT_COL,
                   getAction1Text());

        if (getAction1IntentInfo() != null) {
            values.put(CmHomeContract.CardDataContract.ACTION_1_URI_COL,
                    getAction1IntentInfo().getIntent().toUri(Intent.URI_INTENT_SCHEME)
                            .toString());
        }

        values.put(CmHomeContract.CardDataContract.ACTION_2_TEXT_COL,
                   getAction2Text());

        if (getAction2IntentInfo() != null) {
            values.put(CmHomeContract.CardDataContract.ACTION_2_URI_COL,
                       getAction2IntentInfo().getIntent().
                               toUri(Intent.URI_INTENT_SCHEME).toString());
        }

        values.put(CmHomeContract.CardDataContract.PRIORITY_COL,
                getPriorityAsInt());

        if (getCardClickIntentInfo() != null) {
            values.put(CmHomeContract.CardDataContract.CARD_CLICK_URI_COL,
                       getCardClickIntentInfo().getIntent().
                               toUri(Intent.URI_INTENT_SCHEME).toString());
        }

        return values;
    }

    /**
     * Retrieves all published cards from this application that are available at the current time.
     * @param context The context of the publishing application.
     * @return A List of all live, published cards in CM Home from this application,
     * sorted in ascending order by the createdDate field.
     */
    public static List<CardData> getAllPublishedCardDatas(Context context) {
        return getAllPublishedCardDatas(context,
                                        CmHomeContract.CardDataContract.CONTENT_URI,
                                        CmHomeContract.CardDataImageContract.CONTENT_URI);
    }

    /**
     * Based on the fields that have been set, determine the type of this CardData object.
     * @param cardData A CardData for which to determine type.
     * @return A constant that represents what type this CardData is.
     * @hide
     */
    public static int determineType(CardData cardData) {
        if (LoginCardData.KEY_LOGIN_CARD.equals(cardData.getReasonTextInternal())) {
            return LOGIN_CARD_TYPE_KEY;
        } else if (PlaceCardData.KEY_PLACE_CARD.equals(cardData.getInternalId())) {
            return PLACE_CARD_TYPE_KEY;
        } else if (cardData.getContentCreatedDate() != null &&
                   cardData.getContentSourceImageUriInternal() != null &&
                   !TextUtils.isEmpty(cardData.getTitle()) &&
                   !TextUtils.isEmpty(cardData.getBodyTextInternal()) &&
                   cardData.getAvatarImageUriInternal() != null) {
            return MESSAGE_CARD_TYPE_KEY;
        } else if (cardData.getContentCreatedDate() != null &&
                   cardData.getContentSourceImageUriInternal() != null &&
                   !TextUtils.isEmpty(cardData.getTitle()) &&
                   !TextUtils.isEmpty(cardData.getSmallTextInternal())) {
            return NEWS_CARD_TYPE_KEY;
        } else if (cardData.getContentCreatedDate() != null &&
                   cardData.getContentSourceImageUriInternal() != null &&
                   !TextUtils.isEmpty(cardData.getTitle()) &&
                   cardData.getAvatarImageUriInternal() != null &&
                   !cardData.getImages().isEmpty()) {
            return PHOTO_CARD_TYPE_KEY;
        }

        // If all else fails, return the generic type.
        return CARD_DATA_TYPE_KEY;
    }

    /**
     * @hide
     * <p>Creates a CardData object from a Database cursor that is set to a current row
     * containing CardData data.</p>
     *
     * <p><b>This is intended to be an internal SDK method. Use {@link #getAllPublishedCardDatas(Context)}
     * to retrieve cards.</b></p>
     *
     * @see #getAllPublishedCardDatas(android.content.Context)
     * @param cursor A cursor that has been moved to a non-empty row that contains CardData data.
     * @param authority The authority of the CM Home extension ContentProvider that this CardData
     *                  originated from.
     * @return The CardData object represented in the current row of the input Cursor.
     */
    public static CardData createFromCurrentCursorRow(Cursor cursor, String authority) {
        CardData card = createFromCurrentCursorRow(cursor);
        card.setAuthority(authority);
        return card;
    }

    /**
     * @hide
     * Creates a CardData object from a Database cursor that is set to a current row containing
     * CardData data.
     * <p><b>This is intended to be an internal SDK method.
     * Use {@link #getAllPublishedCardDatas(Context)} to retrieve cards.</b></p>
     * @param cursor A cursor that has been moved to a non-empty row that contains CardData data.
     * @return The CardData object represented in the current row of the input Cursor.
     */
    public static CardData createFromCurrentCursorRow(Cursor cursor) {
        CardData cardData = new CardData();

        cardData.setId(cursor.getInt(cursor.getColumnIndex(CmHomeContract.CardDataContract._ID)));
        cardData.setInternalId(cursor.getString(cursor.getColumnIndex(
                CmHomeContract.CardDataContract
                                                                      .INTERNAL_ID_COL)));
        long createdTime = cursor.getLong(cursor.getColumnIndex(CmHomeContract.CardDataContract
                                                                .DATE_CREATED_COL));
        cardData.setCreatedDate(new Date(createdTime));
        long modifiedTime = cursor.getLong(cursor.getColumnIndex(CmHomeContract.CardDataContract
                                                                 .LAST_MODIFIED_COL));
        cardData.setLastModifiedDateInternal(new Date(modifiedTime));
        long contentCreatedTime = cursor.getLong(
                cursor.getColumnIndex(CmHomeContract.CardDataContract.DATE_CONTENT_CREATED_COL));
        cardData.setContentCreatedDate(new Date(contentCreatedTime));
        cardData.setReasonTextInternal(cursor.getString(cursor.getColumnIndex(
                CmHomeContract.CardDataContract
                        .REASON_COL)));
        String contentSourceUriString =
                cursor.getString(cursor.getColumnIndex(
                        CmHomeContract.CardDataContract.CONTENT_SOURCE_IMAGE_URI_COL));

        if (!TextUtils.isEmpty(contentSourceUriString)) {
            cardData.setContentSourceImageInternal(Uri.parse(contentSourceUriString));
        }

        String avatarImageUriString =
                cursor.getString(cursor.getColumnIndex(
                        CmHomeContract.CardDataContract.AVATAR_IMAGE_URI_COL));
        if (!TextUtils.isEmpty(avatarImageUriString)) {
            cardData.setAvatarImageInternal(Uri.parse(avatarImageUriString));
        }

        cardData.setTitle(cursor.getString(
                cursor.getColumnIndex(CmHomeContract.CardDataContract.TITLE_TEXT_COL)));
        cardData.setSmallTextInternal(
                cursor.getString(cursor.getColumnIndex(
                        CmHomeContract.CardDataContract.SMALL_TEXT_COL)));
        cardData.setBodyTextInternal(cursor.getString(
                cursor.getColumnIndex(CmHomeContract.CardDataContract.BODY_TEXT_COL)));
        cardData.setCategory(cursor.getString(
                cursor.getColumnIndex(CmHomeContract.CardDataContract.CATEGORY_COL)));
        cardData.setAction1Text(
                cursor.getString(cursor.getColumnIndex(
                        CmHomeContract.CardDataContract.ACTION_1_TEXT_COL)));

        String clickActionUriString = cursor.getString(
                cursor.getColumnIndex(CmHomeContract.CardDataContract.CARD_CLICK_URI_COL));
        if (!TextUtils.isEmpty(clickActionUriString)) {
            try {
                Intent cardClickIntent = Intent.parseUri(clickActionUriString,
                                                         Intent.URI_INTENT_SCHEME);
                cardData.setCardClickIntent(cardClickIntent, isIntentBroadcast(cardClickIntent));
            } catch (URISyntaxException e) {
                Log.e(TAG, "Unable to parse uri to Intent: " + clickActionUriString);
            }
        }

        String action1UriString = cursor.getString(
                cursor.getColumnIndex(CmHomeContract.CardDataContract.ACTION_1_URI_COL));
        if (!TextUtils.isEmpty(action1UriString)) {
            try {
                Intent action1Intent = Intent.parseUri(action1UriString,
                                                          Intent.URI_INTENT_SCHEME);
                cardData.setAction1Intent(action1Intent,
                        isIntentBroadcast(action1Intent));
            } catch (URISyntaxException e) {
                Log.e(TAG, "Unable to parse uri to Intent: " + action1UriString);
            }
        }

        cardData.setAction2Text(cursor.getString(
                cursor.getColumnIndex(CmHomeContract.CardDataContract.ACTION_2_TEXT_COL)));

        String action2UriString = cursor.getString(
                cursor.getColumnIndex(CmHomeContract.CardDataContract.ACTION_2_URI_COL));
        if (!TextUtils.isEmpty(action2UriString)) {
            try {
                Intent action2Intent = Intent.parseUri(action2UriString,
                                                          Intent.URI_INTENT_SCHEME);
                cardData.setAction2Intent(action2Intent,
                        isIntentBroadcast(action2Intent));
            } catch (URISyntaxException e) {
                Log.e(TAG, "Unable to parse uri to Intent: " + action2UriString);
            }
        }

        int priority = cursor.getInt(cursor.getColumnIndex(CmHomeContract.CardDataContract
                                                                        .PRIORITY_COL));
        cardData.setPriority(priority);

        return cardData;
    }

    /**
     * Checks if an Intent is a Broadcast intent or an Action Intent by checking the extra value
     * attached by this class when any Intent setter is called.
     * @param intent The Intent to check for the Broadcast extra
     * @return true if the input intent is a Broadcast Intent.
     */
    private static boolean isIntentBroadcast(Intent intent) {
        boolean isBroadcast = false;
        if (intent != null) {
            isBroadcast = intent.getBooleanExtra(CmHomeContract.CardDataContract.IS_BROADCAST_INTENT_EXTRA,
                                                 false);
        }
        return isBroadcast;
    }

    /**
     * @hide
     * Retrieves a list of cards that are currently published for the given CardData Uri and
     * CardDataImage Uri.
     * <p><b>This is intended to be an internal SDK method. You should use
     * {@link org.cyanogenmod.launcher.home.api.cards.CardData#getAllPublishedCardDatas(Context)}</b></p>
     * @param context A Context object to retrieve the ContentResolver.
     * @param cardDataContentUri The Content Uri containing the cards to query for.
     * @param cardDataImageContentUri The Content Uri containing the CardDataImage objects to
     *                                query for.
     * @return A list of CardData objects that are currently published.
     */
    public static List<CardData> getAllPublishedCardDatas(Context context,
                                                          Uri cardDataContentUri,
                                                          Uri cardDataImageContentUri) {
        ContentResolver contentResolver = context.getContentResolver();
        List<CardData> allCards = new ArrayList<CardData>();
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(cardDataContentUri,
                                           CmHomeContract.CardDataContract.PROJECTION_ALL,
                                           null,
                                           null,
                                           CmHomeContract.CardDataContract.DATE_CREATED_COL);
        // Catching all Exceptions, since we can't be sure what the extension will do.
        } catch (Exception e) {
            Log.e(TAG, "Error querying for CardDatas, ContentProvider threw an exception for uri:" +
                       " " + cardDataContentUri, e);
        }

        if (cursor != null) {
            while (cursor.moveToNext()) {
                CardData cardData = createFromCurrentCursorRow(cursor,
                                                               cardDataContentUri.getAuthority());
                allCards.add(cardData);
            }

            cursor.close();
        }


        // Retrieve all CardDataImages for each CardData.
        // Doing this in a separate loop since each iteration
        // will also be querying the ContentProvider.
        for (CardData card : allCards) {
            List<CardDataImage> images = CardDataImage
                    .getPublishedCardDataImagesForCardDataId(context,
                                                             cardDataImageContentUri,
                                                             card.getId());
            for (CardDataImage image : images) {
                card.addCardDataImageInternal(image);
            }
        }

        return allCards;
    }

    /**
     * A wrapper class that contains information about a CardData related intent,
     * as well as the Intent itself.
     */
    public class CardDataIntentInfo {
        private boolean mIsBroadcast;
        private Intent mIntent;

        public CardDataIntentInfo(boolean isBroadcast, Intent theIntent) {
            mIsBroadcast = isBroadcast;
            mIntent = theIntent;
        }

        /**
         * Returns true if the encapsulated intent returned by {@link #getIntent()} is a
         * Broadcast Intent.
         * @return true if the Intent returned by {@link #getIntent()} is a Broadcast,
         *         false otherwise.
         */
        public boolean isBroadcast() {
            return mIsBroadcast;
        }

        /**
         * Retrieves the Intent.
         * @return An Intent object.
         */
        public Intent getIntent() {
            return mIntent;
        }
    }

    /**
     * Contains information about a deleted cards, including all relevant ID fields and the
     * authority of the publishing
     * <a href="http://developer.android.com/reference/android/content/ContentProvider.html">ContentProvider</a>.
     */
    public static final class CardDeletedInfo implements Parcelable {
        private long   mId;
        private String mInternalId;
        private String mGlobalId;
        private String mAuthority;

        public CardDeletedInfo(long id, String internalId, String globalId, String authority) {
            setId(id);
            setInternalId(internalId);
            setGlobalId(globalId);
            setAuthority(authority);
        }

        private CardDeletedInfo(Parcel in) {
            mId = in.readLong();
            mInternalId = in.readString();
            mGlobalId = in.readString();
            mAuthority = in.readString();
        }

        /**
         * Set the id of this CardData. This should be the primary key in the SQLite database for
         * this CardData.
         * @param id The long value that is the primary key for the row representing this CardData.
         */
        protected void setId(long id) {
            mId = id;
        }

        /**
         * Retrieves the ID for this CardData. This field is the primary key of this CardData in
         * the SQLite database for this application.
         * @return The id of this CardData.
         */
        public long getId() {
            return mId;
        }

        /**
         * Sets an internal id to be used for tracking this CardData in a way that will not be
         * visible to the user.
         * @param internalId A String to identify this card, unique among other CardDatas.
         */
        protected void setInternalId(String internalId) {
            mInternalId = internalId;
        }

        /**
         * Retrieves the internal ID, which is a hidden ID that uniquely identifies this CardData.
         * @return The currently assigned internal ID String.
         */
        public String getInternalId() {
            return mInternalId;
        }

        /**
         * Sets the global ID for this card. This should be in the format of
         * |contentprovider-authority|/|CardData id|
         * @param globalId The String that represents this card globally,
         *                 among all CM Home extensions installed on the device.
         */
        protected void setGlobalId(String globalId) {
            mGlobalId = globalId;
        }

        /**
         * Retrieves the global ID for this card.
         */
        public String getGlobalId() {
            return mGlobalId;
        }

        /**
         * Set the ContentProvider authority where this CardData is stored.
         * @param authority The authority of the ContentProvider where this CardData is stored.
         */
        protected void setAuthority(String authority) {
            mAuthority = authority;
        }

        /**
         * Retrieves the authority of the ContentProvider where this CardData is stored.
         * @return The authority of the ContentProvider where this CardData is stored.
         */
        public String getAuthority() {
            return mAuthority;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeLong(mId);
            parcel.writeString(mInternalId);
            parcel.writeString(mGlobalId);
            parcel.writeString(mAuthority);
        }

        public static final Creator<CardDeletedInfo> CREATOR =
                new Creator<CardDeletedInfo>() {
                    public CardDeletedInfo createFromParcel(Parcel in) {
                        return new CardDeletedInfo(in);
                    }

                    public CardDeletedInfo[] newArray(int size) {
                        return new CardDeletedInfo[size];
                    }
        };
    }
}
