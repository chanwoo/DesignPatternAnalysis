import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import java.util.Date;
public class StateGui implements ActionListener{
    private JFrame mainFrame;
    private JPanel controlPanel, editPanel;
    private CalendarEditor editor;
    private JButton save, exit;
    
    public StateGui(CalendarEditor edit){
        editor = edit;
    }
    
    public void createGui(){
        mainFrame = new JFrame("State Pattern Example");
        Container content = mainFrame.getContentPane();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        
        editPanel = new JPanel();
        editPanel.setLayout(new BorderLayout());
        JTable appointmentTable = new JTable(new StateTableModel((Appointment [])editor.getAppointments().toArray(new Appointment[1])));
        editPanel.add(new JScrollPane(appointmentTable));
        content.add(editPanel);
        
        controlPanel = new JPanel();
        save = new JButton("Save Appointments");
        exit = new JButton("Exit");
        controlPanel.add(save);
        controlPanel.add(exit);
        content.add(controlPanel);
        
        save.addActionListener(this);
        exit.addActionListener(this);
        
        mainFrame.addWindowListener(new WindowCloseManager());
        mainFrame.pack();
        mainFrame.setVisible(true);
    }
    
    
    public void actionPerformed(ActionEvent evt){
        Object originator = evt.getSource();
        if (originator == save){
            saveAppointments();
        }
        else if (originator == exit){
            exitApplication();
        }
    }
    
    private class WindowCloseManager extends WindowAdapter{
        public void windowClosing(WindowEvent evt){
            exitApplication();
        }
    }
    
    private void saveAppointments(){
        editor.save();
    }
    
    private void exitApplication(){
        System.exit(0);
    }
    
    private class StateTableModel extends AbstractTableModel{
        private final String [] columnNames = {
            "Appointment", "Contacts", "Location", "Start Date", "End Date" };
        private Appointment [] data;
        
        public StateTableModel(Appointment [] appointments){
            data = appointments;
        }
        
        public String getColumnName(int column){
            return columnNames[column];
        }
        public int getRowCount(){ return data.length; }
        public int getColumnCount(){ return columnNames.length; }
        public Object getValueAt(int row, int column){
            Object value = null;
            switch(column){
                case 0: value = data[row].getReason();
                    break;
                case 1: value = data[row].getContacts();
                    break;
                case 2: value = data[row].getLocation();
                    break;
                case 3: value = data[row].getStartDate();
                    break;
                case 4: value = data[row].getEndDate();
                    break;
            }
            return value;
        }
        public boolean isCellEditable(int row, int column){
            return ((column == 0) || (column == 2)) ? true : false;
        }
        public void setValueAt(Object value, int row, int column){
            switch(column){
                case 0: data[row].setReason((String)value);
                    editor.edit();
                    break;
                case 1:
                    break;
                case 2: data[row].setLocation(new LocationImpl((String)value));
                    editor.edit();
                    break;
                case 3:
                    break;
                case 4:
                    break;
            }
        }
    }
}