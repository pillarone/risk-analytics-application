long startTime

eventTestStart = { name ->

    startTime = System.currentTimeMillis()

}
eventTestEnd = { name ->

    println "Test ${name} finished in ${System.currentTimeMillis() - startTime}ms"

}
eventTestFailure = { name ->

    println "Test ${name} finished in ${System.currentTimeMillis() - startTime}ms (Failed)."

}