package leo

val Evaluator.helpScriptLine: ScriptLine
  get() =
    helpName lineTo script(
      "leo" lineTo script(
        "version" lineTo script(literal(version)),
        "author" lineTo script(literal("Michał Pociecha-Łoś"))
      ),
      "command" lineTo script(
        listName lineTo script(
          "command" lineTo script(anythingName),
          "command" lineTo script(applyName),
          "command" lineTo script(applyingName),
          "command" lineTo script(asName),
          "command" lineTo script(beName),
          "command" lineTo script(beingName),
          "command" lineTo script(checkName),
          "command" lineTo script(combineName),
          "command" lineTo script(combiningName),
          "command" lineTo script(commentName),
          "command" lineTo script(contentName),
          "command" lineTo script(debugName),
          "command" lineTo script(doName),
          "command" lineTo script(doingName),
          "command" lineTo script(evaluateName),
          "command" lineTo script(exampleName),
          "command" lineTo script(failName),
          "command" lineTo script(giveName),
          "command" lineTo script(givenName),
          "command" lineTo script(hashName),
          "command" lineTo script(haveName),
          "command" lineTo script(helpName),
          "command" lineTo script(isName lineTo script(equalName lineTo script(toName))),
          "command" lineTo script(isName lineTo script(matchingName)),
          "command" lineTo script(isName lineTo script(notName)),
          "command" lineTo script(letName lineTo script(applyName)),
          "command" lineTo script(letName lineTo script(beName)),
          "command" lineTo script(letName lineTo script(doName)),
          "command" lineTo script(letName lineTo script(haveName)),
          "command" lineTo script(listName),
          "command" lineTo script(loadName),
          "command" lineTo script(orName),
          "command" lineTo script(printName),
          "command" lineTo script(privateName),
          "command" lineTo script(quoteName),
          "command" lineTo script(recurseName),
          "command" lineTo script(recursiveName),
          "command" lineTo script(repeatName),
          "command" lineTo script(selectName),
          "command" lineTo script(setName),
          "command" lineTo script(takeName),
          "command" lineTo script(testName),
          "command" lineTo script(theName),
          "command" lineTo script(tryName),
          "command" lineTo script(updateName),
          "command" lineTo script(useName),
          "command" lineTo script(withName)
        )
      ),
      "function" lineTo script(
        listName lineTo context.privateDictionary.functionListScript
      ),
      value.scriptLine
    )

val Dictionary.functionListScript: Script
  get() =
    script().fold(definitionStack.reverse) { plus(it.bindingListScript) }

val Definition.bindingListScript: Script
  get() =
    when (this) {
      is GivenDefinition -> script("given" lineTo given.value.script)
      is LetDefinition -> script("function" lineTo let.value.script)
      is RecursiveDefinition -> recursive.dictionary.functionListScript
    }
