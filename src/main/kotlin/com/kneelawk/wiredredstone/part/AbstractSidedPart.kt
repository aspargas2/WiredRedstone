package com.kneelawk.wiredredstone.part

import alexiil.mc.lib.multipart.api.*
import alexiil.mc.lib.multipart.api.event.NeighbourUpdateEvent
import alexiil.mc.lib.multipart.api.event.PartAddedEvent
import alexiil.mc.lib.multipart.api.event.PartRemovedEvent
import alexiil.mc.lib.net.IMsgReadCtx
import alexiil.mc.lib.net.IMsgWriteCtx
import alexiil.mc.lib.net.NetByteBuf
import com.kneelawk.graphlib.GraphLib
import com.kneelawk.wiredredstone.util.ConnectableUtils
import com.kneelawk.wiredredstone.util.ConnectableUtils.isValidFace
import com.kneelawk.wiredredstone.util.SimpleItemDropTarget
import com.kneelawk.wiredredstone.util.getWorld
import com.kneelawk.wiredredstone.util.requireNonNull
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.loot.context.LootContext
import net.minecraft.loot.context.LootContextParameters
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShape

/**
 * A part that is on the side of a block and can be part of the Redstone-ish network.
 *
 * Subtypes of this could be parts for wires, bundle cables, or gates.
 */
abstract class AbstractSidedPart(definition: PartDefinition, holder: MultipartHolder, override val side: Direction) :
    AbstractPart(definition, holder), BlockNodeContainer, SidedPart {

    private var ctx: SidedPartContext? = null

    private val shapeCache = mutableMapOf<Direction, VoxelShape>()

    constructor(definition: PartDefinition, holder: MultipartHolder, tag: NbtCompound) : this(
        definition, holder, Direction.byId(tag.getByte("side").toInt())
    )

    constructor(definition: PartDefinition, holder: MultipartHolder, buffer: NetByteBuf, ctx: IMsgReadCtx) : this(
        definition, holder, Direction.byId(buffer.readByte().toInt())
    )

    override fun toTag(): NbtCompound {
        val tag = super.toTag()
        tag.putByte("side", side.id.toByte())
        return tag
    }

    override fun writeCreationData(buffer: NetByteBuf, ctx: IMsgWriteCtx) {
        super.writeCreationData(buffer, ctx)
        buffer.writeByte(side.id)
    }

    override fun getSidedContext(): SidedPartContext {
        return ctx.requireNonNull("SidedPartContext is still null (onAdded must not have been called yet)")
    }

    override fun onAdded(bus: MultipartEventBus) {
        ctx = holder.container.getFirstPart(AbstractSidedPart::class.java)?.ctx ?: SidedPartContext(bus)
        ctx!!.setPart(side, this)

        bus.addListener(this, NeighbourUpdateEvent::class.java) {
            val world = getWorld()
            if (world is ServerWorld) {
                if (shouldBreak()) {
                    removeAndDrop()
                } else {
                    if (ConnectableUtils.shouldUpdateConnectionsForNeighborUpdate(
                            shapeCache, world, getPos(), it.pos
                        )
                    ) {
                        // Something could be blocking our connection
                        GraphLib.getController(world).updateConnections(getSidedPos())
                    }
                }
            }
        }

        bus.addListener(this, PartAddedEvent::class.java) {
            val world = getWorld()
            if (it.part !== this && world is ServerWorld) {
                // Something could be blocking our connection
                GraphLib.getController(world).updateConnections(getSidedPos())
            }
        }

        bus.addListener(this, PartRemovedEvent::class.java) {
            val world = getWorld()
            if (it.removed !== this && world is ServerWorld) {
                // A connection could be unblocked
                GraphLib.getController(world).updateConnections(getSidedPos())
            }
        }
    }

    protected open fun shouldBreak(): Boolean {
        val world = getWorld()
        val offset = getPos().offset(side)
        val state = world.getBlockState(offset)
        return !isValidFace(state, world, offset, side.opposite)
    }

    fun removeAndDrop() {
        val world = getWorld() as? ServerWorld ?: return
        val pos = getPos()
        val state = world.getBlockState(pos)
        val origin = Vec3d.of(pos).add(shape.boundingBox.center)

        playBreakSound()
        sendNetworkUpdate(this, NET_SPAWN_BREAK_PARTICLES)

        val context = LootContext.Builder(world).random(world.random)
            .parameter(LootContextParameters.BLOCK_STATE, state)
            .parameter(LootContextParameters.ORIGIN, origin)
            .parameter(LootContextParameters.TOOL, ItemStack.EMPTY)
            .parameter(PartLootParams.BROKEN_PART, PartLootParams.BrokenSinglePart(this))
            // No good way to tell if other parts are affected by this too
            .parameter(PartLootParams.ADDITIONAL_PARTS, emptyArray())
            .optionalParameter(LootContextParameters.BLOCK_ENTITY, holder.container.multipartBlockEntity)
            .build(PartLootParams.PART_TYPE)

        addDrops(SimpleItemDropTarget(world, origin), context)

        holder.remove()
    }

    override fun onPlacedBy(player: PlayerEntity?, hand: Hand?) {
        val world = getWorld()
        if (!world.isClient && world is ServerWorld) {
            GraphLib.getController(world).onChanged(getPos())
        }
    }

    override fun onRemoved() {
        ctx!!.setPart(side, null)

        val world = getWorld()
        if (!world.isClient && world is ServerWorld) {
            GraphLib.getController(world).onChanged(getPos())
        }
    }

    override fun getPos(): BlockPos {
        return holder.container.multipartPos
    }
}