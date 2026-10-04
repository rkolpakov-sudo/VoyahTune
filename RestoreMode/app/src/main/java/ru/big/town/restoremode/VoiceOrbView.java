package ru.big.town.restoremode;

import android.content.Context;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.ComposeShader;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.SystemClock;
import android.view.View;
import androidx.core.view.ViewCompat;

/* JADX INFO: loaded from: classes2.dex */
final class VoiceOrbView extends View {
    private static final float HALO_RADIUS = 1.35f;
    private final Shader core;
    private final Shader cyanReflection;
    private float envelope;
    private boolean error;
    private final Shader glass;
    private final Shader[] halos;
    private final Shader[] innerReflections;
    private long lastFrame;
    private float motion;
    private final Paint paint;
    private final Path petal;
    private final float[] petalAngles;
    private float petalRotation;
    private final Shader[] petals;
    private float phase;
    private boolean recognized;
    private final Shader redHalo;
    private final Shader redInnerReflection;
    private final Shader redPetal;
    private final Shader redReflection;
    private final Shader rim;
    private final Path sphere;
    private float successAmount;
    private final ColorMatrix successTint;
    private float target;
    private final float[] tintValues;

    VoiceOrbView(Context context) {
        super(context);
        this.paint = new Paint(1);
        this.petal = new Path();
        this.sphere = new Path();
        this.successTint = new ColorMatrix();
        this.tintValues = new float[20];
        this.glass = radial(-0.18f, -0.25f, HALO_RADIUS, new int[]{-1256513216, -452325340, -1038604221}, new float[]{0.0f, 0.72f, 1.0f});
        this.rim = new SweepGradient(0.0f, 0.0f, new int[]{1429531117, 407201468, 1432197851, 1442794631, 1429531117}, (float[]) null);
        this.core = radial(0.0f, 0.0f, 0.48f, new int[]{-1, -168820737, 1435625471, 9562111}, new float[]{0.0f, 0.28f, 0.65f, 1.0f});
        this.redReflection = radial(-0.12f, -0.86f, 1.3f, new int[]{-2048253093, 636101467, 15344475}, new float[]{0.0f, 0.52f, 1.0f});
        this.cyanReflection = radial(0.58f, 0.7f, 1.1f, new int[]{1883040242, 423422450, 3992050}, new float[]{0.0f, 0.55f, 1.0f});
        this.petalAngles = new float[]{40.0f, 205.0f, 285.0f, 120.0f};
        this.petals = new Shader[]{petalShader(-48247), petalShader(-6588929), petalShader(-12603393), petalShader(-12386330)};
        this.halos = new Shader[]{haloShader(-48247), haloShader(-6588929), haloShader(-12603393), haloShader(-12386330)};
        this.innerReflections = new Shader[]{reflectionShader(-48247), reflectionShader(-6588929), reflectionShader(-12603393), reflectionShader(-12386330)};
        this.redPetal = petalShader(-44421);
        this.redHalo = haloShader(-44421);
        this.redInnerReflection = reflectionShader(-44421);
        setContentDescription("Голосовой помощник");
    }

    void level(float f) {
        this.target = Math.max(0.0f, Math.min(1.0f, f));
    }

    void state(boolean z, boolean z2) {
        this.error = z2;
        this.recognized = false;
        this.successAmount = 0.0f;
        this.paint.setColorFilter(null);
        if (!z) {
            this.target = 0.0f;
        }
        invalidate();
    }

    void recognized() {
        this.recognized = true;
        this.error = false;
        this.target = 0.0f;
        invalidate();
    }

    private void updateSuccessTint(float f) {
        float f2;
        float f3;
        if (this.recognized) {
            float f4 = this.successAmount;
            if (f4 >= 1.0f) {
                return;
            }
            float fMin = Math.min(1.0f, f4 + (f / 0.85f));
            this.successAmount = fMin;
            float f5 = fMin * fMin * (3.0f - (fMin * 2.0f));
            int i = 0;
            while (i < 3) {
                if (i == 0) {
                    f2 = 0.45f;
                } else {
                    f2 = i == 1 ? 1.0f : 0.84f;
                }
                int i2 = 0;
                while (i2 < 3) {
                    if (i2 == 0) {
                        f3 = 0.213f;
                    } else {
                        f3 = i2 == 1 ? 0.715f : 0.072f;
                    }
                    this.tintValues[(i * 5) + i2] = (i == i2 ? 1.0f - f5 : 0.0f) + (f5 * f2 * f3);
                    i2++;
                }
                i++;
            }
            float[] fArr = this.tintValues;
            fArr[18] = 1.0f;
            this.successTint.set(fArr);
            this.paint.setColorFilter(new ColorMatrixColorFilter(this.successTint));
        }
    }

    private static Shader radial(float f, float f2, float f3, int[] iArr, float[] fArr) {
        return new RadialGradient(f, f2, f3, iArr, fArr, Shader.TileMode.CLAMP);
    }

    private static Shader petalShader(int i) {
        int i2 = i & ViewCompat.MEASURED_SIZE_MASK;
        return radial(-0.05f, -0.3f, 0.82f, new int[]{-788529153, (-1073741824) | i2, i2 | 402653184}, new float[]{0.0f, 0.43f, 1.0f});
    }

    private static Shader haloShader(int i) {
        int i2 = i & ViewCompat.MEASURED_SIZE_MASK;
        return radial(-0.03f, -0.38f, HALO_RADIUS, new int[]{(-1073741824) | i2, (-1811939328) | i2, 1342177280 | i2, 335544320 | i2, i2}, new float[]{0.0f, 0.25f, 0.5f, 0.75f, 1.0f});
    }

    private static Shader reflectionShader(int i) {
        int i2 = i & ViewCompat.MEASURED_SIZE_MASK;
        return new ComposeShader(radial(0.0f, -0.86f, 1.05f, new int[]{(-1342177280) | i2, 1694498816 | i2, i2}, new float[]{0.0f, 0.45f, 1.0f}), radial(0.0f, 0.0f, 1.0f, new int[]{ViewCompat.MEASURED_SIZE_MASK, ViewCompat.MEASURED_SIZE_MASK, 1895825407, -855638017, 419430399}, new float[]{0.0f, 0.64f, 0.82f, 0.94f, 1.0f}), BlendMode.DST_IN);
    }

    private void transformPetal(Canvas canvas, int i) {
        float f = i;
        float fSin = (float) Math.sin(this.phase + (1.4f * f));
        float fCos = (float) Math.cos((this.phase * 0.83f) + (1.8f * f));
        float fSin2 = (float) Math.sin((this.phase * 1.37f) + (2.3f * f));
        float fCos2 = (float) Math.cos((this.phase * 1.71f) - (f * 1.1f));
        float f2 = this.motion;
        float f3 = (0.34f * f2) + 0.12f;
        canvas.rotate(this.petalAngles[i] + this.petalRotation + (((f2 * 24.0f) + 15.0f) * fSin));
        canvas.translate(fSin2 * f3 * 0.16f, fCos2 * f3 * 0.12f);
        float f4 = this.motion;
        canvas.skew(fSin2 * ((0.22f * f4) + 0.08f), ((f4 * 0.1f) + 0.04f) * fCos);
        float f5 = this.motion;
        canvas.scale((fCos * ((0.23f * f5) + 0.12f)) + 0.85f, (fSin * ((f5 * 0.14f) + 0.06f)) + 0.94f);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        float f;
        float f2;
        float f3;
        Canvas canvas2 = canvas;
        super.onDraw(canvas);
        long jUptimeMillis = SystemClock.uptimeMillis();
        long j = this.lastFrame;
        float fMin = j == 0 ? 0.016f : Math.min(0.1f, (jUptimeMillis - j) / 1000.0f);
        this.lastFrame = jUptimeMillis;
        updateSuccessTint(fMin);
        float f4 = this.envelope;
        float f5 = this.target;
        float f6 = -fMin;
        float fExp = f4 + ((f5 - f4) * (1.0f - ((float) Math.exp((f5 > f4 ? 9 : 4) * f6))));
        this.envelope = fExp;
        float fMin2 = Math.min(1.0f, fExp * 1.6f);
        float f7 = this.motion;
        float fExp2 = f7 + ((fMin2 - f7) * (1.0f - ((float) Math.exp(((double) f6) * (fMin2 > f7 ? 5.0d : 1.4d)))));
        this.motion = fExp2;
        this.phase += ((2.6f * fExp2) + 0.7f) * fMin;
        this.petalRotation = (this.petalRotation + (fMin * ((fExp2 * 70.0f) + 18.0f))) % 360.0f;
        float fMin3 = Math.min(Math.min(getWidth(), getHeight()) * 0.29f, getResources().getDisplayMetrics().density * 87.0f) * ((this.envelope * 0.18f) + 1.0f);
        float f8 = getResources().getDisplayMetrics().density / fMin3;
        canvas2.save();
        canvas2.translate(getWidth() / 2.0f, getHeight() / 2.0f);
        canvas2.scale(fMin3, fMin3);
        this.paint.setStyle(Paint.Style.FILL);
        this.paint.setBlendMode(null);
        canvas2.save();
        canvas2.rotate((((float) Math.sin(this.phase * 0.42f)) * 18.0f) - 24.0f);
        this.paint.setBlendMode(BlendMode.SCREEN);
        int i = 0;
        while (true) {
            f = 24.0f;
            f2 = 15.0f;
            f3 = 1.4f;
            if (i >= this.halos.length) {
                break;
            }
            canvas2.save();
            canvas2.rotate(this.petalAngles[i] + this.petalRotation + (((float) Math.sin(this.phase + (i * 1.4f))) * ((this.motion * 24.0f) + 15.0f)));
            this.paint.setShader(this.error ? this.redHalo : this.halos[i]);
            this.paint.setAlpha((int) ((this.motion * 50.0f) + 205.0f));
            canvas2.drawCircle(-0.03f, -0.38f, HALO_RADIUS, this.paint);
            canvas2.restore();
            i++;
        }
        canvas2.restore();
        this.paint.setBlendMode(null);
        this.paint.setShader(this.glass);
        this.paint.setAlpha(255);
        canvas2.drawCircle(0.0f, 0.0f, 1.0f, this.paint);
        this.sphere.reset();
        this.sphere.addCircle(0.0f, 0.0f, 0.997f, Path.Direction.CW);
        canvas2.save();
        canvas2.clipPath(this.sphere);
        this.paint.setBlendMode(BlendMode.SCREEN);
        this.paint.setShader(this.redReflection);
        this.paint.setAlpha(230);
        canvas2.drawCircle(0.0f, 0.0f, 1.0f, this.paint);
        this.paint.setShader(this.error ? this.redReflection : this.cyanReflection);
        canvas2.drawCircle(0.0f, 0.0f, 1.0f, this.paint);
        canvas2.rotate((((float) Math.sin(this.phase * 0.42f)) * 18.0f) - 24.0f);
        this.paint.setBlendMode(BlendMode.SCREEN);
        int i2 = 0;
        while (i2 < this.petals.length) {
            float f9 = i2;
            float fSin = (float) Math.sin(this.phase + (f9 * f3));
            float fCos = (float) Math.cos((this.phase * 0.83f) + (1.8f * f9));
            float f10 = f;
            float f11 = f2;
            float fSin2 = (float) Math.sin((this.phase * 1.37f) + (2.3f * f9));
            float fCos2 = (float) Math.cos((this.phase * 1.71f) - (f9 * 1.1f));
            float f12 = (this.motion * 0.34f) + 0.12f;
            canvas2.save();
            transformPetal(canvas2, i2);
            this.petal.reset();
            float f13 = f3;
            this.petal.moveTo(-0.15f, 0.09f);
            float f14 = fCos * f12;
            float f15 = fSin2 * f12;
            float f16 = fCos2 * f12;
            float f17 = fSin * f12;
            this.petal.cubicTo(f14 - 0.55f, (0.35f * f15) - 0.08f, (0.4f * f16) - 0.8f, f17 - 0.66f, (0.65f * f15) - 0.3f, (0.28f * f14) - 0.78f);
            this.petal.cubicTo(f14 + 0.08f, (0.45f * f16) - 0.96f, (f15 * 0.55f) + 0.72f, f14 - 0.65f, (0.55f * f17) + 0.43f, (0.3f * f16) - 0.23f);
            this.petal.cubicTo(0.29f - (f16 * 0.6f), f17 + 0.02f, (f15 * 0.25f) - 0.01f, 0.19f, -0.15f, 0.09f);
            this.paint.setShader(this.error ? this.redPetal : this.petals[i2]);
            this.paint.setAlpha(i2 == 2 ? 185 : 225);
            canvas2.drawPath(this.petal, this.paint);
            canvas2.restore();
            i2++;
            f = f10;
            f2 = f11;
            f3 = f13;
        }
        float f18 = f;
        float f19 = f2;
        float f20 = f3;
        this.paint.setShader(this.core);
        this.paint.setAlpha((int) ((this.envelope * 30.0f) + 225.0f));
        float f21 = 0.48f;
        canvas2.drawCircle(0.0f, 0.0f, 0.48f, this.paint);
        this.paint.setShader(null);
        int i3 = 0;
        while (i3 < 22) {
            float f22 = i3 * 2.399963f;
            float f23 = ((((i3 * 13) % 23) / 23.0f) * 0.62f) + 0.22f;
            double d = (this.phase * (((i3 % 5) * 0.035f) + f21)) + f22;
            float fCos3 = ((float) Math.cos(d)) * f23;
            float fSin3 = ((float) Math.sin(d)) * f23 * 0.8f;
            float fPow = (float) Math.pow((Math.sin((this.phase * 1.8f) + f22) * 0.5d) + 0.5d, 8.0d);
            float f24 = ((fPow * 0.4f) + 0.45f) * f8;
            this.paint.setColor(this.error ? -17978 : -3343873);
            this.paint.setAlpha((int) ((22.0f * fPow) + 10.0f));
            canvas2.drawCircle(fCos3, fSin3, 2.4f * f24, this.paint);
            this.paint.setAlpha((int) ((135.0f * fPow) + 35.0f));
            canvas2.drawCircle(fCos3, fSin3, f24, this.paint);
            if (fPow > 0.86f && i3 % 4 == 0) {
                this.paint.setStrokeWidth(f8 * 0.45f);
                float f25 = f24 * 2.2f;
                canvas2.drawLine(fCos3 - f25, fSin3, fCos3 + f25, fSin3, this.paint);
                canvas2 = canvas;
                canvas2.drawLine(fCos3, fSin3 - f25, fCos3, fSin3 + f25, this.paint);
            }
            i3++;
            f21 = 0.48f;
        }
        for (int i4 = 0; i4 < this.innerReflections.length; i4++) {
            canvas2.save();
            canvas2.rotate(this.petalAngles[i4] + this.petalRotation + (((float) Math.sin(this.phase + (i4 * f20))) * ((this.motion * f18) + f19)));
            this.paint.setShader(this.error ? this.redInnerReflection : this.innerReflections[i4]);
            this.paint.setAlpha((int) ((this.motion * 35.0f) + 150.0f));
            canvas2.drawCircle(0.0f, 0.0f, 1.0f, this.paint);
            canvas2.restore();
        }
        canvas2.restore();
        this.paint.setBlendMode(null);
        this.paint.setShader(this.error ? this.redPetal : this.rim);
        this.paint.setAlpha(150);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeWidth(f8);
        canvas2.drawCircle(0.0f, 0.0f, 0.993f, this.paint);
        this.paint.setShader(null);
        canvas2.restore();
        if (isAttachedToWindow() && getWindowVisibility() == 0) {
            postInvalidateOnAnimation();
        }
    }
}
