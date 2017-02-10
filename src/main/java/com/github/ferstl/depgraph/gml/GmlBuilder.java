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

import java.util.Map.Entry;
import com.github.ferstl.depgraph.dot.EdgeAttributeRenderer;
import com.github.ferstl.depgraph.dot.NodeNameRenderer;
import com.github.ferstl.depgraph.graph.AbstractGraphBuilder;
import com.github.ferstl.depgraph.graph.GraphNode;

/**
 * A builder to create <a href="http://www.graphviz.org/doc/info/lang.html">DOT</a> strings by defining edges between
 * Nodes. The builder allows some customizations including custom {@link NodeNameRenderer}s and
 * {@link EdgeAttributeRenderer}s.
 *
 * @param <T> Type of the graph nodes.
 */
public final class GmlBuilder extends AbstractGraphBuilder {

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append("graph [\n");
    
    //output nodes
    for (Entry<String, GraphNode> entry : nodeDefinitions.entrySet()) {
      result.append("node [\n");
      result.append("id \"").append(entry.getKey()).append("\"\n");
      result.append("label \"").append(entry.getKey()).append("\"\n");
      result.append("]\n\n");
    }
    
    //output edges
    for (GraphEdge edge : edgeDefinitions) {
      result.append("edge [\n");
      result.append("source \"").append(edge.from).append("\"\n");
      result.append("target \"").append(edge.to).append("\"\n");
      result.append("]\n\n");
    }
    
    result.append("]");
    return result.toString();
  }

}
