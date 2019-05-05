package com.hanista.mobogram.messenger.exoplayer.upstream;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import com.hanista.mobogram.messenger.exoplayer.util.TraceUtil;
import com.hanista.mobogram.messenger.exoplayer.util.Util;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

public final class Loader {
    private static final int MSG_END_OF_SOURCE = 0;
    private static final int MSG_FATAL_ERROR = 2;
    private static final int MSG_IO_EXCEPTION = 1;
    private LoadTask currentTask;
    private final ExecutorService downloadExecutorService;
    private boolean loading;

    public interface Callback {
        void onLoadCanceled(Loadable loadable);

        void onLoadCompleted(Loadable loadable);

        void onLoadError(Loadable loadable, IOException iOException);
    }

    public interface Loadable {
        void cancelLoad();

        boolean isLoadCanceled();

        void load();
    }

    @SuppressLint({"HandlerLeak"})
    private final class LoadTask extends Handler implements Runnable {
        private static final String TAG = "LoadTask";
        private final Callback callback;
        private volatile Thread executorThread;
        private final Loadable loadable;

        public LoadTask(Looper looper, Loadable loadable, Callback callback) {
            super(looper);
            this.loadable = loadable;
            this.callback = callback;
        }

        private void onFinished() {
            Loader.this.loading = false;
            Loader.this.currentTask = null;
        }

        public void handleMessage(Message message) {
            if (message.what == Loader.MSG_FATAL_ERROR) {
                throw ((Error) message.obj);
            }
            onFinished();
            if (this.loadable.isLoadCanceled()) {
                this.callback.onLoadCanceled(this.loadable);
                return;
            }
            switch (message.what) {
                case Loader.MSG_END_OF_SOURCE /*0*/:
                    this.callback.onLoadCompleted(this.loadable);
                case Loader.MSG_IO_EXCEPTION /*1*/:
                    this.callback.onLoadError(this.loadable, (IOException) message.obj);
                default:
            }
        }

        public void quit() {
            this.loadable.cancelLoad();
            if (this.executorThread != null) {
                this.executorThread.interrupt();
            }
        }

        public void run() {
            try {
                this.executorThread = Thread.currentThread();
                if (!this.loadable.isLoadCanceled()) {
                    TraceUtil.beginSection(this.loadable.getClass().getSimpleName() + ".load()");
                    this.loadable.load();
                    TraceUtil.endSection();
                }
                sendEmptyMessage(Loader.MSG_END_OF_SOURCE);
            } catch (IOException e) {
                obtainMessage(Loader.MSG_IO_EXCEPTION, e).sendToTarget();
            } catch (InterruptedException e2) {
                Assertions.checkState(this.loadable.isLoadCanceled());
                sendEmptyMessage(Loader.MSG_END_OF_SOURCE);
            } catch (Throwable e3) {
                Log.e(TAG, "Unexpected exception loading stream", e3);
                obtainMessage(Loader.MSG_IO_EXCEPTION, new UnexpectedLoaderException(e3)).sendToTarget();
            } catch (Throwable e32) {
                Log.e(TAG, "Unexpected error loading stream", e32);
                obtainMessage(Loader.MSG_FATAL_ERROR, e32).sendToTarget();
                throw e32;
            }
        }
    }

    public static final class UnexpectedLoaderException extends IOException {
        public UnexpectedLoaderException(Exception exception) {
            super("Unexpected " + exception.getClass().getSimpleName() + ": " + exception.getMessage(), exception);
        }
    }

    public Loader(String str) {
        this.downloadExecutorService = Util.newSingleThreadExecutor(str);
    }

    public void cancelLoading() {
        Assertions.checkState(this.loading);
        this.currentTask.quit();
    }

    public boolean isLoading() {
        return this.loading;
    }

    public void release() {
        release(null);
    }

    public void release(Runnable runnable) {
        if (this.loading) {
            cancelLoading();
        }
        if (runnable != null) {
            this.downloadExecutorService.submit(runnable);
        }
        this.downloadExecutorService.shutdown();
    }

    public void startLoading(Looper looper, Loadable loadable, Callback callback) {
        Assertions.checkState(!this.loading);
        this.loading = true;
        this.currentTask = new LoadTask(looper, loadable, callback);
        this.downloadExecutorService.submit(this.currentTask);
    }

    public void startLoading(Loadable loadable, Callback callback) {
        Looper myLooper = Looper.myLooper();
        Assertions.checkState(myLooper != null);
        startLoading(myLooper, loadable, callback);
    }
}
