package com.ktor.finance.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun logger(id: String = "finance-server"): Logger = LoggerFactory.getLogger(id)
