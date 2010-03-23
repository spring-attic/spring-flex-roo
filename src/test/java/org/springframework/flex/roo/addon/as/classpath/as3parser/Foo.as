package
{
	import com.foo.Bar;
	
	[ClassLevelTag1]
	public class Foo
	{
		[FieldLevelTag1]
		public var field1:String;
		
		public var field2:String;
		
		public function Foo(){
			var localField1 = "localField1";
		}
		
		public function method1():void{
		
		}
		
		public function fooFactory():Foo{
		
		}
	}
}