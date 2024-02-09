package ru.yandex.practicum.filmorate.comporators;

import java.util.Comparator;
import java.util.Map;

public class CustomizedHashMap implements Comparator<Map.Entry<Integer, Integer>> {

    @Override
    public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
        return -o1.getValue().compareTo(o2.getValue());
    }

}
