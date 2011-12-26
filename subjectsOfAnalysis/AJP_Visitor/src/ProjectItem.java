import java.io.Serializable;
import java.util.ArrayList;
public interface ProjectItem extends Serializable{
    public void accept(ProjectVisitor v);
    public ArrayList getProjectItems();
}