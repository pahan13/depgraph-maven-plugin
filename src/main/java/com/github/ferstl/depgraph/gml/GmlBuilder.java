/*
 * Copyright (c) 2014 - 2016 by Stefan Ferstl <st.ferstl@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.ferstl.depgraph.gml;

import com.github.ferstl.depgraph.dot.AttributeBuilder;
import com.github.ferstl.depgraph.dot.EdgeAttributeRenderer;
import com.github.ferstl.depgraph.dot.NodeAttributeRenderer;
import com.github.ferstl.depgraph.dot.NodeNameRenderer;
import com.github.ferstl.depgraph.graph.GraphBuilder;
import com.github.ferstl.depgraph.graph.GraphNode;

/**
 * A builder to create <a href="http://www.graphviz.org/doc/info/lang.html">DOT</a> strings by defining edges between
 * Nodes. The builder allows some customizations including custom {@link NodeNameRenderer}s and
 * {@link EdgeAttributeRenderer}s.
 *
 * @param <T> Type of the graph nodes.
 */
public final class GmlBuilder implements GraphBuilder<GraphNode> {

  @Override
  public GmlBuilder addEdge(GraphNode from, GraphNode to) {
    // TODO Auto-generated method stub
    return this;
  }

  @Override
  public GraphNode getEffectiveNode(GraphNode node) {
    // TODO Auto-generated method stub
    return node;
  }

  @Override
  public GmlBuilder graphName(String artifactId) {
    // TODO Auto-generated method stub
    return this;
  }

  @Override
  public GmlBuilder addEdge(GraphNode from, GraphNode to, EdgeAttributeRenderer<? super GraphNode> edgeAttributeRenderer) {
    // TODO Auto-generated method stub
    return this;
  }

  @Override
  public GraphBuilder<GraphNode> nodeStyle(AttributeBuilder defaultNodeAttributes) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GraphBuilder<GraphNode> edgeStyle(AttributeBuilder defaultEdgeAttributes) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GraphBuilder<GraphNode> useNodeNameRenderer(NodeNameRenderer<? super GraphNode> nodeNameRenderer) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GraphBuilder<GraphNode> useNodeAttributeRenderer(NodeAttributeRenderer<? super GraphNode> nodeAttributeRenderer) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GraphBuilder<GraphNode> useEdgeAttributeRenderer(EdgeAttributeRenderer<? super GraphNode> edgeAttributeRenderer) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GraphBuilder<GraphNode> omitSelfReferences() {
    // TODO Auto-generated method stub
    return null;
  }

}
