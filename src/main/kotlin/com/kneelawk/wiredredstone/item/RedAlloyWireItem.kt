package com.kneelawk.wiredredstone.item

import alexiil.mc.lib.multipart.api.MultipartHolder
import com.kneelawk.wiredredstone.part.RedAlloyWirePart
import com.kneelawk.wiredredstone.part.WRParts
import com.kneelawk.wiredredstone.util.BlockageUtils
import com.kneelawk.wiredredstone.util.PlacementUtils
import net.minecraft.block.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemUsageContext
import net.minecraft.util.ActionResult
import net.minecraft.util.math.Direction

class RedAlloyWireItem(settings: Settings) : Item(settings) {
    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val world = context.world
        if (world.isClient) {
            return ActionResult.PASS
        }

        val offer = PlacementUtils.tryPlaceWire(context, ::creator) ?: return ActionResult.FAIL

        PlacementUtils.finishPlacement(context, offer, Blocks.REDSTONE_BLOCK.defaultState)

        return ActionResult.SUCCESS
    }

    private fun creator(side: Direction): ((MultipartHolder) -> RedAlloyWirePart) {
        return { holder ->
            RedAlloyWirePart(WRParts.RED_ALLOY_WIRE, holder, side, 0u, 0, BlockageUtils.UNBLOCKED)
        }
    }
}