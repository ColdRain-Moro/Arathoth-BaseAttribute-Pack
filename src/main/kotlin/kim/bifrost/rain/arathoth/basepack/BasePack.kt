package kim.bifrost.rain.arathoth.basepack

import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info

object BasePack : Plugin() {

    override fun onEnable() {
        info("Successfully running ExamplePlugin!")
    }
}