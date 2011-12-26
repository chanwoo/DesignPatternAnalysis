import java.util.ArrayList;
public class DependentTask extends Task{
    private ArrayList dependentTasks = new ArrayList();
    private double dependencyWeightingFactor;
    
    public DependentTask(){ }
    public DependentTask(String newName, Contact newOwner,
        double newTimeRequired, double newWeightingFactor){
        super(newName, newOwner, newTimeRequired);
        dependencyWeightingFactor = newWeightingFactor;
    }
    
    public ArrayList getDependentTasks(){ return dependentTasks; }
    public double getDependencyWeightingFactor(){ return dependencyWeightingFactor; }
    
    public void setDependencyWeightingFactor(double newFactor){ dependencyWeightingFactor = newFactor; }
    
    public void addDependentTask(Task element){
        if (!dependentTasks.contains(element)){
            dependentTasks.add(element);
        }
    }
    
    public void removeDependentTask(Task element){
        dependentTasks.remove(element);
    }
    
    public void accept(ProjectVisitor v){
        v.visitDependentTask(this);
    }
}