public class M {
	public static void main(String[] args) {
		T t = new T();
		S s = new S();

		s.callInout(t);
		
		C c = new C();
		D d = new D();
		
		s.methodForAnalyzeMethodParamToReturnTest(3, t, c, d);
		s.methodForAnalyzeMethodParamToReturnTest2(10, t, c, d);
	}
	
	
}
