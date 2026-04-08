/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2025 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.actions

import android.util.Log
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.bricks.ArcBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException
import kotlin.math.cos
import kotlin.math.sign
import kotlin.math.sin

class PlotArcAction : TemporalAction() {
    private var scope: Scope? = null
    private var direction: ArcBrick.Directions = ArcBrick.Directions.LEFT
    lateinit var radius: Formula
    lateinit var degrees: Formula

    private var degreesValue: Double = 0.0
    private var radiusValue: Double = 0.0
    private var centerX: Double = 0.0
    private var centerY: Double = 0.0
    private var startX: Double = 0.0
    private var startY: Double = 0.0
    private var startMotionDirectionInRadians: Double = Math.toRadians(90.0)

    override fun begin() {
        super.begin()
        if (scope == null) {
            return
        }
        try {
            degreesValue =
                degrees.interpretDouble(scope) * if (direction == ArcBrick.Directions.RIGHT) 1 else -1
            radiusValue = kotlin.math.abs(radius.interpretDouble(scope))
            val sprite = scope!!.sprite
            startX = sprite.look.xInUserInterfaceDimensionUnit.toDouble()
            startY = sprite.look.yInUserInterfaceDimensionUnit.toDouble()
            startMotionDirectionInRadians =
                Math.toRadians(sprite.look.motionDirectionInUserInterfaceDimensionUnit.toDouble())

            val turnSign = if (degreesValue == 0.0) 1.0 else sign(degreesValue)
            val normalX = turnSign * cos(startMotionDirectionInRadians)
            val normalY = -turnSign * sin(startMotionDirectionInRadians)
            centerX = startX + radiusValue * normalX
            centerY = startY + radiusValue * normalY
        } catch (interpretationException: InterpretationException) {
            Log.d(
                javaClass.simpleName,
                "Formula interpretation for this specific Brick failed.",
                interpretationException
            )
        }
    }

    override fun update(percent: Float) {
        if (scope == null) {
            return
        }
        try {
            val traversedDegrees = degreesValue * percent
            val traversedRadians = Math.toRadians(-traversedDegrees)
            val startOffsetX = startX - centerX
            val startOffsetY = startY - centerY
            val rotatedOffsetX =
                startOffsetX * cos(traversedRadians) - startOffsetY * sin(traversedRadians)
            val rotatedOffsetY =
                startOffsetX * sin(traversedRadians) + startOffsetY * cos(traversedRadians)
            val newX = centerX + rotatedOffsetX
            val newY = centerY + rotatedOffsetY
            scope!!.sprite.look.setPositionInUserInterfaceDimensionUnit(newX.toFloat(), newY.toFloat())
            scope!!.sprite.look.motionDirectionInUserInterfaceDimensionUnit =
                Math.toDegrees(startMotionDirectionInRadians).toFloat() + traversedDegrees.toFloat()
        } catch (interpretationException: InterpretationException) {
            Log.d(
                javaClass.simpleName,
                "Formula interpretation for this specific Brick failed.",
                interpretationException
            )
        }
    }

    fun setScope(scope: Scope?) {
        this.scope = scope
    }

    fun setDirection(direction: ArcBrick.Directions) {
        this.direction = direction
    }
}
