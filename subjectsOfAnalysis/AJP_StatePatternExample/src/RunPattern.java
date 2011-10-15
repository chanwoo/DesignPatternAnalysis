import java.io.File;
public class RunPattern{
    public static void main(String [] arguments){
        System.out.println("Example for the State pattern");
        System.out.println();
        
        if (!(new File("appointments.ser").exists())){
            DataCreator.serialize("appointments.ser");
        }
        
        System.out.println("Creating CalendarEditor");
        CalendarEditor appointmentBook = new CalendarEditor();
        System.out.println("");
        
        System.out.println("Created. Appointments:");
        System.out.println(appointmentBook.getAppointments());
        
        System.out.println("Created. Creating GUI:");
        StateGui application = new StateGui(appointmentBook);
        application.createGui();
        System.out.println("");
    }
}