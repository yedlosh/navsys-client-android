package cz.iim.navsysclient.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import cz.iim.navsysclient.R;

public class AssignedColorView extends View {
    private final Paint paint = new Paint();
    private int assignedColor;

    public AssignedColorView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.AssignedColorView,
                0, 0);

        try {
            assignedColor = a.getInteger(R.styleable.AssignedColorView_assignedColor, 0);
        } finally {
            a.recycle();
        }
    }

    public void setAssignedColor(int assignedColor) {
        this.assignedColor = assignedColor;
        invalidate();
        requestLayout();
    }

    @Override
    public void onDraw(Canvas canvas) {
        paint.setColor(assignedColor);
        canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), paint);

    }
}
