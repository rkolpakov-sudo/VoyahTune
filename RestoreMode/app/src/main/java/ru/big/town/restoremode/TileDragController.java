package ru.big.town.restoremode;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.DragEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
final class TileDragController {
    private static final long MOVE_MS = 200;
    private final Commit commit;
    private Item dragged;
    private final GridLayout grid;
    private final LayoutFactory layoutFactory;
    private int originalMinimumWidth;
    private Slot selected;
    private boolean settling;
    private final List<Item> items = new ArrayList();
    private final List<Slot> slots = new ArrayList();
    private final SlotsDrawable overlay = new SlotsDrawable();

    interface Commit {
        void move(int i, int i2);
    }

    interface LayoutFactory {
        GridLayout.LayoutParams create(int i, int i2, int i3, int i4);
    }

    private static final class Item {
        final int height;
        final int position;
        final View view;
        final int width;

        Item(View view, int i, int i2, int i3) {
            this.view = view;
            this.position = i;
            this.width = i2;
            this.height = i3;
        }
    }

    private static final class Slot {
        final int before;
        final List<RectF> bounds;
        final List<Item> order;
        final RectF target;

        Slot(int i, List<Item> list, List<RectF> list2, Item item) {
            this.before = i;
            this.order = list;
            this.bounds = list2;
            this.target = list2.get(list.indexOf(item));
        }
    }

    TileDragController(GridLayout gridLayout, LayoutFactory layoutFactory, Commit commit) {
        this.grid = gridLayout;
        this.layoutFactory = layoutFactory;
        this.commit = commit;
        gridLayout.setOnDragListener(new View.OnDragListener() { // from class: ru.big.town.restoremode.TileDragController$$ExternalSyntheticLambda1
            @Override // android.view.View.OnDragListener
            public final boolean onDrag(View view, DragEvent dragEvent) {
                return TileDragController.this.m1929lambda$new$0$rubigtownrestoremodeTileDragController(view, dragEvent);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$new$0$ru-big-town-restoremode-TileDragController, reason: not valid java name */
    /* synthetic */ boolean m1929lambda$new$0$rubigtownrestoremodeTileDragController(View view, DragEvent dragEvent) {
        return handle(dragEvent, dragEvent.getX(), dragEvent.getY());
    }

    void add(View view, int i, int i2, int i3) {
        this.items.add(new Item(view, i, i2, i3));
        view.setOnDragListener(new View.OnDragListener() { // from class: ru.big.town.restoremode.TileDragController$$ExternalSyntheticLambda0
            @Override // android.view.View.OnDragListener
            public final boolean onDrag(View view2, DragEvent dragEvent) {
                return TileDragController.this.m1928lambda$add$1$rubigtownrestoremodeTileDragController(view2, dragEvent);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$add$1$ru-big-town-restoremode-TileDragController, reason: not valid java name */
    /* synthetic */ boolean m1928lambda$add$1$rubigtownrestoremodeTileDragController(View view, DragEvent dragEvent) {
        return handle(dragEvent, view.getX() + dragEvent.getX(), view.getY() + dragEvent.getY());
    }

    boolean start(View view) {
        if (this.dragged == null && !this.settling) {
            for (Item item : this.items) {
                if (item.view == view) {
                    this.dragged = item;
                }
            }
            if (this.dragged == null || view.getWidth() == 0) {
                this.dragged = null;
            } else {
                this.originalMinimumWidth = this.grid.getMinimumWidth();
                buildSlots();
                if (!view.startDragAndDrop(null, new View.DragShadowBuilder(view), this, 0)) {
                    cancel();
                    return false;
                }
                view.setAlpha(0.0f);
                this.overlay.setBounds(0, 0, this.grid.getWidth(), this.grid.getHeight());
                this.grid.getOverlay().add(this.overlay);
                return true;
            }
        }
        return false;
    }

    private void buildSlots() {
        ArrayList arrayList = new ArrayList(this.items);
        arrayList.remove(this.dragged);
        int width = this.grid.getWidth();
        int i = 0;
        while (i <= arrayList.size()) {
            ArrayList<Item> arrayList2 = new ArrayList(arrayList);
            arrayList2.add(i, this.dragged);
            GridLayout gridLayout = new GridLayout(this.grid.getContext());
            gridLayout.setOrientation(this.grid.getOrientation());
            gridLayout.setRowCount(this.grid.getRowCount());
            gridLayout.setLayoutDirection(this.grid.getLayoutDirection());
            gridLayout.setPadding(this.grid.getPaddingLeft(), this.grid.getPaddingTop(), this.grid.getPaddingRight(), this.grid.getPaddingBottom());
            gridLayout.setMinimumWidth(this.grid.getWidth());
            ArrayList arrayList3 = new ArrayList();
            for (Item item : arrayList2) {
                int[] iArrPlace = TileGridPacking.place(arrayList3, this.grid.getRowCount(), item.width, item.height);
                gridLayout.addView(new View(this.grid.getContext()), this.layoutFactory.create(iArrPlace[0], iArrPlace[1], item.width, item.height));
            }
            gridLayout.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(this.grid.getHeight(), 1073741824));
            gridLayout.layout(0, 0, gridLayout.getMeasuredWidth(), gridLayout.getMeasuredHeight());
            ArrayList arrayList4 = new ArrayList();
            for (int i2 = 0; i2 < gridLayout.getChildCount(); i2++) {
                View childAt = gridLayout.getChildAt(i2);
                arrayList4.add(new RectF(childAt.getLeft(), childAt.getTop(), childAt.getRight(), childAt.getBottom()));
            }
            Slot slot = new Slot(i < arrayList.size() ? ((Item) arrayList.get(i)).position : Integer.MAX_VALUE, arrayList2, arrayList4, this.dragged);
            Iterator<Slot> it = this.slots.iterator();
            boolean z = false;
            while (it.hasNext()) {
                if (it.next().target.equals(slot.target)) {
                    z = true;
                }
            }
            if (!z) {
                this.slots.add(slot);
            }
            width = Math.max(width, gridLayout.getMeasuredWidth());
            i++;
        }
        this.grid.setMinimumWidth(width);
    }

    private boolean handle(DragEvent dragEvent, float f, float f2) {
        if (dragEvent.getLocalState() != this || this.dragged == null) {
            return false;
        }
        int action = dragEvent.getAction();
        if (action != 2) {
            if (action == 3) {
                if (this.settling) {
                    return true;
                }
                select(f, f2);
                settle(f, f2);
            } else if (action == 4 && !this.settling) {
                cancel();
            }
        } else if (!this.settling) {
            select(f, f2);
            scrollAtEdge(f);
        }
        return true;
    }

    private void select(float f, float f2) {
        Slot slot = null;
        float f3 = Float.MAX_VALUE;
        for (Slot slot2 : this.slots) {
            float fCenterX = f - slot2.target.centerX();
            float fCenterY = f2 - slot2.target.centerY();
            float f4 = (fCenterX * fCenterX) + (fCenterY * fCenterY);
            if (f4 < f3) {
                slot = slot2;
                f3 = f4;
            }
        }
        if (slot == null || slot == this.selected) {
            return;
        }
        this.selected = slot;
        for (int i = 0; i < this.selected.order.size(); i++) {
            Item item = this.selected.order.get(i);
            if (item != this.dragged) {
                animateTo(item.view, this.selected.bounds.get(i), MOVE_MS);
            }
        }
        this.overlay.invalidateSelf();
    }

    private void animateTo(View view, RectF rectF, long j) {
        view.setPivotX(0.0f);
        view.setPivotY(0.0f);
        view.animate().translationX(rectF.left - view.getLeft()).translationY(rectF.top - view.getTop()).scaleX(rectF.width() / view.getWidth()).scaleY(rectF.height() / view.getHeight()).setInterpolator(new DecelerateInterpolator()).setDuration(j).start();
    }

    private void settle(float f, float f2) {
        if (this.selected == null) {
            cancel();
            return;
        }
        this.settling = true;
        View view = this.dragged.view;
        view.setTranslationX((f - view.getLeft()) - (view.getWidth() / 2.0f));
        view.setTranslationY((f2 - view.getTop()) - (view.getHeight() / 2.0f));
        view.setAlpha(1.0f);
        view.animate().withEndAction(new Runnable() { // from class: ru.big.town.restoremode.TileDragController$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                TileDragController.this.m1930lambda$settle$2$rubigtownrestoremodeTileDragController();
            }
        });
        animateTo(view, this.selected.target, 260L);
    }

    /* JADX INFO: renamed from: lambda$settle$2$ru-big-town-restoremode-TileDragController, reason: not valid java name */
    /* synthetic */ void m1930lambda$settle$2$rubigtownrestoremodeTileDragController() {
        int i = this.dragged.position;
        int i2 = this.selected.before;
        cancel();
        this.commit.move(i, i2);
    }

    private void scrollAtEdge(float f) {
        if (this.grid.getParent() instanceof HorizontalScrollView) {
            HorizontalScrollView horizontalScrollView = (HorizontalScrollView) this.grid.getParent();
            float scrollX = f - horizontalScrollView.getScrollX();
            float f2 = this.grid.getResources().getDisplayMetrics().density * 48.0f;
            int iRound = Math.round(f2 / 3.0f);
            if (scrollX < f2) {
                horizontalScrollView.smoothScrollBy(-iRound, 0);
            } else if (scrollX > horizontalScrollView.getWidth() - f2) {
                horizontalScrollView.smoothScrollBy(iRound, 0);
            }
        }
    }

    void cancel() {
        this.grid.getOverlay().remove(this.overlay);
        if (this.dragged != null) {
            this.grid.setMinimumWidth(this.originalMinimumWidth);
        }
        for (Item item : this.items) {
            item.view.animate().withEndAction(null).cancel();
            item.view.setAlpha(1.0f);
            item.view.setTranslationX(0.0f);
            item.view.setTranslationY(0.0f);
            item.view.setScaleX(1.0f);
            item.view.setScaleY(1.0f);
        }
        this.dragged = null;
        this.selected = null;
        this.settling = false;
        this.slots.clear();
    }

    private final class SlotsDrawable extends Drawable {
        private final Paint paint;

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return -3;
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int i) {
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
        }

        private SlotsDrawable() {
            this.paint = new Paint(1);
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            float f = TileDragController.this.grid.getResources().getDisplayMetrics().density;
            for (Slot slot : TileDragController.this.slots) {
                this.paint.setStyle(Paint.Style.FILL);
                this.paint.setColor(slot == TileDragController.this.selected ? 1430896639 : 340377599);
                float f2 = 12.0f * f;
                canvas.drawRoundRect(slot.target, f2, f2, this.paint);
                this.paint.setStyle(Paint.Style.STROKE);
                this.paint.setStrokeWidth((slot == TileDragController.this.selected ? 3 : 1) * f);
                this.paint.setColor(slot == TileDragController.this.selected ? -9054721 : -2005543425);
                canvas.drawRoundRect(slot.target, f2, f2, this.paint);
            }
        }
    }
}
