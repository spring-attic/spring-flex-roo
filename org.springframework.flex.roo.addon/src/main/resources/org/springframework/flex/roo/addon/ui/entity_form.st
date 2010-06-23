<?xml version="1.0" encoding="utf-8"?>
<s:TitleWindow xmlns:fx="http://ns.adobe.com/mxml/2009" 
		 xmlns:s="library://ns.adobe.com/flex/spark" 
		 xmlns:mx="library://ns.adobe.com/flex/mx" close="handleCloseEvent();" title="$entityType.simpleTypeName$ Form" width="400" height="300">
	<fx:Script>
		<![CDATA[
			import mx.controls.Alert;
			import mx.events.ValidationResultEvent;
			import mx.managers.PopUpManager;
			
			import $entityType.fullyQualifiedTypeName$;
			
			[Bindable]
			public var $flexScaffoldMetadata.entityReference$:$entityType.simpleTypeName$;
			
			private function handleCloseEvent():void {
				PopUpManager.removePopUp(this);
			}            
			
			private function processSave():void {
				if (!validateAndCommit()){
					return;
				}
				this.dispatchEvent(new $entityType.simpleTypeName$Event(((!$flexScaffoldMetadata.entityReference$.id || $flexScaffoldMetadata.entityReference$.id <= 0) ? $entityType.simpleTypeName$Event.CREATE : $entityType.simpleTypeName$Event.UPDATE), $flexScaffoldMetadata.entityReference$));
				PopUpManager.removePopUp(this);
			}
			
			private function validateAndCommit():Boolean{
				var vResult:ValidationResultEvent;
				$fields:org/springframework/flex/roo/addon/ui/entity_form_field_validator_invoker()$
				$fields:org/springframework/flex/roo/addon/ui/entity_form_field_value_commit()$
				return true;
			}
			
		]]>
	</fx:Script>
	
	<fx:Declarations>
		$fields:org/springframework/flex/roo/addon/ui/entity_form_field_validator()$
	</fx:Declarations>
	
	<s:layout>
		<s:VerticalLayout/>
	</s:layout>
	
	<mx:Form>
		$fields:org/springframework/flex/roo/addon/ui/entity_form_field()$
	</mx:Form>
	
	<s:Group>
		<s:layout>
			<s:HorizontalLayout/>
		</s:layout>
		<s:Button label="Save" click="processSave();"/>
		<s:Button label="Cancel" click="handleCloseEvent();"/>
	</s:Group>
	
	
</s:TitleWindow>
