/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.test.plot

import android.graphics.PointF
import com.badlogic.gdx.utils.Queue
import junit.framework.Assert.assertEquals
import org.catrobat.catroid.plot.Plot
import org.catrobat.catroid.plot.SVGPlotGenerator
import org.catrobat.catroid.test.utils.Reflection
import org.junit.Test

class PlotTest {
    @Test
    @Throws(Exception::class)
    fun testEngraveExportDataRemainsStableWhenRenderQueuePointsChange() {
        val plot = Plot()
        plot.startNewEngraveLine(PointF(100f, 100f))
        plot.addEngravePoint(PointF(150f, 200f))

        val originalPath = SVGPlotGenerator(plot).pathFromData(plot.engraveDataPointLists)

        val engraveQueue =
            Reflection.getPrivateField(plot, "engraveQueue") as Queue<Queue<PointF>>
        val queuedFirstPoint = engraveQueue.first().first()
        queuedFirstPoint.x += 50f
        queuedFirstPoint.y -= 25f

        val mutatedPath = SVGPlotGenerator(plot).pathFromData(plot.engraveDataPointLists)

        assertEquals(originalPath, mutatedPath)
    }
}
