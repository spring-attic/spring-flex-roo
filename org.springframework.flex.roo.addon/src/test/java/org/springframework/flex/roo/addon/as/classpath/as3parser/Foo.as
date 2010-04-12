package com.foo.stuff
{
	import com.foo.Bar;
	
	[ClassLevelTag1]
	public class Foo
	{
		[FieldLevelTag1]
		public var field1:String;
		
		private var field2:Bar;
		
		public function Foo(){
			var localField1 = "localField1";
			field2 = new Bar(localField);
		}
		
		[MethodLevelTag1]
		private function method1():void{
		
		}
		
		public function fooFactory():Foo{
		
		}
		
		public function calculateStuff(bar:String, baz:Bar):String {
		
		}
	}
}