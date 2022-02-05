package eu.buildserscoffee.web.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class FirstInFirstOutList<T> extends ArrayList<T> {

    private int maxCapacity = 100;

    public FirstInFirstOutList(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    @Override
    public boolean add(T t) {
        if(size() >= maxCapacity)
            remove(0);

        return super.add(t);
    }

    @Override
    public void add(int index, T element) {
        if(size() >= maxCapacity)
            remove(0);

        super.add(index, element);
    }

    @Override
    public T set(int index, T element) {
        if(index > maxCapacity)
            return null;
        return super.set(index, element);
    }
}
