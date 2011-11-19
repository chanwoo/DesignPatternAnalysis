package test;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class MyCommand implements Command {

	private String id;

	public MyCommand(String id) {
		this.id = id;
	}

	public boolean execute(Context context) throws Exception {
		System.out.println(id + " executed");

		String target = (String) context.get("ID");
		if (target.equalsIgnoreCase(id))
			return true;
		
		return false;
	}

}
