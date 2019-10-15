package com.company;

import java.util.List;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class MySpliterator implements Spliterator<Integer> {
    private final List<Integer> list;
    AtomicInteger current = new AtomicInteger();

    public MySpliterator(List<Integer> list) {
        this.list = list;
    }

    /**
     * Попробовать выполнить действие и перейти к следующему элементу
     * @param action
     * @return
     */
    @Override
    public boolean tryAdvance(Consumer<? super Integer> action) {
        action.accept(list.get(current.getAndIncrement()));
        return current.get() < list.size();
    }

    /**
     * Попробовать разделиться
     * @return
     */
    @Override
    public Spliterator<Integer> trySplit() {
        int currentSize = list.size() - current.get();
        //если меньше 5, то слишком мало для разделения
        if (currentSize < 5) {
            return null;
        }
        // вычисляем позицию для разделения
        int splitPos = currentSize/2 + current.intValue();
        Spliterator<Integer> spliterator = new MySpliterator(list.subList(current.get(), splitPos));
        if (splitPos < list.size()) {
            current.set(splitPos);
            return spliterator;
        }
        return null;
    }

    @Override
    public long estimateSize() {
        return list.size() - current.get();
    }

    @Override
    public int characteristics() {
        return CONCURRENT;
    }
}
