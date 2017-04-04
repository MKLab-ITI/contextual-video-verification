package gr.iti.mklab.videoverification.weather;

public class GeoCoordinates{
	public double lat;
    public double lon;
    public boolean exist;
    
    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
    
    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
    
    public boolean getExist(){
    	return exist;
    }
    
    public void setExist(boolean exist){
    	this.exist = exist;
    }
  
}
