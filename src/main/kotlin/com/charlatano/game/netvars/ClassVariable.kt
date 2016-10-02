/*
 * Charlatano is a premium CS:GO cheat ran on the JVM.
 * Copyright (C) 2016 - Thomas Nappo, Jonathan Beaudoin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.charlatano.game.netvars

import com.charlatano.game.CSGO.csgoEXE
import com.charlatano.utils.uint
import org.jire.arrowhead.Addressed
import kotlin.LazyThreadSafetyMode.NONE

internal class ClassVariable(override val address: Long, val addressOffset: Long) : Addressed {

	val resolvedAddress by lazy(NONE) { csgoEXE.uint(address) }

	val name by lazy(NONE) {
		val bytes = ByteArray(32)

		val memory = csgoEXE.read(resolvedAddress, bytes.size)!!
		memory.read(0, bytes, 0, bytes.size)

		bytes.toNetVarString()
	}

	val table by lazy(NONE) { csgoEXE.uint(address + 0x28) }

	val offset by lazy(NONE) { addressOffset + csgoEXE.uint(address + 0x2C) }

	val type by lazy(NONE) { csgoEXE.uint(address + 0x4) }

	val elements by lazy(NONE) { csgoEXE.uint(address + 0x34) }

	val stringBufferCount by lazy(NONE) { csgoEXE.uint(address + 0xC) }

}