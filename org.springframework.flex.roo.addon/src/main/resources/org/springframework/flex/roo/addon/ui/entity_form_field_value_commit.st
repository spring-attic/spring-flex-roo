$if(it.number)$
$flexScaffoldMetadata.entityReference$.$it.metadata.fieldName.symbolName$ = parseInt($it.metadata.fieldName.symbolName$Input.text);
$elseif(it.date)$
$flexScaffoldMetadata.entityReference$.$it.metadata.fieldName.symbolName$ = $it.metadata.fieldName.symbolName$Input.selectedDate;
$elseif(it.boolean)$
$flexScaffoldMetadata.entityReference$.$it.metadata.fieldName.symbolName$ = $it.metadata.fieldName.symbolName$Input.selected;
$else$
$flexScaffoldMetadata.entityReference$.$it.metadata.fieldName.symbolName$ = $it.metadata.fieldName.symbolName$Input.text;
$endif$
$\n$