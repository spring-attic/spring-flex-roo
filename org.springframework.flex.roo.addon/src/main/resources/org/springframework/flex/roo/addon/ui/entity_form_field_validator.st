$if(it.date)$
<mx:DateValidator source="{$it.metadata.fieldName.symbolName$Input}" property="selectedDate" $it.validations.keys,it.validations.values:{k,v| $k$="$v$"}$ />
$else$
<mx:StringValidator source="{$it.metadata.fieldName.symbolName$Input}" property="text" $it.validations.keys,it.validations.values:{k,v| $k$="$v$"}$ />
$endif$