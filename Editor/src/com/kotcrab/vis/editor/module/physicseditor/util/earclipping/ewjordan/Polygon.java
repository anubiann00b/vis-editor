/*
 * Copyright 2014-2015 Pawel Pastuszak
 *
 * This file is part of VisEditor.
 *
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kotcrab.vis.editor.module.physicseditor.util.earclipping.ewjordan;

/**
 * @author EwJordan (http://www.ewjordan.com/earClip/)
 */
public class Polygon {
	public float[] x;
	public float[] y;
	public int nVertices;

	public Polygon (float[] _x, float[] _y) {
		nVertices = _x.length;
		x = new float[nVertices];
		y = new float[nVertices];
		for (int i = 0; i < nVertices; ++i) {
			x[i] = _x[i];
			y[i] = _y[i];
		}
	}

	public Polygon (Triangle t) {
		this(t.x, t.y);
	}

	public void set (Polygon p) {
		nVertices = p.nVertices;
		x = new float[nVertices];
		y = new float[nVertices];
		for (int i = 0; i < nVertices; ++i) {
			x[i] = p.x[i];
			y[i] = p.y[i];
		}
	}

	public boolean isConvex () {
		boolean isPositive = false;
		for (int i = 0; i < nVertices; ++i) {
			int lower = (i == 0) ? (nVertices - 1) : (i - 1);
			int middle = i;
			int upper = (i == nVertices - 1) ? (0) : (i + 1);
			float dx0 = x[middle] - x[lower];
			float dy0 = y[middle] - y[lower];
			float dx1 = x[upper] - x[middle];
			float dy1 = y[upper] - y[middle];
			float cross = dx0 * dy1 - dx1 * dy0;
			//Cross product should have same sign
			//for each vertex if poly is convex.
			boolean newIsP = (cross > 0) ? true : false;
			if (i == 0) {
				isPositive = newIsP;
			} else if (isPositive != newIsP) {
				return false;
			}
		}
		return true;
	}

	/*
	 * Tries to add a triangle to the polygon.
	 * Returns null if it can't connect properly.
	 * Assumes bitwise equality of join vertices.
	 */
	public Polygon add (Triangle t) {
		//First, find vertices that connect
		int firstP = -1;
		int firstT = -1;
		int secondP = -1;
		int secondT = -1;
		for (int i = 0; i < nVertices; i++) {
			if (t.x[0] == x[i] && t.y[0] == y[i]) {
				if (firstP == -1) {
					firstP = i;
					firstT = 0;
				} else {
					secondP = i;
					secondT = 0;
				}
			} else if (t.x[1] == x[i] && t.y[1] == y[i]) {
				if (firstP == -1) {
					firstP = i;
					firstT = 1;
				} else {
					secondP = i;
					secondT = 1;
				}
			} else if (t.x[2] == x[i] && t.y[2] == y[i]) {
				if (firstP == -1) {
					firstP = i;
					firstT = 2;
				} else {
					secondP = i;
					secondT = 2;
				}
			}
		}
		//Fix ordering if first should be last vertex of poly
		if (firstP == 0 && secondP == nVertices - 1) {
			firstP = nVertices - 1;
			secondP = 0;
		}

		//Didn't find it
		if (secondP == -1) {
			return null;
		}

		//Find tip index on triangle
		int tipT = 0;
		if (tipT == firstT || tipT == secondT) {
			tipT = 1;
		}
		if (tipT == firstT || tipT == secondT) {
			tipT = 2;
		}

		float[] newx = new float[nVertices + 1];
		float[] newy = new float[nVertices + 1];
		int currOut = 0;
		for (int i = 0; i < nVertices; i++) {
			newx[currOut] = x[i];
			newy[currOut] = y[i];
			if (i == firstP) {
				++currOut;
				newx[currOut] = t.x[tipT];
				newy[currOut] = t.y[tipT];
			}
			++currOut;
		}
		return new Polygon(newx, newy);
	}
}
