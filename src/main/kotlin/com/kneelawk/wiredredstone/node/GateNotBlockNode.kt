package com.kneelawk.wiredredstone.node

import com.kneelawk.graphlib.graph.BlockNode
import com.kneelawk.graphlib.graph.BlockNodeDecoder
import com.kneelawk.graphlib.wire.SidedWireConnectionFilter
import com.kneelawk.wiredredstone.part.AbstractGatePart
import com.kneelawk.wiredredstone.part.GateNotPart
import com.kneelawk.wiredredstone.util.*
import net.minecraft.nbt.NbtElement
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import kotlin.math.max

sealed class GateNotBlockNode : AbstractGateBlockNode<GateNotPart>(GateNotPart::class) {
    override val filter: SidedWireConnectionFilter by lazy {
        RedstoneCarrierFilter.and(
            WireCornerBlockageFilter(side, AbstractGatePart.CONNECTION_WIDTH, AbstractGatePart.CONNECTION_HEIGHT)
        )
    }

    override val redstoneType = RedstoneWireType.RedAlloy

    protected abstract val type: Type

    override fun getTypeId(): Identifier = WRBlockNodes.GATE_NOT_ID

    override fun toTag(): NbtElement? = BlockNodeUtil.writeSidedType(side, type)

    data class Input(private val side: Direction) : GateNotBlockNode() {
        override val type = Type.INPUT

        override fun getSide(): Direction = side

        override fun getConnectDirection(part: GateNotPart): Direction = part.getInputSide()

        override fun getState(world: World, self: NetNode): Int = 0

        override fun setState(world: World, self: NetNode, state: Int) {
            getPart(world, self.pos)?.updateInputPower(state)
        }

        override fun getInput(world: World, self: NetNode): Int {
            val part = getPart(world, self.pos) ?: return 0
            return part.calculateInputPower()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Input

            if (side != other.side) return false

            return true
        }

        override fun hashCode(): Int {
            return side.hashCode() xor -1891208086
        }
    }

    data class Output(private val side: Direction) : GateNotBlockNode() {
        override val type = Type.OUTPUT

        override fun getSide(): Direction = side

        override fun getConnectDirection(part: GateNotPart): Direction = part.getOutputSide()

        override fun getState(world: World, self: NetNode): Int {
            return getPart(world, self.pos)?.getTotalOutputPower() ?: 0
        }

        override fun setState(world: World, self: NetNode, state: Int) {
            getPart(world, self.pos)?.updateOutputReversePower(state)
        }

        override fun getInput(world: World, self: NetNode): Int {
            val part = getPart(world, self.pos) ?: return 0
            return max(part.outputPower, part.calculateOutputReversePower())
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Output

            if (side != other.side) return false

            return true
        }

        override fun hashCode(): Int {
            return side.hashCode() xor -361062015
        }
    }

    object Decoder : BlockNodeDecoder {
        override fun createBlockNodeFromTag(tag: NbtElement?): BlockNode? {
            return BlockNodeUtil.readSidedTyped<Type>(tag) { side, type ->
                when (type) {
                    Type.INPUT -> Input(side)
                    Type.OUTPUT -> Output(side)
                }
            }
        }
    }

    protected enum class Type {
        INPUT, OUTPUT
    }
}