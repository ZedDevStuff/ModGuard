package dev.zeddevstuff.modguard;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class Diff<T>
{
    private List<T> added = new ArrayList<>();
    public List<T> getAdded() { return added; }
    private List<T> removed = new ArrayList<>();
    public List<T> getRemoved() { return removed; }
    private List<T> unchanged = new ArrayList<>();
    public List<T> getUnchanged() { return unchanged; }
    private List<T> modified = new ArrayList<>();
    public List<T> getModified() { return modified; }

    public Diff(List<T> left, List<T> right)
    {
        for(T item : left)
        {
            if(right.contains(item))
            {
                unchanged.add(item);
            }
            else
            {
                removed.add(item);
            }
        }
        for(T item : right)
        {
            if(!left.contains(item))
            {
                added.add(item);
            }
        }
    }

    public Diff(List<T> left, List<T> right, BiFunction<T, List<T>, Optional<T>> find, BiFunction<T, T, Boolean> changed)
    {
        for (T item : left)
        {
            Optional<T> rightItem = find.apply(item, right);
            if (rightItem.isPresent())
            {
                if (changed.apply(item, rightItem.get()))
                {
                    modified.add(rightItem.get());
                }
                else
                {
                    unchanged.add(item);
                }
            } else
            {
                removed.add(item);
            }
        }
        for(int i = 0; i < right.size(); i++)
        {
            T item = right.get(i);
            Optional<T> leftItem = find.apply(item, left);
            if(leftItem.isEmpty())
            {
                added.add(item);
            }
        }
    }

    public static Diff<LoaderUtils.ModEntry> modlistDiff(List<LoaderUtils.ModEntry> left, List<LoaderUtils.ModEntry> right)
    {
        return new Diff<>(left, right,
            (mod, list) -> list.stream().filter(m -> m.id().equals(mod.id())).findFirst(),
            (mod1, mod2) -> !mod1.version().equals(mod2.version())
        );
    }
}
