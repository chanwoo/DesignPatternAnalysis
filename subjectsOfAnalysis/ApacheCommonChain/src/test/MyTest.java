package test;

import org.apache.commons.chain.impl.ChainBase;
import org.apache.commons.chain.impl.ContextBase;

public class MyTest {
	public static void main(String[] args) throws Exception {
		ChainBase base = new ChainBase();

		base.addCommand(new MyCommand("A"));
		base.addCommand(new MyCommand("B"));
		base.addCommand(new MyCommand("C"));

		ContextBase context = new ContextBase();
		context.put("ID", "C");

		base.execute(context);
	}
}
