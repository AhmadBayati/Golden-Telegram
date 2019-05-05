package com.hanista.mobogram.ui.Components;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build.VERSION;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.Emoji;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.p006g.EmojiView;
import com.hanista.mobogram.mobo.p006g.EmojiViewInf;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.tgnet.TLRPC.Document;
import com.hanista.mobogram.tgnet.TLRPC.StickerSetCovered;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.EmojiView.Listener;
import com.hanista.mobogram.ui.Components.SizeNotifierFrameLayoutPhoto.SizeNotifierFrameLayoutPhotoDelegate;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PhotoViewerCaptionEnterView extends FrameLayout implements NotificationCenterDelegate, SizeNotifierFrameLayoutPhotoDelegate {
    private int audioInterfaceState;
    private final int captionMaxLength;
    private ActionMode currentActionMode;
    private PhotoViewerCaptionEnterViewDelegate delegate;
    private ImageView emojiButton;
    private int emojiPadding;
    private EmojiViewInf emojiView;
    private boolean innerTextChange;
    private int keyboardHeight;
    private int keyboardHeightLand;
    private boolean keyboardVisible;
    private int lastSizeChangeValue1;
    private boolean lastSizeChangeValue2;
    private EditText messageEditText;
    private AnimatorSet runningAnimation;
    private AnimatorSet runningAnimation2;
    private ObjectAnimator runningAnimationAudio;
    private int runningAnimationType;
    private SizeNotifierFrameLayoutPhoto sizeNotifierLayout;
    private View windowView;

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoViewerCaptionEnterView.1 */
    class C14311 implements OnClickListener {
        C14311() {
        }

        public void onClick(View view) {
            if (PhotoViewerCaptionEnterView.this.isPopupShowing()) {
                PhotoViewerCaptionEnterView.this.openKeyboardInternal();
            } else {
                PhotoViewerCaptionEnterView.this.showPopup(1);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoViewerCaptionEnterView.2 */
    class C14322 extends EditText {
        C14322(Context context) {
            super(context);
        }

        protected void onMeasure(int i, int i2) {
            try {
                super.onMeasure(i, i2);
            } catch (Throwable e) {
                setMeasuredDimension(MeasureSpec.getSize(i), AndroidUtilities.dp(51.0f));
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoViewerCaptionEnterView.3 */
    class C14333 implements Callback {
        C14333() {
        }

        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            return false;
        }

        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            PhotoViewerCaptionEnterView.this.currentActionMode = actionMode;
            return true;
        }

        public void onDestroyActionMode(ActionMode actionMode) {
            if (PhotoViewerCaptionEnterView.this.currentActionMode == actionMode) {
                PhotoViewerCaptionEnterView.this.currentActionMode = null;
            }
        }

        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            if (VERSION.SDK_INT >= 23) {
                PhotoViewerCaptionEnterView.this.fixActionMode(actionMode);
            }
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoViewerCaptionEnterView.4 */
    class C14344 implements Callback {
        C14344() {
        }

        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            return false;
        }

        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            PhotoViewerCaptionEnterView.this.currentActionMode = actionMode;
            return true;
        }

        public void onDestroyActionMode(ActionMode actionMode) {
            if (PhotoViewerCaptionEnterView.this.currentActionMode == actionMode) {
                PhotoViewerCaptionEnterView.this.currentActionMode = null;
            }
        }

        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            if (VERSION.SDK_INT >= 23) {
                PhotoViewerCaptionEnterView.this.fixActionMode(actionMode);
            }
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoViewerCaptionEnterView.5 */
    class C14355 implements OnKeyListener {
        C14355() {
        }

        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if (i == 4) {
                if (PhotoViewerCaptionEnterView.this.windowView != null && PhotoViewerCaptionEnterView.this.hideActionMode()) {
                    return true;
                }
                if (!PhotoViewerCaptionEnterView.this.keyboardVisible && PhotoViewerCaptionEnterView.this.isPopupShowing()) {
                    if (keyEvent.getAction() != 1) {
                        return true;
                    }
                    PhotoViewerCaptionEnterView.this.showPopup(0);
                    return true;
                }
            }
            return false;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoViewerCaptionEnterView.6 */
    class C14366 implements OnClickListener {
        C14366() {
        }

        public void onClick(View view) {
            if (PhotoViewerCaptionEnterView.this.isPopupShowing()) {
                PhotoViewerCaptionEnterView.this.showPopup(AndroidUtilities.usingHardwareInput ? 0 : 2);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoViewerCaptionEnterView.7 */
    class C14377 implements TextWatcher {
        boolean processChange;

        C14377() {
            this.processChange = false;
        }

        public void afterTextChanged(Editable editable) {
            if (!PhotoViewerCaptionEnterView.this.innerTextChange && this.processChange) {
                ImageSpan[] imageSpanArr = (ImageSpan[]) editable.getSpans(0, editable.length(), ImageSpan.class);
                for (Object removeSpan : imageSpanArr) {
                    editable.removeSpan(removeSpan);
                }
                Emoji.replaceEmoji(editable, PhotoViewerCaptionEnterView.this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                this.processChange = false;
            }
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (!PhotoViewerCaptionEnterView.this.innerTextChange) {
                if (PhotoViewerCaptionEnterView.this.delegate != null) {
                    PhotoViewerCaptionEnterView.this.delegate.onTextChanged(charSequence);
                }
                if (i2 != i3 && i3 - i2 > 1) {
                    this.processChange = true;
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoViewerCaptionEnterView.8 */
    class C14388 implements Runnable {
        C14388() {
        }

        public void run() {
            if (PhotoViewerCaptionEnterView.this.messageEditText != null) {
                try {
                    PhotoViewerCaptionEnterView.this.messageEditText.requestFocus();
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoViewerCaptionEnterView.9 */
    class C14399 implements Listener {
        C14399() {
        }

        public boolean onBackspace() {
            if (PhotoViewerCaptionEnterView.this.messageEditText.length() == 0) {
                return false;
            }
            PhotoViewerCaptionEnterView.this.messageEditText.dispatchKeyEvent(new KeyEvent(0, 67));
            return true;
        }

        public void onClearEmojiRecent() {
        }

        public void onEmojiSelected(String str) {
            if (PhotoViewerCaptionEnterView.this.messageEditText.length() + str.length() <= ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                int selectionEnd = PhotoViewerCaptionEnterView.this.messageEditText.getSelectionEnd();
                if (selectionEnd < 0) {
                    selectionEnd = 0;
                }
                try {
                    PhotoViewerCaptionEnterView.this.innerTextChange = true;
                    CharSequence replaceEmoji = Emoji.replaceEmoji(str, PhotoViewerCaptionEnterView.this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                    PhotoViewerCaptionEnterView.this.messageEditText.setText(PhotoViewerCaptionEnterView.this.messageEditText.getText().insert(selectionEnd, replaceEmoji));
                    selectionEnd += replaceEmoji.length();
                    PhotoViewerCaptionEnterView.this.messageEditText.setSelection(selectionEnd, selectionEnd);
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                } finally {
                    PhotoViewerCaptionEnterView.this.innerTextChange = false;
                }
            }
        }

        public void onGifSelected(Document document) {
        }

        public void onGifTab(boolean z) {
        }

        public void onShowStickerSet(StickerSetCovered stickerSetCovered) {
        }

        public void onStickerSelected(Document document) {
        }

        public void onStickerSetAdd(StickerSetCovered stickerSetCovered) {
        }

        public void onStickerSetRemove(StickerSetCovered stickerSetCovered) {
        }

        public void onStickersSettingsClick() {
        }

        public void onStickersTab(boolean z) {
        }
    }

    public interface PhotoViewerCaptionEnterViewDelegate {
        void onCaptionEnter();

        void onTextChanged(CharSequence charSequence);

        void onWindowSizeChanged(int i);
    }

    public PhotoViewerCaptionEnterView(Context context, SizeNotifierFrameLayoutPhoto sizeNotifierFrameLayoutPhoto, View view) {
        super(context);
        this.captionMaxLength = ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION;
        setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.windowView = view;
        this.sizeNotifierLayout = sizeNotifierFrameLayoutPhoto;
        View linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 51, 2.0f, 0.0f, 0.0f, 0.0f));
        View frameLayout = new FrameLayout(context);
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(0, -2, (float) DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        this.emojiButton = new ImageView(context);
        this.emojiButton.setImageResource(C0338R.drawable.ic_smile_w);
        this.emojiButton.setScaleType(ScaleType.CENTER_INSIDE);
        this.emojiButton.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT), 0, 0);
        frameLayout.addView(this.emojiButton, LayoutHelper.createFrame(48, 48, 83));
        this.emojiButton.setOnClickListener(new C14311());
        this.messageEditText = new C14322(context);
        if (VERSION.SDK_INT >= 23 && this.windowView != null) {
            this.messageEditText.setCustomSelectionActionModeCallback(new C14333());
            this.messageEditText.setCustomInsertionActionModeCallback(new C14344());
        }
        this.messageEditText.setTypeface(FontUtil.m1176a().m1161d());
        this.messageEditText.setHint(LocaleController.getString("AddCaption", C0338R.string.AddCaption));
        this.messageEditText.setImeOptions(268435456);
        this.messageEditText.setInputType(this.messageEditText.getInputType() | MessagesController.UPDATE_MASK_CHAT_ADMINS);
        this.messageEditText.setMaxLines(4);
        this.messageEditText.setHorizontallyScrolling(false);
        this.messageEditText.setTextSize(1, 18.0f);
        this.messageEditText.setGravity(80);
        this.messageEditText.setPadding(0, AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(12.0f));
        this.messageEditText.setBackgroundDrawable(null);
        AndroidUtilities.clearCursorDrawable(this.messageEditText);
        this.messageEditText.setTextColor(-1);
        this.messageEditText.setHintTextColor(-1291845633);
        this.messageEditText.setFilters(new InputFilter[]{new LengthFilter(ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION)});
        frameLayout.addView(this.messageEditText, LayoutHelper.createFrame(-1, -2.0f, 83, 52.0f, 0.0f, 6.0f, 0.0f));
        this.messageEditText.setOnKeyListener(new C14355());
        this.messageEditText.setOnClickListener(new C14366());
        this.messageEditText.addTextChangedListener(new C14377());
    }

    private void fixActionMode(ActionMode actionMode) {
        try {
            Class cls = Class.forName("com.android.internal.view.FloatingActionMode");
            Field declaredField = cls.getDeclaredField("mFloatingToolbar");
            declaredField.setAccessible(true);
            Object obj = declaredField.get(actionMode);
            Class cls2 = Class.forName("com.android.internal.widget.FloatingToolbar");
            Field declaredField2 = cls2.getDeclaredField("mPopup");
            Field declaredField3 = cls2.getDeclaredField("mWidthChanged");
            declaredField2.setAccessible(true);
            declaredField3.setAccessible(true);
            Object obj2 = declaredField2.get(obj);
            declaredField2 = Class.forName("com.android.internal.widget.FloatingToolbar$FloatingToolbarPopup").getDeclaredField("mParent");
            declaredField2.setAccessible(true);
            if (((View) declaredField2.get(obj2)) != this.windowView) {
                declaredField2.set(obj2, this.windowView);
                Method declaredMethod = cls.getDeclaredMethod("updateViewLocationInWindow", new Class[0]);
                declaredMethod.setAccessible(true);
                declaredMethod.invoke(actionMode, new Object[0]);
            }
        } catch (Throwable th) {
            FileLog.m18e("tmessages", th);
        }
    }

    private void onWindowSizeChanged() {
        int height = this.sizeNotifierLayout.getHeight();
        if (!this.keyboardVisible) {
            height -= this.emojiPadding;
        }
        if (this.delegate != null) {
            this.delegate.onWindowSizeChanged(height);
        }
    }

    private void openKeyboardInternal() {
        showPopup(AndroidUtilities.usingHardwareInput ? 0 : 2);
        AndroidUtilities.showKeyboard(this.messageEditText);
    }

    private void showPopup(int i) {
        if (i == 1) {
            if (this.emojiView == null) {
                if (MoboConstants.aP) {
                    this.emojiView = new EmojiView(false, false, getContext());
                } else {
                    this.emojiView = new EmojiView(false, false, getContext());
                }
                this.emojiView.setListener(new C14399());
                this.sizeNotifierLayout.addView(this.emojiView);
            }
            this.emojiView.setVisibility(0);
            if (this.keyboardHeight <= 0) {
                this.keyboardHeight = ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).getInt("kbd_height", AndroidUtilities.dp(200.0f));
            }
            if (this.keyboardHeightLand <= 0) {
                this.keyboardHeightLand = ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).getInt("kbd_height_land3", AndroidUtilities.dp(200.0f));
            }
            int i2 = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? this.keyboardHeightLand : this.keyboardHeight;
            LayoutParams layoutParams = (LayoutParams) this.emojiView.getLayoutParams();
            layoutParams.width = AndroidUtilities.displaySize.x;
            layoutParams.height = i2;
            this.emojiView.setLayoutParams(layoutParams);
            if (!AndroidUtilities.isInMultiwindow) {
                AndroidUtilities.hideKeyboard(this.messageEditText);
            }
            if (this.sizeNotifierLayout != null) {
                this.emojiPadding = i2;
                this.sizeNotifierLayout.requestLayout();
                this.emojiButton.setImageResource(C0338R.drawable.ic_keyboard_w);
                onWindowSizeChanged();
                return;
            }
            return;
        }
        if (this.emojiButton != null) {
            this.emojiButton.setImageResource(C0338R.drawable.ic_smile_w);
        }
        if (this.emojiView != null) {
            this.emojiView.setVisibility(8);
        }
        if (this.sizeNotifierLayout != null) {
            if (i == 0) {
                this.emojiPadding = 0;
            }
            this.sizeNotifierLayout.requestLayout();
            onWindowSizeChanged();
        }
    }

    public void closeKeyboard() {
        AndroidUtilities.hideKeyboard(this.messageEditText);
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.emojiDidLoaded && this.emojiView != null) {
            this.emojiView.invalidateViews();
        }
    }

    public int getCursorPosition() {
        return this.messageEditText == null ? 0 : this.messageEditText.getSelectionStart();
    }

    public int getEmojiPadding() {
        return this.emojiPadding;
    }

    public CharSequence getFieldCharSequence() {
        return this.messageEditText.getText();
    }

    public boolean hideActionMode() {
        if (VERSION.SDK_INT < 23 || this.currentActionMode == null) {
            return false;
        }
        try {
            this.currentActionMode.finish();
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        this.currentActionMode = null;
        return true;
    }

    public void hidePopup() {
        if (isPopupShowing()) {
            showPopup(0);
        }
    }

    public boolean isKeyboardVisible() {
        return (AndroidUtilities.usingHardwareInput && getLayoutParams() != null && ((LayoutParams) getLayoutParams()).bottomMargin == 0) || this.keyboardVisible;
    }

    public boolean isPopupShowing() {
        return this.emojiView != null && this.emojiView.getVisibility() == 0;
    }

    public boolean isPopupView(View view) {
        return view == this.emojiView;
    }

    public void onCreate() {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
        this.sizeNotifierLayout.setDelegate(this);
    }

    public void onDestroy() {
        hidePopup();
        if (isKeyboardVisible()) {
            closeKeyboard();
        }
        this.keyboardVisible = false;
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
        if (this.sizeNotifierLayout != null) {
            this.sizeNotifierLayout.setDelegate(null);
        }
    }

    public void onSizeChanged(int i, boolean z) {
        if (i > AndroidUtilities.dp(50.0f) && this.keyboardVisible && !AndroidUtilities.isInMultiwindow) {
            if (z) {
                this.keyboardHeightLand = i;
                ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).edit().putInt("kbd_height_land3", this.keyboardHeightLand).commit();
            } else {
                this.keyboardHeight = i;
                ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).edit().putInt("kbd_height", this.keyboardHeight).commit();
            }
        }
        if (isPopupShowing()) {
            int i2 = z ? this.keyboardHeightLand : this.keyboardHeight;
            LayoutParams layoutParams = (LayoutParams) this.emojiView.getLayoutParams();
            if (!(layoutParams.width == AndroidUtilities.displaySize.x && layoutParams.height == i2)) {
                layoutParams.width = AndroidUtilities.displaySize.x;
                layoutParams.height = i2;
                this.emojiView.setLayoutParams(layoutParams);
                if (this.sizeNotifierLayout != null) {
                    this.emojiPadding = layoutParams.height;
                    this.sizeNotifierLayout.requestLayout();
                    onWindowSizeChanged();
                }
            }
        }
        if (this.lastSizeChangeValue1 == i && this.lastSizeChangeValue2 == z) {
            onWindowSizeChanged();
            return;
        }
        this.lastSizeChangeValue1 = i;
        this.lastSizeChangeValue2 = z;
        boolean z2 = this.keyboardVisible;
        this.keyboardVisible = i > 0;
        if (this.keyboardVisible && isPopupShowing()) {
            showPopup(0);
        }
        if (!(this.emojiPadding == 0 || this.keyboardVisible || this.keyboardVisible == z2 || isPopupShowing())) {
            this.emojiPadding = 0;
            this.sizeNotifierLayout.requestLayout();
        }
        onWindowSizeChanged();
    }

    public void openKeyboard() {
        this.messageEditText.requestFocus();
        AndroidUtilities.showKeyboard(this.messageEditText);
    }

    public void replaceWithText(int i, int i2, String str) {
        try {
            CharSequence stringBuilder = new StringBuilder(this.messageEditText.getText());
            stringBuilder.replace(i, i + i2, str);
            this.messageEditText.setText(stringBuilder);
            if (str.length() + i <= this.messageEditText.length()) {
                this.messageEditText.setSelection(str.length() + i);
            } else {
                this.messageEditText.setSelection(this.messageEditText.length());
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    public void setDelegate(PhotoViewerCaptionEnterViewDelegate photoViewerCaptionEnterViewDelegate) {
        this.delegate = photoViewerCaptionEnterViewDelegate;
    }

    public void setFieldFocused(boolean z) {
        if (this.messageEditText != null) {
            if (z) {
                if (!this.messageEditText.isFocused()) {
                    this.messageEditText.postDelayed(new C14388(), 600);
                }
            } else if (this.messageEditText.isFocused() && !this.keyboardVisible) {
                this.messageEditText.clearFocus();
            }
        }
    }

    public void setFieldText(CharSequence charSequence) {
        if (this.messageEditText != null) {
            this.messageEditText.setText(charSequence);
            this.messageEditText.setSelection(this.messageEditText.getText().length());
            if (this.delegate != null) {
                this.delegate.onTextChanged(this.messageEditText.getText());
            }
        }
    }
}
