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
    private int recipeKey;
    private boolean isWeb;
    private String link;

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
                R.styleable.RecipeView_content) == null ? contentText : a.getString(R.styleable.RecipeView_content);
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

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        srcImage.setBounds(paddingLeft, paddingTop,
                paddingLeft + contentWidth, paddingTop + contentHeight);
        srcImage.draw(canvas);

        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(84f);
        // Draw the text.
        canvas.drawText(contentText,
                (paddingLeft + (contentWidth - mTextWidth)) / 2,
                (paddingTop + (contentHeight + mTextHeight)) / 2,
                mTextPaint);
    }

    public String getContent() {
        return contentText;
    }

    public void setContent(String content) {
        contentText = content;
        invalidateTextPaintAndMeasurements();
    }

    public void setRecipeKey(int id) {
        recipeKey = id;
    }

    public int getRecipeKey() {
        return recipeKey;
    }

    public Drawable getSrcImage() {
        return srcImage;
    }

    public void setsrcImage(Drawable srcImage) {
        this.srcImage = srcImage;
    }

    public void setIsWeb(boolean val){
        isWeb = val;
    }

    public boolean isWeb(){
        return isWeb;
    }

    public String getLink(){
        return link;
    }

    public void setLink(String input){
        link = input;
    }

}
