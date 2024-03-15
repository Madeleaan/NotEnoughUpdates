/*
 * Copyright (C) 2024 NotEnoughUpdates contributors
 *
 * This file is part of NotEnoughUpdates.
 *
 * NotEnoughUpdates is free software: you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * NotEnoughUpdates is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with NotEnoughUpdates. If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.moulberry.notenoughupdates.miscfeatures.tablisttutorial

import io.github.moulberry.notenoughupdates.util.TabListUtils
import io.github.moulberry.notenoughupdates.util.stripControlCodes
import java.util.*

object TablistAPI {

    @JvmStatic
    fun getWidgetLinesInRegion(widget: TablistTutorial.TabListWidget): List<String> {
        val regex = widget.widgetName.regex ?: Regex.fromLiteral("${widget.widgetName}:")
        val list = mutableListOf<String>()
        // If not a single reset is present, the tab list hasn't been initialized yet
        var sawReset = false

        for (entry in TabListUtils.getTabList()) {
            if (entry.contains("§r")) {
                sawReset = true
            }

            if (list.isNotEmpty()) {
                // New tab section, or empty line indicate a new section
                if (entry == "§r               §r§3§lInfo§r" || entry == "§r") {
                    break
                }
                list.add(entry)
            } else if (entry.stripControlCodes().matches(regex)) {
                list.add(entry)
            }
        }

        if (list.isEmpty() && sawReset) {
//            Thread.dumpStack()
//            println(TabListUtils.getTabList())
            TablistTaskQueue.addToQueue(widget)
        }
        return list
    }

    @JvmStatic
    fun getWidgetLines(widgetName: WidgetNames): List<String> {
        return getWidgetLinesInRegion(TablistTutorial.TabListWidget("CURRENT_REGION", widgetName))
    }

    enum class WidgetNames(val regex: Regex?) {
        PET(null),
        COMMISSIONS(null),
        SKILLS(null),
        FORGE(Regex.fromLiteral("Forges:")),
        EVENTS(Regex.fromLiteral("Event:")),
        POWDER(Regex.fromLiteral("Powders:")),
        CRYSTALS(Regex.fromLiteral("Crystals:")),
        EFFECT(Regex("""Active Effects\(\d+\):""")),
        PROFILE(Regex("Profile: ([A-Z][a-z]*)( .*)?"))
        ;
        // TODO other patterns

        override fun toString(): String {
            return this.name.lowercase().split(" ").joinToString(" ") { str ->
                str.replaceFirstChar {
                    if (it.isLowerCase()) {
                        it.titlecase(
                            Locale.ROOT
                        )
                    } else {
                        it.toString()
                    }
                }
            }
        }
    }
}
