package kim.bifrost.rain.arathoth.basepack.attr

import kim.bifrost.rain.arathoth.api.createAttribute
import kim.bifrost.rain.arathoth.api.handler.event
import kim.bifrost.rain.arathoth.basepack.setItemCoolDown
import kim.bifrost.rain.arathoth.internal.EntityStatusManager.status
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import taboolib.platform.util.isAir
import java.util.*

/**
 * kim.bifrost.rain.arathoth.basepack.attr.Others
 * Arathoth-BaseAttributePack
 *
 * @author 寒雨
 * @since 2022/4/2 1:01
 **/
object Others {
    val attackSpeed = createAttribute("arathoth", "attackSpeed") {
        val cdMap = WeakHashMap<UUID, Long>()
        event(EntityDamageByEntityEvent::class.java) {
            val cd = cdMap[damager.uniqueId] ?: 0L
            if (System.currentTimeMillis() < cd) {
                return@event
            }
            val attacker = damager
            if (attacker is Player) {
                val base = config.getInt("settings.base", 30)
                val rate = config.getDouble("settings.rate", 0.5)
                val item = attacker.inventory.itemInMainHand
                if (item.isAir()) {
                    return@event
                }
                val value = attacker.status(this@createAttribute)?.generateValue() ?: 0.0
                val duration = base - (value * rate).toInt()
                attacker.setItemCoolDown(item, duration)
                cdMap[attacker.uniqueId] = System.currentTimeMillis() + duration * 50L
            }
        }
        config {
            set("patterns", listOf("[VALUE] attack speed"))
            // 默认不开启
            set("enable", false)
            // 攻速间隔
            // 20即1s一刀
            set("settings.base", 20)
            // 属性值与实际减少时间（刻）的换算关系
            // 0.5即代表 40为满攻速
            set("settings.rate", 0.5)
        }
    }.register()

    val health = createAttribute("arathoth", "health") {
        config {
            set("patterns", listOf("[VALUE] health"))
            set("settings.min", 1.0)
            set("settings.max", 2000.0)
            set("settings.base", 20.0)
        }
    }
}