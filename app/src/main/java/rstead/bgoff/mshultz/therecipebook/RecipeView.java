package rstead.bgoff.mshultz.therecipebook;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class RecipeView extends View {
    private String contentText = "Default"; // TODO: use a default from R.string...
    private Drawable srcImage;

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;

    public RecipeView(Context context) {
        super(context);
        init(null, 0);
    }

    public RecipeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public RecipeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.RecipeView, defStyle, 0);

        contentText = a.getString(
                R.styleable.RecipeView_content) == null ?  contentText : a.getString(R.styleable.RecipeView_content);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.

        if (a.hasValue(R.styleable.RecipeView_srcImage)) {
            srcImage = a.getDrawable(
                    R.styleable.RecipeView_srcImage);
            srcImage.setCallback(this);
        }

        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mTextWidth = mTextPaint.measureText(contentText);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        //if (srcImage != null) {
            srcImage.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            srcImage.draw(canvas);
        //}

        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(84f);
        // Draw the text.
        canvas.drawText(contentText,
                (paddingLeft + (contentWidth - mTextWidth)) / 2,
                (paddingTop + (contentHeight + mTextHeight)) / 2,
                mTextPaint);
    }

    /**
     * Gets the example string attribute value.
     *
     * @return The example string attribute value.
     */
    public String getcontent() {
        return contentText;
    }

    /**
     * Sets the view's example string attribute value. In the example view, this string
     * is the text to draw.
     *
     * @param content The example string attribute value to use.
     */
    public void setcontent(String content) {
        contentText = content;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     */
    public Drawable getsrcImage() {
        return srcImage;
    }

    /**
     * Sets the view's example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     *
     * @param srcImage The example drawable attribute value to use.
     */
    public void setsrcImage(Drawable srcImage) {
        this.srcImage = srcImage;
    }
}
