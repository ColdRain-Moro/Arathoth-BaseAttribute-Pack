package kim.bifrost.rain.arathoth.basepack.attr

import kim.bifrost.rain.arathoth.api.createAttribute
import kim.bifrost.rain.arathoth.api.handler.event
import kim.bifrost.rain.arathoth.basepack.customOrI18nName
import kim.bifrost.rain.arathoth.basepack.damager
import kim.bifrost.rain.arathoth.basepack.judgeChance
import kim.bifrost.rain.arathoth.internal.EntityStatusManager.status
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Monster
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import taboolib.module.kether.KetherShell
import taboolib.platform.type.BukkitPlayer

/**
 * kim.bifrost.rain.arathoth.basepack.attr.Defense
 * Arathoth-BaseAttributePack
 *
 * @author 寒雨
 * @since 2022/4/1 19:42
 **/
object Defense {
    val physicalArmor = createAttribute("arathoth", "physicalArmor") {
        event(EntityDamageEvent::class.java) {
            if (cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                val data = entity.status(this@createAttribute) ?: return@event
                val value = data.generateValue()
                damage -= value
            }
        }
        config {
            set("patterns", listOf("[VALUE] physical armor"))
        }
    }.register()

    val monsterArmor = createAttribute("arathoth", "monsterArmor") {
        event(EntityDamageByEntityEvent::class.java) {
            if (damager.damager() is Monster && cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                val data = entity.status(this@createAttribute) ?: return@event
                val value = data.generateValue()
                damage -= value
            }
        }
        config {
            set("patterns", listOf("[VALUE] monster armor"))
        }
    }.register()

    val playerArmor = createAttribute("arathoth", "playerArmor") {
        event(EntityDamageByEntityEvent::class.java) {
            if (damager.damager() is Player) {
                val data = entity.status(this@createAttribute) ?: return@event
                val value = data.generateValue()
                damage -= value
            }
        }
        config {
            set("patterns", listOf("[VALUE] player armor"))
        }
    }.register()

    val projectileArmor = createAttribute("arathoth", "projectileArmor") {
        event(EntityDamageEvent::class.java) {
            if (cause == EntityDamageEvent.DamageCause.PROJECTILE) {
                val data = entity.status(this@createAttribute) ?: return@event
                val value = data.generateValue()
                damage -= value
            }
        }
        config {
            set("patterns", listOf("[VALUE] projectile armor"))
        }
    }.register()

    val magicArmor = createAttribute("arathoth", "magicArmor") {
        event(EntityDamageEvent::class.java) {
            if (cause == EntityDamageEvent.DamageCause.MAGIC) {
                val data = entity.status(this@createAttribute) ?: return@event
                val value = data.generateValue()
                damage -= value
            }
        }
        config {
            set("patterns", listOf("[VALUE] projectile armor"))
        }
    }.register()

    val resistance = createAttribute("arathoth", "resistance") {
        event(EntityDamageEvent::class.java, priority = EventPriority.MONITOR) {
            val data = entity.status(this@createAttribute) ?: return@event
            val value = data.generateValue()
            damage *= 1 - (value / 100)
        }
        config {
            set("patterns", listOf("[VALUE]% resistance"))
        }
    }.register()

    val blockChance = createAttribute("arathoth", "blockChance") {
        event(EntityDamageByEntityEvent::class.java) {
            val data = entity.status(this@createAttribute) ?: return@event
            val value = data.generateValue()
            if (judgeChance(value / 100)) {
                isCancelled = true
                val attacker = damager.damager()
                val bDamage = entity.status(blockDamage)?.generateValue() ?: 0.0
                if (bDamage > 0) {
                    attacker.damage(bDamage)
                }
                if (attacker is Player) {
                    KetherShell.eval(config.getString("block-action.damager", "")!!) {
                        sender = BukkitPlayer(attacker)
                        set("target", (entity as LivingEntity).customOrI18nName)
                        set("damager", attacker.customOrI18nName)
                    }
                }
                if (entity is Player) {
                    KetherShell.eval(config.getString("block-action.target", "")!!) {
                        sender = BukkitPlayer(entity as Player)
                        set("target", (entity as LivingEntity).customOrI18nName)
                        set("damager", attacker.customOrI18nName)
                    }
                }
            }
        }
        config {
            set("patterns", listOf("[VALUE]% block chance"))
            set("dodge-action.damager", "send color *\"&8* &7遭 &f{{ &target }} &7格挡!\"")
            set("dodge-action.target", "send color inline *\"&8* &7格挡了来自 &f{{ &damager }} &7的攻击!\"")
        }
    }.register()

    val blockDamage = createAttribute("arathoth", "blockDamage") {
        config {
            set("patterns", listOf("[VALUE] block damage"))
        }
    }.register()

    val dodge = createAttribute("arathoth", "dodge") {
        event(EntityDamageByEntityEvent::class.java) {
            val data = entity.status(this@createAttribute) ?: return@event
            val hit = damager.status(hitRate)?.generateValue() ?: 0.0
            val value = data.generateValue() - hit
            if (judgeChance(value / 100)) {
                isCancelled = true
                val attacker = damager.damager()
                if (attacker is Player) {
                    KetherShell.eval(config.getString("dodge-action.damager", "")!!) {
                        sender = BukkitPlayer(attacker)
                        set("target", (entity as LivingEntity).customOrI18nName)
                        set("damager", attacker.customOrI18nName)
                    }
                }
                if (entity is Player) {
                    KetherShell.eval(config.getString("dodge-action.target", "")!!) {
                        sender = BukkitPlayer(entity as Player)
                        set("target", (entity as LivingEntity).customOrI18nName)
                        set("damager", attacker.customOrI18nName)
                    }
                }
            }
        }
        config {
            set("patterns", listOf("[VALUE]% dodge"))
            set("dodge-action.damager", "send color *\"&8* &7遭 &f{{ &target }} &7闪避!\"")
            set("dodge-action.target", "send color inline *\"&8* &7闪避了来自 &f{{ &damager }} &7的攻击!\"")
        }
    }.register()

    val hitRate = createAttribute("arathoth", "hitRate") {
        config {
            set("patterns", listOf("[VALUE]% hit rate"))
        }
    }.register()
}