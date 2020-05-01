package shanshin.gleb.diplom.model;

import java.util.List;

public class Page {
    public int prevItemId;
    public int nextItemId;
    public List<UniversalStock> stocks;

    public Page(int prevItemId, int nextItemId, List<UniversalStock> stocks) {
        this.prevItemId = prevItemId;
        this.nextItemId = nextItemId;
        this.stocks = stocks;
    }
}
