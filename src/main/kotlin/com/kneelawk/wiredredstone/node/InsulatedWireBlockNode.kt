package com.kneelawk.wiredredstone.node

import com.kneelawk.graphlib.graph.BlockNode
import com.kneelawk.graphlib.graph.BlockNodeDecoder
import com.kneelawk.graphlib.graph.BlockNodeWrapper
import com.kneelawk.graphlib.graph.NodeView
import com.kneelawk.graphlib.graph.struct.Node
import com.kneelawk.graphlib.util.SidedPos
import com.kneelawk.graphlib.wire.SidedWireBlockNode
import com.kneelawk.graphlib.wire.WireConnectionDiscoverers
import com.kneelawk.graphlib.wire.WireConnectionType
import com.kneelawk.wiredredstone.part.InsulatedWirePart
import com.kneelawk.wiredredstone.part.SidedPart
import com.kneelawk.wiredredstone.util.*
import com.kneelawk.wiredredstone.util.NetNode
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView
import net.minecraft.world.World

data class InsulatedWireBlockNode(private val side: Direction, val color: DyeColor) : SidedWireBlockNode,
    RedstoneCarrierBlockNode {

    override val redstoneType = RedstoneWireType.Colored(color)

    override fun getSide(): Direction = side
    override fun getTypeId(): Identifier = WRBlockNodes.INSULATED_WIRE_ID

    private fun getPart(world: BlockView, pos: BlockPos): InsulatedWirePart? {
        return SidedPart.getPart(world, SidedPos(pos, side)) as? InsulatedWirePart
    }

    override fun findConnections(world: ServerWorld, nv: NodeView, pos: BlockPos): Collection<NetNode> {
        return WireConnectionDiscoverers.wireFindConnections(this, world, nv, pos, RedstoneCarrierFilter)
    }

    override fun canConnect(
        world: ServerWorld, nodeView: NodeView, pos: BlockPos, other: Node<BlockNodeWrapper<*>>
    ): Boolean {
        return WireConnectionDiscoverers.wireCanConnect(this, world, pos, RedstoneCarrierFilter, other)
    }

    override fun getState(world: World, self: NetNode): Int {
        val part = getPart(world, self.pos) ?: return 0
        return part.power
    }

    override fun setState(world: World, self: NetNode, state: Int) {
        val part = getPart(world, self.pos) ?: return
        part.updatePower(state)
        part.redraw()
    }

    override fun getInput(world: World, self: NetNode): Int {
        val part = getPart(world, self.pos) ?: return 0
        val pos = SidedPos(self.pos, side)
        return RedstoneLogic.getReceivingPower(world, pos, part.connections, false, part.blockage)
    }

    override fun onChanged(world: ServerWorld, pos: BlockPos) {
        RedstoneLogic.scheduleUpdate(world, pos)
        getPart(world, pos)?.updateConnections()
    }

    override fun canConnect(
        world: ServerWorld, pos: BlockPos, inDirection: Direction, connectionType: WireConnectionType,
        other: Node<BlockNodeWrapper<*>>
    ): Boolean {
        return ConnectableUtils.canWireConnect(
            world, pos, inDirection, connectionType, side, InsulatedWirePart.WIRE_WIDTH, InsulatedWirePart.WIRE_HEIGHT
        )
    }

    override fun toTag(): NbtElement {
        val tag = NbtCompound()
        tag.putByte("side", side.id.toByte())
        tag.putByte("color", color.id.toByte())
        return tag
    }

    object Decoder : BlockNodeDecoder {
        override fun createBlockNodeFromTag(tag: NbtElement?): BlockNode? {
            if (tag !is NbtCompound) return null

            val side = Direction.byId(tag.getByte("side").toInt())
            val color = DyeColor.byId(tag.getByte("color").toInt())
            return InsulatedWireBlockNode(side, color)
        }
    }
}