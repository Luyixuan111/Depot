import java.util.HashMap;
import java.util.Map;

public class ParcelMap {
    private Map<String, Parcel> parcelMap;

    public ParcelMap() {
        parcelMap = new HashMap<>();
    }

    // Method to add a Parcel to the map
    public void addParcel(Parcel parcel) {
        parcelMap.put(parcel.getParcelId(), parcel);
    }

    // Method to get a Parcel from the map by its ID
    public Parcel getParcel(String parcelId) {
        return parcelMap.get(parcelId);
    }

    // Method to remove a Parcel from the map by its ID
    public void removeParcel(String parcelId) {
        parcelMap.remove(parcelId);
    }

    // Method to check if the map contains a Parcel with a given ID
    public boolean containsParcel(String parcelId) {
        return parcelMap.containsKey(parcelId);
    }

    // Method to get the number of Parcels in the map
    public int size() {
        return parcelMap.size();
    }

    // Method to get all the Parcels in the map
    public Map<String, Parcel> getAllParcels() {
        return parcelMap;
    }
}