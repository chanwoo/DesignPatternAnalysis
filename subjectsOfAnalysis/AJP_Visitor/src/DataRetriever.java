import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class DataRetriever{
    public static Object deserializeData(String fileName){
        Object returnValue = null;
        try{
            File inputFile = new File(fileName);
            if (inputFile.exists() && inputFile.isFile()){
                ObjectInputStream readIn = new ObjectInputStream(new FileInputStream(fileName));
                returnValue = readIn.readObject();
                readIn.close();
            }
            else {
                System.err.println("Unable to locate the file " + fileName);
            }
        }
        catch (ClassNotFoundException exc){
            exc.printStackTrace();
            
        }
        catch (IOException exc){
            exc.printStackTrace();
            
        }
        return returnValue;
    }
}