package kim.bifrost.rain.arathoth.basepack.attr

import kim.bifrost.rain.arathoth.api.createAttribute
import kim.bifrost.rain.arathoth.api.handler.event
import kim.bifrost.rain.arathoth.basepack.customOrI18nName
import kim.bifrost.rain.arathoth.basepack.damager
import kim.bifrost.rain.arathoth.basepack.heal
import kim.bifrost.rain.arathoth.basepack.judgeChance
import kim.bifrost.rain.arathoth.internal.EntityStatusManager.status
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Monster
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import taboolib.module.kether.KetherShell
import taboolib.platform.type.BukkitPlayer

/**
 * kim.bifrost.rain.arathoth.basepack.attr.Attack
 * Arathoth-BaseAttributePack
 *
 * @author 寒雨
 * @since 2022/3/24 23:05
 **/
object Attack {

    val physicalDamage = createAttribute("arathoth", "physicalDamage") {
        event(EntityDamageByEntityEvent::class.java) {
            val data = damager.status(this@createAttribute) ?: return@event
            damage += data.generateValue()
        }
        config {
            set("patterns", listOf("[VALUE] physical damage"))
        }
    }.register()

    val playerDamage = createAttribute("arathoth", "playerDamage") {
        event(EntityDamageByEntityEvent::class.java) {
            if (entity is Player) {
                val data = damager.status(this@createAttribute) ?: return@event
                damage += data.generateValue()
            }
        }
        config {
            set("patterns", listOf("[VALUE] player damage"))
        }
    }.register()

    val monsterDamage = createAttribute("arathoth", "monsterDamage") {
        event(EntityDamageByEntityEvent::class.java) {
            if (entity is Monster) {
                val data = damager.status(this@createAttribute) ?: return@event
                damage += data.generateValue()
            }
        }
        config {
            set("patterns", listOf("[VALUE] monster damage"))
        }
    }.register()

    val lifeSteal = createAttribute("arathoth", "lifeSteal") {
        event(EntityDamageByEntityEvent::class.java) {
            val data = damager.status(this@createAttribute) ?: return@event
            val value = data.generateValue()
            damage += value
            damager.damager().heal(value)
        }
        config {
            set("patterns", listOf("[VALUE] life steal"))
        }
    }.register()

    val criticalChance = createAttribute("arathoth", "criticalChance") {
        config {
            set("patterns", listOf("[VALUE]% critical chance"))
        }
    }.register()

    val criticalDamage = createAttribute("arathoth", "criticalDamage") {
        event(EntityDamageByEntityEvent::class.java) {
            val chance = damager.status(criticalChance)?.generateValue() ?: return@event
            if (judgeChance(chance)) {
                val damage = damager.status(this@createAttribute)?.generateValue() ?: return@event
                this.damage += damage
                val attacker = damager.damager()
                if (attacker is Player) {
                    KetherShell.eval(config.getString("critical-action.damager", "")!!) {
                        sender = BukkitPlayer(attacker)
                        set("target", (entity as LivingEntity).customOrI18nName)
                        set("damager", attacker.customOrI18nName)
                    }
                }
                if (entity is Player) {
                    KetherShell.eval(config.getString("critical-action.target", "")!!) {
                        sender = BukkitPlayer(entity as Player)
                        set("target", (entity as LivingEntity).customOrI18nName)
                        set("damager", attacker.customOrI18nName)
                    }
                }
            }
        }
        config {
            set("patterns", listOf("[VALUE] critical damage"))
            set("critical-action.damager", "send color *\"&8* &7对 &f{{ &target }} &7暴击!\"")
            set("critical-action.target", "send color inline *\"&8* &7遭到 &f{{ &damager }} &7暴击!\"")
        }
    }.register()

    val oblivion = createAttribute("arathoth", "oblivion") {
        event(EntityDamageByEntityEvent::class.java) {
            val data = damager.status(this@createAttribute) ?: return@event
            val value = data.generateValue()
            damager.damager().damage(value)
        }
        config {
            set("patterns", listOf("[VALUE] oblivion"))
        }
    }.register()

    val attackRange = createAttribute("arathoth", "attackRange") {
        event(EntityDamageByEntityEvent::class.java) {
            val data = damager.status(this@createAttribute) ?: return@event
            val value = data.generateValue()
            entity.getNearbyEntities(value, value, value).forEach {
                if (it is LivingEntity) {
                    it.damage(0.0, damager)
                }
            }
        }
        config {
            set("patterns", listOf("[VALUE] attack range"))
        }
    }.register()

    val percentDamage = createAttribute("arathoth", "percentDamage") {
        event(EntityDamageByEntityEvent::class.java) {
            val data = damager.status(this@createAttribute) ?: return@event
            val value = data.generateValue()
            val victim = entity as LivingEntity
            damage += victim.maxHealth * (value / 100)
        }
        config {
            set("patterns", listOf("[VALUE]% percent damage"))
        }
    }.register()

    val currentPercentDamage = createAttribute("arathoth", "currentPercentDamage") {
        event(EntityDamageByEntityEvent::class.java) {
            val data = damager.status(this@createAttribute) ?: return@event
            val value = data.generateValue()
            val victim = entity as LivingEntity
            damage += victim.health * (value / 100)
        }
        config {
            set("patterns", listOf("[VALUE]% current percent damage"))
        }
    }.register()

}

