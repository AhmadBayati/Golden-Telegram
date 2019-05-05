package com.hanista.mobogram.ui.Components.Paint;

import com.hanista.mobogram.messenger.AndroidUtilities;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UndoStore {
    private UndoStoreDelegate delegate;
    private List<UUID> operations;
    private Map<UUID, Runnable> uuidToOperationMap;

    /* renamed from: com.hanista.mobogram.ui.Components.Paint.UndoStore.1 */
    class C13841 implements Runnable {
        C13841() {
        }

        public void run() {
            if (UndoStore.this.delegate != null) {
                UndoStore.this.delegate.historyChanged();
            }
        }
    }

    public interface UndoStoreDelegate {
        void historyChanged();
    }

    public UndoStore() {
        this.uuidToOperationMap = new HashMap();
        this.operations = new ArrayList();
    }

    private void notifyOfHistoryChanges() {
        AndroidUtilities.runOnUIThread(new C13841());
    }

    public boolean canUndo() {
        return !this.operations.isEmpty();
    }

    public void registerUndo(UUID uuid, Runnable runnable) {
        this.uuidToOperationMap.put(uuid, runnable);
        this.operations.add(uuid);
        notifyOfHistoryChanges();
    }

    public void reset() {
        this.operations.clear();
        this.uuidToOperationMap.clear();
        notifyOfHistoryChanges();
    }

    public void setDelegate(UndoStoreDelegate undoStoreDelegate) {
        this.delegate = undoStoreDelegate;
    }

    public void undo() {
        if (this.operations.size() != 0) {
            int size = this.operations.size() - 1;
            UUID uuid = (UUID) this.operations.get(size);
            Runnable runnable = (Runnable) this.uuidToOperationMap.get(uuid);
            this.uuidToOperationMap.remove(uuid);
            this.operations.remove(size);
            runnable.run();
            notifyOfHistoryChanges();
        }
    }

    public void unregisterUndo(UUID uuid) {
        this.uuidToOperationMap.remove(uuid);
        this.operations.remove(uuid);
        notifyOfHistoryChanges();
    }
}
