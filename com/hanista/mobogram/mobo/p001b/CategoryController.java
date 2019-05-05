package com.hanista.mobogram.mobo.p001b;

import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.DispatchQueue;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.tgnet.TLRPC.TL_dialog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/* renamed from: com.hanista.mobogram.mobo.b.d */
public class CategoryController {
    private static volatile CategoryController f187a;
    private DispatchQueue f188b;

    /* renamed from: com.hanista.mobogram.mobo.b.d.1 */
    static class CategoryController implements Runnable {
        final /* synthetic */ List f186a;

        /* renamed from: com.hanista.mobogram.mobo.b.d.1.1 */
        class CategoryController implements Runnable {
            final /* synthetic */ CategoryController f185a;

            CategoryController(CategoryController categoryController) {
                this.f185a = categoryController;
            }

            public void run() {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.categoriesInfoDidLoaded, new Object[0]);
            }
        }

        CategoryController(List list) {
            this.f186a = list;
        }

        public void run() {
            List<Category> arrayList = new ArrayList(this.f186a);
            for (Category category : arrayList) {
                if (category.m276a() == null) {
                    category.m284d().clear();
                }
                category.m277a(0);
                category.m282b(0);
            }
            Iterator it = new ArrayList(MessagesController.getInstance().dialogs).iterator();
            while (it.hasNext()) {
                TL_dialog tL_dialog = (TL_dialog) it.next();
                for (Category category2 : arrayList) {
                    if (category2.m276a() == null || category2.m284d().contains(Long.valueOf(tL_dialog.id))) {
                        if (MessagesController.getInstance().isDialogMuted(tL_dialog.id)) {
                            category2.m277a(category2.m285e() + tL_dialog.unread_count);
                        } else {
                            category2.m282b(category2.m286f() + tL_dialog.unread_count);
                        }
                    }
                    if (category2.m276a() == null) {
                        category2.m284d().add(Long.valueOf(tL_dialog.id));
                    }
                }
            }
            CategoryController.m319c(arrayList);
            AndroidUtilities.runOnUIThread(new CategoryController(this));
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.b.d.2 */
    static class CategoryController implements Comparator<Category> {
        CategoryController() {
        }

        public int m313a(Category category, Category category2) {
            return category.m276a() == null ? -1 : category2.m276a() == null ? 1 : category.m283c() == category2.m283c() ? 0 : category.m283c().intValue() > category2.m283c().intValue() ? 1 : -1;
        }

        public /* synthetic */ int compare(Object obj, Object obj2) {
            return m313a((Category) obj, (Category) obj2);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.b.d.3 */
    static class CategoryController implements Comparator<Category> {
        CategoryController() {
        }

        public int m314a(Category category, Category category2) {
            return category.m276a() == null ? -1 : category2.m276a() == null ? 1 : category.m286f() == category2.m286f() ? 0 : category.m286f() < category2.m286f() ? 1 : -1;
        }

        public /* synthetic */ int compare(Object obj, Object obj2) {
            return m314a((Category) obj, (Category) obj2);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.b.d.4 */
    static class CategoryController implements Comparator<Category> {
        CategoryController() {
        }

        public int m315a(Category category, Category category2) {
            return category.m276a() == null ? -1 : category2.m276a() == null ? 1 : category.m285e() == category2.m285e() ? 0 : category.m285e() < category2.m285e() ? 1 : -1;
        }

        public /* synthetic */ int compare(Object obj, Object obj2) {
            return m315a((Category) obj, (Category) obj2);
        }
    }

    static {
        f187a = null;
    }

    public CategoryController() {
        this.f188b = new DispatchQueue("categoryQueue");
    }

    public static CategoryController m316a() {
        CategoryController categoryController = f187a;
        if (categoryController == null) {
            synchronized (CategoryController.class) {
                categoryController = f187a;
                if (categoryController == null) {
                    categoryController = new CategoryController();
                    f187a = categoryController;
                }
            }
        }
        return categoryController;
    }

    public static void m317a(List<Category> list) {
        CategoryController.m316a().m320b().postRunnable(new CategoryController(list));
    }

    private static void m319c(List<Category> list) {
        if (MoboConstants.f1308A == 1) {
            Collections.sort(list, new CategoryController());
        } else if (MoboConstants.f1308A == 2) {
            Collections.sort(list, new CategoryController());
        } else {
            Collections.sort(list, new CategoryController());
        }
    }

    public DispatchQueue m320b() {
        return this.f188b;
    }
}
