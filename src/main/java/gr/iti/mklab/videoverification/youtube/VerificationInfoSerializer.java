package gr.iti.mklab.videoverification.youtube;

import java.lang.reflect.Type;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * This class implements a custom Serializer for the VerificationInfo.  
 * @author boididou 20.09.2016
 *
 */
public class VerificationInfoSerializer implements JsonSerializer<VerificationInfo>{

	@Override
	/**
	 * fieldsToRemove: The fields of the JsonObject that should be removed from the object
	 * vInfo: the VerificationInfo object given as input
	 * 
	 * The method excludes from the original json the unnecessary fields and returns the customized json.
	 */
	public JsonElement serialize(VerificationInfo vInfo, Type arg1, JsonSerializationContext arg2) {
		
		JsonObject jObj = (JsonObject)new GsonBuilder().create().toJsonTree(vInfo);
		
		String[] fieldsToRemove = new String[] {
				"video_id", "video_title", "video_description_mentioned_locations",
				"video_upload_time", "video_view_count", "video_like_count", "video_dislike_count",
				"video_favorite_count","video_comment_count", "video_duration", "video_dimension",
				"video_definition", "video_licensed_content", "video_thumbnails", "channel_description",
				"channel_description_mentioned_locations", "channel_url", "channel_id"
		};
								
		for (String field:fieldsToRemove) {
			System.out.println("removed fields " + field);
			jObj.remove(field);
		}
		System.out.println(jObj.toString());
		return jObj;
	}
	
	
}
