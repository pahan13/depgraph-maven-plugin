package com.github.ferstl.depgraph.graph;

import com.github.ferstl.depgraph.dot.EdgeAttributeRenderer;

public interface GraphBuilder<T> {

  public GraphBuilder<T> addEdge(T from, T to);

  public T getEffectiveNode(T node);

  public GraphBuilder<T> graphName(String artifactId);

  public GraphBuilder<T> addEdge(T from, T to, EdgeAttributeRenderer<? super T> edgeAttributeRenderer);

}
