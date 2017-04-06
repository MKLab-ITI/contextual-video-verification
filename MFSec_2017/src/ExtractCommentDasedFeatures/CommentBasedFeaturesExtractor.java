package test.extract.features;

/**
 * Extract features from YouTube comments
 * @author olgapapa
 *
 */

public class CommentBasedFeaturesExtractor {
	
	
		public static void extractFeatures(String comment_text, String id) throws Exception{		
			@SuppressWarnings("unused")
			ItemFeatures temp = ItemFeaturesExtractorJSON.extractFeaturesYTComment(comment_text, id);							
		}
		
		public static void main(String[] args) throws Exception {

			String comment_text = "This is a YouTube video comment";
			String id = "YouTubeID-IDX";  // e.g. nkQ-ij3LTTM-0 - first comment of video https://www.youtube.com/watch?v=nkQ-ij3LTTM
			extractFeatures(comment_text, id);
		}

}
