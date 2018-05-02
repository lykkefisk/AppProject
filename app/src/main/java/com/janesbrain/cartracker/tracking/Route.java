package com.janesbrain.cartracker.tracking;

public class Route {

    private Distance _distance;
    private Duration _time;
    private String _endAddress;
    private String _startAddress;
   // public LatLng endLocation;
   // public LatLng startLocation;
//    public List<LatLng> points;

    public void SetDistance(String key, int value){
                _distance= new Distance(key,value);
}
    public void SetDuration(String key, int value){
        _time = new Duration(key, value);
    }

    public void SetAddresses(String starting, String ending){
        _startAddress = starting;
        _endAddress = ending;
    }
}

class Distance{
    public String text;
    public int value;

    public Distance(String text, int value) {
        this.text = text;
        this.value = value;
    }
}

class Duration{
    public String text;
    public int value;

    public Duration(String text, int value) {
        this.text = text;
        this.value = value;
    }
}
