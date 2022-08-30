import java.util.HashMap;

public class DisjointSet {
    HashMap<Integer, Integer> parent = new HashMap<>();

    public void makeSet(int N)
    {
        for (int i = 0; i < N; i++) {
            parent.put(i, i);
        }
    }

    public int Find(int k)
    {
        if (parent.get(k) == k) {
            return k;
        }

        return Find(parent.get(k));
    }

    public void Union(int a, int b)
    {
        int x = Find(a);
        int y = Find(b);

        parent.put(x, y);
    }

    @Override
    public String toString() {
        return parent.toString();
    }
}
