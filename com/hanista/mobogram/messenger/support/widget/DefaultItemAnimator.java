package com.hanista.mobogram.messenger.support.widget;

import android.support.annotation.NonNull;
import android.support.v4.animation.AnimatorCompatHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.view.View;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ViewHolder;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultItemAnimator extends SimpleItemAnimator {
    private static final boolean DEBUG = false;
    private ArrayList<ViewHolder> mAddAnimations;
    private ArrayList<ArrayList<ViewHolder>> mAdditionsList;
    private ArrayList<ViewHolder> mChangeAnimations;
    private ArrayList<ArrayList<ChangeInfo>> mChangesList;
    private ArrayList<ViewHolder> mMoveAnimations;
    private ArrayList<ArrayList<MoveInfo>> mMovesList;
    private ArrayList<ViewHolder> mPendingAdditions;
    private ArrayList<ChangeInfo> mPendingChanges;
    private ArrayList<MoveInfo> mPendingMoves;
    private ArrayList<ViewHolder> mPendingRemovals;
    private ArrayList<ViewHolder> mRemoveAnimations;

    /* renamed from: com.hanista.mobogram.messenger.support.widget.DefaultItemAnimator.1 */
    class C08501 implements Runnable {
        final /* synthetic */ ArrayList val$moves;

        C08501(ArrayList arrayList) {
            this.val$moves = arrayList;
        }

        public void run() {
            Iterator it = this.val$moves.iterator();
            while (it.hasNext()) {
                MoveInfo moveInfo = (MoveInfo) it.next();
                DefaultItemAnimator.this.animateMoveImpl(moveInfo.holder, moveInfo.fromX, moveInfo.fromY, moveInfo.toX, moveInfo.toY);
            }
            this.val$moves.clear();
            DefaultItemAnimator.this.mMovesList.remove(this.val$moves);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.support.widget.DefaultItemAnimator.2 */
    class C08512 implements Runnable {
        final /* synthetic */ ArrayList val$changes;

        C08512(ArrayList arrayList) {
            this.val$changes = arrayList;
        }

        public void run() {
            Iterator it = this.val$changes.iterator();
            while (it.hasNext()) {
                DefaultItemAnimator.this.animateChangeImpl((ChangeInfo) it.next());
            }
            this.val$changes.clear();
            DefaultItemAnimator.this.mChangesList.remove(this.val$changes);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.support.widget.DefaultItemAnimator.3 */
    class C08523 implements Runnable {
        final /* synthetic */ ArrayList val$additions;

        C08523(ArrayList arrayList) {
            this.val$additions = arrayList;
        }

        public void run() {
            Iterator it = this.val$additions.iterator();
            while (it.hasNext()) {
                DefaultItemAnimator.this.animateAddImpl((ViewHolder) it.next());
            }
            this.val$additions.clear();
            DefaultItemAnimator.this.mAdditionsList.remove(this.val$additions);
        }
    }

    private static class VpaListenerAdapter implements ViewPropertyAnimatorListener {
        private VpaListenerAdapter() {
        }

        public void onAnimationCancel(View view) {
        }

        public void onAnimationEnd(View view) {
        }

        public void onAnimationStart(View view) {
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.support.widget.DefaultItemAnimator.4 */
    class C08534 extends VpaListenerAdapter {
        final /* synthetic */ ViewPropertyAnimatorCompat val$animation;
        final /* synthetic */ ViewHolder val$holder;

        C08534(ViewHolder viewHolder, ViewPropertyAnimatorCompat viewPropertyAnimatorCompat) {
            this.val$holder = viewHolder;
            this.val$animation = viewPropertyAnimatorCompat;
            super();
        }

        public void onAnimationEnd(View view) {
            this.val$animation.setListener(null);
            ViewCompat.setAlpha(view, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            DefaultItemAnimator.this.dispatchRemoveFinished(this.val$holder);
            DefaultItemAnimator.this.mRemoveAnimations.remove(this.val$holder);
            DefaultItemAnimator.this.dispatchFinishedWhenDone();
        }

        public void onAnimationStart(View view) {
            DefaultItemAnimator.this.dispatchRemoveStarting(this.val$holder);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.support.widget.DefaultItemAnimator.5 */
    class C08545 extends VpaListenerAdapter {
        final /* synthetic */ ViewPropertyAnimatorCompat val$animation;
        final /* synthetic */ ViewHolder val$holder;

        C08545(ViewHolder viewHolder, ViewPropertyAnimatorCompat viewPropertyAnimatorCompat) {
            this.val$holder = viewHolder;
            this.val$animation = viewPropertyAnimatorCompat;
            super();
        }

        public void onAnimationCancel(View view) {
            ViewCompat.setAlpha(view, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        }

        public void onAnimationEnd(View view) {
            this.val$animation.setListener(null);
            DefaultItemAnimator.this.dispatchAddFinished(this.val$holder);
            DefaultItemAnimator.this.mAddAnimations.remove(this.val$holder);
            DefaultItemAnimator.this.dispatchFinishedWhenDone();
        }

        public void onAnimationStart(View view) {
            DefaultItemAnimator.this.dispatchAddStarting(this.val$holder);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.support.widget.DefaultItemAnimator.6 */
    class C08556 extends VpaListenerAdapter {
        final /* synthetic */ ViewPropertyAnimatorCompat val$animation;
        final /* synthetic */ int val$deltaX;
        final /* synthetic */ int val$deltaY;
        final /* synthetic */ ViewHolder val$holder;

        C08556(ViewHolder viewHolder, int i, int i2, ViewPropertyAnimatorCompat viewPropertyAnimatorCompat) {
            this.val$holder = viewHolder;
            this.val$deltaX = i;
            this.val$deltaY = i2;
            this.val$animation = viewPropertyAnimatorCompat;
            super();
        }

        public void onAnimationCancel(View view) {
            if (this.val$deltaX != 0) {
                ViewCompat.setTranslationX(view, 0.0f);
            }
            if (this.val$deltaY != 0) {
                ViewCompat.setTranslationY(view, 0.0f);
            }
        }

        public void onAnimationEnd(View view) {
            this.val$animation.setListener(null);
            DefaultItemAnimator.this.dispatchMoveFinished(this.val$holder);
            DefaultItemAnimator.this.mMoveAnimations.remove(this.val$holder);
            DefaultItemAnimator.this.dispatchFinishedWhenDone();
        }

        public void onAnimationStart(View view) {
            DefaultItemAnimator.this.dispatchMoveStarting(this.val$holder);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.support.widget.DefaultItemAnimator.7 */
    class C08567 extends VpaListenerAdapter {
        final /* synthetic */ ChangeInfo val$changeInfo;
        final /* synthetic */ ViewPropertyAnimatorCompat val$oldViewAnim;

        C08567(ChangeInfo changeInfo, ViewPropertyAnimatorCompat viewPropertyAnimatorCompat) {
            this.val$changeInfo = changeInfo;
            this.val$oldViewAnim = viewPropertyAnimatorCompat;
            super();
        }

        public void onAnimationEnd(View view) {
            this.val$oldViewAnim.setListener(null);
            ViewCompat.setAlpha(view, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            ViewCompat.setTranslationX(view, 0.0f);
            ViewCompat.setTranslationY(view, 0.0f);
            DefaultItemAnimator.this.dispatchChangeFinished(this.val$changeInfo.oldHolder, true);
            DefaultItemAnimator.this.mChangeAnimations.remove(this.val$changeInfo.oldHolder);
            DefaultItemAnimator.this.dispatchFinishedWhenDone();
        }

        public void onAnimationStart(View view) {
            DefaultItemAnimator.this.dispatchChangeStarting(this.val$changeInfo.oldHolder, true);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.support.widget.DefaultItemAnimator.8 */
    class C08578 extends VpaListenerAdapter {
        final /* synthetic */ ChangeInfo val$changeInfo;
        final /* synthetic */ View val$newView;
        final /* synthetic */ ViewPropertyAnimatorCompat val$newViewAnimation;

        C08578(ChangeInfo changeInfo, ViewPropertyAnimatorCompat viewPropertyAnimatorCompat, View view) {
            this.val$changeInfo = changeInfo;
            this.val$newViewAnimation = viewPropertyAnimatorCompat;
            this.val$newView = view;
            super();
        }

        public void onAnimationEnd(View view) {
            this.val$newViewAnimation.setListener(null);
            ViewCompat.setAlpha(this.val$newView, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            ViewCompat.setTranslationX(this.val$newView, 0.0f);
            ViewCompat.setTranslationY(this.val$newView, 0.0f);
            DefaultItemAnimator.this.dispatchChangeFinished(this.val$changeInfo.newHolder, false);
            DefaultItemAnimator.this.mChangeAnimations.remove(this.val$changeInfo.newHolder);
            DefaultItemAnimator.this.dispatchFinishedWhenDone();
        }

        public void onAnimationStart(View view) {
            DefaultItemAnimator.this.dispatchChangeStarting(this.val$changeInfo.newHolder, false);
        }
    }

    private static class ChangeInfo {
        public int fromX;
        public int fromY;
        public ViewHolder newHolder;
        public ViewHolder oldHolder;
        public int toX;
        public int toY;

        private ChangeInfo(ViewHolder viewHolder, ViewHolder viewHolder2) {
            this.oldHolder = viewHolder;
            this.newHolder = viewHolder2;
        }

        private ChangeInfo(ViewHolder viewHolder, ViewHolder viewHolder2, int i, int i2, int i3, int i4) {
            this(viewHolder, viewHolder2);
            this.fromX = i;
            this.fromY = i2;
            this.toX = i3;
            this.toY = i4;
        }

        public String toString() {
            return "ChangeInfo{oldHolder=" + this.oldHolder + ", newHolder=" + this.newHolder + ", fromX=" + this.fromX + ", fromY=" + this.fromY + ", toX=" + this.toX + ", toY=" + this.toY + '}';
        }
    }

    private static class MoveInfo {
        public int fromX;
        public int fromY;
        public ViewHolder holder;
        public int toX;
        public int toY;

        private MoveInfo(ViewHolder viewHolder, int i, int i2, int i3, int i4) {
            this.holder = viewHolder;
            this.fromX = i;
            this.fromY = i2;
            this.toX = i3;
            this.toY = i4;
        }
    }

    public DefaultItemAnimator() {
        this.mPendingRemovals = new ArrayList();
        this.mPendingAdditions = new ArrayList();
        this.mPendingMoves = new ArrayList();
        this.mPendingChanges = new ArrayList();
        this.mAdditionsList = new ArrayList();
        this.mMovesList = new ArrayList();
        this.mChangesList = new ArrayList();
        this.mAddAnimations = new ArrayList();
        this.mMoveAnimations = new ArrayList();
        this.mRemoveAnimations = new ArrayList();
        this.mChangeAnimations = new ArrayList();
    }

    private void animateAddImpl(ViewHolder viewHolder) {
        ViewPropertyAnimatorCompat animate = ViewCompat.animate(viewHolder.itemView);
        this.mAddAnimations.add(viewHolder);
        animate.alpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT).setDuration(getAddDuration()).setListener(new C08545(viewHolder, animate)).start();
    }

    private void animateChangeImpl(ChangeInfo changeInfo) {
        View view = null;
        ViewHolder viewHolder = changeInfo.oldHolder;
        View view2 = viewHolder == null ? null : viewHolder.itemView;
        ViewHolder viewHolder2 = changeInfo.newHolder;
        if (viewHolder2 != null) {
            view = viewHolder2.itemView;
        }
        if (view2 != null) {
            ViewPropertyAnimatorCompat duration = ViewCompat.animate(view2).setDuration(getChangeDuration());
            this.mChangeAnimations.add(changeInfo.oldHolder);
            duration.translationX((float) (changeInfo.toX - changeInfo.fromX));
            duration.translationY((float) (changeInfo.toY - changeInfo.fromY));
            duration.alpha(0.0f).setListener(new C08567(changeInfo, duration)).start();
        }
        if (view != null) {
            duration = ViewCompat.animate(view);
            this.mChangeAnimations.add(changeInfo.newHolder);
            duration.translationX(0.0f).translationY(0.0f).setDuration(getChangeDuration()).alpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT).setListener(new C08578(changeInfo, duration, view)).start();
        }
    }

    private void animateMoveImpl(ViewHolder viewHolder, int i, int i2, int i3, int i4) {
        View view = viewHolder.itemView;
        int i5 = i3 - i;
        int i6 = i4 - i2;
        if (i5 != 0) {
            ViewCompat.animate(view).translationX(0.0f);
        }
        if (i6 != 0) {
            ViewCompat.animate(view).translationY(0.0f);
        }
        ViewPropertyAnimatorCompat animate = ViewCompat.animate(view);
        this.mMoveAnimations.add(viewHolder);
        animate.setDuration(getMoveDuration()).setListener(new C08556(viewHolder, i5, i6, animate)).start();
    }

    private void animateRemoveImpl(ViewHolder viewHolder) {
        ViewPropertyAnimatorCompat animate = ViewCompat.animate(viewHolder.itemView);
        this.mRemoveAnimations.add(viewHolder);
        animate.setDuration(getRemoveDuration()).alpha(0.0f).setListener(new C08534(viewHolder, animate)).start();
    }

    private void dispatchFinishedWhenDone() {
        if (!isRunning()) {
            dispatchAnimationsFinished();
        }
    }

    private void endChangeAnimation(List<ChangeInfo> list, ViewHolder viewHolder) {
        for (int size = list.size() - 1; size >= 0; size--) {
            ChangeInfo changeInfo = (ChangeInfo) list.get(size);
            if (endChangeAnimationIfNecessary(changeInfo, viewHolder) && changeInfo.oldHolder == null && changeInfo.newHolder == null) {
                list.remove(changeInfo);
            }
        }
    }

    private void endChangeAnimationIfNecessary(ChangeInfo changeInfo) {
        if (changeInfo.oldHolder != null) {
            endChangeAnimationIfNecessary(changeInfo, changeInfo.oldHolder);
        }
        if (changeInfo.newHolder != null) {
            endChangeAnimationIfNecessary(changeInfo, changeInfo.newHolder);
        }
    }

    private boolean endChangeAnimationIfNecessary(ChangeInfo changeInfo, ViewHolder viewHolder) {
        boolean z = false;
        if (changeInfo.newHolder == viewHolder) {
            changeInfo.newHolder = null;
        } else if (changeInfo.oldHolder != viewHolder) {
            return false;
        } else {
            changeInfo.oldHolder = null;
            z = true;
        }
        ViewCompat.setAlpha(viewHolder.itemView, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        ViewCompat.setTranslationX(viewHolder.itemView, 0.0f);
        ViewCompat.setTranslationY(viewHolder.itemView, 0.0f);
        dispatchChangeFinished(viewHolder, z);
        return true;
    }

    private void resetAnimation(ViewHolder viewHolder) {
        AnimatorCompatHelper.clearInterpolator(viewHolder.itemView);
        endAnimation(viewHolder);
    }

    public boolean animateAdd(ViewHolder viewHolder) {
        resetAnimation(viewHolder);
        ViewCompat.setAlpha(viewHolder.itemView, 0.0f);
        this.mPendingAdditions.add(viewHolder);
        return true;
    }

    public boolean animateChange(ViewHolder viewHolder, ViewHolder viewHolder2, int i, int i2, int i3, int i4) {
        if (viewHolder == viewHolder2) {
            return animateMove(viewHolder, i, i2, i3, i4);
        }
        float translationX = ViewCompat.getTranslationX(viewHolder.itemView);
        float translationY = ViewCompat.getTranslationY(viewHolder.itemView);
        float alpha = ViewCompat.getAlpha(viewHolder.itemView);
        resetAnimation(viewHolder);
        int i5 = (int) (((float) (i3 - i)) - translationX);
        int i6 = (int) (((float) (i4 - i2)) - translationY);
        ViewCompat.setTranslationX(viewHolder.itemView, translationX);
        ViewCompat.setTranslationY(viewHolder.itemView, translationY);
        ViewCompat.setAlpha(viewHolder.itemView, alpha);
        if (viewHolder2 != null) {
            resetAnimation(viewHolder2);
            ViewCompat.setTranslationX(viewHolder2.itemView, (float) (-i5));
            ViewCompat.setTranslationY(viewHolder2.itemView, (float) (-i6));
            ViewCompat.setAlpha(viewHolder2.itemView, 0.0f);
        }
        this.mPendingChanges.add(new ChangeInfo(viewHolder2, i, i2, i3, i4, null));
        return true;
    }

    public boolean animateMove(ViewHolder viewHolder, int i, int i2, int i3, int i4) {
        View view = viewHolder.itemView;
        int translationX = (int) (((float) i) + ViewCompat.getTranslationX(viewHolder.itemView));
        int translationY = (int) (((float) i2) + ViewCompat.getTranslationY(viewHolder.itemView));
        resetAnimation(viewHolder);
        int i5 = i3 - translationX;
        int i6 = i4 - translationY;
        if (i5 == 0 && i6 == 0) {
            dispatchMoveFinished(viewHolder);
            return false;
        }
        if (i5 != 0) {
            ViewCompat.setTranslationX(view, (float) (-i5));
        }
        if (i6 != 0) {
            ViewCompat.setTranslationY(view, (float) (-i6));
        }
        this.mPendingMoves.add(new MoveInfo(translationX, translationY, i3, i4, null));
        return true;
    }

    public boolean animateRemove(ViewHolder viewHolder) {
        resetAnimation(viewHolder);
        this.mPendingRemovals.add(viewHolder);
        return true;
    }

    public boolean canReuseUpdatedViewHolder(@NonNull ViewHolder viewHolder, @NonNull List<Object> list) {
        return !list.isEmpty() || super.canReuseUpdatedViewHolder(viewHolder, list);
    }

    void cancelAll(List<ViewHolder> list) {
        for (int size = list.size() - 1; size >= 0; size--) {
            ViewCompat.animate(((ViewHolder) list.get(size)).itemView).cancel();
        }
    }

    public void endAnimation(ViewHolder viewHolder) {
        int size;
        View view = viewHolder.itemView;
        ViewCompat.animate(view).cancel();
        for (size = this.mPendingMoves.size() - 1; size >= 0; size--) {
            if (((MoveInfo) this.mPendingMoves.get(size)).holder == viewHolder) {
                ViewCompat.setTranslationY(view, 0.0f);
                ViewCompat.setTranslationX(view, 0.0f);
                dispatchMoveFinished(viewHolder);
                this.mPendingMoves.remove(size);
            }
        }
        endChangeAnimation(this.mPendingChanges, viewHolder);
        if (this.mPendingRemovals.remove(viewHolder)) {
            ViewCompat.setAlpha(view, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            dispatchRemoveFinished(viewHolder);
        }
        if (this.mPendingAdditions.remove(viewHolder)) {
            ViewCompat.setAlpha(view, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            dispatchAddFinished(viewHolder);
        }
        for (size = this.mChangesList.size() - 1; size >= 0; size--) {
            ArrayList arrayList = (ArrayList) this.mChangesList.get(size);
            endChangeAnimation(arrayList, viewHolder);
            if (arrayList.isEmpty()) {
                this.mChangesList.remove(size);
            }
        }
        for (int size2 = this.mMovesList.size() - 1; size2 >= 0; size2--) {
            arrayList = (ArrayList) this.mMovesList.get(size2);
            int size3 = arrayList.size() - 1;
            while (size3 >= 0) {
                if (((MoveInfo) arrayList.get(size3)).holder == viewHolder) {
                    ViewCompat.setTranslationY(view, 0.0f);
                    ViewCompat.setTranslationX(view, 0.0f);
                    dispatchMoveFinished(viewHolder);
                    arrayList.remove(size3);
                    if (arrayList.isEmpty()) {
                        this.mMovesList.remove(size2);
                    }
                } else {
                    size3--;
                }
            }
        }
        for (size = this.mAdditionsList.size() - 1; size >= 0; size--) {
            arrayList = (ArrayList) this.mAdditionsList.get(size);
            if (arrayList.remove(viewHolder)) {
                ViewCompat.setAlpha(view, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                dispatchAddFinished(viewHolder);
                if (arrayList.isEmpty()) {
                    this.mAdditionsList.remove(size);
                }
            }
        }
        if (this.mRemoveAnimations.remove(viewHolder)) {
        }
        if (this.mAddAnimations.remove(viewHolder)) {
        }
        if (this.mChangeAnimations.remove(viewHolder)) {
        }
        if (this.mMoveAnimations.remove(viewHolder)) {
            dispatchFinishedWhenDone();
        } else {
            dispatchFinishedWhenDone();
        }
    }

    public void endAnimations() {
        int size;
        for (size = this.mPendingMoves.size() - 1; size >= 0; size--) {
            MoveInfo moveInfo = (MoveInfo) this.mPendingMoves.get(size);
            View view = moveInfo.holder.itemView;
            ViewCompat.setTranslationY(view, 0.0f);
            ViewCompat.setTranslationX(view, 0.0f);
            dispatchMoveFinished(moveInfo.holder);
            this.mPendingMoves.remove(size);
        }
        for (size = this.mPendingRemovals.size() - 1; size >= 0; size--) {
            dispatchRemoveFinished((ViewHolder) this.mPendingRemovals.get(size));
            this.mPendingRemovals.remove(size);
        }
        for (size = this.mPendingAdditions.size() - 1; size >= 0; size--) {
            ViewHolder viewHolder = (ViewHolder) this.mPendingAdditions.get(size);
            ViewCompat.setAlpha(viewHolder.itemView, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            dispatchAddFinished(viewHolder);
            this.mPendingAdditions.remove(size);
        }
        for (size = this.mPendingChanges.size() - 1; size >= 0; size--) {
            endChangeAnimationIfNecessary((ChangeInfo) this.mPendingChanges.get(size));
        }
        this.mPendingChanges.clear();
        if (isRunning()) {
            int size2;
            ArrayList arrayList;
            int size3;
            for (size2 = this.mMovesList.size() - 1; size2 >= 0; size2--) {
                arrayList = (ArrayList) this.mMovesList.get(size2);
                for (size3 = arrayList.size() - 1; size3 >= 0; size3--) {
                    MoveInfo moveInfo2 = (MoveInfo) arrayList.get(size3);
                    View view2 = moveInfo2.holder.itemView;
                    ViewCompat.setTranslationY(view2, 0.0f);
                    ViewCompat.setTranslationX(view2, 0.0f);
                    dispatchMoveFinished(moveInfo2.holder);
                    arrayList.remove(size3);
                    if (arrayList.isEmpty()) {
                        this.mMovesList.remove(arrayList);
                    }
                }
            }
            for (size2 = this.mAdditionsList.size() - 1; size2 >= 0; size2--) {
                arrayList = (ArrayList) this.mAdditionsList.get(size2);
                for (size3 = arrayList.size() - 1; size3 >= 0; size3--) {
                    ViewHolder viewHolder2 = (ViewHolder) arrayList.get(size3);
                    ViewCompat.setAlpha(viewHolder2.itemView, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    dispatchAddFinished(viewHolder2);
                    arrayList.remove(size3);
                    if (arrayList.isEmpty()) {
                        this.mAdditionsList.remove(arrayList);
                    }
                }
            }
            for (size2 = this.mChangesList.size() - 1; size2 >= 0; size2--) {
                arrayList = (ArrayList) this.mChangesList.get(size2);
                for (size3 = arrayList.size() - 1; size3 >= 0; size3--) {
                    endChangeAnimationIfNecessary((ChangeInfo) arrayList.get(size3));
                    if (arrayList.isEmpty()) {
                        this.mChangesList.remove(arrayList);
                    }
                }
            }
            cancelAll(this.mRemoveAnimations);
            cancelAll(this.mMoveAnimations);
            cancelAll(this.mAddAnimations);
            cancelAll(this.mChangeAnimations);
            dispatchAnimationsFinished();
        }
    }

    public boolean isRunning() {
        return (this.mPendingAdditions.isEmpty() && this.mPendingChanges.isEmpty() && this.mPendingMoves.isEmpty() && this.mPendingRemovals.isEmpty() && this.mMoveAnimations.isEmpty() && this.mRemoveAnimations.isEmpty() && this.mAddAnimations.isEmpty() && this.mChangeAnimations.isEmpty() && this.mMovesList.isEmpty() && this.mAdditionsList.isEmpty() && this.mChangesList.isEmpty()) ? false : true;
    }

    public void runPendingAnimations() {
        int i = !this.mPendingRemovals.isEmpty() ? 1 : 0;
        int i2 = !this.mPendingMoves.isEmpty() ? 1 : 0;
        int i3 = !this.mPendingChanges.isEmpty() ? 1 : 0;
        int i4 = !this.mPendingAdditions.isEmpty() ? 1 : 0;
        if (i != 0 || i2 != 0 || i4 != 0 || i3 != 0) {
            ArrayList arrayList;
            Runnable c08501;
            Iterator it = this.mPendingRemovals.iterator();
            while (it.hasNext()) {
                animateRemoveImpl((ViewHolder) it.next());
            }
            this.mPendingRemovals.clear();
            if (i2 != 0) {
                arrayList = new ArrayList();
                arrayList.addAll(this.mPendingMoves);
                this.mMovesList.add(arrayList);
                this.mPendingMoves.clear();
                c08501 = new C08501(arrayList);
                if (i != 0) {
                    ViewCompat.postOnAnimationDelayed(((MoveInfo) arrayList.get(0)).holder.itemView, c08501, getRemoveDuration());
                } else {
                    c08501.run();
                }
            }
            if (i3 != 0) {
                arrayList = new ArrayList();
                arrayList.addAll(this.mPendingChanges);
                this.mChangesList.add(arrayList);
                this.mPendingChanges.clear();
                c08501 = new C08512(arrayList);
                if (i != 0) {
                    ViewCompat.postOnAnimationDelayed(((ChangeInfo) arrayList.get(0)).oldHolder.itemView, c08501, getRemoveDuration());
                } else {
                    c08501.run();
                }
            }
            if (i4 != 0) {
                ArrayList arrayList2 = new ArrayList();
                arrayList2.addAll(this.mPendingAdditions);
                this.mAdditionsList.add(arrayList2);
                this.mPendingAdditions.clear();
                Runnable c08523 = new C08523(arrayList2);
                if (i == 0 && i2 == 0 && i3 == 0) {
                    c08523.run();
                } else {
                    ViewCompat.postOnAnimationDelayed(((ViewHolder) arrayList2.get(0)).itemView, c08523, (i != 0 ? getRemoveDuration() : 0) + Math.max(i2 != 0 ? getMoveDuration() : 0, i3 != 0 ? getChangeDuration() : 0));
                }
            }
        }
    }
}
