/*
 * Charlatano is a premium CS:GO cheat ran on the JVM.
 * Copyright (C) 2016 Thomas Nappo, Jonathan Beaudoin
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

package com.charlatano.overlay

import com.charlatano.utils.natives.CUser32
import com.jogamp.newt.event.WindowAdapter
import com.jogamp.newt.event.WindowEvent
import com.jogamp.newt.opengl.GLWindow
import com.jogamp.opengl.*
import com.jogamp.opengl.util.FPSAnimator
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import org.anglur.joglext.jogl2d.GLGraphics2D
import kotlin.concurrent.thread
import java.util.concurrent.ThreadLocalRandom as tlr

fun main(args: Array<String>) {
	CharlatanoOverlay.open()
	
	CharlatanoOverlay {
		it.drawRect(250, 250, 1000, 1000)
	}
}

object CharlatanoOverlay : GLEventListener {
	
	private val TITLE = tlr.current().nextLong(Long.MAX_VALUE).toString()
	private val WINDOW_WIDTH = 2500
	private val WINDOW_HEIGHT = 1400
	private val FPS = 60
	
	val window by lazy {
		val glp = GLProfile.getDefault()
		val caps = GLCapabilities(glp)
		caps.isBackgroundOpaque = false
		caps.alphaBits = 8
		
		GLWindow.create(caps)
	}
	
	init {
		GLProfile.initSingleton()
	}
	
	fun open(width: Int = WINDOW_WIDTH, height: Int = WINDOW_HEIGHT, x: Int = 0, y: Int = 0) {
		window.isUndecorated = true
		window.isFullscreen = false
		window.isAlwaysOnTop = true
		val animator = FPSAnimator(window, FPS, true)
		
		window.addWindowListener(object : WindowAdapter() {
			override fun windowDestroyNotify(e: WindowEvent) {
				thread {
					if (animator.isStarted)
						animator.stop()
					System.exit(0)
				}.start()
			}
		})
		
		window.addGLEventListener(this)
		window.setSize(width, height)
		window.setPosition(x, y)
		window.title = TITLE
		window.isVisible = true
		animator.start()
		
		val hwnd = CUser32.FindWindowA(null, TITLE)
		WindowTools.transparentWindow(hwnd)
	}
	
	val g = GLGraphics2D()
	
	override fun display(gLDrawable: GLAutoDrawable) {
		if (bodies.isEmpty) return
		
		val gl2 = gLDrawable.gl.gL2
		gl2.glClear(GL.GL_COLOR_BUFFER_BIT)
		
		g.prePaint(gLDrawable.context)
		for (x in 0..bodies.size - 1) {
			bodies[x](g)
		}
	}
	
	override fun init(glDrawable: GLAutoDrawable) {
		val gl = glDrawable.gl.gL2
		gl.glEnable(GL.GL_BLEND)
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA)
	}
	
	override fun reshape(gLDrawable: GLAutoDrawable, x: Int, y: Int, width: Int, height: Int) {
		val gl = gLDrawable.gl.gL2
		gl.glViewport(0, 0, width, height)
	}
	
	override fun dispose(gLDrawable: GLAutoDrawable) {
		g.glDispose()
	}
	
	private val bodies = ObjectArrayList<CharlatanoOverlay.(GLGraphics2D) -> Unit>()
	
	operator fun invoke(body: CharlatanoOverlay.(GLGraphics2D) -> Unit) {
		bodies.add(body)
	}
	
}