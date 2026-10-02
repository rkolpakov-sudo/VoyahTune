package ru.big.town.restoremode;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.LruCache;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import ru.big.town.common.SuspensionWidgetProtocol;

/* JADX INFO: loaded from: classes2.dex */
final class SuspensionWidgetView extends View {
    private static final ExecutorService DECODER = Executors.newSingleThreadExecutor();
    private static final LruCache<Integer, Bitmap> FRAMES = new LruCache<Integer, Bitmap>(6291456) { // from class: ru.big.town.restoremode.SuspensionWidgetView.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.util.LruCache
        public int sizeOf(Integer num, Bitmap bitmap) {
            return bitmap.getByteCount();
        }
    };
    private static final String[] LEVELS = {"Самый низкий", "Низкий", "Средний", "Высокий"};
    private static final String[] MODES = {"Посадка", "Sport", "Прежний режим", "Outing"};
    private ValueAnimator animator;
    private boolean available;
    private Bitmap bitmap;
    private final RectF[] buttons;
    private boolean compact;
    private boolean decoding;
    private int direction;
    private int frame;
    private int height;
    private int lastHeight;
    private ArrayAdapter<String> levelAdapter;
    private AlertDialog levelDialog;
    private boolean longPressed;
    private String lowestBlockedReason;
    private String message;
    private String mode;
    private final Paint paint;
    private int pending;
    private final RectF rect;
    private final Selection selection;
    private float touchX;
    private float touchY;

    interface Selection {
        void select(int i);
    }

    private static int frameForHeight(int i) {
        switch (i) {
            case 1:
                return 80;
            case 2:
                return 76;
            case 3:
                return 70;
            case 4:
                return 66;
            case 5:
                return 60;
            case 6:
                return 50;
            case 7:
                return 40;
            case 8:
                return 30;
            default:
                return 20;
        }
    }

    SuspensionWidgetView(Context context, Selection selection) {
        super(context);
        this.paint = new Paint(1);
        this.rect = new RectF();
        this.buttons = new RectF[]{new RectF(), new RectF(), new RectF(), new RectF()};
        this.height = -1;
        this.direction = -1;
        this.pending = -1;
        this.frame = 60;
        this.lastHeight = -1;
        this.message = "Ожидание Native";
        this.mode = "Режим + подвеска";
        this.lowestBlockedReason = "Нет данных режима движения";
        this.selection = selection;
        setBackgroundResource(R.drawable.card_dark_ripple);
        setClickable(true);
        setFocusable(true);
        loadFrame(60);
    }

    void update(Bundle bundle) {
        String str;
        this.height = bundle.getInt(SuspensionWidgetProtocol.HEIGHT, -1);
        this.direction = bundle.getInt(SuspensionWidgetProtocol.DIRECTION, -1);
        this.pending = bundle.getInt(SuspensionWidgetProtocol.PENDING, -1);
        this.available = bundle.getBoolean(SuspensionWidgetProtocol.AVAILABLE, false);
        this.message = bundle.getString(SuspensionWidgetProtocol.MESSAGE, "");
        int i = bundle.getInt(SuspensionWidgetProtocol.DRIVE, -1);
        if (i == 4) {
            str = "Удобная посадка недоступна в Outing";
        } else {
            str = (i < 1 || i > 6) ? "Нет данных режима движения" : "";
        }
        String string = bundle.getString(SuspensionWidgetProtocol.LOWEST_BLOCKED_REASON, str);
        this.lowestBlockedReason = string;
        if (string == null) {
            this.lowestBlockedReason = "";
        }
        int i2 = this.height;
        if (i2 >= 1 && i2 <= 9 && i2 != this.lastHeight) {
            int iFrameForHeight = frameForHeight(i2);
            ValueAnimator valueAnimator = this.animator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            if (this.lastHeight < 0) {
                this.frame = iFrameForHeight;
                loadFrame(iFrameForHeight);
            } else {
                ValueAnimator valueAnimatorOfInt = ValueAnimator.ofInt(this.frame, iFrameForHeight);
                this.animator = valueAnimatorOfInt;
                valueAnimatorOfInt.setDuration(Math.max(300L, ((long) Math.abs(this.frame - iFrameForHeight)) * 16));
                this.animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: ru.big.town.restoremode.SuspensionWidgetView$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        this.f$0.m1924lambda$update$0$rubigtownrestoremodeSuspensionWidgetView(valueAnimator2);
                    }
                });
                this.animator.start();
            }
            this.lastHeight = this.height;
        }
        setContentDescription("Подвеска: " + levelText() + ". " + this.message + ". " + this.lowestBlockedReason);
        ArrayAdapter<String> arrayAdapter = this.levelAdapter;
        if (arrayAdapter != null) {
            arrayAdapter.notifyDataSetChanged();
        }
        invalidate();
    }

    /* JADX INFO: renamed from: lambda$update$0$ru-big-town-restoremode-SuspensionWidgetView, reason: not valid java name */
    /* synthetic */ void m1924lambda$update$0$rubigtownrestoremodeSuspensionWidgetView(ValueAnimator valueAnimator) {
        int iIntValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        this.frame = iIntValue;
        loadFrame(iIntValue);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean canSelect(int i) {
        if (!this.available || this.pending >= 0) {
            return false;
        }
        return i != 0 || this.lowestBlockedReason.isEmpty();
    }

    private void loadFrame(int i) {
        final int iMax = Math.max(20, Math.min(80, (i / 2) * 2));
        Bitmap bitmap = FRAMES.get(Integer.valueOf(iMax));
        if (bitmap != null) {
            this.bitmap = bitmap;
            invalidate();
        } else {
            if (this.decoding) {
                return;
            }
            this.decoding = true;
            final Context applicationContext = getContext().getApplicationContext();
            DECODER.execute(new Runnable() { // from class: ru.big.town.restoremode.SuspensionWidgetView$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1921lambda$loadFrame$2$rubigtownrestoremodeSuspensionWidgetView(applicationContext, iMax);
                }
            });
        }
    }

    /* JADX INFO: renamed from: lambda$loadFrame$2$ru-big-town-restoremode-SuspensionWidgetView, reason: not valid java name */
    /* synthetic */ void m1921lambda$loadFrame$2$rubigtownrestoremodeSuspensionWidgetView(Context context, final int i) {
        final Bitmap bitmapDecodeStream = null;
        try {
            InputStream inputStreamOpen = context.getAssets().open("suspension/body_" + i + ".png");
            try {
                bitmapDecodeStream = BitmapFactory.decodeStream(inputStreamOpen);
                if (bitmapDecodeStream != null) {
                    FRAMES.put(Integer.valueOf(i), bitmapDecodeStream);
                }
                if (inputStreamOpen != null) {
                    inputStreamOpen.close();
                }
            } catch (Throwable th) {
                if (inputStreamOpen != null) {
                    try {
                        inputStreamOpen.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (Exception unused) {
        }
        post(new Runnable() { // from class: ru.big.town.restoremode.SuspensionWidgetView$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1920lambda$loadFrame$1$rubigtownrestoremodeSuspensionWidgetView(bitmapDecodeStream, i);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$loadFrame$1$ru-big-town-restoremode-SuspensionWidgetView, reason: not valid java name */
    /* synthetic */ void m1920lambda$loadFrame$1$rubigtownrestoremodeSuspensionWidgetView(Bitmap bitmap, int i) {
        this.decoding = false;
        if (bitmap != null) {
            this.bitmap = bitmap;
        }
        invalidate();
        if (bitmap == null || i == (this.frame / 2) * 2 || !isAttachedToWindow()) {
            return;
        }
        loadFrame(this.frame);
    }

    private String levelText() {
        int i = this.height;
        if (i < 1 || i > 9) {
            return "Нет данных высоты";
        }
        if (i % 2 == 0) {
            int i2 = this.direction;
            if (i2 == 1) {
                return "Поднимается…";
            }
            return i2 == 2 ? "Опускается…" : "Регулировка…";
        }
        if (i == 1) {
            return "Максимальный";
        }
        if (i == 3) {
            return "Повышенный";
        }
        if (i != 5) {
            return i != 7 ? "Самый низкий" : "Низкий";
        }
        return "Средний";
    }

    private void text(Canvas canvas, String str, float f, float f2, float f3, int i, Paint.Align align) {
        this.paint.setColor(i);
        this.paint.setTextSize(f3);
        this.paint.setTextAlign(align);
        canvas.drawText(str, f, f2, this.paint);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r16v1 */
    /* JADX WARN: Type inference failed for: r16v2 */
    /* JADX WARN: Type inference failed for: r16v3 */
    /* JADX WARN: Type inference failed for: r16v4 */
    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        Canvas canvas2;
        float f;
        float f2;
        ?? CanSelect;
        String str;
        SuspensionWidgetView suspensionWidgetView = this;
        super.onDraw(canvas);
        float width = suspensionWidgetView.getWidth();
        float height = suspensionWidgetView.getHeight();
        float f3 = suspensionWidgetView.getResources().getDisplayMetrics().density;
        int i = 1;
        boolean z = width < 350.0f * f3 || height < 230.0f * f3;
        suspensionWidgetView.compact = z;
        float f4 = f3 * 12.0f;
        float fMin = z ? Math.min(18.0f * f3, Math.max(11.0f * f3, width / 17.0f)) : 26.0f * f3;
        suspensionWidgetView.paint.setTextSize(fMin);
        float f5 = width - (f4 * 2.0f);
        float f6 = f4 + fMin;
        suspensionWidgetView.text(canvas, TextUtils.ellipsize("Подвеска · " + suspensionWidgetView.levelText(), new TextPaint(suspensionWidgetView.paint), f5, TextUtils.TruncateAt.END).toString(), f4, f6, fMin, -1118482, Paint.Align.LEFT);
        float f7 = height - f4;
        boolean z2 = suspensionWidgetView.compact;
        float f8 = f7 - ((z2 ? 28.0f : 50.0f) * f3);
        float f9 = f8 - (10.0f * f3);
        if (z2) {
            canvas2 = canvas;
            f = f8;
            f2 = f4;
        } else {
            if (suspensionWidgetView.pending >= 0) {
                str = "Ожидаем: " + LEVELS[suspensionWidgetView.pending];
            } else if (suspensionWidgetView.lowestBlockedReason.isEmpty() || !suspensionWidgetView.available) {
                str = suspensionWidgetView.message.isEmpty() ? suspensionWidgetView.mode : suspensionWidgetView.message;
            } else {
                str = suspensionWidgetView.lowestBlockedReason;
            }
            float f10 = 16.0f * f3;
            suspensionWidgetView.paint.setTextSize(f10);
            f = f8;
            canvas2 = canvas;
            suspensionWidgetView.text(canvas2, TextUtils.ellipsize(str, new TextPaint(suspensionWidgetView.paint), f5, TextUtils.TruncateAt.END).toString(), f4, f6 + (24.0f * f3), f10, -5326906, Paint.Align.LEFT);
            f2 = f4;
            float f11 = f6 + (30.0f * f3);
            Bitmap bitmap = suspensionWidgetView.bitmap;
            if (bitmap != null && f9 > f11) {
                float fMin2 = Math.min(f5 / bitmap.getWidth(), (f9 - f11) / suspensionWidgetView.bitmap.getHeight());
                float width2 = suspensionWidgetView.bitmap.getWidth() * fMin2;
                suspensionWidgetView.rect.set((width - width2) / 2.0f, f11, (width + width2) / 2.0f, (suspensionWidgetView.bitmap.getHeight() * fMin2) + f11);
                suspensionWidgetView.paint.setAlpha(suspensionWidgetView.height < 1 ? 100 : 255);
                canvas2.drawBitmap(suspensionWidgetView.bitmap, (Rect) null, suspensionWidgetView.rect, suspensionWidgetView.paint);
                suspensionWidgetView.paint.setAlpha(255);
            }
        }
        for (int i2 = 0; i2 < 4; i2++) {
            suspensionWidgetView.buttons[i2].setEmpty();
        }
        int i3 = suspensionWidgetView.compact ? 1 : 4;
        float f12 = (f5 - (((i3 - 1) * 5) * f3)) / i3;
        int i4 = 0;
        while (i4 < i3) {
            RectF rectF = suspensionWidgetView.buttons[i4];
            float f13 = f2 + (i4 * ((5.0f * f3) + f12));
            rectF.set(f13, f, f13 + f12, f7);
            boolean z3 = suspensionWidgetView.compact;
            int i5 = (z3 || suspensionWidgetView.height != new int[]{9, 7, 5, i}[i4]) ? 0 : i;
            if (z3) {
                CanSelect = (!suspensionWidgetView.available || suspensionWidgetView.pending >= 0) ? 0 : i;
            } else {
                CanSelect = suspensionWidgetView.canSelect(i4);
            }
            suspensionWidgetView.paint.setColor(CanSelect == 0 ? -13091255 : i5 != 0 ? -13729591 : -12299673);
            float f14 = 8.0f * f3;
            canvas2.drawRoundRect(rectF, f14, f14, suspensionWidgetView.paint);
            String str2 = suspensionWidgetView.compact ? "Выбрать уровень" : LEVELS[i4];
            float fCenterX = rectF.centerX();
            float f15 = rectF.top;
            boolean z4 = suspensionWidgetView.compact;
            int i6 = i4;
            suspensionWidgetView.text(canvas2, str2, fCenterX, f15 + ((z4 ? 19 : 21) * f3), z4 ? f2 : Math.min(f3 * 17.0f, f12 / 8.5f), CanSelect != 0 ? -1118482 : -8024936, Paint.Align.CENTER);
            if (!suspensionWidgetView.compact) {
                suspensionWidgetView.text(canvas, (i6 != 0 || suspensionWidgetView.lowestBlockedReason.isEmpty()) ? MODES[i6] : "Недоступно", rectF.centerX(), rectF.bottom - (f3 * 9.0f), Math.min(13.0f * f3, f12 / 9.0f), CanSelect != 0 ? -3352860 : -8024936, Paint.Align.CENTER);
            }
            i4 = i6 + 1;
            i = 1;
            suspensionWidgetView = this;
            canvas2 = canvas;
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0) {
            this.longPressed = false;
        }
        this.touchX = motionEvent.getX();
        this.touchY = motionEvent.getY();
        return super.onTouchEvent(motionEvent);
    }

    @Override // android.view.View
    public boolean performLongClick() {
        this.longPressed = true;
        return super.performLongClick();
    }

    @Override // android.view.View
    public boolean performClick() {
        super.performClick();
        if (!this.longPressed && this.available && this.pending < 0) {
            int i = 0;
            if (this.compact) {
                final String[] strArr = new String[4];
                while (i < 4) {
                    strArr[i] = LEVELS[i] + " · " + MODES[i];
                    i++;
                }
                if (this.levelDialog != null) {
                    return true;
                }
                this.levelAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, strArr) { // from class: ru.big.town.restoremode.SuspensionWidgetView.2
                    @Override // android.widget.BaseAdapter, android.widget.ListAdapter
                    public boolean areAllItemsEnabled() {
                        return false;
                    }

                    @Override // android.widget.BaseAdapter, android.widget.ListAdapter
                    public boolean isEnabled(int i2) {
                        return SuspensionWidgetView.this.canSelect(i2);
                    }

                    @Override // android.widget.ArrayAdapter, android.widget.Adapter
                    public View getView(int i2, View view, ViewGroup viewGroup) {
                        TextView textView = (TextView) super.getView(i2, view, viewGroup);
                        textView.setText(strArr[i2] + ((i2 != 0 || SuspensionWidgetView.this.lowestBlockedReason.isEmpty()) ? "" : "\n" + SuspensionWidgetView.this.lowestBlockedReason));
                        textView.setEnabled(isEnabled(i2));
                        textView.setAlpha(isEnabled(i2) ? 1.0f : 0.45f);
                        return textView;
                    }
                };
                AlertDialog alertDialogCreate = new AlertDialog.Builder(getContext()).setTitle("Подвеска и режим движения").setAdapter(this.levelAdapter, new DialogInterface.OnClickListener() { // from class: ru.big.town.restoremode.SuspensionWidgetView$$ExternalSyntheticLambda2
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i2) {
                        this.f$0.m1922xf18bdcd7(dialogInterface, i2);
                    }
                }).create();
                this.levelDialog = alertDialogCreate;
                alertDialogCreate.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: ru.big.town.restoremode.SuspensionWidgetView$$ExternalSyntheticLambda3
                    @Override // android.content.DialogInterface.OnDismissListener
                    public final void onDismiss(DialogInterface dialogInterface) {
                        this.f$0.m1923xab036a76(dialogInterface);
                    }
                });
                this.levelDialog.show();
            } else {
                while (i < 4) {
                    if (this.buttons[i].contains(this.touchX, this.touchY) && canSelect(i)) {
                        this.selection.select(i);
                    }
                    i++;
                }
            }
        }
        return true;
    }

    /* JADX INFO: renamed from: lambda$performClick$3$ru-big-town-restoremode-SuspensionWidgetView, reason: not valid java name */
    /* synthetic */ void m1922xf18bdcd7(DialogInterface dialogInterface, int i) {
        if (canSelect(i)) {
            this.selection.select(i);
        }
    }

    /* JADX INFO: renamed from: lambda$performClick$4$ru-big-town-restoremode-SuspensionWidgetView, reason: not valid java name */
    /* synthetic */ void m1923xab036a76(DialogInterface dialogInterface) {
        this.levelDialog = null;
        this.levelAdapter = null;
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        ValueAnimator valueAnimator = this.animator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        AlertDialog alertDialog = this.levelDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        super.onDetachedFromWindow();
    }
}
