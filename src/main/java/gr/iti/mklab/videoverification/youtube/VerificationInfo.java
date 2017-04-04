package gr.iti.mklab.videoverification.youtube;

import java.math.BigInteger;
import java.net.URL;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mongodb.BasicDBObject;

public class VerificationInfo {
	
	@Expose
    @SerializedName(value = "video_id")
	private String videoId;
	
	@Expose
    @SerializedName(value = "video_title")
	private String videoTitle;
	
	@Expose
    @SerializedName(value = "video_description")
	private String videoDescription;
	
	@Expose
    @SerializedName(value = "video_description_mentioned_locations")
	private Set<String> videoMentionedLocations;
	
	@Expose
    @SerializedName(value = "video_upload_time")
	private String videoUploadTime;
	
	@Expose
    @SerializedName(value = "video_view_count")
	private BigInteger videoViewCount;
	
	@Expose
    @SerializedName(value = "video_like_count")
	private BigInteger videoLikeCount;
	
	@Expose
    @SerializedName(value = "video_dislike_count")
	private BigInteger videoDislikeCount;
	
	@Expose
    @SerializedName(value = "video_favorite_count")
	private BigInteger videoFavoriteCount;
	
	@Expose
    @SerializedName(value = "video_comment_count")
	private BigInteger videoCommentCount;
		
	@Expose
    @SerializedName(value = "video_duration")
	private String videoDuration;
	
	@Expose
    @SerializedName(value = "video_dimension")
	private String videoDimension;
	
	@Expose
    @SerializedName(value = "video_definition")
	private String videoDefinition;
	
	@Expose
    @SerializedName(value = "video_licensed_content")
	private Boolean videoLicensedContent;
	
	@Expose
    @SerializedName(value = "video_recording_location_description")
	private String videoRecordingLocationDescription;
	
	@Expose
    @SerializedName(value = "video_recording_time")
	private String videoRecordingTime;
		
	@Expose
    @SerializedName(value = "num_verification_comments")
	private Integer numVerificationComments;
	
	@Expose
    @SerializedName(value = "video_comments")
	private List<String> videoComments;
	
	@Expose
    @SerializedName(value = "video_comments_2")
	private List<BasicDBObject> videoComments2;
	
	@Expose
    @SerializedName(value = "video_author_comments")
	private List<String> videoAuthorComments;
	
	@Expose
    @SerializedName(value = "video_publishedAt_comments")
	private List<String> videoPublishedAtComments;	
	
	@Expose
    @SerializedName(value = "video_author_url_comments")
	private List<String> videoAuthorURLComments;
	
	@Expose
    @SerializedName(value = "verification_comments")
	private List<String> verificationComments;
	
	@Expose
    @SerializedName(value = "video_thumbnails")
	private List<String> videoThumbnails;
	
	@Expose
    @SerializedName(value = "reverse_image_thumbnails_search_url")
	private List<String> reverseImageThumbnailSearchUrl;
	
	@Expose
    @SerializedName(value = "twitter_search_url")
	private URL twitterSearchUrl;
	
	@Expose
    @SerializedName(value = "tweet_ids_sharing_video")
	private List<String> tweetIdsSharingVideo;
	
	@Expose
    @SerializedName(value = "channel_id")
	private String channelId;
	
	@Expose
    @SerializedName(value = "channel_url")
	private String channelUrl;
	
	@Expose
    @SerializedName(value = "channel_description")
	private String channelDescription;
	
	@Expose
    @SerializedName(value = "channel_description_mentioned_locations")
	private Set<String> channelMentionedLocations;
	
	@Expose
    @SerializedName(value = "channel_about_page")
	private String channelAboutPage;
	
	@Expose
    @SerializedName(value = "channel_created_time")
	private String channelCreatedTime;
	
	@Expose
    @SerializedName(value = "channel_location")
	private String channelLocation;
	
	@Expose
    @SerializedName(value = "channel_view_count")
	private BigInteger channelViewCount;
	
	@Expose
    @SerializedName(value = "channel_comment_count")
	private BigInteger channelCommentCount;
	
	@Expose
    @SerializedName(value = "channel_subscriber_count")
	private BigInteger channelSubscriberCount;
	
	@Expose
    @SerializedName(value = "channel_video_count")
	private BigInteger channelVideoCount;
	
	@Expose
    @SerializedName(value = "channel_videos_per_month")
	private double channelVideosPerMonth; 
	
	@Expose
    @SerializedName(value = "channel_google_account")
	private String channelGoogleAccount;
	
	@Expose
    @SerializedName(value = "processing_status")
	private String processingStatus;
	
	@Expose
    @SerializedName(value = "message")
	private String messageError;

	public String getVideoId() {
		return videoId;
	}
	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getVideoUploadTime() {
		return videoUploadTime;
	}
	public void setVideoUploadTime(String videoUploadTime) {
		this.videoUploadTime = videoUploadTime;
	}
	public String getChannelCreatedTime() {
		return channelCreatedTime;
	}
	public void setChannelCreatedTime(String channelCreatedTime) {
		this.channelCreatedTime = channelCreatedTime;
	}
	public List<String> getReverseImageThumbnailSearchUrl() {
		return reverseImageThumbnailSearchUrl;
	}
	public void setReverseImageThumbnailSearchUrl(
			List<String> reverseImageThumbnailSearchUrl) {
		this.reverseImageThumbnailSearchUrl = reverseImageThumbnailSearchUrl;
	}	
	public String getChannelLocation() {
		return channelLocation;
	}
	public void setChannelLocation(String channelLocation) {
		this.channelLocation = channelLocation;
	}
	
	public String getVideoDescription() {
		return videoDescription;
	}
	public void setVideoDescription(String videoDescription) {
		this.videoDescription = videoDescription;
	}
	public BigInteger getVideoViewCount() {
		return videoViewCount;
	}
	public void setVideoViewCount(BigInteger videoViewCount) {
		this.videoViewCount = videoViewCount;
	}
	public BigInteger getVideoLikeCount() {
		return videoLikeCount;
	}
	public void setVideoLikeCount(BigInteger videoLikeCount) {
		this.videoLikeCount = videoLikeCount;
	}
	public BigInteger getVideoDislikeCount() {
		return videoDislikeCount;
	}
	public void setVideoDislikeCount(BigInteger videoDislikeCount) {
		this.videoDislikeCount = videoDislikeCount;
	}
	public BigInteger getVideoFavoriteCount() {
		return videoFavoriteCount;
	}
	public void setVideoFavoriteCount(BigInteger videoFavoriteCount) {
		this.videoFavoriteCount = videoFavoriteCount;
	}
	public BigInteger getVideoCommentCount() {
		return videoCommentCount;
	}
	public void setVideoCommentCount(BigInteger videoCommentCount) {
		this.videoCommentCount = videoCommentCount;
	}
	public BigInteger getChannelViewCount() {
		return channelViewCount;
	}
	public void setChannelViewCount(BigInteger channelViewCount) {
		this.channelViewCount = channelViewCount;
	}
	public BigInteger getChannelCommentCount() {
		return channelCommentCount;
	}
	public void setChannelCommentCount(BigInteger channelCommentCount) {
		this.channelCommentCount = channelCommentCount;
	}
	public BigInteger getChannelSubscriberCount() {
		return channelSubscriberCount;
	}
	public void setChannelSubscriberCount(BigInteger channelSubscriberCount) {
		this.channelSubscriberCount = channelSubscriberCount;
	}
	public BigInteger getChannelVideoCount() {
		return channelVideoCount;
	}
	public void setChannelVideoCount(BigInteger channelVideoCount) {
		this.channelVideoCount = channelVideoCount;
	}
	public String getVideoDuration() {
		return videoDuration;
	}
	public void setVideoDuration(String videoDuration) {
		this.videoDuration = videoDuration;
	}
	public String getVideoDimension() {
		return videoDimension;
	}
	public void setVideoDimension(String videoDimension) {
		this.videoDimension = videoDimension;
	}
	public String getVideoDefinition() {
		return videoDefinition;
	}
	public void setVideoDefinition(String videoDefinition) {
		this.videoDefinition = videoDefinition;
	}
	public Boolean getVideoLicensedContent() {
		return videoLicensedContent;
	}
	public void setVideoLicensedContent(Boolean videoLicensedContent) {
		this.videoLicensedContent = videoLicensedContent;
	}
	public double getChannelVideosPerMonth() {
		return channelVideosPerMonth;
	}
	public void setChannelVideosPerMonth(double channelVideosPerMonth) {
		this.channelVideosPerMonth = channelVideosPerMonth;
	}
	public URL getTwitterSearchUrl() {
		return twitterSearchUrl;
	}
	public void setTwitterSearchUrl(URL twitterSearchUrl) {
		this.twitterSearchUrl = twitterSearchUrl;
	}
	
	
	public String getChannelGoogleAccount() {
		return channelGoogleAccount;
	}
	public void setChannelGoogleAccount(String channelGoogleAccount) {
		this.channelGoogleAccount = channelGoogleAccount;
	}
	public List<String> getVerificationComments() {
		return verificationComments;
	}
	public void setVerificationComments(List<String> verificationComments) {
		this.verificationComments = verificationComments;
	}
	public Integer getNumVerificationComments() {
		return numVerificationComments;
	}
	public void setNumVerificationComments(Integer numVerificationComments) {
		this.numVerificationComments = numVerificationComments;
	}
	public String getChannelUrl() {
		return channelUrl;
	}
	public void setChannelUrl(String channelUrl) {
		this.channelUrl = channelUrl;
	}
	public String getChannelAboutPage() {
		return channelAboutPage;
	}
	public void setChannelAboutPage(String channelAboutPage) {
		this.channelAboutPage = channelAboutPage;
	}
	public String getChannelDescription() {
		return channelDescription;
	}
	public void setChannelDescription(String channelDescription) {
		this.channelDescription = channelDescription;
	}

	public Set<String> getChannelMentionedLocations() {
		return channelMentionedLocations;
	}
	public void setChannelMentionedLocations(
			Set<String> channelMentionedLocations) {
		this.channelMentionedLocations = channelMentionedLocations;
	}
	public Set<String> getVideoMentionedLocations() {
		return videoMentionedLocations;
	}
	public void setVideoMentionedLocations(Set<String> videoMentionedLocations) {
		this.videoMentionedLocations = videoMentionedLocations;
	}
	public String getVideoTitle() {
		return videoTitle;
	}
	public void setVideoTitle(String videoTitle) {
		this.videoTitle = videoTitle;
	}
	public String getVideoRecordingLocationDescription() {
		return videoRecordingLocationDescription;
	}
	public void setVideoRecordingLocationDescription(
			String videoRecordingLocationDescription) {
		this.videoRecordingLocationDescription = videoRecordingLocationDescription;
	}
	public String getVideoRecordingTime() {
		return videoRecordingTime;
	}
	public void setVideoRecordingTime(String videoRecordingTime) {
		this.videoRecordingTime = videoRecordingTime;
	}
	public List<String> getVideoThumbnails() {
		return videoThumbnails;
	}
	public void setVideoThumbnails(List<String> videoThumbnails) {
		this.videoThumbnails = videoThumbnails;
	}
	
	public List<String> getTweetIdsSharingVideo() {
		return tweetIdsSharingVideo;
	}
	public void setTweetIdsSharingVideo(List<String> tweetIdsSharingVideo) {
		this.tweetIdsSharingVideo = tweetIdsSharingVideo;
	}
	public List<String> getVideoComments() {
		return videoComments;
	}
	public void setVideoComments(List<String> videoComments) {
		this.videoComments = videoComments;
	}
	
	public List<BasicDBObject> getVideoComments2() {
		return videoComments2;
	}
	public void setVideoComments2(List<BasicDBObject> videoComments2) {
		this.videoComments2 = videoComments2;
	}
	
	public List<String> getVideoAuthorComments() {
		return videoAuthorComments;
	}
	
	public void setVideoAuthorComments(List<String> videoAuthorComments) {
		this.videoAuthorComments = videoAuthorComments;
	}
	
	public List<String> getVideoAuthorURLComments() {
		return videoAuthorURLComments;
	}
	
	public void setVideoAuthorURLComments(List<String> videoAuthorURLComments) {
		this.videoAuthorURLComments = videoAuthorURLComments;
	}
	
	public List<String> getVideoPublishedAtComments() {
		return videoPublishedAtComments;
	}
	
	public void setVideoPublishedAtComments(List<String> videoPublishedAtComments) {
		this.videoPublishedAtComments = videoPublishedAtComments;
	}		
	
	public String getProcessingStatus() {
		return processingStatus;
	}
	public void setProcessingStatus(String processingStatus) {
		this.processingStatus = processingStatus;
	}
	
	public String getMessageError() {
		return messageError;
	}
	public void setMessageError(String messageError) {
		this.messageError = messageError;
	}
	public String toJSONString() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		return gson.toJson(this);
	}
	
}
