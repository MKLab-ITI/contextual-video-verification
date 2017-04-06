package test.extract.features;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Feature Item
 * @author olgapapa
 *
 */


public class ItemFeatures {
	
	@Expose
    @SerializedName(value = "id")
	private String id;
	
	@Expose
    @SerializedName(value = "itemLength",  alternate={"item_length"})
	private Integer itemLength;
	
	@Expose
    @SerializedName(value = "numWords", alternate= {"num_words"})
	private Integer numWords;
	
	@Expose
    @SerializedName(value = "containsQuestionMark", alternate= {"contains_questionmark"})
	private Boolean containsQuestionMark;
	
	@Expose
    @SerializedName(value = "containsExclamationMark", alternate= {"contains_exclamationmark"})
	private Boolean containsExclamationMark;
	
	@Expose
    @SerializedName(value = "numQuestionMark", alternate= {"num_questionmark"})
	private Integer numQuestionMark;
	
	@Expose
    @SerializedName(value = "numExclamationMark", alternate= {"num_exclamationmark"})
	private Integer numExclamationMark;
	
	@Expose
    @SerializedName(value = "containsHappyEmo", alternate= {"contains_happy_emoticon"})
	private Boolean containsHappyEmo;
	
	@Expose
    @SerializedName(value = "containsSadEmo", alternate= {"contains_sad_emoticon"})
	private Boolean containsSadEmo;
	
	@Expose
    @SerializedName(value = "containsFirstOrderPron", alternate= {"contains_first_order_pronoun"})
	private Boolean containsFirstOrderPron;
	
	@Expose
    @SerializedName(value = "containsSecondOrderPronoun", alternate= {"contains_second_order_pronoun"})
	private Boolean containsSecondOrderPron;
	
	@Expose
    @SerializedName(value = "containsThirdOrderPronoun", alternate= {"contains_third_order_pronoun"})
	private Boolean containsThirdOrderPron;
	
	@Expose
    @SerializedName(value = "numUppercaseChars", alternate= {"num_uppercasechars"})
	private Integer numUppercaseChars;
	
	@Expose
    @SerializedName(value = "numNegSentiWords", alternate= {"num_neg_sentiment_words"})
	private Integer numNegSentiWords;
	
	@Expose
    @SerializedName(value = "numPosSentiWords", alternate= {"num_pos_sentiment_words"})
	private Integer numPosSentiWords;
	
	@Expose
    @SerializedName(value = "hasColon", alternate= {"has_colon"})
	private Boolean hasColon;
	
	@Expose
    @SerializedName(value = "hasPlease", alternate= {"has_please"})
	private Boolean hasPlease;
	
	@Expose
    @SerializedName(value = "numSlangs", alternate= {"num_slangs"})
	private Integer numSlangs;
	
	@Expose
    @SerializedName(value = "readability")
	private Double readability;
	
		
	public void setId(String id){
		this.id = id;
	}
	
	public String getId(){
		return id;
	}
	
	public void setItemLength(Integer itemLength){
		this.itemLength = itemLength;
	}
	
	public Integer getItemLength(){
		return itemLength;
	}
	
	public void setNumWords(Integer numWords){
		this.numWords = numWords;
	}
	
	public Integer getNumWords(){
		return numWords;
	}
	
	public void setContainsExclamationMark(Boolean containsExclamationMark){
		this.containsExclamationMark = containsExclamationMark;
	}
	
	public boolean getContainsExclamationMark(){
		return containsExclamationMark;
	}
	
	public void setContainsQuestionMark(Boolean containsQuestionMark){
		this.containsQuestionMark = containsQuestionMark;
	}

	public boolean getContainsQuestionMark(){
		return containsQuestionMark;
	}
	
	public void setnumExclamationMark(Integer numExclamationMark){
		this.numExclamationMark = numExclamationMark;
	}
	
	public Integer getNumExclamationMark(){
		return numExclamationMark;
	}
	
	public void setNumQuestionMark(Integer numQuestionMark){
		this.numQuestionMark = numQuestionMark;
	}
	
	public Integer getNumQuestionMark(){
		return numQuestionMark;
	}
	
	public void setContainsHappyEmo(boolean containsHappyEmo){
		this.containsHappyEmo = containsHappyEmo;
	}
	
	public boolean getContainsHappyEmo(){
		return containsHappyEmo;
	}
	
	public void setContainsSadEmo(boolean containsSadEmo){
		this.containsSadEmo = containsSadEmo;
	}
	
	public boolean getContainsSadEmo(){
		return containsSadEmo;
	}
	
	public void setContainsFirstOrderPron(boolean containsFirstOrderPron){
		this.containsFirstOrderPron = containsFirstOrderPron;
	}
	
	public Boolean getContainsFirstOrderPron(){
		return containsFirstOrderPron;
	}
		
	public void setContainsSecondOrderPron(boolean containsSecondOrderPron){
		this.containsSecondOrderPron = containsSecondOrderPron;
	}
	
	public Boolean getContainsSecondOrderPron(){
		return containsSecondOrderPron;
	}
	
	public void setContainsThirdOrderPron(boolean containsThirdOrderPron){
		this.containsThirdOrderPron = containsThirdOrderPron;
	}
	 
	public Boolean getContainsThirdOrderPron(){
		return containsThirdOrderPron;
	}
	
	public void setNumUppercaseChars(Integer numUppercaseChars){
		this.numUppercaseChars = numUppercaseChars;
	}
	
	public Integer getNumUppercaseChars(){
		return numUppercaseChars;
	}
	
	public void setNumNegSentiWords(Integer numNegSentiWords){
		this.numNegSentiWords = numNegSentiWords;
	}
	
	public Integer getNumNegSentiWords(){
		return numNegSentiWords;
	}
	
	public void setNumPosSentiWords(Integer numPosSentiWords){
		this.numPosSentiWords = numPosSentiWords;
	}
	
	public Integer getNumPosSentiWords(){
		return numPosSentiWords;
	}
	
	public void setNumSlangs(Integer numSlangs){
		this.numSlangs = numSlangs;
	}
	
	public Integer getNumSlangs() {
		return numSlangs;
	}
	
	public void setHasColon(boolean hasColon) {
		this.hasColon = hasColon;
	}
	
	public boolean getHasColon() {
		return hasColon;
	}
	
	public void setHasPlease(boolean hasPlease) {
		this.hasPlease = hasPlease;
	}
	
	public boolean getHasPlease() {
		return hasPlease;
	}
	
	public void setReadability(Double readability){
		this.readability = readability;
	}
	
	public Double getReadability() {
		return readability;
	}	
	 
}
