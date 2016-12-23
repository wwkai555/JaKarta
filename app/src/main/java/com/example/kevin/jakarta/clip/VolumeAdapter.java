package com.example.kevin.jakarta.clip;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.kevin.jakarta.BR;
import com.example.kevin.jakarta.databinding.ListVolumeBarBinding;
import com.vedecoder.AEDecoder;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

/**
 * Created by ants on 12/12/2016.
 */

public class VolumeAdapter extends RecyclerView.Adapter<VolumeAdapter.ViewHolder> {
    private static long timeUS;

    public void setDurationMSPerBar(long timeMS) {
        timeUS = timeMS * 1000;
        Log.d("VolumeAdapter", "timeUS : " + timeUS);
    }

    interface Spec {
        boolean debug = true;
        long durationUSPerBar = timeUS;
        long decibelSamples = 80;
    }


    @NonNull private List<Item> items = emptyList();
    @NonNull private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    {
        setHasStableIds(true);
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ListVolumeBarBinding binding = ListVolumeBarBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override public long getItemId(int position) {
        return items.get(position).getTimeMS();
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        holder.binding.setItem(items.get(position));
    }

    @Override public int getItemCount() {
        return items.size();
    }

    public static class Item extends BaseObservable {
        private float volume = 0.5F;
        private final long timeMS;

        Item(long timeMS) {
            this.timeMS = timeMS;
        }

        @Bindable
        public float getVolume() {
            return volume;
        }

        public void setVolume(float volume) {
            this.volume = volume;
            notifyPropertyChanged(BR.volume);
        }

        long getTimeMS() {
            return timeMS;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @NonNull private final ListVolumeBarBinding binding;

        ViewHolder(@NonNull ListVolumeBarBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private static @NonNull List<Item> digest(@NonNull List<Item> items, @NonNull ByteBuffer buffer, long size, long pts, long duration) {
        int start = (int) (pts / Spec.durationUSPerBar);
        int end = (int) ((pts + duration) / Spec.durationUSPerBar);
        long sizePerBar = size / (end - start + 1);
        for (int i = start; i < end; i++) {
            Item item = safeGet(items, i);
            if (null == item) break;
            item.setVolume(calculateVolume(buffer, (int) ((i - start) * sizePerBar)));
        }
        return items;
    }

    @Nullable private static <T> T safeGet(@NonNull List<T> list, int index) {
        if (index < 0 || index >= list.size()) return null;
        return list.get(index);
    }

    private static float calculateVolume(@NonNull ByteBuffer buffer, int position) {
        int sum = 0;
        int count = 0;
        try {
            buffer.position(position + 10);
            for (int i = 0; i < Spec.decibelSamples; i++) {
                Short sample = buffer.order(ByteOrder.LITTLE_ENDIAN).getShort();
                sum += Math.abs(sample);
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sum / (float) (count * Short.MAX_VALUE);
    }


    private void appendBuffer(@NonNull ByteBuffer buffer, long size, long pts, long duration) {
        final List<Item> items = digest(this.items, buffer, size, pts, duration);
        mainThreadHandler.post(new Runnable() {
            @Override public void run() {
                VolumeAdapter.this.items = items;
                notifyDataSetChanged();
            }
        });
    }

    private void setDurationUS(long durationUS) {
        Log.d("VolumeAdapter", "durationUS : " + durationUS / 1e3);
        int count = (int) (durationUS / Spec.durationUSPerBar);
        List<Item> items = new ArrayList<>(count);
        for (int i = 0; i < count; i++) items.add(new Item(Spec.durationUSPerBar * i));
        this.items = unmodifiableList(items);
        notifyDataSetChanged();
    }

    private class AEDecoderOutput implements AEDecoder.AEDecoderOutput {

        @Override public void decoded(final ByteBuffer byteBuffer, final long size, final long pts, final long duration) {
            appendBuffer(byteBuffer, size, pts, duration);
        }
    }

    public void setInput(@NonNull String path) {
        final AEDecoder decoder = AEDecoder.fromPath(path, 1, 1);
        if (null == decoder)
            if (Spec.debug) throw new RuntimeException("Could not read audio file from " + path);
            else return;
        decoder.setOutput(new AEDecoderOutput());
        setDurationUS(decoder.getDurationUs());
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override public void run() {
                while (decoder.decodeFrame() == 0) ;
            }
        });
    }

    //for test
    public void test(final String path) {
        Observable.create(new Observable.OnSubscribe<AEDecoder>() {
            @Override public void call(Subscriber<? super AEDecoder> subscriber) {
                subscriber.onNext(AEDecoder.fromPath(path, 1, 1));
            }
        }).map(new Func1<AEDecoder, Pair<AEDecoder, List<Item>>>() {
            @Override public Pair<AEDecoder, List<Item>> call(AEDecoder aeDecoder) {
                if (null == aeDecoder)
                    if (Spec.debug)
                        throw new RuntimeException("Could not read audio file from " + path);
                aeDecoder.setOutput(new AEDecoderOutput());
                long aLong = aeDecoder.getDurationUs();
                Log.d("VolumeAdapter", "durationMS : " + aLong / 1e3);
                int count = (int) (aLong / Spec.durationUSPerBar);
                List<Item> items = new ArrayList<>(count);
                for (int i = 0; i < count; i++) items.add(new Item(Spec.durationUSPerBar * i));
                return new Pair<>(aeDecoder, unmodifiableList(items));
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Pair<AEDecoder, List<Item>>>() {
                    @Override public void call(final Pair<AEDecoder, List<Item>> pair) {
                        VolumeAdapter.this.items = pair.second;
                        notifyDataSetChanged();
                        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                            @Override public void run() {
                                while (pair.first.decodeFrame() == 0) ;
                            }
                        });
                    }
                });
    }

}
