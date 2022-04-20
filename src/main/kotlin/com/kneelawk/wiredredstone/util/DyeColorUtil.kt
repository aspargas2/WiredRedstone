package com.kneelawk.wiredredstone.util

import com.kneelawk.wiredredstone.item.WRItems.BLACK_INSULATED_WIRE
import com.kneelawk.wiredredstone.item.WRItems.BLUE_INSULATED_WIRE
import com.kneelawk.wiredredstone.item.WRItems.BROWN_INSULATED_WIRE
import com.kneelawk.wiredredstone.item.WRItems.CYAN_INSULATED_WIRE
import com.kneelawk.wiredredstone.item.WRItems.GRAY_INSULATED_WIRE
import com.kneelawk.wiredredstone.item.WRItems.GREEN_INSULATED_WIRE
import com.kneelawk.wiredredstone.item.WRItems.LIGHT_BLUE_INSULATED_WIRE
import com.kneelawk.wiredredstone.item.WRItems.LIGHT_GRAY_INSULATED_WIRE
import com.kneelawk.wiredredstone.item.WRItems.LIME_INSULATED_WIRE
import com.kneelawk.wiredredstone.item.WRItems.MAGENTA_INSULATED_WIRE
import com.kneelawk.wiredredstone.item.WRItems.ORANGE_INSULATED_WIRE
import com.kneelawk.wiredredstone.item.WRItems.PINK_INSULATED_WIRE
import com.kneelawk.wiredredstone.item.WRItems.PURPLE_INSULATED_WIRE
import com.kneelawk.wiredredstone.item.WRItems.RED_INSULATED_WIRE
import com.kneelawk.wiredredstone.item.WRItems.WHITE_INSULATED_WIRE
import com.kneelawk.wiredredstone.item.WRItems.YELLOW_INSULATED_WIRE
import net.minecraft.block.Block
import net.minecraft.block.Blocks.*
import net.minecraft.item.Item
import net.minecraft.util.DyeColor
import net.minecraft.util.DyeColor.*

object DyeColorUtil {
    private val DYE_COLORS_TO_WOOL = mapOf(
        WHITE to WHITE_WOOL,
        ORANGE to ORANGE_WOOL,
        MAGENTA to MAGENTA_WOOL,
        LIGHT_BLUE to LIGHT_BLUE_WOOL,
        YELLOW to YELLOW_WOOL,
        LIME to LIME_WOOL,
        PINK to PINK_WOOL,
        GRAY to GRAY_WOOL,
        LIGHT_GRAY to LIGHT_GRAY_WOOL,
        CYAN to CYAN_WOOL,
        PURPLE to PURPLE_WOOL,
        BLUE to BLUE_WOOL,
        BROWN to BROWN_WOOL,
        GREEN to GREEN_WOOL,
        RED to RED_WOOL,
        BLACK to BLACK_WOOL
    )

    private val DYE_COLORS_TO_INSULATED_WIRES = mapOf(
        WHITE to WHITE_INSULATED_WIRE,
        ORANGE to ORANGE_INSULATED_WIRE,
        MAGENTA to MAGENTA_INSULATED_WIRE,
        LIGHT_BLUE to LIGHT_BLUE_INSULATED_WIRE,
        YELLOW to YELLOW_INSULATED_WIRE,
        LIME to LIME_INSULATED_WIRE,
        PINK to PINK_INSULATED_WIRE,
        GRAY to GRAY_INSULATED_WIRE,
        LIGHT_GRAY to LIGHT_GRAY_INSULATED_WIRE,
        CYAN to CYAN_INSULATED_WIRE,
        PURPLE to PURPLE_INSULATED_WIRE,
        BLUE to BLUE_INSULATED_WIRE,
        BROWN to BROWN_INSULATED_WIRE,
        GREEN to GREEN_INSULATED_WIRE,
        RED to RED_INSULATED_WIRE,
        BLACK to BLACK_INSULATED_WIRE
    )

    fun wool(color: DyeColor): Block {
        return DYE_COLORS_TO_WOOL[color]!!
    }

    fun insulatedWire(color: DyeColor): Item {
        return DYE_COLORS_TO_INSULATED_WIRES[color]!!
    }
}