package com.hanista.mobogram.mobo.p001b;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.p004e.DataBaseAccess;
import com.hanista.mobogram.tgnet.TLRPC.TL_dialog;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.BottomSheet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* renamed from: com.hanista.mobogram.mobo.b.h */
public class CategoryUtil {
    private static int f245a;

    /* renamed from: com.hanista.mobogram.mobo.b.h.1 */
    static class CategoryUtil implements OnClickListener {
        final /* synthetic */ CategoryUtil f242a;
        final /* synthetic */ boolean f243b;
        final /* synthetic */ List f244c;

        CategoryUtil(CategoryUtil categoryUtil, boolean z, List list) {
            this.f242a = categoryUtil;
            this.f243b = z;
            this.f244c = list;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (this.f242a == null) {
                return;
            }
            if (i == 0 && this.f243b) {
                this.f242a.didSelectCategory(null);
                return;
            }
            CategoryUtil categoryUtil = this.f242a;
            List list = this.f244c;
            if (this.f243b) {
                i--;
            }
            categoryUtil.didSelectCategory((Category) list.get(i));
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.b.h.a */
    public interface CategoryUtil {
        void didSelectCategory(Category category);
    }

    static {
        f245a = -1;
    }

    public static Long m344a(Category category) {
        Long a = new DataBaseAccess().m840a(category);
        CategoryUtil.m353d();
        return a;
    }

    public static ArrayList<TL_dialog> m345a() {
        ArrayList arrayList = MessagesController.getInstance().dialogs;
        ArrayList<TL_dialog> arrayList2 = new ArrayList();
        List k = new DataBaseAccess().m896k();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            TL_dialog tL_dialog = (TL_dialog) it.next();
            if (!k.contains(Long.valueOf(tL_dialog.id))) {
                arrayList2.add(tL_dialog);
            }
        }
        return arrayList2;
    }

    public static ArrayList<TL_dialog> m346a(long j, ArrayList<TL_dialog> arrayList) {
        ArrayList<TL_dialog> arrayList2 = new ArrayList();
        List e = new DataBaseAccess().m875e(Long.valueOf(j));
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            TL_dialog tL_dialog = (TL_dialog) it.next();
            if (e.contains(Long.valueOf(tL_dialog.id))) {
                arrayList2.add(tL_dialog);
            }
        }
        return arrayList2;
    }

    public static void m347a(BaseFragment baseFragment, String str, boolean z, boolean z2, CategoryUtil categoryUtil) {
        List<Category> g = new DataBaseAccess().m882g();
        if (g.size() == 0) {
            Toast.makeText(baseFragment.getParentActivity(), LocaleController.getString("NoCategories", C0338R.string.NoCategories), 1).show();
            return;
        }
        List arrayList = new ArrayList();
        if (z) {
            arrayList.add(LocaleController.getString("All", C0338R.string.All));
        }
        for (Category b : g) {
            arrayList.add(b.m281b());
        }
        OnClickListener categoryUtil2 = new CategoryUtil(categoryUtil, z, g);
        if (arrayList.size() > 7 || z2) {
            Builder builder = new Builder(baseFragment.getParentActivity());
            builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]), categoryUtil2);
            if (str != null) {
                builder.setTitle(str);
            }
            builder.create().show();
            return;
        }
        BottomSheet.Builder builder2 = new BottomSheet.Builder(baseFragment.getParentActivity());
        builder2.setItems((CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]), categoryUtil2);
        if (str != null) {
            builder2.setTitle(str);
        }
        builder2.create().show();
    }

    public static void m348a(BaseFragment baseFragment, boolean z, CategoryUtil categoryUtil) {
        CategoryUtil.m347a(baseFragment, null, z, false, categoryUtil);
    }

    public static void m349a(Long l) {
        new DataBaseAccess().m872d(l);
        CategoryUtil.m353d();
    }

    public static String m350b(Long l) {
        if (new DataBaseAccess().m889i(l) == null) {
            return LocaleController.getString("AddToCategory", C0338R.string.AddToCategory);
        }
        return LocaleController.formatString("RemoveFromCategory", C0338R.string.RemoveFromCategory, new DataBaseAccess().m889i(l).m281b());
    }

    public static boolean m351b() {
        return MoboConstants.f1358y && CategoryUtil.m354e();
    }

    public static boolean m352c() {
        return MoboConstants.f1359z && CategoryUtil.m354e();
    }

    private static void m353d() {
        f245a = new DataBaseAccess().m885h();
    }

    private static boolean m354e() {
        if (f245a == -1) {
            CategoryUtil.m353d();
        }
        return f245a > 0;
    }
}
