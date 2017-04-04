package gr.iti.mklab.videoverification.youtube;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.configuration.ConfigurationException;
import org.bson.Document;
import org.bson.conversions.Bson;

import gr.iti.mklab.VideoVerificationController;
import gr.iti.mklab.utils.Configuration;
import gr.iti.mklab.utils.MongoConfiguration;
import gr.iti.mklab.utils.vUtils;


import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.Comment;
import com.google.api.services.youtube.model.CommentListResponse;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.api.services.youtube.model.ThumbnailDetails;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;

/**
 * Query YouTube API and retrieve video information
 * 
 * @author cmpoi
 * 
 * modified and updated - @author olgapapa
 *
 */

public class VideoVerifier {
	/**
	 * Define a global variable that identifies the name of a file that contains
	 * the developer's API key.
	 */


	/**
	 * Define a global instance of a Youtube object, which will be used to make
	 * YouTube Data API requests.
	 */
	private static YouTube youtube;
	private static LexicalizedParser lp;
	private static CRFClassifier<CoreLabel> classifier;
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	private static SimpleDateFormat dateFormatnew = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss a Z");
	private static String processing_status = "processing_status";
	private static String mongoDocId = "_id";
	
	 public VideoVerifier(){
	        try {
				Configuration.load(getClass().getResourceAsStream(VideoVerificationController.properties_file));
				MongoConfiguration.load(getClass().getResourceAsStream(VideoVerificationController.mongo_properties_file));
			} catch (ConfigurationException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        System.out.println(Configuration.PATH_OF_LEXICALIZED_PARSER);
	        setLp(LexicalizedParser.loadModel(Configuration.PATH_OF_LEXICALIZED_PARSER));
	        setClassifier(CRFClassifier
					.getClassifierNoExceptions(Configuration.PATH_OF_CLASSIFIER_FOR_LOCATION));	       
	 }


	public static LexicalizedParser getLp() {
		return lp;
	}

	public static void setLp(LexicalizedParser lp) {
		VideoVerifier.lp = lp;
	}

	public static CRFClassifier<CoreLabel> getClassifier() {
		return classifier;
	}

	public static void setClassifier(CRFClassifier<CoreLabel> classifier) {
		VideoVerifier.classifier = classifier;
	}

	/**
	 * Function that returns details for the specified video id.
	 * 
	 * @param videoIds
	 *            String with video id.
	 * @return Video retrieved video in json format.
	 */
	public static Video retrieveVideoById(String videoId) {

		String apikey =  Configuration.API_KEY;
	
		youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
			@Override
			public void initialize(HttpRequest request) throws IOException {
			}
		}).setApplicationName("youtube-video-verification").build();
		YouTube.Videos.List videoRequest;
		List<Video> videoList = new ArrayList<Video>();
		Video video = new Video();
		try {
			videoRequest = youtube.videos().list("snippet,statistics,contentDetails");
			videoRequest.setKey(apikey);
			videoRequest.setId(videoId);
			VideoListResponse listResponse = videoRequest.execute();
			videoList = listResponse.getItems();
			if (videoList.size() > 0)
				video = videoList.get(0);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return video;
	}

	public Channel retrieveChannelById(String channelId) {

		String apikey =  Configuration.API_KEY;
		youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
			@Override
			public void initialize(HttpRequest request) throws IOException {
			}
		}).setApplicationName("youtube-video-verification").build();

		YouTube.Channels.List channelRequest;

		List<Channel> channelList = new ArrayList<Channel>();
		Channel returnChannel = new Channel();

		try {
			channelRequest = youtube.channels().list(
					"brandingSettings,contentDetails,contentOwnerDetails,id,invideoPromotion,localizations,snippet,statistics,status,topicDetails");
			channelRequest.setKey(apikey);
			channelRequest.setId(channelId);
			ChannelListResponse listResponse = channelRequest.execute();
			channelList = listResponse.getItems();
			returnChannel = channelList.get(0);		

		} catch (IOException e) {
			e.printStackTrace();
		}
		return returnChannel;
	}
	
	public static List<Comment> retrieveVideoCommentsReplies(String commentId) {

		String apikey =  Configuration.API_KEY;

	    youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
				@Override
				public void initialize(HttpRequest request) throws IOException {
				}
			}).setApplicationName("youtube-video-verification").build();
			  //  System.out.println("begin");
			YouTube.Comments.List commentsListResponse;
			List<Comment> comments = new ArrayList<Comment>();

			try {			
				commentsListResponse = youtube.comments().list("snippet");
				commentsListResponse.setParentId(commentId);
				commentsListResponse.setTextFormat("plainText");
				commentsListResponse.setKey(apikey);
					// execute our query
					CommentListResponse listResponse = commentsListResponse.execute();				
					comments.addAll(listResponse.getItems());

			} catch (IOException e) {
				e.printStackTrace();
			}
			return comments;
		}
	
	

	
	public static List<CommentThread> retrieveVideoComments(String videoId) {

		String apikey =  Configuration.API_KEY;

		youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
			@Override
			public void initialize(HttpRequest request) throws IOException {
			}
		}).setApplicationName("youtube-video-verification").build();

		YouTube.CommentThreads.List commentsRequest;
		List<CommentThread> comments = new ArrayList<CommentThread>();

		try {
			// Construct our query
			commentsRequest = youtube.commentThreads().list("snippet");
			commentsRequest.setVideoId(videoId);
			commentsRequest.setTextFormat("plainText");
			commentsRequest.setKey(apikey);
			commentsRequest.setMaxResults(50L);			
	
			String nextToken = "";
			int cnt = 1;
			do {
				cnt = cnt + 1;
				commentsRequest.setPageToken(nextToken);
				// execute our query
				CommentThreadListResponse listResponse = commentsRequest.execute();				
				comments.addAll(listResponse.getItems());
				nextToken = listResponse.getNextPageToken();
			} while (nextToken != null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return comments;
	}
	
	static String between(String value, String a, String b) {
        // Return a substring between the two strings.
        int posA = value.indexOf(a);
        if (posA == -1) {
            return "";
        }
        int posB = value.lastIndexOf(b);
        if (posB == -1) {
            return "";
        }
        int adjustedPosA = posA + a.length();
        if (adjustedPosA >= posB) {
            return "";
        }
        return value.substring(adjustedPosA, posB);
    }

	public VerificationInfo createVerificationInfo(Video video, Channel channel, List<CommentThread> comments)
			throws ParseException, IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		VerificationInfo vInfo = new VerificationInfo();
		// VIDEO-RELATED
		vInfo.setVideoId(video.getId());
		Date date = dateFormat.parse(video.getSnippet().getPublishedAt().toString());
		dateFormatnew.setTimeZone(TimeZone.getTimeZone( "UTC" ) );
		String formattedDate = dateFormatnew.format(date);
		vInfo.setVideoUploadTime(formattedDate);

		List<String> videoThumbnails = new ArrayList<String>();
		List<String> videoThumbnailsSearchUrls = new ArrayList<String>();

		// youtube thumbnails
		ThumbnailDetails allThumbnails = video.getSnippet().getThumbnails();
		if (allThumbnails.getDefault() != null)
			videoThumbnails.add(allThumbnails.getDefault().getUrl());
		if (allThumbnails.getHigh() != null)
			videoThumbnails.add(allThumbnails.getHigh().getUrl());
		if (allThumbnails.getMedium() != null)
			videoThumbnails.add(allThumbnails.getMedium().getUrl());
		if (allThumbnails.getMaxres() != null)
			videoThumbnails.add(allThumbnails.getMaxres().getUrl());
		if (allThumbnails.getStandard() != null)
			videoThumbnails.add(allThumbnails.getStandard().getUrl());

		// manual hidden thumbnails
		for (int i = 0; i < 4; i++) {
			String manual_thumb = "http://img.youtube.com/vi/" + vInfo.getVideoId() + "/" + i + ".jpg";
			videoThumbnails.add(manual_thumb);
		}
		vInfo.setVideoThumbnails(videoThumbnails);

		// create the reversed image urls for all videoThumbnails
		String googleReverseImageUrl = "https://www.google.com/searchbyimage?&image_url=";
		for (String thumbnail : videoThumbnails) {
			String reverseImageThumbnailSearchUrl = googleReverseImageUrl + thumbnail;
			videoThumbnailsSearchUrls.add(reverseImageThumbnailSearchUrl);
		}
		vInfo.setReverseImageThumbnailSearchUrl(videoThumbnailsSearchUrls);

		// video title
		vInfo.setVideoTitle(video.getSnippet().getTitle());
		Set<String> videoTitleMentionedDescriptions = vUtils.getUtils().getLocationMentions(vInfo.getVideoTitle(),
				getClassifier());

		// video description
		vInfo.setVideoDescription(video.getSnippet().getDescription());
		// video description mentioned locations
		Set<String> videoMentionedLocations = vUtils.getUtils().getLocationMentions(vInfo.getVideoDescription(),
				getClassifier());
		videoMentionedLocations.addAll(videoTitleMentionedDescriptions);
		vInfo.setVideoMentionedLocations(videoMentionedLocations);

		// video statistics
		vInfo.setVideoViewCount(video.getStatistics().getViewCount());
		vInfo.setVideoLikeCount(video.getStatistics().getLikeCount());
		vInfo.setVideoDislikeCount(video.getStatistics().getDislikeCount());
		vInfo.setVideoFavoriteCount(video.getStatistics().getFavoriteCount());
		vInfo.setVideoCommentCount(video.getStatistics().getCommentCount());

		// video content details
		String dur = video.getContentDetails().getDuration();
		//vInfo.setVideoDuration(video.getContentDetails().getDuration());
		vInfo.setVideoDuration(vUtils.getUtils().DurationFormat(dur));
		vInfo.setVideoDimension(video.getContentDetails().getDimension());
		vInfo.setVideoDefinition(video.getContentDetails().getDefinition());
		vInfo.setVideoLicensedContent(video.getContentDetails().getLicensedContent());

		// video recording details
		if (video.getRecordingDetails() != null) {
			if (video.getRecordingDetails().getRecordingDate() != null) {
				vInfo.setVideoRecordingTime(video.getRecordingDetails().getRecordingDate().toString());
			}
			vInfo.setVideoRecordingLocationDescription(video.getRecordingDetails().getLocationDescription());
		} else {
			vInfo.setVideoRecordingTime("");
			vInfo.setVideoRecordingLocationDescription("");
			//System.out.println("Recording details are null");
		}

		// twitter search url
		String twitterSearchUrl = "https://twitter.com/search?f=tweets&vertical=default&q=https://www.youtube.com/watch?v=" + vInfo.getVideoId();
		try {
			vInfo.setTwitterSearchUrl(new URL(twitterSearchUrl));
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		
		// CHANNEL-RELATED
		vInfo.setChannelId(channel.getId());
		String channelUrl = "https://www.youtube.com/channel/" + vInfo.getChannelId();
		vInfo.setChannelUrl(channelUrl);
		String channelAboutPage = "https://www.youtube.com/user/" + channel.getSnippet().getTitle() + "/about";
		vInfo.setChannelAboutPage(channelAboutPage);
		// channel created time
		Date date_channel = dateFormat.parse(channel.getSnippet().getPublishedAt().toString());
		dateFormatnew.setTimeZone(TimeZone.getTimeZone( "UTC" ) );
		String formattedDateChannel = dateFormatnew.format(date_channel);
		vInfo.setChannelCreatedTime(formattedDateChannel);
		// channel location
		String channelLocation = channel.getSnippet().getCountry();
		if (channelLocation == null) {
			vInfo.setChannelLocation("Not available");
		} else {
			vInfo.setChannelLocation(channelLocation);
		}
		// channel description
		vInfo.setChannelDescription(channel.getSnippet().getDescription());

		// channel description mentioned locations
		Set<String> channelMentionedLocations = vUtils.getUtils().getLocationMentions(vInfo.getChannelDescription(),
				getClassifier());
		vInfo.setChannelMentionedLocations(channelMentionedLocations);

		// channel statistics
		vInfo.setChannelViewCount(channel.getStatistics().getViewCount());
		vInfo.setChannelCommentCount(channel.getStatistics().getCommentCount());
		vInfo.setChannelSubscriberCount(channel.getStatistics().getSubscriberCount());
		vInfo.setChannelVideoCount(channel.getStatistics().getVideoCount());

		// number of videos per month posted by channel
		//SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Date parsedDateCurrent, parsedDateChannelCreated;
		try {
			// video upload time in epoch
			parsedDateCurrent = new Date(System.currentTimeMillis() / 1000);
			// = dateFormat.parse(vInfo.getVideoUploadTime());
			// channel created time in epoch
			parsedDateChannelCreated = dateFormatnew.parse(vInfo.getChannelCreatedTime());

			// time between video uploaded and channel created in months
			long timeInMonths = (parsedDateChannelCreated.getTime() - parsedDateCurrent.getTime()) / (30 * 24 * 3600);

			double videosPerMonth = Double.parseDouble(vInfo.getChannelVideoCount().toString()) / timeInMonths;

			vInfo.setChannelVideosPerMonth(videosPerMonth);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// channel google account
		String googlePlusId = channel.getContentDetails().getGooglePlusUserId();
		if (googlePlusId != null) {
			String channelGoogleAccount = "https://plus.google.com/"
					+ channel.getContentDetails().getGooglePlusUserId();
			vInfo.setChannelGoogleAccount(channelGoogleAccount);
		}

		// COMMENTS-RELATED
		// set the verification comments
		List<String> helpfulComments = new ArrayList<String>();
		// set the total comments
		List<BasicDBObject> videoComments2 = new ArrayList<BasicDBObject>();

		
		// load the verification words from properties file
		//String[] words = getProperties().getProperty("verification_words").split(",");
		String[] words = Configuration.VERIFICATION_WORDS.split(",");
		
		for (CommentThread comment : comments) {
			
				String input = comment.getSnippet().getTopLevelComment().getSnippet().getTextDisplay();				
				if (vUtils.getUtils().isAVerificationComment(input, words)) {
					helpfulComments.add(input);
				}			
				BasicDBObject reliesDetails = new BasicDBObject();			
				Date date2 = dateFormat.parse(comment.getSnippet().getTopLevelComment().getSnippet().getPublishedAt().toString());
				dateFormatnew.setTimeZone(TimeZone.getTimeZone( "UTC" ) );
				String formattedDate2 = dateFormatnew.format(date2);			
				BasicDBObject documentDetail = new BasicDBObject();
				documentDetail.put("comment", input);
				documentDetail.put("author", comment.getSnippet().getTopLevelComment().getSnippet().getAuthorDisplayName());
				documentDetail.put("publishedAt", formattedDate2);
				documentDetail.put("author_url", comment.getSnippet().getTopLevelComment().getSnippet().getAuthorChannelUrl());
				documentDetail.put("like_count", comment.getSnippet().getTopLevelComment().getSnippet().getLikeCount());
				documentDetail.put("replies", reliesDetails);
				videoComments2.add(documentDetail);
			}
			vInfo.setVerificationComments(helpfulComments);
			vInfo.setNumVerificationComments(helpfulComments.size());
			vInfo.setVideoComments2(videoComments2);		
		return vInfo;
	}
	
	public static BigInteger getNumberOfComments(String videoId)
			throws UnsupportedEncodingException {
		BigInteger number_of_comments = null;
		VideoVerifier vidVerif = new VideoVerifier();
		String retrieveId = videoId;
		// retrieve the video
		Video video = vidVerif.retrieveVideoById(retrieveId);

			if (video.isEmpty()) {
				System.out.println("This video cannot be found!");
			} else {				
				number_of_comments = video.getStatistics().getCommentCount();
			}
		return number_of_comments;
	}
	
	public static void updateVerifyVideo(String videoId, String fields) throws Exception {
		Bson filter = null;
		Bson newValue = null;
		Bson updateOperationDocument = null;
		String video_comment_count_field = "video_comment_count";
		System.out.println("start verifying async");
		VideoVerifier vidVerif = new VideoVerifier();
		String retrieveId = videoId;
		// retrieve the video
		Video video = vidVerif.retrieveVideoById(retrieveId);
		VerificationInfo vInfo = new VerificationInfo();
		JsonObject verificationResult = new JsonObject();		
	
		if (video.isEmpty()) {
			verificationResult.addProperty("message", "This video cannot be found!");
		} else {
		
				MongoClient mongoClient = null;
				 MongoCredential credential = MongoCredential.createCredential(MongoConfiguration.USER, MongoConfiguration.ADMIN_DB, MongoConfiguration.PWD.toCharArray());
				try {
					//mongoClient = new MongoClient(Configuration.MONGO_HOST, 27017);
					 mongoClient = new MongoClient(new ServerAddress(), Arrays.asList(credential));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				MongoDatabase database = mongoClient.getDatabase(MongoConfiguration.VIDEO_CONTEXT_DB);
				MongoCollection<Document> colYT = database.getCollection(MongoConfiguration.DB_YOUTUBE_COLLECTION);
							
				 filter = new Document(mongoDocId, videoId);
				 newValue = new Document(processing_status, "processing");
				 updateOperationDocument = new Document("$set", newValue);
				 colYT.updateOne(filter, updateOperationDocument);
						 
				 FindIterable<Document> iterable = database.getCollection(MongoConfiguration.DB_YOUTUBE_COLLECTION).find(new Document(mongoDocId, videoId));
				 int video_comments_count = iterable.first().getInteger(video_comment_count_field);					
					 String video_comments_count_upt = VideoVerifier.getNumberOfComments(videoId).toString();
					
					 if (Integer.valueOf(video_comments_count_upt) > video_comments_count){						
										// retrieve video's channel
										Channel channel = vidVerif.retrieveChannelById(video.getSnippet().getChannelId());
						
										List<CommentThread> comments = vidVerif.retrieveVideoComments(retrieveId);											
										try {
											vInfo = vidVerif.createVerificationInfo(video, channel, comments);
										} catch (UnsupportedEncodingException e) {
											e.printStackTrace();
										}	
										Gson gson = new Gson();
										String jsonString = null;					
										if (fields==null) fields = "all";
										
										//in case we need just the fields for InVid project, we perform dynamic serialization of the VerificationInfo class
										if (fields.equals("invid")) {
											gson = new GsonBuilder()
													.registerTypeHierarchyAdapter(VerificationInfo.class, new VerificationInfoSerializer())
													.create();
											jsonString = gson.toJson(vInfo);
												//else we do the normal way
										} else if (fields.equals("all")){				
											jsonString = vInfo.toJSONString();
											
										}								
										JsonElement element = gson.fromJson(jsonString, JsonElement.class);
										verificationResult = element.getAsJsonObject();						
													 
						 
						   Object o = JSON.parse(verificationResult.toString());
		        		   DBObject dbObj = (DBObject) o;			        	
		        		   filter = new Document(mongoDocId, videoId);			        	
		        		   updateOperationDocument = new Document("$set", dbObj);
		        		   colYT.updateOne(filter, updateOperationDocument);
		    		  				        		  
						   newValue = new Document(processing_status, "done");
						   updateOperationDocument = new Document("$set", newValue);
						   colYT.updateOne(filter, updateOperationDocument);
					 }else{
						 System.out.println("No NEW comments - No update - Finish YouTube verification");
						 filter = new Document(mongoDocId, videoId);	
						 newValue = new Document(processing_status, "done");
						 updateOperationDocument = new Document("$set", newValue);
						 colYT.updateOne(filter, updateOperationDocument);
					 }	
	
			 mongoClient.close();
		}
	}

	
	public static void verifyVideo(String videoId, String fields) throws Exception {
		
		long startTime = System.currentTimeMillis();
		
		Bson filter = null;
		Bson newValue = null;
		Bson updateOperationDocument = null;
		VideoVerifier vidVerif = new VideoVerifier();
		

		MongoClient mongoClient = null;
		MongoCredential credential = MongoCredential.createCredential(MongoConfiguration.USER, MongoConfiguration.ADMIN_DB, MongoConfiguration.PWD.toCharArray());
		try {
			 mongoClient = new MongoClient(new ServerAddress(), Arrays.asList(credential));			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("mongo problem");
			e.printStackTrace();
			mongoClient.close();
		}
		
		MongoDatabase database = mongoClient.getDatabase(MongoConfiguration.VIDEO_CONTEXT_DB);
		MongoCollection<Document> colYT = database.getCollection(MongoConfiguration.DB_YOUTUBE_COLLECTION);
		
		 Document d = new Document(mongoDocId, videoId);		
		 filter = new Document(mongoDocId, videoId);
		 d.append(processing_status, "processing");		        	
    	 colYT.insertOne(d);
		
		VerificationInfo vInfo = new VerificationInfo();
		JsonObject verificationResult = new JsonObject();		
		
		System.out.println("start verifying async");
		
		String retrieveId = videoId;
		// retrieve the video
		Video video = vidVerif.retrieveVideoById(retrieveId);

		if (video.isEmpty()) {
			verificationResult.addProperty("message", "This video cannot be found!");
		} else {			
					// retrieve video's channel
					Channel channel = vidVerif.retrieveChannelById(video.getSnippet().getChannelId());
					
					List<CommentThread> comments = vidVerif.retrieveVideoComments(retrieveId);	
					System.out.println("Comments retrieved!");
					try {
						vInfo = vidVerif.createVerificationInfo(video, channel, comments);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}	
					Gson gson = new Gson();
					String jsonString = null;					
					if (fields==null) fields = "all";
					
					//in case we need just the fields for InVid project, we perform dynamic serialization of the VerificationInfo class
					if (fields.equals("invid")) {
						gson = new GsonBuilder()
								.registerTypeHierarchyAdapter(VerificationInfo.class, new VerificationInfoSerializer())
								.create();
						jsonString = gson.toJson(vInfo);
							//else we do the normal way
					} else if (fields.equals("all")){				
						jsonString = vInfo.toJSONString();
						
					}
					JsonElement element = gson.fromJson(jsonString, JsonElement.class);
					verificationResult = element.getAsJsonObject();			
		}		
		
		   Object o = JSON.parse(verificationResult.toString());
		   DBObject dbObj = (DBObject) o;			        	
		   filter = new Document(mongoDocId, videoId);			        	
		   updateOperationDocument = new Document("$set", dbObj);
		   colYT.updateOne(filter, updateOperationDocument);
	  				        		  
		   newValue = new Document(processing_status, "done");
		   updateOperationDocument = new Document("$set", newValue);
		   colYT.updateOne(filter, updateOperationDocument);
		   mongoClient.close();
		   
		   long stopTime = System.currentTimeMillis();
		   long elapsedTime = stopTime - startTime;
		   System.out.println("Elapsed time " + elapsedTime);
		   System.out.println("END YOUTUBE!");
	}
}
