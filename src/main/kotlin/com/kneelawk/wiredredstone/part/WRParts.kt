package com.kneelawk.wiredredstone.part

import alexiil.mc.lib.multipart.api.PartDefinition
import com.kneelawk.wiredredstone.WRConstants

object WRParts {
    // Wires
    val RED_ALLOY_WIRE by lazy { definition("red_alloy_wire", ::RedAlloyWirePart, ::RedAlloyWirePart) }
    val INSULATED_WIRE by lazy { definition("insulated_wire", ::InsulatedWirePart, ::InsulatedWirePart) }
    val BUNDLED_CABLE by lazy { definition("bundled_cable", ::BundledCablePart, ::BundledCablePart) }

    // Gates
    val GATE_DIODE by lazy { definition("gate_diode", ::GateDiodePart, ::GateDiodePart) }
    val GATE_NOR by lazy { definition("gate_nor", ::GateNorPart, ::GateNorPart) }
    val GATE_NOT by lazy { definition("gate_not", ::GateNotPart, ::GateNotPart) }
    val GATE_REPEATER by lazy { definition("gate_repeater", ::GateRepeaterPart, ::GateRepeaterPart) }

    private fun definition(
        path: String, reader: PartDefinition.IPartNbtReader, loader: PartDefinition.IPartNetLoader
    ): PartDefinition {
        return PartDefinition(WRConstants.id(path), reader, loader)
    }

    fun init() {
        PartDefinition.PARTS[RED_ALLOY_WIRE.identifier] = RED_ALLOY_WIRE
        PartDefinition.PARTS[INSULATED_WIRE.identifier] = INSULATED_WIRE
        PartDefinition.PARTS[BUNDLED_CABLE.identifier] = BUNDLED_CABLE
        PartDefinition.PARTS[GATE_DIODE.identifier] = GATE_DIODE
        PartDefinition.PARTS[GATE_NOR.identifier] = GATE_NOR
        PartDefinition.PARTS[GATE_NOT.identifier] = GATE_NOT
        PartDefinition.PARTS[GATE_REPEATER.identifier] = GATE_REPEATER
    }
}
