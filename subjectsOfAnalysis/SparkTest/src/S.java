public class S {
	I a, b;
	
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
	
	public I methodForAnalyzeMethodParamToReturnTest(int num, T t, C c, D d) {
		a = c;
		
		I local = a
		
		return local;
	}
	
	public D methodForAnalyzeMethodParamToReturnTest2(int num, T t, C c, D d) {
		D local = d;
		
		D local2 = local;
		
		return local2;
	}
	
}
