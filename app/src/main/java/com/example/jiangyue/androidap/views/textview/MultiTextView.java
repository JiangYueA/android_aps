package com.example.jiangyue.androidap.views.textview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ReplacementSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangyue on 16/6/15.
 * 多行textview
 */
public class MultiTextView extends TextView {

    // some default params
    private static int DEFAULT_ABSOLUTE_TEXT_SIZE;
    private static float DEFAULT_RELATIVE_TEXT_SIZE = 1;

    private List<Piece> mPieces;
    private Context mContext;

    /**
     * Create a new instance of a this class
     *
     * @param context
     */
    public MultiTextView(Context context) {
        super(context);
        init(context);
    }

    public MultiTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MultiTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mPieces = new ArrayList<>();
        MultiTextView.DEFAULT_ABSOLUTE_TEXT_SIZE = (int) getTextSize();
    }

    /**
     * Use this method to add a {@link MultiTextView.Piece} to a MultiTextView.
     * Each {@link MultiTextView.Piece } is added sequentially, so the
     * order you call this method matters.
     *
     * @param aPiece the Piece
     */
    public void addPiece(Piece aPiece) {
        mPieces.add(aPiece);
    }

    /**
     * Adds a Piece at this specific location. The underlying data structure is a
     * {@link List}, so expect the same type of behaviour.
     *
     * @param aPiece   the Piece to add.
     * @param location the index at which to add.
     */
    public void addPiece(Piece aPiece, int location) {
        mPieces.add(location, aPiece);
    }

    /**
     * Replaces the Piece at the specified location with this new Piece. The underlying data
     * structure is a {@link List}, so expect the same type of behaviour.
     *
     * @param newPiece the Piece to insert.
     * @param location the index at which to insert.
     */
    public void replacePieceAt(int location, Piece newPiece) {
        mPieces.set(location, newPiece);
    }

    /**
     * Removes the Piece at this specified location. The underlying data structure is a
     * {@link List}, so expect the same type of behaviour.
     *
     * @param location the index of the Piece to remove
     */
    public void removePiece(int location) {
        mPieces.remove(location);
    }

    /**
     * Get a specific {@link MultiTextView.Piece} in position index.
     *
     * @param location position of Piece (0 based)
     * @return Piece o null if invalid index
     */
    public Piece getPiece(int location) {
        if (location >= 0 && location < mPieces.size()) {
            return mPieces.get(location);
        }

        return null;
    }

    /**
     * Call this method when you're done adding {@link MultiTextView.Piece}s
     * and want this TextView to display the final, styled version of it's String contents.
     * <p/>
     * You MUST also call this method whenever you make a modification to the text of a Piece that
     * has already been displayed.
     */
    public void display() {

        // generate the final string based on the pieces
        StringBuilder builder = new StringBuilder();
        for (Piece aPiece : mPieces) {
            if (aPiece.leftIcon != -1) {
                builder.append("&");
            }
            builder.append(aPiece.text);
        }

        // apply spans
        int cursor = 0;
        SpannableString finalString = new SpannableString(builder.toString());
        for (Piece aPiece : mPieces) {
            if (aPiece.leftIcon != -1) {
                applySpannablesTo(aPiece, finalString, 1 + cursor, 1 + cursor + aPiece.text.length());
            } else {
                applySpannablesTo(aPiece, finalString, cursor, cursor + aPiece.text.length());
            }
            cursor += aPiece.text.length();
        }

        // set the styled text
        setText(finalString);
    }

    private void applySpannablesTo(Piece aPiece, SpannableString finalString, int start, int end) {

        if (aPiece.subscript) {
            finalString.setSpan(new SubscriptSpan(), start, end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (aPiece.superscript) {
            finalString.setSpan(new SuperscriptSpan(), start, end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (aPiece.strike) {
            finalString.setSpan(new StrikethroughSpan(), start, end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (aPiece.underline) {
            finalString.setSpan(new UnderlineSpan(), start, end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        // style
        finalString.setSpan(new StyleSpan(aPiece.style), start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // absolute text size
        finalString.setSpan(new AbsoluteSizeSpan(aPiece.textSize), start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // relative text size
        finalString.setSpan(new RelativeSizeSpan(aPiece.textSizeRelative), start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // text color
        finalString.setSpan(new ForegroundColorSpan(aPiece.textColor), start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //set delete line
        if (aPiece.deleteLine) {
            finalString.setSpan(new StrikethroughSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        // background color
        if (aPiece.backgroundColor != -1) {
            finalString.setSpan(new BackgroundColorSpan(aPiece.backgroundColor), start, end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        //set icon
        if (aPiece.leftIcon != -1) {
            finalString.setSpan(new MultiImageSpan(mContext.getResources(), aPiece.leftIcon, aPiece.leftSize), start - 1, start, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    /**
     * Resets the styling of this view and sets it's content to an empty String.
     */
    public void reset() {
        mPieces = new ArrayList<>();
        setText("");
    }

    /**
     * Change text color of all pieces of textview.
     */
    public void changeTextColor(int textColor) {
        for (Piece mPiece : mPieces) {
            mPiece.setTextColor(textColor);
        }
        display();
    }

    /**
     * A Piece represents a part of the text that you want to style. Say for example you want this
     * MultiTextView to display "Hello World" such that "Hello" is displayed in Bold and "World" is
     * displayed in Italics. Since these have different styles, they are both separate Pieces.
     * <p/>
     * You create a Piece by using it's {@link MultiTextView.Piece.Builder}
     */
    public static class Piece {

        private String text;
        private int textColor;
        private final int textSize;
        private final int backgroundColor;
        private final float textSizeRelative;
        private final int style;
        private final boolean underline;
        private final boolean superscript;
        private final boolean strike;
        private final boolean subscript;
        private final boolean deleteLine;
        private int leftIcon;
        private int leftSize;

        public Piece(Builder builder) {
            this.text = builder.text;
            this.textSize = builder.textSize;
            this.textColor = builder.textColor;
            this.backgroundColor = builder.backgroundColor;
            this.textSizeRelative = builder.textSizeRelative;
            this.style = builder.style;
            this.underline = builder.underline;
            this.superscript = builder.superscript;
            this.subscript = builder.subscript;
            this.deleteLine = builder.deleteLine;
            this.strike = builder.strike;
            this.leftIcon = builder.leftIcon;
            this.leftSize = builder.leftSize;
        }

        /**
         * Sets the text of this Piece. If you're creating a new Piece, you should do so using it's
         * {@link MultiTextView.Piece.Builder}.
         * <p/>
         * Use this method if you want to modify the text of an existing Piece that is already
         * displayed. After doing so, you MUST call {@code display()} for the changes to show up.
         *
         * @param text the text to display
         */
        public void setText(String text) {
            this.text = text;
        }


        /**
         * Sets the text color of this Piece. If you're creating a new Piece, you should do so using it's
         * {@link MultiTextView.Piece.Builder}.
         * <p/>
         * Use this method if you want to change the text color of an existing Piece that is already
         * displayed. After doing so, you MUST call {@code display()} for the changes to show up.
         *
         * @param textColor of text (it is NOT android Color resources ID, use getResources().getColor(R.color.colorId) for it)
         */
        public void setTextColor(int textColor) {
            this.textColor = textColor;
        }

        /**
         * Builder of Pieces
         */
        public static class Builder {

            // required
            private final String text;

            // optional
            private int textSize = DEFAULT_ABSOLUTE_TEXT_SIZE;
            private int textColor = Color.BLACK;
            private int backgroundColor = -1;
            private float textSizeRelative = DEFAULT_RELATIVE_TEXT_SIZE;
            private int style = Typeface.NORMAL;
            private boolean underline = false;
            private boolean strike = false;
            private boolean superscript = false;
            private boolean subscript = false;
            private boolean deleteLine = false;
            private int leftIcon = -1;
            private int leftSize = -1;

            /**
             * Creates a new Builder for this Piece.
             *
             * @param text the text of this Piece
             */
            public Builder(String text) {
                if (!TextUtils.isEmpty(text)) {
                    this.text = text;
                } else {
                    this.text = " ";
                }
            }

            /**
             * Creates a new Builder for this Piece.
             *
             * @param text
             * @param length
             */
            public Builder(String text, int length) {
                if (!TextUtils.isEmpty(text)) {
                    this.text = text.length() > length ? text.substring(0, length) + "..." : text;
                } else {
                    this.text = " ";
                }
            }

            /**
             * set leftIocn
             *
             * @param leftIcon
             */
            public Builder setLeftIcon(int leftIcon) {
                this.leftIcon = leftIcon;
                return this;
            }

            /**
             * set leftIocn size
             *
             * @param leftSize
             * @return
             */
            public Builder setLeftSize(int leftSize) {
                this.leftSize = leftSize;
                return this;
            }

            /**
             * Sets the absolute text size.
             *
             * @param textSize text size in pixels
             * @return a Builder
             */
            public Builder textSize(int textSize) {
                this.textSize = textSize;
                return this;
            }

            /**
             * Sets the text color.
             *
             * @param textColor the color
             * @return a Builder
             */
            public Builder textColor(int textColor) {
                this.textColor = textColor;
                return this;
            }

            /**
             * Sets the background color.
             *
             * @param backgroundColor the color
             * @return a Builder
             */
            public Builder backgroundColor(int backgroundColor) {
                this.backgroundColor = backgroundColor;
                return this;
            }

            /**
             * Sets the relative text size.
             *
             * @param textSizeRelative relative text size
             * @return a Builder
             */
            public Builder textSizeRelative(float textSizeRelative) {
                this.textSizeRelative = textSizeRelative;
                return this;
            }

            /**
             * Sets a style to this Piece.
             *
             * @param style see {@link Typeface}
             * @return a Builder
             */
            public Builder style(int style) {
                this.style = style;
                return this;
            }

            /**
             * Underlines this Piece.
             *
             * @return a Builder
             */
            public Builder underline() {
                this.underline = true;
                return this;
            }

            /**
             * Strikes this Piece.
             *
             * @return a Builder
             */
            public Builder strike() {
                this.strike = true;
                return this;
            }

            /**
             * Sets this Piece as a superscript.
             *
             * @return a Builder
             */
            public Builder superscript() {
                this.superscript = true;
                return this;
            }

            /**
             * Sets this Piece as a subscript.
             *
             * @return a Builder
             */
            public Builder subscript() {
                this.subscript = true;
                return this;
            }

            /**
             * set delete line
             *
             * @return
             */
            public Builder deleteLine() {
                this.deleteLine = true;
                return this;
            }

            /**
             * Creates a {@link MultiTextView.Piece} with the customized
             * parameters.
             *
             * @return a Piece
             */
            public Piece build() {
                return new Piece(this);
            }
        }
    }

    /**
     * set text icon
     */
    public static class MultiImageSpan extends ReplacementSpan {
        private Drawable mDrawable;
        private Rect rect;
        int paddin;
        private WeakReference<Drawable> mDrawableRef;

        private MultiImageSpan(Resources resources, int res, int size) {
            this.mDrawable = resources.getDrawable(res);
            this.paddin = 0;
            int width = size;
            int height = size;
            this.mDrawable.setBounds(0, 0, width > 0 ? width - paddin : 0, height > 0 ? height - paddin : 0);
            this.rect = new Rect(0, 0, width > 0 ? width : 0, height > 0 ? height : 0);
        }

        public Drawable getDrawable() {
            return this.mDrawable;
        }

        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            Drawable d = this.getCachedDrawable();
            if (fm != null) {
                fm.ascent = -rect.bottom;
                fm.descent = 0;
                fm.top = fm.ascent;
                fm.bottom = 0;
            }

            return rect.right;
        }

        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            Drawable b = this.getCachedDrawable();
            canvas.save();
            int transY = bottom - rect.bottom;
            canvas.translate(x + paddin / 2, (float) transY + paddin / 2);
            b.draw(canvas);
            canvas.restore();
        }

        private Drawable getCachedDrawable() {
            WeakReference wr = this.mDrawableRef;
            Drawable d = null;
            if (wr != null) {
                d = (Drawable) wr.get();
            }

            if (d == null) {
                d = this.getDrawable();
                this.mDrawableRef = new WeakReference(d);
            }

            return d;
        }
    }
}
