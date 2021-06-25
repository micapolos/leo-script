package leo

val Evaluator.helpScriptLine: ScriptLine get() =
	helpName lineTo script(
		"keyword" lineTo script(
			listName lineTo script(
				"keyword" lineTo script(anyName),
				"keyword" lineTo script(asName),
				"keyword" lineTo script(beName),
				"keyword" lineTo script(bindName),
				"keyword" lineTo script(checkName),
				"keyword" lineTo script(commentName),
				"keyword" lineTo script(contentName),
				"keyword" lineTo script(doName),
				"keyword" lineTo script(doingName),
				"keyword" lineTo script(evaluateName),
				"keyword" lineTo script(exampleName),
				"keyword" lineTo script(failName),
				"keyword" lineTo script(giveName),
				"keyword" lineTo script(hashName),
				"keyword" lineTo script(isName lineTo script(equalName lineTo script(toName))),
				"keyword" lineTo script(isName lineTo script(matchingName)),
				"keyword" lineTo script(isName lineTo script(notName)),
				"keyword" lineTo script(letName lineTo script(beName)),
				"keyword" lineTo script(letName lineTo script(doName)),
				"keyword" lineTo script(listName),
				"keyword" lineTo script(loadName),
				"keyword" lineTo script(printName),
				"keyword" lineTo script(privateName),
				"keyword" lineTo script(quoteName),
				"keyword" lineTo script(recurseName),
				"keyword" lineTo script(recursiveName),
				"keyword" lineTo script(repeatName),
				"keyword" lineTo script(setName),
				"keyword" lineTo script(switchName),
				"keyword" lineTo script(takeName),
				"keyword" lineTo script(testName),
				"keyword" lineTo script(tryName),
				"keyword" lineTo script(updateName),
				"keyword" lineTo script(useName),
				"keyword" lineTo script(withName))),
		"binding" lineTo script(
			listName lineTo context.privateDictionary.bindingListScript),
		value.scriptLine)

val Dictionary.bindingListScript: Script get() =
	script().fold(definitionStack.reverse) { plus(it.bindingListScript) }

val Definition.bindingListScript: Script get() =
	when (this) {
		is LetDefinition -> script("binding" lineTo let.value.script)
		is RecursiveDefinition -> recursive.dictionary.bindingListScript
	}
