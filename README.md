# contextual-video-verification
Provides support to end users for verifying web videos using metadata and contextual signals.

This contains:
 -  An evolving dataset of fake and real videos derived exclusively from the YouTube platform.
 -  A JAVA RESTful web service for collecting and analysing context of Web Videos. Currently, it is compatible only with YouTube platform and collects information of the video itself (title, description, comments, etc.), the channel where the video was uploaded and the tweets that share the video.
 

### RESTful web service

The main class which exposes all the functionalities is the ```VideoVerificationController```. In the constructor, several objects are initialized, as well as the configuration.

#### Properties configuration - remote.properties

- apikey: The YouTube API key
- verification_words: A list of verification words
- path_of_classifier_for_location: The directory where the location classifier is stored
- path_of_lexicalized_parser: The directory where the lexicalized parser is stored
- chromedriver: The directory where the chromedriver is stored
- weather_api_key: Th darksky weather API key
- google_geo_api_key: The Google Geo API key
- report_base_url: The IP where the service is hosted
- tweet_verification_service_url: The IP where the tweet verification service is hosted

#### MongoDB configuration - mongoRemote.properties

- mongohostip: The IP where mongoDB is hosted
- username: MongoDB authentication username
- password: MongoDB authentication password
- video_contex_db: Name of main database
- log_collection: Name of log collection
- db_youtube_collection: Name of the collection where the YouTube data are stored
- db_twitter_collection: Name of the collection where the aggregate statistics of the tweets are stored
- db_tweets_collection: Name of the collection where all tweets sharing the processed videos are stored
- admin_db: Admin database required for the authentication (the database where the users are defined)

#### Request

http://caa.iti.gr:8090/verify_video?id=<YouTube_ID>

#### Responses

- http://caa.iti.gr:8090/get_ytverification?id=<YouTube_ID>
- http://caa.iti.gr:8090/get_twverification?id=<YouTube_ID>

More details can be found at the swagger documentation: http://caa.iti.gr:8080/swagger/?url=http://caa.iti.gr:8080/Context_Aggregation_and_Analysis.json

For further details, contact Symeon Papadopoulos (papadop@iti.gr) or Olga Papadopoulou (olgapapa@iti.gr).
