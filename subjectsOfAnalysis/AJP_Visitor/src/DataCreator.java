import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
public class DataCreator{
    private static final String DEFAULT_FILE = "data.ser";
    
    public static void main(String [] args){
        String fileName;
        if (args.length == 1){
            fileName = args[0];
        }
        else{
            fileName = DEFAULT_FILE;
        }
        serialize(fileName);
    }
    
    public static void serialize(String fileName){
        try{
            serializeToFile(createData(), fileName);
        }
        catch (IOException exc){
            exc.printStackTrace();
        }
    }
    
    private static Serializable createData(){
        Contact contact = new ContactImpl("Test", "Subject", "Volunteer", "United Patterns Consortium");
        
        Project project = new Project("Project 1", "Test Project");
        
        Task task1 = new Task("Task 1", contact, 1);
        Task task2 = new Task("Task 2", contact, 1);

        project.addProjectItem(new Deliverable("Deliverable 1", "Layer 1 deliverable", contact, 50.0, 50.0));
        project.addProjectItem(task1);
        project.addProjectItem(task2);
        project.addProjectItem(new DependentTask("Dependent Task 1", contact, 1, 1));
        
        Task task3 = new Task("Task 3", contact, 1);
        Task task4 = new Task("Task 4", contact, 1);
        Task task5 = new Task("Task 5", contact, 1);
        Task task6 = new Task("Task 6", contact, 1);
        
        DependentTask dtask2 = new DependentTask("Dependent Task 2", contact, 1, 1);
        
        task1.addProjectItem(task3);
        task1.addProjectItem(task4);
        task1.addProjectItem(task5);
        task1.addProjectItem(dtask2);
        
        dtask2.addDependentTask(task5);
        dtask2.addDependentTask(task6);
        dtask2.addProjectItem(new Deliverable("Deliverable 2", "Layer 3 deliverable", contact, 50.0, 50.0));
        
        task3.addProjectItem(new Deliverable("Deliverable 3", "Layer 3 deliverable", contact, 50.0, 50.0));
        task4.addProjectItem(new Task("Task 7", contact, 1));
        task4.addProjectItem(new Deliverable("Deliverable 4", "Layer 3 deliverable", contact, 50.0, 50.0));
        return project;
    }
    
    private static void serializeToFile(Serializable content, String fileName) throws IOException{
        ObjectOutputStream serOut = new ObjectOutputStream(new FileOutputStream(fileName));
        serOut.writeObject(content);
        serOut.close();
    }
}