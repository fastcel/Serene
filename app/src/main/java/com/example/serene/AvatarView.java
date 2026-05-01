package com.example.serene;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * AvatarView
 * ----------
 * Draws a simple avatar (body + head + face expression + optional accessory)
 * directly onto a Canvas. Call setAvatarConfig() to update and redraw.
 */
public class AvatarView extends View {

    // ─── Config ──────────────────────────────────────────────────────────────
    private String accessory = "None";
    private String face      = "Happy";
    private int    bodyColor = 0xFFD4956A;

    // ─── Paints ───────────────────────────────────────────────────────────────
    private final Paint bodyPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint eyePaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint smilePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint accPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);

    public AvatarView(Context context) { super(context); init(); }
    public AvatarView(Context context, AttributeSet attrs) { super(context, attrs); init(); }
    public AvatarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle); init();
    }

    private void init() {
        eyePaint.setColor(0xFF3D2B1F);
        eyePaint.setStyle(Paint.Style.FILL);

        smilePaint.setColor(0xFF3D2B1F);
        smilePaint.setStyle(Paint.Style.STROKE);
        smilePaint.setStrokeWidth(4f);
        smilePaint.setStrokeCap(Paint.Cap.ROUND);

        accPaint.setStyle(Paint.Style.FILL);
    }

    // ─── Public API ───────────────────────────────────────────────────────────

    public void setAvatarConfig(String accessory, String face, int color) {
        this.accessory = accessory;
        this.face      = face;
        this.bodyColor = color;
        invalidate();
    }

    // ─── Drawing ──────────────────────────────────────────────────────────────

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float w = getWidth();
        float h = getHeight();
        float cx = w / 2f;

        // Body (rounded rectangle, lower portion)
        bodyPaint.setColor(bodyColor);
        float bodyW  = w * 0.45f;
        float bodyH  = h * 0.30f;
        float bodyTop = h * 0.62f;
        RectF bodyRect = new RectF(cx - bodyW / 2, bodyTop, cx + bodyW / 2, bodyTop + bodyH);
        canvas.drawRoundRect(bodyRect, bodyW / 2, bodyH / 2, bodyPaint);

        // Head (circle)
        float headR  = w * 0.28f;
        float headCY = h * 0.40f;
        canvas.drawCircle(cx, headCY, headR, bodyPaint);

        // Eyes
        float eyeOffX = headR * 0.35f;
        float eyeOffY = headR * 0.10f;
        float eyeR    = headR * 0.10f;
        drawEyes(canvas, cx, headCY + eyeOffY, eyeOffX, eyeR);

        // Face expression
        drawFace(canvas, cx, headCY, headR);

        // Accessory (drawn on top)
        if (!accessory.equals("None")) {
            drawAccessory(canvas, cx, headCY, headR);
        }
    }

    private void drawEyes(Canvas canvas, float cx, float cy, float offsetX, float r) {
        canvas.drawCircle(cx - offsetX, cy, r, eyePaint);
        canvas.drawCircle(cx + offsetX, cy, r, eyePaint);
    }

    private void drawFace(Canvas canvas, float cx, float headCY, float headR) {
        float mouthY = headCY + headR * 0.38f;
        float mouthW = headR * 0.50f;
        float eyeOffY = headR * 0.10f;
        float eyeOffX = headR * 0.35f;
        float eyeR    = headR * 0.10f;

        switch (face) {
            case "Happy": {
                // Arc smile
                RectF smileRect = new RectF(cx - mouthW, mouthY - mouthW * 0.5f,
                        cx + mouthW, mouthY + mouthW * 0.5f);
                Path smilePath = new Path();
                smilePath.arcTo(smileRect, 0, 180, true);
                canvas.drawPath(smilePath, smilePaint);
                break;
            }
            case "Neutral": {
                canvas.drawLine(cx - mouthW, mouthY, cx + mouthW, mouthY, smilePaint);
                break;
            }
            case "Wink": {
                // Right eye closed (line)
                canvas.drawLine(cx + eyeOffX - eyeR, headCY + eyeOffY,
                        cx + eyeOffX + eyeR, headCY + eyeOffY, smilePaint);
                // Smile
                RectF smileRect = new RectF(cx - mouthW, mouthY - mouthW * 0.5f,
                        cx + mouthW, mouthY + mouthW * 0.5f);
                Path smilePath = new Path();
                smilePath.arcTo(smileRect, 0, 180, true);
                canvas.drawPath(smilePath, smilePaint);
                break;
            }
            case "Cool": {
                // Smirk (half arc)
                RectF smirkRect = new RectF(cx - mouthW * 0.6f, mouthY - mouthW * 0.4f,
                        cx + mouthW * 0.6f, mouthY + mouthW * 0.4f);
                Path smirkPath = new Path();
                smirkPath.arcTo(smirkRect, 10, 160, true);
                canvas.drawPath(smirkPath, smilePaint);
                break;
            }
            case "Surprised": {
                // Open "O" mouth
                float r = headR * 0.18f;
                Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                fillPaint.setColor(0xFF3D2B1F);
                fillPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(cx, mouthY, r, fillPaint);
                break;
            }
            case "Sleepy": {
                // Half-closed eyes (semi-circles)
                Paint halfEye = new Paint(Paint.ANTI_ALIAS_FLAG);
                halfEye.setColor(0xFF3D2B1F);
                halfEye.setStyle(Paint.Style.FILL);
                RectF lEye = new RectF(cx - eyeOffX - eyeR, headCY + eyeOffY - eyeR,
                        cx - eyeOffX + eyeR, headCY + eyeOffY);
                RectF rEye = new RectF(cx + eyeOffX - eyeR, headCY + eyeOffY - eyeR,
                        cx + eyeOffX + eyeR, headCY + eyeOffY);
                canvas.drawArc(lEye, 180, 180, true, halfEye);
                canvas.drawArc(rEye, 180, 180, true, halfEye);
                // Flat mouth
                canvas.drawLine(cx - mouthW * 0.6f, mouthY, cx + mouthW * 0.6f, mouthY, smilePaint);
                break;
            }
        }
    }

    private void drawAccessory(Canvas canvas, float cx, float headCY, float headR) {
        accPaint.setStyle(Paint.Style.FILL);
        accPaint.setStrokeWidth(5f);

        switch (accessory) {
            case "Glasses": {
                accPaint.setStyle(Paint.Style.STROKE);
                accPaint.setColor(0xFF3D2B1F);
                float gy = headCY + headR * 0.10f;
                float gx = headR * 0.35f;
                float gr = headR * 0.22f;
                canvas.drawCircle(cx - gx, gy, gr, accPaint);
                canvas.drawCircle(cx + gx, gy, gr, accPaint);
                canvas.drawLine(cx - gx + gr, gy, cx + gx - gr, gy, accPaint); // bridge
                break;
            }
            case "Sunglasses": {
                accPaint.setStyle(Paint.Style.FILL);
                accPaint.setColor(0xCC2A2A2A);
                float gy = headCY + headR * 0.10f;
                float gx = headR * 0.35f;
                float gr = headR * 0.22f;
                canvas.drawCircle(cx - gx, gy, gr, accPaint);
                canvas.drawCircle(cx + gx, gy, gr, accPaint);
                accPaint.setStyle(Paint.Style.STROKE);
                accPaint.setColor(0xFF1A1A1A);
                accPaint.setStrokeWidth(4f);
                canvas.drawLine(cx - gx + gr, gy, cx + gx - gr, gy, accPaint);
                break;
            }
            case "Cap": {
                accPaint.setColor(0xFF5B4FCF);
                accPaint.setStyle(Paint.Style.FILL);
                float capTop  = headCY - headR * 1.0f;
                float capMid  = headCY - headR * 0.45f;
                float capBrimL = cx - headR * 1.1f;
                float capBrimR = cx + headR * 0.4f;
                // Dome
                RectF capRect = new RectF(cx - headR, capTop, cx + headR, capMid + headR * 0.2f);
                canvas.drawArc(capRect, 180, 180, true, accPaint);
                // Brim
                RectF brimRect = new RectF(capBrimL, capMid, capBrimR, capMid + headR * 0.18f);
                canvas.drawRoundRect(brimRect, 8f, 8f, accPaint);
                break;
            }
            case "Headband": {
                accPaint.setColor(0xFFE95F8B);
                accPaint.setStyle(Paint.Style.STROKE);
                accPaint.setStrokeWidth(headR * 0.18f);
                float bandY = headCY - headR * 0.50f;
                canvas.drawLine(cx - headR, bandY, cx + headR, bandY, accPaint);
                break;
            }
            case "Earrings": {
                accPaint.setColor(0xFFFFD700);
                accPaint.setStyle(Paint.Style.FILL);
                float ey = headCY + headR * 0.20f;
                float ex = headR * 0.92f;
                canvas.drawCircle(cx - ex, ey, headR * 0.10f, accPaint);
                canvas.drawCircle(cx + ex, ey, headR * 0.10f, accPaint);
                break;
            }
        }
    }
}
