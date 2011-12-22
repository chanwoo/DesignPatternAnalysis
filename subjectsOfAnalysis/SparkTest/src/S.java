public class S {
	
	public I callInout(T t) {
		I arg1 = new A();
		I arg2 = new B();
		
		I result = null;
		
		result = t.inout(arg1, methodD());
		
		result = t.publicFieldOfT;
		
		result = T.publicStaticFieldOfT;
		
		result = T.staticMethodA();
		
		t.publicFieldOfT = result;
		
		t.publicFieldOfT.foo();
		
		return methodD();
	}
	
	public I methodD() {
		return new D();
	}
	
}
