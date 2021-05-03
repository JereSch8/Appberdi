package com.jackemate.appberdi.domain.shared

import java.util.logging.Logger

// https://stackoverflow.com/a/34462577
fun <R : Any> R.logger(): Lazy<Logger> {
    return lazy { Logger.getLogger(this.javaClass.simpleName) }
}
