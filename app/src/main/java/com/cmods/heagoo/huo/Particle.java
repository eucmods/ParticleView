/**
 * @Author 
 * @AIDE AIDE+
*/
package com.cmods.heagoo.huo;

public class Particle {
    public float radius;
    public float x, y;
    public int vx, vy;
    public int alpha;

    public Particle(float radius, float x, float y, int vx, int vy, int alpha) {
        this.radius = radius;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.alpha = alpha;
    }

    @Override
    public String toString() {
        return "Particle{" +
			"radius=" + radius +
			", x=" + x +
			", y=" + y +
			", vx=" + vx +
			", vy=" + vy +
			", alpha=" + alpha +
			'}';
    }
}


