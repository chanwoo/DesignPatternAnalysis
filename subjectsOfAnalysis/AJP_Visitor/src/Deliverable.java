import java.util.ArrayList;
public class Deliverable implements ProjectItem{
    private String name;
    private String description;
    private Contact owner;
    private double materialsCost;
    private double productionCost;
    
    public Deliverable(){ }
    public Deliverable(String newName, String newDescription,
        Contact newOwner, double newMaterialsCost, double newProductionCost){
        name = newName;
        description = newDescription;
        owner = newOwner;
        materialsCost = newMaterialsCost;
        productionCost = newProductionCost;
    }
    
    public String getName(){ return name; }
    public String getDescription(){ return description; }
    public Contact getOwner(){ return owner; }
    public double getMaterialsCost(){ return materialsCost; }
    public double getProductionCost(){ return productionCost; }
    
    public void setMaterialsCost(double newCost){ materialsCost = newCost; }
    public void setProductionCost(double newCost){ productionCost = newCost; }
    public void setName(String newName){ name = newName; }
    public void setDescription(String newDescription){ description = newDescription; }
    public void setOwner(Contact newOwner){ owner = newOwner; }

    public void accept(ProjectVisitor v){
        v.visitDeliverable(this);
    }
    
    public ArrayList getProjectItems(){
        return null;
    }
}