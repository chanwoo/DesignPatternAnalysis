import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;
public class Appointment implements Serializable{
    private String reason;
    private ArrayList contacts;
    private Location location;
    private Date startDate;
    private Date endDate;

    public Appointment(String reason, ArrayList contacts, Location location, Date startDate, Date endDate){
        this.reason = reason;
        this.contacts = contacts;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    public String getReason(){ return reason; }
    public ArrayList getContacts(){ return contacts; }
    public Location getLocation(){ return location; }
    public Date getStartDate(){ return startDate; }
    public Date getEndDate(){ return endDate; }
    
    public void setReason(String reason){ this.reason = reason; }
    public void setContacts(ArrayList contacts){ this.contacts = contacts; }
    public void setLocation(Location location){ this.location = location; }
    public void setStartDate(Date startDate){ this.startDate = startDate; }
    public void setEndDate(Date endDate){ this.endDate = endDate; }
    
    public String toString(){
        return "Appointment:" + "\n    Reason: " + reason +
		"\n    Location: " + location + "\n    Start: " +
            startDate + "\n    End: " + endDate + "\n";
    }
}