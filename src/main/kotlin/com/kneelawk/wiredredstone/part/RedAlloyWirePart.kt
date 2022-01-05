package com.kneelawk.wiredredstone.part

import alexiil.mc.lib.multipart.api.MultipartEventBus
import alexiil.mc.lib.multipart.api.MultipartHolder
import alexiil.mc.lib.multipart.api.PartDefinition
import alexiil.mc.lib.multipart.api.event.PartRedstonePowerEvent
import alexiil.mc.lib.multipart.api.render.PartModelKey
import alexiil.mc.lib.net.IMsgReadCtx
import alexiil.mc.lib.net.NetByteBuf
import com.kneelawk.wiredredstone.item.WRItems
import com.kneelawk.wiredredstone.part.key.RedAlloyWirePartKey
import com.kneelawk.wiredredstone.partext.RedAlloyWirePartExt
import com.kneelawk.wiredredstone.util.*
import net.minecraft.block.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.loot.context.LootContext
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape

class RedAlloyWirePart : AbstractRedstoneWirePart {
    companion object {
        private val CONFLICT_SHAPES = BoundingBoxUtils.getWireConflictShapes(2.0, 2.0)
        private val OUTLINE_SHAPES = BoundingBoxUtils.getWireOutlineShapes(10.0, 2.0)
    }

    constructor(
        definition: PartDefinition, holder: MultipartHolder, side: Direction, connections: UByte, powered: Boolean
    ) : super(definition, holder, side, connections, powered)

    constructor(definition: PartDefinition, holder: MultipartHolder, tag: NbtCompound) : super(definition, holder, tag)

    constructor(definition: PartDefinition, holder: MultipartHolder, buffer: NetByteBuf, ctx: IMsgReadCtx) : super(
        definition, holder, buffer, ctx
    )

    override val partExtType = RedAlloyWirePartExt.Type

    override fun onAdded(bus: MultipartEventBus) {
        super.onAdded(bus)

        bus.addListener(this, PartRedstonePowerEvent.PartStrongRedstonePowerEvent::class.java) { e ->
            // Fix comparator side input
            if (getWorld().getBlockState(getPos().offset(e.side)).block == Blocks.COMPARATOR) {
                e.set(getWeakRedstonePower(e.side))
            } else {
                e.set(getStrongRedstonePower(e.side))
            }
        }

        bus.addListener(this, PartRedstonePowerEvent.PartWeakRedstonePowerEvent::class.java) { e ->
            e.set(getWeakRedstonePower(e.side))
        }
    }

    private fun getStrongRedstonePower(powerSide: Direction): Int {
        return if (RedstoneLogic.wiresGivePower && powered && powerSide == side) 15 else 0
    }

    private fun getWeakRedstonePower(powerSide: Direction): Int {
        return if (RedstoneLogic.wiresGivePower && powered && powerSide != side) 15 else 0
    }

    override fun isReceivingPower(): Boolean {
        return RedstoneLogic.isReceivingPower(getWorld(), getSidedPos(), connections, true)
    }

    override fun getShape(): VoxelShape {
        return CONFLICT_SHAPES[side]!!
    }

    override fun getModelKey(): PartModelKey {
        return RedAlloyWirePartKey(side, connections, powered)
    }

    override fun getOutlineShape(): VoxelShape {
        return OUTLINE_SHAPES[BoundingBoxUtils.ShapeKey(side, connections)]
    }

    override fun getPickStack(): ItemStack {
        return ItemStack(WRItems.RED_ALLOY_WIRE)
    }

    override fun addDrops(target: ItemDropTarget, context: LootContext) {
        LootTableUtil.addPartDrops(getWorld(), target, context, WRParts.RED_ALLOY_WIRE.identifier)
    }
}