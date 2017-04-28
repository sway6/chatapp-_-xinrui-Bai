package entity;

/**
 * Created by baixinrui on 4/15/16.
 */
public class Client {
    public double latitude;
    public double longitude;
    public String name;
    public String address;

    public Client(double latitude , double longitude , String name , String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.address = address;
    }
}
