package com.redbox

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.PropertySource

@SpringBootApplication
@PropertySource("classpath:.env")
class RedboxApplication

fun main(args: Array<String>) {
    runApplication<RedboxApplication>(*args)
}
