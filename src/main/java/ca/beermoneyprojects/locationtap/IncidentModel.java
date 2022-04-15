package ca.beermoneyprojects.locationtap;
import ca.beermoneyprojects.incidents.Incident;

public class IncidentModel {
    private Incident incident;
    private int incident_image;

    // Constructor
    public IncidentModel(Incident incident, int incident_image) {
        this.incident = incident;
        this.incident_image = incident_image;
    }

    // Getter and Setter
    public Incident getIncident_name() {
        return incident;
    }

    public void setIncident_name(Incident incident_name) {
        this.incident = incident_name;
    }

    public int getIncident_image() {
        return incident_image;
    }

    public void setIncident_image(int incident_image) {
        this.incident_image = incident_image;
    }
}
