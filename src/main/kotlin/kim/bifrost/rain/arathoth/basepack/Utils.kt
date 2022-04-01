package kim.bifrost.rain.arathoth.basepack

import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.inventory.ItemStack
import taboolib.common.util.random
import taboolib.module.nms.getI18nName
import kotlin.math.min

/**
 * kim.bifrost.rain.arathoth.basepack.Utils
 * Arathoth-BaseAttributePack
 *
 * @author 寒雨
 * @since 2022/3/25 0:32
 **/
fun LivingEntity.heal(value: Double) {
    health = min(maxHealth, value + health)
}

fun Entity.damager(): LivingEntity {
    if (this is Projectile) {
        return shooter as LivingEntity
    }
    return this as LivingEntity
}

fun judgeChance(value: Double): Boolean {
    return random(0.0, 100.0) <= value
}

fun Player.setItemCoolDown(item: ItemStack, value: Int) {
    if (hasCooldown(item.type)) {
        return
    }
    setCooldown(item.type, value)
}

val LivingEntity.customOrI18nName: String
    get() = customName ?: getI18nName()
