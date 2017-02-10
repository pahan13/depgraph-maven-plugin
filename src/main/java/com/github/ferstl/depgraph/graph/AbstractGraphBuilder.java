package com.github.ferstl.depgraph.graph;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import com.github.ferstl.depgraph.dot.AttributeBuilder;
import com.github.ferstl.depgraph.dot.EdgeAttributeRenderer;
import com.github.ferstl.depgraph.dot.NodeAttributeRenderer;
import com.github.ferstl.depgraph.dot.NodeNameRenderer;

public abstract class AbstractGraphBuilder implements GraphBuilder<GraphNode> {
  private NodeNameRenderer<? super GraphNode> nodeNameRenderer;

  protected String graphName;
  private boolean omitSelfReferences;
  protected final Map<String, GraphNode> nodeDefinitions;
  protected final Set<GraphEdge> edgeDefinitions;

  public AbstractGraphBuilder() {
    this.graphName = "G";
    
    this.nodeNameRenderer = createDefaultNodeNameRenderer();

    this.nodeDefinitions = new LinkedHashMap<>();
    this.edgeDefinitions = new LinkedHashSet<>();
  }

  /**
   * toString method is used to output target graph :(
   */
  @Override
  public abstract String toString();
  
  @Override
  public AbstractGraphBuilder graphName(String name) {
    this.graphName = name;
    return this;
  }

  public AbstractGraphBuilder useNodeNameRenderer(NodeNameRenderer<? super GraphNode> nodeNameRenderer) {
    this.nodeNameRenderer = nodeNameRenderer;
    return this;
  }

  public AbstractGraphBuilder omitSelfReferences() {
    this.omitSelfReferences = true;
    return this;
  }

  // no edge will be created in case one or both nodes are null.
  @Override
  public AbstractGraphBuilder addEdge(GraphNode from, GraphNode to) {
    if (from != null && to != null) {
      //add nodes
      addNode(from);
      addNode(to);

      //add edge
      String fromName = this.nodeNameRenderer.createNodeName(from);
      String toName = this.nodeNameRenderer.createNodeName(to);

      if (!this.omitSelfReferences || !fromName.equals(toName)) {
        this.edgeDefinitions.add(new GraphEdge(fromName, toName));
      }
    }

    return this;
  }

  /**
   * Returns the node that was added <strong>first</strong> to this builder or the given node if new.
   *
   * @param node Node.
   * @return The firstly added node or the given node if not present.
   */
  @Override
  public GraphNode getEffectiveNode(GraphNode node) {
    String key = this.nodeNameRenderer.createNodeName(node);
    if (this.nodeDefinitions.containsKey(key)) {
      return this.nodeDefinitions.get(key);
    }

    return node;
  }

  private void addNode(GraphNode node) {
    String nodeName = this.nodeNameRenderer.createNodeName(node);
    this.nodeDefinitions.put(nodeName, node);
  }

  static NodeNameRenderer<GraphNode> createDefaultNodeNameRenderer() {
    return new NodeNameRenderer<GraphNode>() {

      @Override
      public String createNodeName(GraphNode node) {
        return node.toString();
      }
    };
  }

  protected static class GraphEdge {
    public final String from;
    public final String to;
    
    public GraphEdge(String from, String to) {
      super();
      this.from = from;
      this.to = to;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((from == null) ? 0 : from.hashCode());
      result = prime * result + ((to == null) ? 0 : to.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      GraphEdge other = (GraphEdge) obj;
      if (from == null) {
        if (other.from != null) return false;
      } else if (!from.equals(other.from)) return false;
      if (to == null) {
        if (other.to != null) return false;
      } else if (!to.equals(other.to)) return false;
      return true;
    }
  }

  public AbstractGraphBuilder nodeStyle(AttributeBuilder attributeBuilder) {
    //ignored since it is useful only for .dot format
    return this;
  }

  public AbstractGraphBuilder edgeStyle(AttributeBuilder attributeBuilder) {
  //ignored since it is useful only for .dot format
    return this;
  }

  public AbstractGraphBuilder useNodeAttributeRenderer(NodeAttributeRenderer<? super GraphNode> nodeAttributeRenderer) {
    //ignored since it is useful only for .dot format
    return this;
  }

  public AbstractGraphBuilder useEdgeAttributeRenderer(EdgeAttributeRenderer<? super GraphNode> edgeAttributeRenderer) {
    //ignored since it is useful only for .dot format
    return this;
  }

  @Override
  public AbstractGraphBuilder addEdge(GraphNode from, GraphNode to, EdgeAttributeRenderer<? super GraphNode> edgeAttributeRenderer) {
    //this EdgeAttributeRenderer is useful only for dot format so here it is omitted 
    addEdge(from, to);
    return this;
  }
}
