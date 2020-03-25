package com.smoly87.fem.core.boundaryconditions;

public class IntervalQuery {
    protected final Integer start;
    protected final Integer end;
    protected final Integer len;

    public Integer getStart() {
        return start;
    }

    public Integer getEnd() {
        return end;
    }

    public Integer getLen() {
        return len;
    }

    public IntervalQuery(Integer start, Integer end, Integer len) {
        this.start = start;
        this.end = end;
        this.len = len;
    }
}
