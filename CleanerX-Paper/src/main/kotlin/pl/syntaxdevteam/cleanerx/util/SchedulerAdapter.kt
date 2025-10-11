package pl.syntaxdevteam.cleanerx.util

import org.bukkit.Bukkit
import pl.syntaxdevteam.cleanerx.CleanerX

/**
 * Provides a small abstraction layer over the Bukkit/Folia schedulers so the plugin can
 * transparently run tasks on both platforms without duplicating scheduling code.
 */
object SchedulerAdapter {
    private val foliaDetected: Boolean = try {
        Class.forName("io.papermc.paper.threadedregions.RegionizedServer")
        true
    } catch (_: ClassNotFoundException) {
        false
    }

    /**
     * Executes the given [task] asynchronously. On Folia the async scheduler is used, while on
     * Paper we fall back to the standard asynchronous Bukkit scheduler.
     */
    fun runAsync(plugin: CleanerX, task: () -> Unit) {
        if (foliaDetected) {
            Bukkit.getAsyncScheduler().runNow(plugin) {
                task()
            }
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable { task() })
        }
    }

    /**
     * Executes the given [task] on the main server thread (global region on Folia).
     */
    fun runSync(plugin: CleanerX, task: () -> Unit) {
        if (foliaDetected) {
            Bukkit.getGlobalRegionScheduler().run(plugin) {
                task()
            }
        } else {
            Bukkit.getScheduler().runTask(plugin, Runnable { task() })
        }
    }
}
