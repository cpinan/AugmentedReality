package com.carlospinan.augmentedreality.objects;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * @author Carlos Piñan
 */
public class RadarObject extends BaseObject {

    private Paint paintRadarBorder;
    private Paint paintRadar;
    private Paint paintCenter;
    private Paint paintLineRadar;

    public RadarObject(float width, float height) {
        super(width, height);
        paintRadarBorder = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintRadarBorder.setARGB(255, 255, 255, 255);
        paintRadarBorder.setStyle(Paint.Style.STROKE);

        paintRadar = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintRadar.setARGB(128, 0, 0, 0);
        paintRadar.setStyle(Paint.Style.FILL);

        paintCenter = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintCenter.setARGB(255, 255, 0, 0);
        paintCenter.setStyle(Paint.Style.STROKE);
        paintCenter.setStrokeWidth(5.0f);

        paintLineRadar = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintLineRadar.setARGB(255, 255, 255, 255);
        paintLineRadar.setStyle(Paint.Style.STROKE);
        paintLineRadar.setStrokeWidth(3.0f);
        paintLineRadar.setFakeBoldText(true);
    }

    @Override
    public void onDraw(Canvas canvas) {
        float radarRadius = width * 0.08f;
        float cx = width - radarRadius * 1.2f;
        float cy = radarRadius * 1.2f;

        RectF radarRect = new RectF(
                cx - radarRadius - 1,
                cy - radarRadius - 1,
                cx + radarRadius + 1,
                cy + radarRadius + 1
        );

        if (getCurrentOrientation() != null && getCurrentOrientation().length > 0) {
            float[] orientation = getCurrentOrientation();
            canvas.save();
            canvas.clipRect(radarRect);
            canvas.rotate(-orientation[0], cx, cy);
            canvas.drawLine(cx, cy, cx, cy + radarRadius, paintLineRadar);
            canvas.drawCircle(cx, cy, radarRadius, paintRadar);
            canvas.drawCircle(cx, cy, radarRadius, paintRadarBorder);
            canvas.drawCircle(cx, cy, radarRadius * 0.05f, paintCenter);
            canvas.restore();
        }

    }

}
