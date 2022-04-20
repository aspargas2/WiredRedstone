package com.kneelawk.wiredredstone.client.render.part

import alexiil.mc.lib.multipart.api.render.PartRenderContext
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.kneelawk.wiredredstone.WRConstants
import com.kneelawk.wiredredstone.client.render.*
import com.kneelawk.wiredredstone.part.key.GateDiodePartKey
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh
import net.minecraft.util.Identifier
import java.util.function.Consumer

object GateDiodePartBaker : WRPartBaker<GateDiodePartKey> {
    private val GATE_DIODE_BACKGROUND = WRConstants.id("block/gate_diode/background")
    private val GATE_DIODE_ON = WRConstants.id("block/gate_diode/redstone_on")
    private val GATE_DIODE_OFF = WRConstants.id("block/gate_diode/redstone_off")

    private val cache: LoadingCache<GateDiodePartKey, Mesh> =
        CacheBuilder.newBuilder().build(CacheLoader.from(::makeMesh))

    private fun makeMesh(key: GateDiodePartKey): Mesh {
        val modelId = if (key.powered) {
            GATE_DIODE_ON
        } else {
            GATE_DIODE_OFF
        }

        val backgroundModel = RenderUtils.getModel(GATE_DIODE_BACKGROUND)
        val redstoneModel = RenderUtils.getModel(modelId)

        val material = if (key.powered) {
            WRMaterials.POWERED_MATERIAL
        } else {
            WRMaterials.UNPOWERED_MATERIAL
        }

        val builder = RenderUtils.MESH_BUILDER
        val emitter = TransformingQuadEmitter.Multi(
            builder.emitter, arrayOf(RotateQuadTransform(key.direction), SideQuadTransform(key.side))
        )

        RenderUtils.fromVanilla(backgroundModel, emitter, WRMaterials.UNPOWERED_MATERIAL)
        RenderUtils.fromVanilla(redstoneModel, emitter, material)

        return builder.build()
    }

    override fun emitQuads(key: GateDiodePartKey, ctx: PartRenderContext) {
        ctx.meshConsumer().accept(cache[key])
    }

    override fun registerModels(out: Consumer<Identifier>) {
        out.accept(GATE_DIODE_BACKGROUND)
        out.accept(GATE_DIODE_ON)
        out.accept(GATE_DIODE_OFF)
    }
}