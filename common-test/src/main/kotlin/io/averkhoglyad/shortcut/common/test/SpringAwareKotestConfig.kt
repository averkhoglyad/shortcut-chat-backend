package io.averkhoglyad.shortcut.common.test

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.extensions.spring.SpringAutowireConstructorExtension
import io.kotest.extensions.spring.SpringExtension

class SpringAwareKotestConfig : AbstractProjectConfig() {

    override fun extensions() = listOf(SpringExtension, SpringAutowireConstructorExtension)

}
