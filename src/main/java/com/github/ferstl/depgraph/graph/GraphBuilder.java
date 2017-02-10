package com.github.ferstl.depgraph.graph;

public interface GraphBuilder<T> {

  public GraphBuilder<T> addEdge(T from, T to);

  public T getEffectiveNode(T node);
}
