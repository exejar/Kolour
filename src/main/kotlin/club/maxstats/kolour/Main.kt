package club.maxstats.kolour

import club.maxstats.kolour.render.ShapeRenderer
import net.weavemc.loader.api.ModInitializer
import net.weavemc.loader.api.event.EventBus

/* Main class only handles weave subscriptions essential for the rendering library to work */
/* If you wish to use this library for other modding frameworks, you will have to hook these events yourself */
class Main: ModInitializer {
    override fun preInit() {
        EventBus.subscribe(ShapeRenderer())
    }
}