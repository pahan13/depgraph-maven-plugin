package com.github.ferstl.depgraph.graph;

import com.github.ferstl.depgraph.dot.AttributeBuilder;
import com.github.ferstl.depgraph.dot.EdgeAttributeRenderer;
import com.github.ferstl.depgraph.dot.NodeAttributeRenderer;
import com.github.ferstl.depgraph.dot.NodeNameRenderer;

public interface GraphBuilder<T> {

  public GraphBuilder<T> addEdge(T from, T to);

  public T getEffectiveNode(T node);

  public GraphBuilder<T> graphName(String artifactId);

  public GraphBuilder<T> addEdge(T from, T to, EdgeAttributeRenderer<? super T> edgeAttributeRenderer);

  public GraphBuilder<T> nodeStyle(AttributeBuilder defaultNodeAttributes);

  public GraphBuilder<T> edgeStyle(AttributeBuilder defaultEdgeAttributes);

  public GraphBuilder<T> useNodeNameRenderer(NodeNameRenderer<? super T> nodeNameRenderer);

  public GraphBuilder<T> useNodeAttributeRenderer(NodeAttributeRenderer<? super T> nodeAttributeRenderer);

  public GraphBuilder<T> useEdgeAttributeRenderer(EdgeAttributeRenderer<? super T> edgeAttributeRenderer);

  public GraphBuilder<T> omitSelfReferences();

}
