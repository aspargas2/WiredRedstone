package com.kneelawk.wiredredstone

import com.kneelawk.wiredredstone.item.WRItems
import com.kneelawk.wiredredstone.part.WRParts
import com.kneelawk.wiredredstone.partext.WRPartExts
import com.kneelawk.wiredredstone.util.RedstoneLogic
import com.kneelawk.wiredredstone.wirenet.WRBlockNodeDiscoverer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents

@Suppress("unused")
fun init() {
    WRParts.init()
    WRItems.init()
    WRPartExts.init()
    WRBlockNodeDiscoverer.init()

    // Not sure where better to do this
    ServerTickEvents.END_WORLD_TICK.register { world ->
        RedstoneLogic.flushUpdates(world)
    }
}
